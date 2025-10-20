package com.nexigroup.pagopa.cruscotto.kpi.framework;

import com.nexigroup.pagopa.cruscotto.domain.enumeration.OutcomeStatus;
import com.nexigroup.pagopa.cruscotto.service.*;
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
    
    public KpiOrchestrator(List<KpiProcessor<?, ?, ?>> processors, 
                          InstanceService instanceService,
                          InstanceModuleService instanceModuleService) {
        this.kpiProcessors = processors.stream()
                .collect(Collectors.toMap(KpiProcessor::getModuleCode, Function.identity()));
        this.instanceService = instanceService;
        this.instanceModuleService = instanceModuleService;
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
            // Process main result
            R kpiResult = processor.processKpiResult(context);
            
            // Process detail results
            List<D> detailResults = processor.processDetailResults(context, kpiResult);
            
            // Process analytic data (only for monthly evaluations)
            for (D detailResult : detailResults) {
                // Check if this is a monthly result that needs analytic data
                if (shouldProcessAnalyticData(detailResult)) {
                    List<A> analyticData = processor.processAnalyticData(context, detailResult);
                    // Save analytic data through appropriate service
                }
            }
            
            // Calculate final outcome
            OutcomeStatus finalOutcome = processor.calculateFinalOutcome(context, kpiResult, detailResults);
            
            // Update instance module outcome
            instanceModuleService.updateAutomaticOutcome(context.getInstanceModule().getId(), finalOutcome);
            
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
}