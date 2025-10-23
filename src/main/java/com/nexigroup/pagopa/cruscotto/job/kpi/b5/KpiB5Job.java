package com.nexigroup.pagopa.cruscotto.job.kpi.b5;

import com.nexigroup.pagopa.cruscotto.config.ApplicationProperties;
import com.nexigroup.pagopa.cruscotto.job.config.JobConstant;
import com.nexigroup.pagopa.cruscotto.domain.enumeration.ModuleCode;
import com.nexigroup.pagopa.cruscotto.service.InstanceService;
import com.nexigroup.pagopa.cruscotto.service.InstanceModuleService;
import com.nexigroup.pagopa.cruscotto.service.KpiConfigurationService;
import com.nexigroup.pagopa.cruscotto.service.KpiB5Service;
import com.nexigroup.pagopa.cruscotto.service.dto.InstanceDTO;
import com.nexigroup.pagopa.cruscotto.service.dto.InstanceModuleDTO;
import com.nexigroup.pagopa.cruscotto.service.dto.KpiConfigurationDTO;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import lombok.RequiredArgsConstructor;
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
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
@DisallowConcurrentExecution
public class KpiB5Job extends QuartzJobBean {

    private static final Logger LOGGER = LoggerFactory.getLogger(KpiB5Job.class);

    private final InstanceService instanceService;
    private final InstanceModuleService instanceModuleService;
    private final KpiConfigurationService kpiConfigurationService;
    private final KpiB5Service kpiB5Service;
    private final ApplicationProperties applicationProperties;
    private final Scheduler scheduler;

    @Override
    @Transactional
    protected void executeInternal(@NonNull JobExecutionContext context) {
        LOGGER.info("Start calculate kpi B.5");

        try {
            if (!applicationProperties.getJob().getKpiB5Job().isEnabled()) {
                LOGGER.info("Job calculate kpi B.5 disabled. Exit...");
                return;
            }

            List<InstanceDTO> instanceDTOS = instanceService.findInstanceToCalculate(
                ModuleCode.B5,
                applicationProperties.getJob().getKpiB5Job().getLimit()
            );

            if (instanceDTOS.isEmpty()) {
                LOGGER.info("No instances found to calculate KPI B.5");
                return;
            }

            LOGGER.info("Found {} instances to calculate KPI B.5", instanceDTOS.size());

            KpiConfigurationDTO kpiConfigurationDTO = kpiConfigurationService
                .findKpiConfigurationByCode(ModuleCode.B5.code)
                .orElseThrow(() -> new RuntimeException("KPI Configuration not found for module B.5"));

            for (InstanceDTO instanceDTO : instanceDTOS) {
                try {
                    processInstance(instanceDTO, kpiConfigurationDTO);
                } catch (Exception e) {
                    LOGGER.error("Error processing instance {} for kpi B.5: {}", instanceDTO.getId(), e.getMessage(), e);
                }
            }

        } catch (Exception exception) {
            LOGGER.error("Problem during calculate kpi B.5", exception);
        }

        LOGGER.info("End calculate kpi B.5");
    }

    private void processInstance(InstanceDTO instanceDTO, KpiConfigurationDTO kpiConfigurationDTO) {
        LOGGER.info("Processing instance {} for KPI B.5", instanceDTO.getId());

        try {
            // Find the instance module to update the status
            InstanceModuleDTO instanceModuleDTO = instanceModuleService
                .findOne(instanceDTO.getId(), kpiConfigurationDTO.getModuleId())
                .orElseThrow(() -> new RuntimeException("KPI B.5 InstanceModule not found"));

            processInstanceModule(instanceDTO, instanceModuleDTO, kpiConfigurationDTO);

        } catch (Exception e) {
            LOGGER.error("Error processing instance {} for KPI B.5: {}", instanceDTO.getId(), e.getMessage(), e);
        }
    }

    private void processInstanceModule(InstanceDTO instanceDTO, InstanceModuleDTO instanceModuleDTO, 
                                     KpiConfigurationDTO kpiConfigurationDTO) {
        LOGGER.info("Processing instance module {} for KPI B.5", instanceModuleDTO.getId());

        try {
            // Update instance status from "planned" to "in progress"
            instanceService.updateInstanceStatusInProgress(instanceDTO.getId());

            // Calcola la data di analisi basata sulla configurazione  
            LocalDate analysisDate = calculateAnalysisDate(instanceDTO, instanceModuleDTO);
            
            LOGGER.info("Calculating KPI B.5 for instance {} on date {} (period: {} to {})", 
                       instanceDTO.getId(), analysisDate.format(DateTimeFormatter.ISO_LOCAL_DATE),
                       instanceDTO.getAnalysisPeriodStartDate(), instanceDTO.getAnalysisPeriodEndDate());

            // Implementazione logica KPI B.5 - Calcolo utilizzo pagamenti spontanei
            // Calculate KPI B.5 and get the actual outcome
            com.nexigroup.pagopa.cruscotto.domain.enumeration.OutcomeStatus outcome = 
                kpiB5Service.calculateKpiB5(instanceDTO.getId(), instanceModuleDTO.getId(), analysisDate);

            // Update instance module with the actual calculated outcome
            instanceModuleService.updateAutomaticOutcome(instanceModuleDTO.getId(), outcome);
            
            LOGGER.info("Instance module {} updated with outcome: {}", instanceModuleDTO.getId(), outcome);
            
            // Trigger calculateStateInstanceJob to update instance state
            try {
                JobDetail job = scheduler.getJobDetail(JobKey.jobKey(JobConstant.CALCULATE_STATE_INSTANCE_JOB, "DEFAULT"));
                
                if (job == null) {
                    LOGGER.error("CalculateStateInstanceJob not found in scheduler");
                    return;
                }

                Trigger trigger = TriggerBuilder.newTrigger()
                    .usingJobData("instanceId", instanceDTO.getId())
                    .withSchedule(SimpleScheduleBuilder.simpleSchedule()
                        .withMisfireHandlingInstructionFireNow()
                        .withRepeatCount(0))
                    .forJob(job)
                    .build();

                scheduler.scheduleJob(trigger);

                LOGGER.info("Triggered calculateStateInstanceJob for instance: {}", instanceDTO.getId());

            } catch (Exception e) {
                LOGGER.error("Error triggering calculateStateInstanceJob for instance {}: {}", 
                           instanceDTO.getId(), e.getMessage(), e);
            }
            
            LOGGER.info("KPI B.5 calculation completed successfully for instance {}", instanceDTO.getId());

        } catch (Exception e) {
            LOGGER.error("Error processing instance module {} for KPI B.5: {}", instanceModuleDTO.getId(), e.getMessage(), e);
            
            try {
                // Update outcome to KO in case of failure
                instanceModuleService.updateAutomaticOutcome(instanceModuleDTO.getId(), 
                    com.nexigroup.pagopa.cruscotto.domain.enumeration.OutcomeStatus.KO);
                
                // Trigger calculateStateInstanceJob even in case of error
                JobDetail job = scheduler.getJobDetail(JobKey.jobKey(JobConstant.CALCULATE_STATE_INSTANCE_JOB, "DEFAULT"));
                
                if (job != null) {
                    Trigger trigger = TriggerBuilder.newTrigger()
                        .usingJobData("instanceId", instanceDTO.getId())
                        .withSchedule(SimpleScheduleBuilder.simpleSchedule()
                            .withMisfireHandlingInstructionFireNow()
                            .withRepeatCount(0))
                        .forJob(job)
                        .build();

                    scheduler.scheduleJob(trigger);
                    LOGGER.info("Triggered calculateStateInstanceJob for instance {} after error", instanceDTO.getId());
                } else {
                    LOGGER.error("CalculateStateInstanceJob not found in scheduler after error");
                }
                
            } catch (Exception schedulingException) {
                LOGGER.error("Failed to trigger calculateStateInstanceJob after error for instance {}: {}", 
                           instanceDTO.getId(), schedulingException.getMessage(), schedulingException);
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
}