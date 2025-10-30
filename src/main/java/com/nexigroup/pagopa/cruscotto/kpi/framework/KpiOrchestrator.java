package com.nexigroup.pagopa.cruscotto.kpi.framework;

import com.nexigroup.pagopa.cruscotto.domain.enumeration.ModuleCode;
import com.nexigroup.pagopa.cruscotto.domain.enumeration.OutcomeStatus;
import com.nexigroup.pagopa.cruscotto.service.*;
import com.nexigroup.pagopa.cruscotto.service.dto.KpiAnalyticDataDTO;
import com.nexigroup.pagopa.cruscotto.service.dto.KpiDetailResultDTO;
import com.nexigroup.pagopa.cruscotto.service.dto.KpiResultDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Generic KPI orchestrator service that coordinates KPI processing
 * This demonstrates how the framework can be extended to handle multiple KPIs
 */
@Service
public class KpiOrchestrator {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(KpiOrchestrator.class);
    
    private final Map<String, KpiProcessor<?, ?, ?>> kpiProcessors;
    private final InstanceService instanceService;
    private final InstanceModuleService instanceModuleService;
    private final GenericKpiResultService genericKpiResultService;
    private final GenericKpiDetailResultService genericKpiDetailResultService;
    private final GenericKpiAnalyticDataService genericKpiAnalyticDataService;
    
    public KpiOrchestrator(List<KpiProcessor<?, ?, ?>> processors, 
                          InstanceService instanceService,
                          InstanceModuleService instanceModuleService,
                          GenericKpiResultService genericKpiResultService,
                          GenericKpiDetailResultService genericKpiDetailResultService,
                          GenericKpiAnalyticDataService genericKpiAnalyticDataService) {
        this.kpiProcessors = processors.stream()
                .collect(Collectors.toMap(KpiProcessor::getModuleCode, Function.identity()));
        this.instanceService = instanceService;
        this.instanceModuleService = instanceModuleService;
        this.genericKpiResultService = genericKpiResultService;
        this.genericKpiDetailResultService = genericKpiDetailResultService;
        this.genericKpiAnalyticDataService = genericKpiAnalyticDataService;
    }
    
    /**
     * Process any KPI using the appropriate processor
     */
    @SuppressWarnings("unchecked")
    public <R extends KpiResult, D extends KpiDetailResult, A extends KpiAnalyticData> 
           OutcomeStatus processKpi(KpiExecutionContext context) {
        
        String moduleCode = context.getConfiguration().getModuleCode();
        KpiProcessor<R, D, A> processor = (KpiProcessor<R, D, A>) kpiProcessors.get(moduleCode);
        
        if (processor == null) {
            throw new IllegalArgumentException("No processor found for module code: " + moduleCode);
        }
        
        LOGGER.info("Processing KPI {} using processor: {}", moduleCode, processor.getClass().getSimpleName());
        
        try {
            // Clear previous results for this instance
            clearPreviousResults(context);
            
            // Process and save main result
            R kpiResult = processor.processKpiResult(context);
            R savedKpiResult = (R) genericKpiResultService.save((KpiResultDTO) kpiResult);
            
            // Process and save detail results
            List<D> detailResults = processor.processDetailResults(context, savedKpiResult);
            List<D> savedDetailResults = detailResults.stream()
                    .map(detail -> (D) genericKpiDetailResultService.save((KpiDetailResultDTO) detail))
                    .collect(Collectors.toList());
            
            // Process and save analytic data
            for (D detailResult : savedDetailResults) {
                if (shouldProcessAnalyticData(detailResult)) {
                    List<A> analyticData = processor.processAnalyticData(context, detailResult);
                    genericKpiAnalyticDataService.saveAll(
                            analyticData.stream()
                                    .map(data -> (KpiAnalyticDataDTO) data)
                                    .collect(Collectors.toList())
                    );
                }
            }
            
            // Calculate final outcome
            OutcomeStatus finalOutcome = processor.calculateFinalOutcome(context, savedKpiResult, savedDetailResults);
            
            // Update instance module outcome
            instanceModuleService.updateAutomaticOutcome(context.getInstanceModule().getId(), finalOutcome);
            
            // Update KPI result with final outcome
            ((KpiResultDTO) savedKpiResult).setOutcome(finalOutcome);
            genericKpiResultService.save((KpiResultDTO) savedKpiResult);
            
            LOGGER.info("Successfully processed KPI {} with outcome: {}", moduleCode, finalOutcome);
            return finalOutcome;
            
        } catch (Exception e) {
            LOGGER.error("Error processing KPI {}: {}", moduleCode, e.getMessage(), e);
            throw new RuntimeException("Failed to process KPI " + moduleCode, e);
        }
    }
    
    /**
     * Determine if analytic data should be processed for this detail result
     * Override this method to customize when analytic data is generated
     */
    protected boolean shouldProcessAnalyticData(KpiDetailResult detailResult) {
        // Default: process analytic data for monthly evaluations
        // Each KPI can override this logic if needed
        return true; // Simplified for now
    }
    
    /**
     * Get available KPI processors
     */
    public Map<String, KpiProcessor<?, ?, ?>> getAvailableProcessors() {
        return Map.copyOf(kpiProcessors);
    }
    
    /**
     * Clear previous results for the given instance to avoid duplicates
     */
    private void clearPreviousResults(KpiExecutionContext context) {
        ModuleCode moduleCode = ModuleCode.fromCode(context.getConfiguration().getModuleCode());
        if (moduleCode == null) {
            throw new IllegalArgumentException("Invalid module code: " + context.getConfiguration().getModuleCode());
        }
        Long instanceModuleId = context.getInstanceModule().getId();
        
        try {
            // Clear previous analytic data
            genericKpiAnalyticDataService.deleteAllByInstanceModuleId(moduleCode, instanceModuleId);
            
            // Clear previous detail results  
            genericKpiDetailResultService.deleteAllByInstanceModuleId(moduleCode, instanceModuleId);
            
            // Clear previous main results (find and delete by instance module)
            List<KpiResultDTO> existingResults = genericKpiResultService.findByInstanceModuleId(moduleCode, instanceModuleId);
            for (KpiResultDTO result : existingResults) {
                genericKpiResultService.delete(result.getId());
            }
            
            LOGGER.debug("Cleared previous results for module {} and instance module {}", moduleCode, instanceModuleId);
            
        } catch (Exception e) {
            LOGGER.warn("Error clearing previous results for module {} and instance module {}: {}", 
                    moduleCode, instanceModuleId, e.getMessage());
            // Don't fail the whole process if cleanup fails
        }
    }
}