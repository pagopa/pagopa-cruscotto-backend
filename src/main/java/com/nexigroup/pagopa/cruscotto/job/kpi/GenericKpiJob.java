package com.nexigroup.pagopa.cruscotto.job.kpi;

import com.nexigroup.pagopa.cruscotto.domain.enumeration.ModuleCode;
import com.nexigroup.pagopa.cruscotto.job.config.JobConstant;
import com.nexigroup.pagopa.cruscotto.kpi.framework.KpiExecutionContext;
import com.nexigroup.pagopa.cruscotto.kpi.framework.KpiOrchestrator;
import com.nexigroup.pagopa.cruscotto.service.*;
import com.nexigroup.pagopa.cruscotto.service.dto.*;
import lombok.RequiredArgsConstructor;
import org.quartz.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.lang.NonNull;
import org.springframework.scheduling.quartz.QuartzJobBean;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;

/**
 * Generic KPI Job that can process any KPI based on configuration.
 * This eliminates the need for separate job classes for each KPI.
 * 
 * Usage: Schedule this job with JobDataMap containing:
 * - "moduleCode": The KPI module code (e.g., "B.6", "B.1", etc.)
 * - "enabled": Boolean flag to enable/disable the job
 * 
 * The job will automatically:
 * 1. Load KPI configuration
 * 2. Find active instances 
 * 3. Delegate to appropriate KPI processor
 * 4. Handle common error scenarios
 */
@Component
@RequiredArgsConstructor
@DisallowConcurrentExecution
public class GenericKpiJob extends QuartzJobBean {

    private static final Logger LOGGER = LoggerFactory.getLogger(GenericKpiJob.class);

    private final InstanceService instanceService;
    private final InstanceModuleService instanceModuleService;
    private final KpiConfigurationService kpiConfigurationService;
    private final KpiOrchestrator kpiOrchestrator;
    private final Scheduler scheduler;

    @Override
    public void executeInternal(@NonNull JobExecutionContext context) {
        JobDataMap dataMap = context.getJobDetail().getJobDataMap();
        String moduleCode = dataMap.getString("moduleCode");
        boolean enabled = dataMap.getBooleanValue("enabled");
        int limit = dataMap.getInt("limit");

        LOGGER.info("Start calculate KPI {} - Generic Job", moduleCode);

        if (!enabled) {
            LOGGER.info("Job calculate KPI {} disabled. Exit...", moduleCode);
            return;
        }

        try {
            // Load KPI configuration
            KpiConfigurationDTO kpiConfiguration = kpiConfigurationService
                    .findByModuleCode(ModuleCode.valueOf(moduleCode.replace(".", "_")))
                    .orElseThrow(() -> new NullPointerException("KPI " + moduleCode + " configuration not found"));

            // Find all instances to calculate for this KPI (using the same method as KpiB6Job)
            List<InstanceDTO> instances = instanceService.findInstanceToCalculate(
                    ModuleCode.valueOf(moduleCode.replace(".", "_")),
                    limit
            );

            if (instances.isEmpty()) {
                LOGGER.info("No instances to calculate for KPI {}. Exit....", moduleCode);
                return;
            }

            processInstances(moduleCode, kpiConfiguration, instances);

        } catch (Exception e) {
            LOGGER.error("Error processing KPI {}: {}", moduleCode, e.getMessage(), e);
            throw e; // Re-throw to trigger job failure handling
        }

        LOGGER.info("End calculate KPI {} - Generic Job", moduleCode);
    }

    private void processInstances(String moduleCode, KpiConfigurationDTO kpiConfiguration, List<InstanceDTO> instances) {
        for (InstanceDTO instance : instances) {
            try {
                processInstance(moduleCode, kpiConfiguration, instance);
            } catch (Exception e) {
                LOGGER.error("Error processing instance {} for KPI {}: {}", 
                        instance.getId(), moduleCode, e.getMessage(), e);
                instanceService.updateInstanceStatusError(instance.getId());
            }
        }
    }

    private void processInstance(String moduleCode, KpiConfigurationDTO kpiConfiguration, InstanceDTO instance) {
        LOGGER.info("Processing KPI {} for instance: {}", moduleCode, instance.getId());

        // Update instance status
        instanceService.updateInstanceStatusInProgress(instance.getId());

        try {
            // Get instance module
            InstanceModuleDTO instanceModule = instanceModuleService
                    .findOne(instance.getId(), kpiConfiguration.getModuleId())
                    .orElseThrow(() -> new NullPointerException("KPI " + moduleCode + " InstanceModule not found"));

            // Create execution context with KPI-specific data
            KpiExecutionContext executionContext = createExecutionContext(
                    moduleCode, instance, instanceModule, kpiConfiguration);

            // Delegate to orchestrator
            var outcome = kpiOrchestrator.processKpi(executionContext);
            
            // Trigger state calculation job to determine final instance state
            triggerStateCalculationJob(instance.getId());
            
            LOGGER.info("Completed KPI {} for instance: {} with outcome: {}", 
                    moduleCode, instance.getId(), outcome);

        } catch (Exception e) {
            instanceService.updateInstanceStatusError(instance.getId());
            throw e;
        }
    }

    /**
     * Create execution context - kept generic, processors handle their own data loading
     */
    private KpiExecutionContext createExecutionContext(String moduleCode, InstanceDTO instance, 
                                                      InstanceModuleDTO instanceModule, 
                                                      KpiConfigurationDTO kpiConfiguration) {
        return KpiExecutionContext.builder()
                .instance(instance)
                .instanceModule(instanceModule)
                .configuration(kpiConfiguration)
                .analysisStart(instance.getAnalysisPeriodStartDate())
                .analysisEnd(instance.getAnalysisPeriodEndDate())
                .partnerFiscalCode(instance.getPartnerFiscalCode())
                .additionalParameters(new HashMap<>()) // Empty - processors load their own data
                .build();
    }

    /**
     * Triggers the CalculateStateInstanceJob to update instance state after KPI processing.
     * This allows the state machine to determine the final instance state based on all module outcomes.
     * 
     * @param instanceId The ID of the instance to calculate state for
     */
    private void triggerStateCalculationJob(Long instanceId) {
        try {
            LOGGER.info("Triggering state calculation job for instance: {}", instanceId);
            
            JobDetail job = scheduler.getJobDetail(JobKey.jobKey(JobConstant.CALCULATE_STATE_INSTANCE_JOB, "DEFAULT"));
            if (job != null) {
                Trigger trigger = TriggerBuilder.newTrigger()
                        .usingJobData("instanceId", instanceId)
                        .withSchedule(SimpleScheduleBuilder.simpleSchedule()
                                .withMisfireHandlingInstructionFireNow()
                                .withRepeatCount(0))
                        .startNow()
                        .build();
                        
                scheduler.scheduleJob(trigger);
                LOGGER.info("Successfully triggered state calculation job for instance: {}", instanceId);
            } else {
                LOGGER.warn("CalculateStateInstanceJob not found in scheduler - instance state may not be updated");
            }
            
        } catch (SchedulerException e) {
            LOGGER.error("Failed to trigger state calculation job for instance {}: {}", instanceId, e.getMessage(), e);
            // Don't fail the whole KPI process if state calculation trigger fails
            // The KPI processing is still successful, just the instance state won't be automatically updated
        }
    }
}