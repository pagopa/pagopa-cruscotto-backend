package com.nexigroup.pagopa.cruscotto.kpi.framework;

import com.nexigroup.pagopa.cruscotto.domain.enumeration.OutcomeStatus;

import java.util.List;

/**
 * Generic KPI processor interface using composition and generics
 * 
 * @param <R> KPI Result type (e.g., KpiB6ResultDTO)
 * @param <D> KPI Detail Result type (e.g., KpiB6DetailResultDTO) 
 * @param <A> KPI Analytic Data type (e.g., KpiB6AnalyticDataDTO)
 */
public interface KpiProcessor<R extends KpiResult, D extends KpiDetailResult, A extends KpiAnalyticData> {
    
    /**
     * Process the main KPI result
     */
    R processKpiResult(KpiExecutionContext context);
    
    /**
     * Process detail results (monthly/total evaluations)
     */
    List<D> processDetailResults(KpiExecutionContext context, R kpiResult);
    
    /**
     * Process analytic data for drill-down capabilities
     */
    List<A> processAnalyticData(KpiExecutionContext context, D detailResult);
    
    /**
     * Calculate final outcome based on evaluation type and detail results
     */
    OutcomeStatus calculateFinalOutcome(KpiExecutionContext context, R kpiResult, List<D> detailResults);
    
    /**
     * Get the KPI module code this processor handles
     */
    String getModuleCode();
}