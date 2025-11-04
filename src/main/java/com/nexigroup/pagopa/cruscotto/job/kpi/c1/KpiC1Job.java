package com.nexigroup.pagopa.cruscotto.job.kpi.c1;

import com.nexigroup.pagopa.cruscotto.config.ApplicationProperties;
import com.nexigroup.pagopa.cruscotto.domain.enumeration.ModuleCode;
import com.nexigroup.pagopa.cruscotto.domain.enumeration.OutcomeStatus;
import com.nexigroup.pagopa.cruscotto.job.config.JobConstant;
import com.nexigroup.pagopa.cruscotto.service.InstanceService;
import com.nexigroup.pagopa.cruscotto.service.InstanceModuleService;
import com.nexigroup.pagopa.cruscotto.service.KpiConfigurationService;
import com.nexigroup.pagopa.cruscotto.service.dto.InstanceDTO;
import com.nexigroup.pagopa.cruscotto.service.dto.InstanceModuleDTO;
import com.nexigroup.pagopa.cruscotto.service.dto.KpiConfigurationDTO;

import java.time.LocalDate;
import java.util.List;

import lombok.AllArgsConstructor;
import org.springframework.lang.NonNull;
import org.quartz.DisallowConcurrentExecution;
import org.quartz.JobExecutionContext;
import org.quartz.JobDetail;
import org.quartz.JobBuilder;
import org.quartz.JobKey;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;
import org.quartz.SimpleScheduleBuilder;
import org.quartz.Scheduler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.quartz.QuartzJobBean;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
@DisallowConcurrentExecution
public class KpiC1Job extends QuartzJobBean {

    private static final Logger LOGGER = LoggerFactory.getLogger(KpiC1Job.class);

    private final InstanceService instanceService;
    private final InstanceModuleService instanceModuleService;
    private final ApplicationProperties applicationProperties;
    private final KpiConfigurationService kpiConfigurationService;
    private final Scheduler scheduler;

    @Override
    protected void executeInternal(@NonNull JobExecutionContext context) {
        LOGGER.info("Start calculate kpi C.1");

        try {
            if (!applicationProperties.getJob().getKpiC1Job().isEnabled()) {
                LOGGER.info("Job calculate kpi C.1 disabled. Exit...");
                return;
            }

            List<InstanceDTO> instanceDTOS = instanceService.findInstanceToCalculate(
                ModuleCode.C1,
                applicationProperties.getJob().getKpiC1Job().getLimit()
            );

            if (instanceDTOS.isEmpty()) {
                LOGGER.info("No instance to calculate C.1. Exit....");
            } else {
                KpiConfigurationDTO kpiConfigurationDTO = kpiConfigurationService
                    .findKpiConfigurationByCode(ModuleCode.C1.code)
                    .orElseThrow(() -> new NullPointerException("KPI C.1 Configuration not found"));

                LOGGER.info("Kpi configuration {}", kpiConfigurationDTO);

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

                        // Update instance status from "planned" to "in progress"
                        instanceService.updateInstanceStatusInProgress(instanceDTO.getId());

                        InstanceModuleDTO instanceModuleDTO = instanceModuleService
                            .findOne(instanceDTO.getId(), kpiConfigurationDTO.getModuleId())
                            .orElseThrow(() -> new NullPointerException("KPI C.1 InstanceModule not found"));

                        LOGGER.info("Starting KPI C.1 calculation for instance module id: {}", instanceModuleDTO.getId());

                        // Calculate analysis date
                        LocalDate analysisDate = LocalDate.now();

                        // Get configuration parameters
                        Double eligibilityThreshold = kpiConfigurationDTO.getEligibilityThreshold() != null
                            ? kpiConfigurationDTO.getEligibilityThreshold()
                            : 0.0;
                        
                        Boolean excludePlannedShutdown = kpiConfigurationDTO.getExcludePlannedShutdown() != null
                            ? kpiConfigurationDTO.getExcludePlannedShutdown()
                            : false;
                            
                        Boolean excludeUnplannedShutdown = kpiConfigurationDTO.getExcludeUnplannedShutdown() != null
                            ? kpiConfigurationDTO.getExcludeUnplannedShutdown()
                            : false;

                        // TODO: Implement KPI C.1 calculation logic here
                        // This is where the specific business logic for KPI C.1 will be implemented
                        
                        LOGGER.info("KPI C.1 calculation completed for instance: {}", instanceDTO.getInstanceIdentification());

                        // Update instance status to completed
                        instanceService.updateInstanceStatusCompleted(instanceDTO.getId());

                    } catch (Exception e) {
                        LOGGER.error("Error processing instance {} for KPI C.1: {}", instanceDTO.getInstanceIdentification(), e.getMessage(), e);
                        
                        try {
                            // Update instance status to error
                            instanceService.updateInstanceStatusError(instanceDTO.getId());
                        } catch (Exception updateError) {
                            LOGGER.error("Failed to update instance status to error: {}", updateError.getMessage(), updateError);
                        }
                    }
                });
            }

        } catch (Exception e) {
            LOGGER.error("Error in KPI C.1 job execution: {}", e.getMessage(), e);
        }

        LOGGER.info("End calculate kpi C.1");
    }

    /**
     * Schedules KPI C.1 job execution for a single instance
     * 
     * @param instanceIdentification the instance identification to process
     */
    public void scheduleJobForSingleInstance(String instanceIdentification) throws Exception {
        LOGGER.info("Scheduling KpiC1Job for instance: {}", instanceIdentification);
        
        // Create job detail for this specific instance
        JobDetail jobDetail = JobBuilder.newJob(KpiC1Job.class)
            .withIdentity("KpiC1Job-" + instanceIdentification, "KPI_JOBS")
            .usingJobData("instanceIdentification", instanceIdentification)
            .build();

        // Create trigger to run immediately
        Trigger trigger = TriggerBuilder.newTrigger()
            .withIdentity("KpiC1Trigger-" + instanceIdentification, "KPI_TRIGGERS")
            .withSchedule(SimpleScheduleBuilder.simpleSchedule()
                .withMisfireHandlingInstructionFireNow()
                .withRepeatCount(0))
            .forJob(jobDetail)
            .build();

        // Schedule the job
        scheduler.scheduleJob(jobDetail, trigger);
        LOGGER.info("Successfully scheduled KpiC1Job for instance: {}", instanceIdentification);
    }
}