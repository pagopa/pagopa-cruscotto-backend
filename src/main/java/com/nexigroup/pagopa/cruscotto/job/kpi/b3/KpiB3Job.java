package com.nexigroup.pagopa.cruscotto.job.kpi.b3;

import com.nexigroup.pagopa.cruscotto.config.ApplicationProperties;
import com.nexigroup.pagopa.cruscotto.domain.enumeration.ModuleCode;
import com.nexigroup.pagopa.cruscotto.domain.enumeration.OutcomeStatus;
import com.nexigroup.pagopa.cruscotto.service.InstanceService;
import com.nexigroup.pagopa.cruscotto.service.InstanceModuleService;
import com.nexigroup.pagopa.cruscotto.service.KpiConfigurationService;
import com.nexigroup.pagopa.cruscotto.service.dto.InstanceDTO;
import com.nexigroup.pagopa.cruscotto.service.dto.InstanceModuleDTO;
import com.nexigroup.pagopa.cruscotto.service.dto.KpiConfigurationDTO;
import java.util.List;
import lombok.AllArgsConstructor;
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
@AllArgsConstructor
@DisallowConcurrentExecution
public class KpiB3Job extends QuartzJobBean {

    private static final Logger LOGGER = LoggerFactory.getLogger(KpiB3Job.class);

    private final InstanceService instanceService;

    private final InstanceModuleService instanceModuleService;

    private final ApplicationProperties applicationProperties;

    private final KpiConfigurationService kpiConfigurationService;

    private final Scheduler scheduler;

    @Override
    protected void executeInternal(@NonNull JobExecutionContext context) {
        LOGGER.info("Start calculate kpi B.3");

        try {
            if (!applicationProperties.getJob().getKpiB3Job().isEnabled()) {
                LOGGER.info("Job calculate kpi B.3 disabled. Exit...");
                return;
            }

            List<InstanceDTO> instanceDTOS = instanceService.findInstanceToCalculate(
                ModuleCode.B3,
                applicationProperties.getJob().getKpiB3Job().getLimit()
            );

            if (instanceDTOS.isEmpty()) {
                LOGGER.info("No instance to calculate B.3. Exit....");
            } else {
                KpiConfigurationDTO kpiConfigurationDTO = kpiConfigurationService
                    .findKpiConfigurationByCode(ModuleCode.B3.code)
                    .orElseThrow(() -> new NullPointerException("KPI B.3 Configuration not found"));

                LOGGER.info("Kpi configuration {}", kpiConfigurationDTO);

                Double eligibilityThreshold = kpiConfigurationDTO.getEligibilityThreshold() != null
                    ? kpiConfigurationDTO.getEligibilityThreshold()
                    : 0.0;
                Double tolerance = kpiConfigurationDTO.getTolerance() != null ? kpiConfigurationDTO.getTolerance() : 0.0;

                instanceDTOS.forEach(instanceDTO -> {
                    try {
                        LOGGER.info(
                            "Start elaboration instance {} for partner {} - {} with period {} - {}",
                            instanceDTO.getInstanceIdentification(),
                            instanceDTO.getPartnerFiscalCode(),
                            instanceDTO.getPartnerName(),
                            instanceDTO.getAnalysisPeriodStartDate(),
                            instanceDTO.getAnalysisPeriodEndDate()
                        );

                        // TODO: Implementare la logica di calcolo del KPI B.3
                        processKpiB3Calculation(instanceDTO, kpiConfigurationDTO, eligibilityThreshold, tolerance);

                        // Trova il modulo di istanza per aggiornare lo stato
                        InstanceModuleDTO instanceModuleDTO = instanceModuleService
                            .findOne(instanceDTO.getId(), kpiConfigurationDTO.getModuleId())
                            .orElseThrow(() -> new NullPointerException("KPI B.3 InstanceModule not found"));

                        // Aggiorna lo stato dell'istanza a OK
                        instanceModuleService.updateAutomaticOutcome(instanceModuleDTO.getId(), OutcomeStatus.OK);

                        LOGGER.info(
                            "End elaboration instance {} for partner {} - {}",
                            instanceDTO.getInstanceIdentification(),
                            instanceDTO.getPartnerFiscalCode(),
                            instanceDTO.getPartnerName()
                        );

                    } catch (Exception e) {
                        LOGGER.error("Error processing instance {} for KPI B.3: {}", instanceDTO.getInstanceIdentification(), e.getMessage(), e);
                        
                        // Trova il modulo di istanza per aggiornare lo stato in caso di errore
                        try {
                            InstanceModuleDTO instanceModuleDTO = instanceModuleService
                                .findOne(instanceDTO.getId(), kpiConfigurationDTO.getModuleId())
                                .orElseThrow(() -> new NullPointerException("KPI B.3 InstanceModule not found"));

                            // Aggiorna lo stato dell'istanza a KO
                            instanceModuleService.updateAutomaticOutcome(instanceModuleDTO.getId(), OutcomeStatus.KO);
                        } catch (Exception updateException) {
                            LOGGER.error("Error updating outcome status for instance {}: {}", instanceDTO.getInstanceIdentification(), updateException.getMessage());
                        }
                    }
                });
            }
        } catch (Exception e) {
            LOGGER.error("Error in KPI B.3 job execution: {}", e.getMessage(), e);
        }

        LOGGER.info("End calculate kpi B.3");
    }

    /**
     * Processa il calcolo del KPI B.3 per una specifica istanza
     * 
     * @param instanceDTO l'istanza da processare
     * @param kpiConfigurationDTO la configurazione del KPI
     * @param eligibilityThreshold la soglia di eleggibilità
     * @param tolerance la tolleranza
     */
    private void processKpiB3Calculation(InstanceDTO instanceDTO, KpiConfigurationDTO kpiConfigurationDTO, 
                                        Double eligibilityThreshold, Double tolerance) {
        
        LOGGER.debug("Processing KPI B.3 calculation for instance: {}", instanceDTO.getInstanceIdentification());
        
        // TODO: Implementare qui la logica specifica per il calcolo del KPI B.3
        // Questa è la struttura di base che dovrà essere personalizzata in base ai requisiti specifici
        
        try {
            // Esempio di struttura per il calcolo:
            // 1. Recuperare i dati necessari per il calcolo
            // 2. Eseguire i calcoli matematici richiesti
            // 3. Applicare le soglie e la tolleranza
            // 4. Salvare i risultati
            
            LOGGER.info("KPI B.3 calculation completed for instance: {}", instanceDTO.getInstanceIdentification());
            
        } catch (Exception e) {
            LOGGER.error("Error in KPI B.3 calculation for instance {}: {}", 
                        instanceDTO.getInstanceIdentification(), e.getMessage(), e);
            throw e;
        }
    }

    /**
     * Schedula un job singolo per un'istanza specifica
     */
    public void scheduleJobForSingleInstance(String instanceIdentification) {
        try {
            LOGGER.info("Scheduling single KPI B.3 job for instance: {}", instanceIdentification);
            
            JobDetail jobDetail = org.quartz.JobBuilder.newJob(KpiB3Job.class)
                .withIdentity("kpiB3Job_" + instanceIdentification, "SINGLE_INSTANCE")
                .build();

            Trigger trigger = TriggerBuilder.newTrigger()
                .withIdentity("kpiB3JobTrigger_" + instanceIdentification, "SINGLE_INSTANCE")
                .withSchedule(SimpleScheduleBuilder.simpleSchedule().withRepeatCount(0))
                .startNow()
                .build();

            scheduler.scheduleJob(jobDetail, trigger);
            
            LOGGER.info("Single KPI B.3 job scheduled successfully for instance: {}", instanceIdentification);
            
        } catch (Exception e) {
            LOGGER.error("Error scheduling single KPI B.3 job for instance {}: {}", 
                        instanceIdentification, e.getMessage(), e);
        }
    }
}