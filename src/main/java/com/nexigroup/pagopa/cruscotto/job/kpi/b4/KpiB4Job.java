package com.nexigroup.pagopa.cruscotto.job.kpi.b4;

import com.nexigroup.pagopa.cruscotto.config.ApplicationProperties;
import com.nexigroup.pagopa.cruscotto.domain.enumeration.ModuleCode;
import com.nexigroup.pagopa.cruscotto.domain.enumeration.OutcomeStatus;
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
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.transaction.annotation.Transactional;

import org.springframework.lang.NonNull;
import org.quartz.DisallowConcurrentExecution;
import org.quartz.JobDetail;
import org.quartz.JobExecutionContext;
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
    private final Scheduler scheduler;

    @PersistenceContext
    private EntityManager entityManager;

    public KpiB4Job(
        InstanceService instanceService,
        InstanceModuleService instanceModuleService,
        ApplicationProperties applicationProperties,
        KpiConfigurationService kpiConfigurationService,
        KpiB4DataService kpiB4DataService,
        Scheduler scheduler) {
        this.instanceService = instanceService;
        this.instanceModuleService = instanceModuleService;
        this.applicationProperties = applicationProperties;
        this.kpiConfigurationService = kpiConfigurationService;
        this.kpiB4DataService = kpiB4DataService;
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
            kpiB4DataService.saveKpiB4Results(instanceDTO, instanceModuleDTO, 
                                            kpiConfigurationDTO, analysisDate, outcome);

            LOGGER.info("KPI B.4 calculation completed for instance module {}", instanceModuleDTO.getId());

        } catch (Exception e) {
            LOGGER.error("Error processing instance module {} for KPI B.4: {}", 
                        instanceModuleDTO.getId(), e.getMessage(), e);
            
            // In caso di errore, salva il risultato con outcome KO
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
     * Calcola l'outcome del KPI B.4 basato sulla logica dell'analisi.
     * Verifica la percentuale di request "paCreate" rispetto a "GPD"/"ACA".
     */
    private OutcomeStatus calculateKpiB4Outcome(InstanceDTO instanceDTO, KpiConfigurationDTO kpiConfigurationDTO) {
        LOGGER.info("Calculating KPI B.4 outcome for instance {} partner {}", 
                   instanceDTO.getId(), instanceDTO.getPartnerFiscalCode());

        try {
            LocalDate periodStart = instanceDTO.getAnalysisPeriodStartDate();
            LocalDate periodEnd = instanceDTO.getAnalysisPeriodEndDate();
            String partnerFiscalCode = instanceDTO.getPartnerFiscalCode();

            LOGGER.info("Analysis period: {} to {} for partner: {}", periodStart, periodEnd, partnerFiscalCode);

            // Query per contare le request dalla tabella pagopa_apilog
            Long totalGpdAcaRequests = countApiRequests(partnerFiscalCode, periodStart, periodEnd, "GPD", "ACA");
            Long totalPaCreateRequests = countApiRequests(partnerFiscalCode, periodStart, periodEnd, "paCreate");

            LOGGER.info("API requests for partner {}: GPD/ACA={}, paCreate={}", 
                       partnerFiscalCode, totalGpdAcaRequests, totalPaCreateRequests);

            // Calcola la percentuale di paCreate rispetto al totale
            double paCreatePercentage = 0.0;
            if (totalGpdAcaRequests > 0) {
                paCreatePercentage = (totalPaCreateRequests.doubleValue() / totalGpdAcaRequests.doubleValue()) * 100.0;
            }

            // Recupera soglia e tolleranza dalla configurazione
            Double thresholdPercentage = kpiConfigurationDTO.getEligibilityThreshold(); // Soglia (default 0%)
            Double tolerancePercentage = kpiConfigurationDTO.getTolerance(); // Tolleranza (default 1%)

            if (thresholdPercentage == null) thresholdPercentage = 0.0; // Default: 0% paCreate consentito
            if (tolerancePercentage == null) tolerancePercentage = 1.0; // Default: 1% tolleranza

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

        } catch (Exception e) {
            LOGGER.error("Error calculating KPI B.4 outcome for instance {}: {}", 
                        instanceDTO.getId(), e.getMessage(), e);
            return OutcomeStatus.KO;
        }
    }

    /**
     * Conta le request API dalla tabella pagopa_apilog per il periodo specificato.
     */
    private Long countApiRequests(String partnerFiscalCode, LocalDate startDate, LocalDate endDate, String... apiTypes) {
        StringBuilder queryBuilder = new StringBuilder();
        queryBuilder.append("SELECT COALESCE(SUM(p.totReq), 0) FROM PagopaApiLog p ");
        queryBuilder.append("WHERE p.cfPartner = :partnerFiscalCode ");
        queryBuilder.append("AND p.date BETWEEN :startDate AND :endDate ");
        
        if (apiTypes.length == 1) {
            queryBuilder.append("AND p.api = :apiType");
        } else {
            queryBuilder.append("AND p.api IN (");
            for (int i = 0; i < apiTypes.length; i++) {
                if (i > 0) queryBuilder.append(", ");
                queryBuilder.append(":apiType").append(i);
            }
            queryBuilder.append(")");
        }

        var query = entityManager.createQuery(queryBuilder.toString(), Long.class);
        query.setParameter("partnerFiscalCode", partnerFiscalCode);
        query.setParameter("startDate", startDate);
        query.setParameter("endDate", endDate);
        
        if (apiTypes.length == 1) {
            query.setParameter("apiType", apiTypes[0]);
        } else {
            for (int i = 0; i < apiTypes.length; i++) {
                query.setParameter("apiType" + i, apiTypes[i]);
            }
        }

        Long result = query.getSingleResult();
        LOGGER.debug("API count for partner {} ({} to {}), types {}: {}", 
                    partnerFiscalCode, startDate, endDate, String.join(",", apiTypes), result);
        
        return result != null ? result : 0L;
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