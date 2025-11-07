package com.nexigroup.pagopa.cruscotto.job.kpi.c2;

import com.nexigroup.pagopa.cruscotto.config.ApplicationProperties;
import com.nexigroup.pagopa.cruscotto.domain.enumeration.ModuleCode;
import com.nexigroup.pagopa.cruscotto.domain.enumeration.OutcomeStatus;
import com.nexigroup.pagopa.cruscotto.job.config.JobConstant;
import com.nexigroup.pagopa.cruscotto.repository.PagopaApiLogRepository;
import com.nexigroup.pagopa.cruscotto.repository.PagopaSendRepository;
import com.nexigroup.pagopa.cruscotto.service.*;
import com.nexigroup.pagopa.cruscotto.service.dto.AnagInstitutionDTO;
import com.nexigroup.pagopa.cruscotto.service.dto.InstanceDTO;
import com.nexigroup.pagopa.cruscotto.service.dto.InstanceModuleDTO;
import com.nexigroup.pagopa.cruscotto.service.dto.KpiConfigurationDTO;
import com.nexigroup.pagopa.cruscotto.service.filter.AnagInstitutionFilter;
import com.nexigroup.pagopa.cruscotto.service.impl.KpiC2ServiceImpl;
import org.quartz.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.lang.NonNull;
import org.springframework.scheduling.quartz.QuartzJobBean;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Component
@DisallowConcurrentExecution
public class KpiC2Job extends QuartzJobBean {

    private static final Logger LOGGER = LoggerFactory.getLogger(KpiC2Job.class);

    private final InstanceService instanceService;
    private final InstanceModuleService instanceModuleService;
    private final ApplicationProperties applicationProperties;
    private final KpiConfigurationService kpiConfigurationService;
    private final KpiC2DataService kpiC2DataService;
    private final Scheduler scheduler;
    private final AnagInstitutionService anagInstitutionService;
    private final PagopaSendRepository pagopaSendRepository;

    public KpiC2Job(
        InstanceService instanceService,
        InstanceModuleService instanceModuleService,
        ApplicationProperties applicationProperties,
        KpiConfigurationService kpiConfigurationService,
        KpiC2DataService kpiC2DataService,
        PagopaApiLogRepository pagopaApiLogRepository,
        Scheduler scheduler, AnagInstitutionService anagInstitutionService, PagopaSendRepository pagopaSendRepository) {
        this.instanceService = instanceService;
        this.instanceModuleService = instanceModuleService;
        this.applicationProperties = applicationProperties;
        this.kpiConfigurationService = kpiConfigurationService;
        this.kpiC2DataService = kpiC2DataService;
        this.scheduler = scheduler;
        this.anagInstitutionService = anagInstitutionService;
        this.pagopaSendRepository = pagopaSendRepository;
    }

    @Override
    @Transactional
    protected void executeInternal(@NonNull JobExecutionContext context) {
        LOGGER.info("Start calculate kpi C.2");

        try {
            if (!applicationProperties.getJob().getKpiC2Job().isEnabled()) {
                LOGGER.info("Job calculate kpi C.2 disabled. Exit...");
                return;
            }

            List<InstanceDTO> instanceDTOS = instanceService.findInstanceToCalculate(
                ModuleCode.C2,
                applicationProperties.getJob().getKpiC2Job().getLimit()
            );

            if (instanceDTOS.isEmpty()) {
                LOGGER.info("No instances to calculate for kpi C.2");
                return;
            }

            LOGGER.info("Found {} instances to calculate for kpi C.2", instanceDTOS.size());

            KpiConfigurationDTO kpiConfigurationDTO = kpiConfigurationService
                .findKpiConfigurationByCode(ModuleCode.C2.code)
                .orElseThrow(() -> new RuntimeException("KPI Configuration not found for module C.2"));

            for (InstanceDTO instanceDTO : instanceDTOS) {
                try {
                    processInstance(instanceDTO, kpiConfigurationDTO);
                } catch (Exception e) {
                    LOGGER.error("Error processing instance {} for kpi C.2: {}", instanceDTO.getId(), e.getMessage(), e);
                }
            }

        } catch (Exception e) {
            LOGGER.error("Error in KPI C.2 job execution: {}", e.getMessage(), e);
        }

        LOGGER.info("End calculate kpi C.2");
    }

    private void processInstance(InstanceDTO instanceDTO, KpiConfigurationDTO kpiConfigurationDTO) {
        LOGGER.info("Processing instance {} for KPI C.2", instanceDTO.getId());

        try {
            // Find the instance module to update the status
            InstanceModuleDTO instanceModuleDTO = instanceModuleService
                .findOne(instanceDTO.getId(), kpiConfigurationDTO.getModuleId())
                .orElseThrow(() -> new RuntimeException("KPI C.2 InstanceModule not found"));

            processInstanceModule(instanceDTO, instanceModuleDTO, kpiConfigurationDTO);

        } catch (Exception e) {
            LOGGER.error("Error processing instance {} for KPI C.2: {}", instanceDTO.getId(), e.getMessage(), e);
        }
    }

    private void processInstanceModule(InstanceDTO instanceDTO, InstanceModuleDTO instanceModuleDTO,
                                       KpiConfigurationDTO kpiConfigurationDTO) {
        LOGGER.info("Processing instance module {} for KPI C.2", instanceModuleDTO.getId());

        try {
            AnagInstitutionFilter filter = new AnagInstitutionFilter();
            filter.setPartnerId(instanceDTO.getPartnerId());
            List<AnagInstitutionDTO> inistutionListPartner = anagInstitutionService.findAllNoPaging(filter);
            List<String> listInstitutionFiscalCode = inistutionListPartner.stream()
                .map(anagInstitutionDTO -> anagInstitutionDTO.getInstitutionIdentification().getFiscalCode()).toList();

            // REQUISITO: Verifica prerequisiti - controllo presenza dati API log per il periodo
            if (!hasPagoPaSendDataForPeriod(instanceDTO,listInstitutionFiscalCode)) {
                LOGGER.warn("SKIPPING KPI C.2 calculation for instance {} - No API log data for analysis period {} to {}",
                    instanceDTO.getId(),
                    instanceDTO.getAnalysisPeriodStartDate(),
                    instanceDTO.getAnalysisPeriodEndDate());
                return; // Non eseguire l'analisi se non ci sono dati
            }

            // Update instance status from "planned" to "in progress"
            instanceService.updateInstanceStatusInProgress(instanceDTO.getId());

            // Calcola la data di analisi basata sulla configurazione
            LocalDate analysisDate = calculateAnalysisDate(instanceDTO, instanceModuleDTO);

            LOGGER.info("Calculating KPI C.2 for instance {} on date {} (period: {} to {})",
                instanceDTO.getId(), analysisDate.format(DateTimeFormatter.ISO_LOCAL_DATE),
                instanceDTO.getAnalysisPeriodStartDate(), instanceDTO.getAnalysisPeriodEndDate());

            // Implementazione logica KPI C.2 basata sull'analisi
            OutcomeStatus outcome = calculateKpiC2Outcome(instanceDTO, kpiConfigurationDTO, listInstitutionFiscalCode);

            LOGGER.info("KPI C.2 outcome for instance {}: {} (Partner: {})",
                instanceDTO.getId(), outcome, instanceDTO.getPartnerFiscalCode());

            // Salva i risultati tramite il service esistente
            OutcomeStatus finalOutcome = kpiC2DataService.saveKpiC2Results(instanceDTO, instanceModuleDTO,
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

            LOGGER.info("KPI C.2 calculation completed for instance module {} with final outcome: {}",
                instanceModuleDTO.getId(), finalOutcome);

        } catch (Exception e) {
            LOGGER.error("Error processing instance module {} for KPI C.2: {}",
                instanceModuleDTO.getId(), e.getMessage(), e);

            // In caso di errore, salva il risultato con outcome KO solo se il problema non è mancanza dati
            try {
                LocalDate analysisDate = calculateAnalysisDate(instanceDTO, instanceModuleDTO);
                kpiC2DataService.saveKpiC2Results(instanceDTO, instanceModuleDTO,
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
     * @param instanceDTO               l'istanza da analizzare
     * @param listInstitutionFiscalCode
     * @return true se esistono dati per il periodo, false altrimenti
     */
    private boolean hasPagoPaSendDataForPeriod(InstanceDTO instanceDTO, List<String> listInstitutionFiscalCode) {
        LocalDate periodStart = instanceDTO.getAnalysisPeriodStartDate();
        LocalDate periodEnd = instanceDTO.getAnalysisPeriodEndDate();
        // aaaaaa
        if (periodStart == null || periodEnd == null) {
            LOGGER.warn("Analysis period not defined for instance {}: start={}, end={}",
                instanceDTO.getId(), periodStart, periodEnd);
            return false;
        }

        Long numberInstitutionSend = pagopaSendRepository.calculateTotalNumberInstitutionSend(null, listInstitutionFiscalCode, periodStart.atStartOfDay(), endOfDay(periodEnd));
        Boolean hasData = numberInstitutionSend>0 ? true : false;
        LOGGER.info("API log data check for period {} to {}: {}",
            periodStart, periodEnd, hasData ? "DATA FOUND" : "NO DATA");

        return hasData;
    }
    private LocalDateTime endOfDay(LocalDate date){
        return LocalDateTime.of(date, LocalTime.MAX);
    }
    /**
     * Calcola l'outcome del KPI C.2 basato sulla logica dell'analisi.
     * Considera il tipo di valutazione configurata:
     * - TOTALE: Verifica la percentuale di request "paCreate" rispetto a "GPD"/"ACA" sull'intero periodo
     * - MESE: Se almeno un mese ha esito KO nei detail results, l'esito complessivo è KO
     */
    private OutcomeStatus calculateKpiC2Outcome(InstanceDTO instanceDTO, KpiConfigurationDTO kpiConfigurationDTO, List<String> inistutionListPartner) {
        LOGGER.info("Calculating KPI C.2 outcome for instance {} partner {} with evaluation type: {}",
            instanceDTO.getId(), instanceDTO.getPartnerFiscalCode(), kpiConfigurationDTO.getEvaluationType());

        try {
            LocalDate periodStart = instanceDTO.getAnalysisPeriodStartDate();
            LocalDate periodEnd = instanceDTO.getAnalysisPeriodEndDate();
            String partnerFiscalCode = instanceDTO.getPartnerFiscalCode();

            LOGGER.info("Analysis period: {} to {} for partner: {}", periodStart, periodEnd, partnerFiscalCode);

            // REQUISITO: Verifica prerequisiti - se non esistono dati nella tabella pagopa_apilog
            // per il periodo dell'istanza, l'analisi non deve essere effettuata
            if (!hasPagoPaSendDataForPeriod(instanceDTO,inistutionListPartner)) {
                LOGGER.warn("SKIPPING KPI C.2 analysis for instance {} - No API log data found for period {} to {}",
                    instanceDTO.getId(), periodStart, periodEnd);
                throw new RuntimeException("No API log data available for analysis period");
            }

            // Verifica il tipo di valutazione configurata
            if (kpiConfigurationDTO.getEvaluationType() != null &&
                kpiConfigurationDTO.getEvaluationType().toString().equals("MESE")) {

                LOGGER.info("Using MONTHLY evaluation type - outcome will be determined by checking detail results after saving");

                // Per la valutazione mensile, il calcolo iniziale restituisce sempre OK
                // L'outcome finale sarà determinato dal KpiC2DataServiceImpl controllando
                // se ci sono detail results mensili con outcome KO
                return OutcomeStatus.OK;

            } else {
                // Valutazione TOTALE: usa la logica sui dati complessivi del periodo
                LOGGER.info("Using TOTAL evaluation type - calculating outcome on entire period");
                return calculateTotalPeriodOutcome(partnerFiscalCode, periodStart, periodEnd, kpiConfigurationDTO, inistutionListPartner);
            }

        } catch (Exception e) {
            LOGGER.error("Error calculating KPI C.2 outcome for instance {}: {}",
                instanceDTO.getId(), e.getMessage(), e);
            return OutcomeStatus.KO;
        }
    }

    /**
     * Calcola l'outcome basato sui dati del periodo totale.
     */
    private OutcomeStatus calculateTotalPeriodOutcome(String partnerFiscalCode, LocalDate periodStart,
                                                      LocalDate periodEnd, KpiConfigurationDTO kpiConfigurationDTO, List<String> inistutionListPartner) {

        // Query per contare le request dalla tabella pagopa_apilog usando il repository
        Long totalNumebrInstitution = pagopaSendRepository.calculateTotalNumberInsitution(null,  inistutionListPartner);
        Long totalNumebrInstitutionSend = pagopaSendRepository.calculateTotalNumberInstitutionSend(null, inistutionListPartner ,periodStart.atStartOfDay(), endOfDay(periodEnd));
        BigDecimal percentageInstitution = KpiC2ServiceImpl.getPercentagePeriodInstitution(totalNumebrInstitution, totalNumebrInstitutionSend);

        Long totalNumberPayment = pagopaSendRepository.calculateTotalNumberPayment(partnerFiscalCode, inistutionListPartner,periodStart.atStartOfDay(), endOfDay(periodEnd));
        Long totalNumberNotification = pagopaSendRepository.calculateTotalNumberNotification(partnerFiscalCode, inistutionListPartner,periodStart.atStartOfDay(), endOfDay(periodEnd));
        BigDecimal percentageNotification = KpiC2ServiceImpl.getPercentagePeriodNotification(totalNumberPayment, totalNumberNotification);

        // Gestione valori null (nel caso non ci siano dati)
        if (totalNumebrInstitution == null) totalNumebrInstitution = 0L;
        if (totalNumebrInstitutionSend == null) totalNumebrInstitutionSend = 0L;

        LOGGER.info("API requests for partner {}: TOT  GPD/ACA={}, KO GPD/ACA={}",
            partnerFiscalCode, totalNumebrInstitution, totalNumebrInstitutionSend);


        // Recupera soglia e tolleranza dalla configurazione
        Double institutionTolerance = kpiConfigurationDTO.getInstitutionTolerance() !=null ? kpiConfigurationDTO.getInstitutionTolerance().doubleValue():0.0;
        Double notificationTolerance = kpiConfigurationDTO.getNotificationTolerance()!=null ? kpiConfigurationDTO.getNotificationTolerance().doubleValue():0.0;

        BigDecimal toleranceInstitutionBD = BigDecimal.valueOf(institutionTolerance);
        BigDecimal toleranceNotificationBD = BigDecimal.valueOf(notificationTolerance);

        // Se la percentuale CP è <= (soglia + tolleranza) → OK, altrimenti → KO
        boolean isCompliantInstitution = percentageInstitution.compareTo(toleranceInstitutionBD) >= 0;
        boolean isCompliantNotification = percentageNotification.compareTo(toleranceNotificationBD) >= 0;

        OutcomeStatus outcome = isCompliantInstitution && isCompliantNotification ? OutcomeStatus.OK : OutcomeStatus.KO;

        LOGGER.info("KPI C.2 calculation::  " +
                "percentageInstitution={}%, percentageNotification={}%" +
                ", toleranceInstitution={}%, toleranceNotification={}%, " +
                "outcome={}",
            percentageInstitution, percentageNotification, institutionTolerance, notificationTolerance, outcome);
        return outcome;
    }

    /**
     * Schedules a job for a single instance calculation.
     * This method can be used for on-demand calculations.
     *
     * @param instanceIdentification the instance identification
     */
    public void scheduleJobForSingleInstance(String instanceIdentification) {
        LOGGER.info("Scheduling KPI C.2 job for single instance: {}", instanceIdentification);

        try {
            JobDetail jobDetail = org.quartz.JobBuilder.newJob(KpiC2Job.class)
                .withIdentity("kpiC2Job-" + instanceIdentification + "-" + System.currentTimeMillis())
                .build();

            Trigger trigger = TriggerBuilder.newTrigger()
                .withIdentity("kpiC2Trigger-" + instanceIdentification + "-" + System.currentTimeMillis())
                .forJob(jobDetail)
                .withSchedule(SimpleScheduleBuilder.simpleSchedule())
                .startNow()
                .build();

            scheduler.scheduleJob(jobDetail, trigger);

            LOGGER.info("KPI C.2 job scheduled successfully for instance: {}", instanceIdentification);

        } catch (Exception e) {
            LOGGER.error("Error scheduling KPI C.2 job for instance {}: {}",
                instanceIdentification, e.getMessage(), e);
        }
    }
}
