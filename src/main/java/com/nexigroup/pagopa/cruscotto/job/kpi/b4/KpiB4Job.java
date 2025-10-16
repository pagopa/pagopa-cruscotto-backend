package com.nexigroup.pagopa.cruscotto.job.kpi.b4;

import com.nexigroup.pagopa.cruscotto.config.ApplicationProperties;
import com.nexigroup.pagopa.cruscotto.job.config.JobConstant;
import com.nexigroup.pagopa.cruscotto.domain.enumeration.ModuleCode;
import com.nexigroup.pagopa.cruscotto.domain.enumeration.OutcomeStatus;
import com.nexigroup.pagopa.cruscotto.repository.PagopaApiLogRepository;
import com.nexigroup.pagopa.cruscotto.service.InstanceService;
import com.nexigroup.pagopa.cruscotto.service.InstanceModuleService;
import com.nexigroup.pagopa.cruscotto.service.KpiConfigurationService;
import com.nexigroup.pagopa.cruscotto.service.KpiB4DataService;
import com.nexigroup.pagopa.cruscotto.service.dto.InstanceDTO;
import com.nexigroup.pagopa.cruscotto.service.dto.InstanceModuleDTO;
import com.nexigroup.pagopa.cruscotto.service.dto.KpiConfigurationDTO;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import org.springframework.transaction.annotation.Transactional;

import org.springframework.lang.NonNull;
import org.quartz.DisallowConcurrentExecution;
import org.quartz.JobDetail;
import org.quartz.JobExecutionContext;
import org.quartz.JobKey;
import org.quartz.Scheduler;
import org.quartz.SimpleScheduleBuilder;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.quartz.QuartzJobBean;
import org.springframework.stereotype.Component;

@Component
@DisallowConcurrentExecution
public class KpiB4Job extends QuartzJobBean {

    private static final Logger LOGGER = LoggerFactory.getLogger(KpiB4Job.class);

    private final InstanceService instanceService;
    private final InstanceModuleService instanceModuleService;
    private final ApplicationProperties applicationProperties;
    private final KpiConfigurationService kpiConfigurationService;
    private final KpiB4DataService kpiB4DataService;
    private final PagopaApiLogRepository pagopaApiLogRepository;
    private final Scheduler scheduler;

    public KpiB4Job(
        InstanceService instanceService,
        InstanceModuleService instanceModuleService,
        ApplicationProperties applicationProperties,
        KpiConfigurationService kpiConfigurationService,
        KpiB4DataService kpiB4DataService,
        PagopaApiLogRepository pagopaApiLogRepository,
        Scheduler scheduler) {
        this.instanceService = instanceService;
        this.instanceModuleService = instanceModuleService;
        this.applicationProperties = applicationProperties;
        this.kpiConfigurationService = kpiConfigurationService;
        this.kpiB4DataService = kpiB4DataService;
        this.pagopaApiLogRepository = pagopaApiLogRepository;
        this.scheduler = scheduler;
    }

    @Override
    @Transactional
    protected void executeInternal(@NonNull JobExecutionContext context) {
        LOGGER.info("Start calculate kpi B.4");

        try {
            if (!applicationProperties.getJob().getKpiB4Job().isEnabled()) {
                LOGGER.info("Job calculate kpi B.4 disabled. Exit...");
                return;
            }

            List<InstanceDTO> instanceDTOS = instanceService.findInstanceToCalculate(
                ModuleCode.B4,
                applicationProperties.getJob().getKpiB4Job().getLimit()
            );

            if (instanceDTOS.isEmpty()) {
                LOGGER.info("No instances to calculate for kpi B.4");
                return;
            }

            LOGGER.info("Found {} instances to calculate for kpi B.4", instanceDTOS.size());

            KpiConfigurationDTO kpiConfigurationDTO = kpiConfigurationService
                .findKpiConfigurationByCode(ModuleCode.B4.code)
                .orElseThrow(() -> new RuntimeException("KPI Configuration not found for module B.4"));

            for (InstanceDTO instanceDTO : instanceDTOS) {
                try {
                    processInstance(instanceDTO, kpiConfigurationDTO);
                } catch (Exception e) {
                    LOGGER.error("Error processing instance {} for kpi B.4: {}", instanceDTO.getId(), e.getMessage(), e);
                }
            }

        } catch (Exception e) {
            LOGGER.error("Error in KPI B.4 job execution: {}", e.getMessage(), e);
        }

        LOGGER.info("End calculate kpi B.4");
    }

    private void processInstance(InstanceDTO instanceDTO, KpiConfigurationDTO kpiConfigurationDTO) {
        LOGGER.info("Processing instance {} for KPI B.4", instanceDTO.getId());

        try {
            // Find the instance module to update the status
            InstanceModuleDTO instanceModuleDTO = instanceModuleService
                .findOne(instanceDTO.getId(), kpiConfigurationDTO.getModuleId())
                .orElseThrow(() -> new RuntimeException("KPI B.4 InstanceModule not found"));

            processInstanceModule(instanceDTO, instanceModuleDTO, kpiConfigurationDTO);

        } catch (Exception e) {
            LOGGER.error("Error processing instance {} for KPI B.4: {}", instanceDTO.getId(), e.getMessage(), e);
        }
    }

    private void processInstanceModule(InstanceDTO instanceDTO, InstanceModuleDTO instanceModuleDTO, 
                                     KpiConfigurationDTO kpiConfigurationDTO) {
        LOGGER.info("Processing instance module {} for KPI B.4", instanceModuleDTO.getId());

        try {
            // REQUISITO: Verifica prerequisiti - controllo presenza dati API log per il periodo
            if (!hasApiLogDataForPeriod(instanceDTO)) {
                LOGGER.warn("SKIPPING KPI B.4 calculation for instance {} - No API log data for analysis period {} to {}", 
                           instanceDTO.getId(), 
                           instanceDTO.getAnalysisPeriodStartDate(),
                           instanceDTO.getAnalysisPeriodEndDate());
                return; // Non eseguire l'analisi se non ci sono dati
            }

            // Update instance status from "planned" to "in progress"
            instanceService.updateInstanceStatusInProgress(instanceDTO.getId());

            // Calcola la data di analisi basata sulla configurazione  
            LocalDate analysisDate = calculateAnalysisDate(instanceDTO, instanceModuleDTO);
            
            LOGGER.info("Calculating KPI B.4 for instance {} on date {} (period: {} to {})", 
                       instanceDTO.getId(), analysisDate.format(DateTimeFormatter.ISO_LOCAL_DATE),
                       instanceDTO.getAnalysisPeriodStartDate(), instanceDTO.getAnalysisPeriodEndDate());

            // Implementazione logica KPI B.4 basata sull'analisi
            OutcomeStatus outcome = calculateKpiB4Outcome(instanceDTO, kpiConfigurationDTO);

            LOGGER.info("KPI B.4 outcome for instance {}: {} (Partner: {})", 
                       instanceDTO.getId(), outcome, instanceDTO.getPartnerFiscalCode());

            // Salva i risultati tramite il service esistente
            OutcomeStatus finalOutcome = kpiB4DataService.saveKpiB4Results(instanceDTO, instanceModuleDTO, 
                                                                          kpiConfigurationDTO, analysisDate, outcome);

            // Update automatic outcome of instance module with the final outcome (potentially corrected for monthly evaluation)
            instanceModuleService.updateAutomaticOutcome(instanceModuleDTO.getId(), finalOutcome);

            LOGGER.info("Instance module {} updated with final outcome: {}", instanceModuleDTO.getId(), finalOutcome);

            // Trigger calculateStateInstanceJob to update instance state
            try {
                JobDetail job = scheduler.getJobDetail(JobKey.jobKey(JobConstant.CALCULATE_STATE_INSTANCE_JOB, "DEFAULT"));

                Trigger trigger = TriggerBuilder.newTrigger()
                    .usingJobData("instanceId", instanceDTO.getId())
                    .withSchedule(SimpleScheduleBuilder.simpleSchedule().withMisfireHandlingInstructionFireNow().withRepeatCount(0))
                    .forJob(job)
                    .build();

                scheduler.scheduleJob(trigger);

                LOGGER.info("Triggered calculateStateInstanceJob for instance: {}", instanceDTO.getId());

            } catch (Exception e) {
                LOGGER.error("Error triggering calculateStateInstanceJob for instance {}: {}", 
                           instanceDTO.getId(), e.getMessage(), e);
            }

            LOGGER.info("KPI B.4 calculation completed for instance module {} with final outcome: {}", 
                       instanceModuleDTO.getId(), finalOutcome);

        } catch (Exception e) {
            LOGGER.error("Error processing instance module {} for KPI B.4: {}", 
                        instanceModuleDTO.getId(), e.getMessage(), e);
            
            // In caso di errore, salva il risultato con outcome KO solo se il problema non è mancanza dati
            try {
                LocalDate analysisDate = calculateAnalysisDate(instanceDTO, instanceModuleDTO);
                kpiB4DataService.saveKpiB4Results(instanceDTO, instanceModuleDTO, 
                                                kpiConfigurationDTO, analysisDate, OutcomeStatus.KO);
            } catch (Exception saveException) {
                LOGGER.error("Error saving error outcome for instance module {}: {}", 
                           instanceModuleDTO.getId(), saveException.getMessage(), saveException);
            }
        }
    }

    private LocalDate calculateAnalysisDate(InstanceDTO instanceDTO, InstanceModuleDTO instanceModuleDTO) {
        // La data di analisi è la data prevista dall'istanza o la data odierna
        LocalDate analysisDate = instanceDTO.getPredictedDateAnalysis();
        if (analysisDate == null) {
            analysisDate = LocalDate.now();
        }
        
        LOGGER.debug("Analysis date calculated for instance {}: {}", instanceDTO.getId(), analysisDate);
        return analysisDate;
    }

    /**
     * Verifica se esistono dati nella tabella pagopa_apilog per il periodo dell'istanza.
     * Se non esistono record per il periodo, l'analisi non deve essere effettuata.
     *
     * @param instanceDTO l'istanza da analizzare
     * @return true se esistono dati per il periodo, false altrimenti
     */
    private boolean hasApiLogDataForPeriod(InstanceDTO instanceDTO) {
        LocalDate periodStart = instanceDTO.getAnalysisPeriodStartDate();
        LocalDate periodEnd = instanceDTO.getAnalysisPeriodEndDate();
        
        if (periodStart == null || periodEnd == null) {
            LOGGER.warn("Analysis period not defined for instance {}: start={}, end={}", 
                       instanceDTO.getId(), periodStart, periodEnd);
            return false;
        }

        boolean hasData = pagopaApiLogRepository.existsDataInPeriod(periodStart, periodEnd);
        LOGGER.info("API log data check for period {} to {}: {}", 
                   periodStart, periodEnd, hasData ? "DATA FOUND" : "NO DATA");
        
        return hasData;
    }

    /**
     * Calcola l'outcome del KPI B.4 basato sulla logica dell'analisi.
     * Considera il tipo di valutazione configurata:
     * - TOTALE: Verifica la percentuale di request "paCreate" rispetto a "GPD"/"ACA" sull'intero periodo
     * - MESE: Se almeno un mese ha esito KO nei detail results, l'esito complessivo è KO
     */
    private OutcomeStatus calculateKpiB4Outcome(InstanceDTO instanceDTO, KpiConfigurationDTO kpiConfigurationDTO) {
        LOGGER.info("Calculating KPI B.4 outcome for instance {} partner {} with evaluation type: {}", 
                   instanceDTO.getId(), instanceDTO.getPartnerFiscalCode(), kpiConfigurationDTO.getEvaluationType());

        try {
            LocalDate periodStart = instanceDTO.getAnalysisPeriodStartDate();
            LocalDate periodEnd = instanceDTO.getAnalysisPeriodEndDate();
            String partnerFiscalCode = instanceDTO.getPartnerFiscalCode();

            LOGGER.info("Analysis period: {} to {} for partner: {}", periodStart, periodEnd, partnerFiscalCode);

            // REQUISITO: Verifica prerequisiti - se non esistono dati nella tabella pagopa_apilog 
            // per il periodo dell'istanza, l'analisi non deve essere effettuata
            if (!hasApiLogDataForPeriod(instanceDTO)) {
                LOGGER.warn("SKIPPING KPI B.4 analysis for instance {} - No API log data found for period {} to {}", 
                           instanceDTO.getId(), periodStart, periodEnd);
                throw new RuntimeException("No API log data available for analysis period");
            }

            // Verifica il tipo di valutazione configurata
            if (kpiConfigurationDTO.getEvaluationType() != null && 
                kpiConfigurationDTO.getEvaluationType().toString().equals("MESE")) {
                
                LOGGER.info("Using MONTHLY evaluation type - outcome will be determined by checking detail results after saving");
                
                // Per la valutazione mensile, il calcolo iniziale restituisce sempre OK
                // L'outcome finale sarà determinato dal KpiB4DataServiceImpl controllando 
                // se ci sono detail results mensili con outcome KO
                return OutcomeStatus.OK;
                
            } else {
                // Valutazione TOTALE: usa la logica sui dati complessivi del periodo
                LOGGER.info("Using TOTAL evaluation type - calculating outcome on entire period");
                return calculateTotalPeriodOutcome(partnerFiscalCode, periodStart, periodEnd, kpiConfigurationDTO);
            }

        } catch (Exception e) {
            LOGGER.error("Error calculating KPI B.4 outcome for instance {}: {}", 
                        instanceDTO.getId(), e.getMessage(), e);
            return OutcomeStatus.KO;
        }
    }

    /**
     * Calcola l'outcome basato sui dati del periodo totale.
     */
    private OutcomeStatus calculateTotalPeriodOutcome(String partnerFiscalCode, LocalDate periodStart, 
                                                     LocalDate periodEnd, KpiConfigurationDTO kpiConfigurationDTO) {
        
        // Query per contare le request dalla tabella pagopa_apilog usando il repository
        Long totalGpdAcaRequests = pagopaApiLogRepository.calculateTotalGpdAcaRequests(partnerFiscalCode, periodStart, periodEnd);
        Long totalPaCreateRequests = pagopaApiLogRepository.calculateTotalPaCreateRequests(partnerFiscalCode, periodStart, periodEnd);
        
        // Gestione valori null (nel caso non ci siano dati)
        if (totalGpdAcaRequests == null) totalGpdAcaRequests = 0L;
        if (totalPaCreateRequests == null) totalPaCreateRequests = 0L;

        LOGGER.info("API requests for partner {}: GPD/ACA={}, paCreate={}", 
                   partnerFiscalCode, totalGpdAcaRequests, totalPaCreateRequests);

        // Calcola la percentuale di paCreate rispetto al totale
        double paCreatePercentage = 0.0;
        if (totalGpdAcaRequests > 0) {
            paCreatePercentage = (totalPaCreateRequests.doubleValue() / totalGpdAcaRequests.doubleValue()) * 100.0;
        }

        // Recupera soglia e tolleranza dalla configurazione
        Double thresholdPercentage = kpiConfigurationDTO.getEligibilityThreshold();
        Double tolerancePercentage = kpiConfigurationDTO.getTolerance();

        if (thresholdPercentage == null) thresholdPercentage = 0.0;
        if (tolerancePercentage == null) tolerancePercentage = 0.0;

        double maxAllowedPercentage = thresholdPercentage + tolerancePercentage;

        LOGGER.info("KPI B.4 calculation: paCreate {}%, threshold {}%, tolerance {}%, max allowed {}%", 
                   paCreatePercentage, thresholdPercentage, tolerancePercentage, maxAllowedPercentage);

        // Se la percentuale di paCreate supera la soglia + tolleranza, il KPI è KO
        if (paCreatePercentage > maxAllowedPercentage) {
            LOGGER.warn("KPI B.4 NON-COMPLIANT for partner {}: paCreate {}% exceeds max allowed {}%", 
                       partnerFiscalCode, paCreatePercentage, maxAllowedPercentage);
            return OutcomeStatus.KO;
        } else {
            LOGGER.info("KPI B.4 COMPLIANT for partner {}: paCreate {}% within allowed {}%", 
                       partnerFiscalCode, paCreatePercentage, maxAllowedPercentage);
            return OutcomeStatus.OK;
        }
    }

    /**
     * Schedules a job for a single instance calculation.
     * This method can be used for on-demand calculations.
     * 
     * @param instanceIdentification the instance identification
     */
    public void scheduleJobForSingleInstance(String instanceIdentification) {
        LOGGER.info("Scheduling KPI B.4 job for single instance: {}", instanceIdentification);

        try {
            JobDetail jobDetail = org.quartz.JobBuilder.newJob(KpiB4Job.class)
                .withIdentity("kpiB4Job-" + instanceIdentification + "-" + System.currentTimeMillis())
                .build();

            Trigger trigger = TriggerBuilder.newTrigger()
                .withIdentity("kpiB4Trigger-" + instanceIdentification + "-" + System.currentTimeMillis())
                .forJob(jobDetail)
                .withSchedule(SimpleScheduleBuilder.simpleSchedule())
                .startNow()
                .build();

            scheduler.scheduleJob(jobDetail, trigger);

            LOGGER.info("KPI B.4 job scheduled successfully for instance: {}", instanceIdentification);

        } catch (Exception e) {
            LOGGER.error("Error scheduling KPI B.4 job for instance {}: {}", 
                        instanceIdentification, e.getMessage(), e);
        }
    }
}