package com.nexigroup.pagopa.cruscotto.job.kpi;

import com.nexigroup.pagopa.cruscotto.config.ApplicationProperties;
import com.nexigroup.pagopa.cruscotto.domain.enumeration.ModuleCode;
import com.nexigroup.pagopa.cruscotto.domain.enumeration.StationStatus;
import com.nexigroup.pagopa.cruscotto.job.config.JobConstant;
import com.nexigroup.pagopa.cruscotto.kpi.framework.KpiExecutionContext;
import com.nexigroup.pagopa.cruscotto.kpi.framework.KpiOrchestrator;
import com.nexigroup.pagopa.cruscotto.service.*;
import com.nexigroup.pagopa.cruscotto.service.dto.*;
import com.nexigroup.pagopa.cruscotto.service.filter.AnagStationFilter;
import lombok.RequiredArgsConstructor;
import org.quartz.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.lang.NonNull;
import org.springframework.scheduling.quartz.QuartzJobBean;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

    private final ApplicationProperties applicationProperties;
    private final InstanceService instanceService;
    private final InstanceModuleService instanceModuleService;
    private final KpiConfigurationService kpiConfigurationService;
    private final KpiOrchestrator kpiOrchestrator;
    private final AnagStationService anagStationService; // Added for B.6 data fetching
    private final Scheduler scheduler;

    @Override
    public void executeInternal(@NonNull JobExecutionContext context) {
        JobDataMap dataMap = context.getJobDetail().getJobDataMap();
        String moduleCode = dataMap.getString("moduleCode");
        boolean enabled = dataMap.getBooleanValue("enabled");

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
                    applicationProperties.getJob().getKpiB6Job().getLimit() // TODO: make this generic based on moduleCode
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
            
            // Update instance status based on outcome
            instanceService.updateInstanceStatusCompleted(instance.getId());
            LOGGER.info("Completed KPI {} for instance: {} with outcome: {}", 
                    moduleCode, instance.getId(), outcome);

        } catch (Exception e) {
            instanceService.updateInstanceStatusError(instance.getId());
            throw e;
        }
    }

    /**
     * Create execution context with KPI-specific data loading
     */
    private KpiExecutionContext createExecutionContext(String moduleCode, InstanceDTO instance, 
                                                      InstanceModuleDTO instanceModule, 
                                                      KpiConfigurationDTO kpiConfiguration) {
        Map<String, Object> additionalParams = new HashMap<>();
        
        // Load KPI-specific data based on module code
        if ("B6".equals(moduleCode)) {
            // Load station data for KPI B.6
            additionalParams.put("stationData", loadStationDataForB6(instance));
        }
        // Add other KPI-specific data loading logic here as needed
        
        return KpiExecutionContext.builder()
                .instance(instance)
                .instanceModule(instanceModule)
                .configuration(kpiConfiguration)
                .analysisStart(instance.getAnalysisPeriodStartDate())
                .analysisEnd(instance.getAnalysisPeriodEndDate())
                .partnerFiscalCode(instance.getPartnerFiscalCode())
                .additionalParameters(additionalParams)
                .build();
    }

    /**
     * Load station data for KPI B.6
     */
    private List<AnagStationDTO> loadStationDataForB6(InstanceDTO instance) {
        LOGGER.info("Loading station data for KPI B.6, partner: {}", instance.getPartnerFiscalCode());
        
        AnagStationFilter filter = new AnagStationFilter();
        filter.setShowNotActive(false); // Only active stations
        
        Page<AnagStationDTO> stationPage = anagStationService.findAll(filter, PageRequest.of(0, Integer.MAX_VALUE));
        
        // Filter by partner fiscal code and only active stations
        List<AnagStationDTO> stationData = stationPage.getContent().stream()
                .filter(station -> instance.getPartnerFiscalCode().equals(station.getPartnerFiscalCode()))
                .filter(station -> StationStatus.ATTIVA.equals(station.getStatus()))
                .toList();

        LOGGER.info("Found {} stations for analysis", stationData.size());
        return stationData;
    }
}