package com.nexigroup.pagopa.cruscotto.kpi.framework.aggregation;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

/**
 * Generic interface for data aggregation strategies
 * Allows different KPIs to compose different aggregation behaviors
 */
public interface DataAggregator<T, R> {
    
    /**
     * Aggregate data for a specific time period
     * 
     * @param data The raw data to aggregate
     * @param startDate Period start date
     * @param endDate Period end date
     * @return Aggregated result
     */
    R aggregate(List<T> data, LocalDate startDate, LocalDate endDate);
    
    /**
     * Group data by a specific criterion and aggregate
     * 
     * @param data The raw data to aggregate
     * @param groupingFunction Function to extract grouping key
     * @return Map of grouped aggregations
     */
    <K> Map<K, R> aggregateByGroup(List<T> data, java.util.function.Function<T, K> groupingFunction);
    
    /**
     * Get the aggregator name for debugging
     */
    String getAggregatorName();
}