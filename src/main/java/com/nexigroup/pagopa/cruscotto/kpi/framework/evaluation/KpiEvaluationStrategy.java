package com.nexigroup.pagopa.cruscotto.kpi.framework.evaluation;

import com.nexigroup.pagopa.cruscotto.domain.enumeration.OutcomeStatus;

import java.math.BigDecimal;

/**
 * Strategy interface for evaluating KPI outcomes with tolerance
 * Provides flexible evaluation logic that can be composed into KPI processors
 */
public interface KpiEvaluationStrategy<T extends Number> {
    
    /**
     * Evaluate if the actual value meets the threshold considering tolerance
     * 
     * @param actualValue The measured value
     * @param threshold The target threshold
     * @param tolerance The allowed tolerance percentage
     * @param differencePercentage The calculated difference percentage (actual - expected) / expected * 100
     * @return OK if meets criteria, KO otherwise
     */
    OutcomeStatus evaluate(T actualValue, T threshold, BigDecimal tolerance, BigDecimal differencePercentage);
    
    /**
     * Get the strategy name for logging/debugging
     */
    String getStrategyName();
}