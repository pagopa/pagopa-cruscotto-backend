package com.nexigroup.pagopa.cruscotto.kpi.framework;

import com.nexigroup.pagopa.cruscotto.domain.enumeration.EvaluationType;
import com.nexigroup.pagopa.cruscotto.domain.enumeration.OutcomeStatus;
import com.nexigroup.pagopa.cruscotto.kpi.framework.evaluation.KpiEvaluationStrategy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

/**
 * Abstract base class providing common KPI processing functionality
 * Uses composition to allow flexible strategy injection
 */
public abstract class AbstractKpiProcessor<R extends KpiResult, D extends KpiDetailResult, A extends KpiAnalyticData> 
        implements KpiProcessor<R, D, A> {
    
    protected final Logger logger = LoggerFactory.getLogger(getClass());
    
    protected final KpiEvaluationStrategy<Number> evaluationStrategy;
    
    protected AbstractKpiProcessor(KpiEvaluationStrategy<Number> evaluationStrategy) {
        this.evaluationStrategy = evaluationStrategy;
    }
    
    @Override
    public OutcomeStatus calculateFinalOutcome(KpiExecutionContext context, R kpiResult, List<D> detailResults) {
        EvaluationType evaluationType = context.getConfiguration().getEvaluationType();
        
        if (evaluationType == EvaluationType.MESE) {
            // For monthly evaluation, any KO month results in final KO
            return detailResults.stream()
                    .anyMatch(detail -> getDetailOutcome(detail) == OutcomeStatus.KO) 
                    ? OutcomeStatus.KO : OutcomeStatus.OK;
        } else {
            // For total evaluation, use the total period result
            return detailResults.stream()
                    .filter(detail -> isDetailResultForTotalPeriod(detail))
                    .findFirst()
                    .map(this::getDetailOutcome)
                    .orElse(OutcomeStatus.KO);
        }
    }
    
    /**
     * Calculate percentage difference: (actual - expected) / expected * 100
     */
    protected BigDecimal calculateDifferencePercentage(Number actual, Number expected) {
        if (expected == null || expected.doubleValue() == 0) {
            return BigDecimal.ZERO;
        }
        
        double difference = actual.doubleValue() - expected.doubleValue();
        double percentage = (difference / expected.doubleValue()) * 100.0;
        
        return BigDecimal.valueOf(percentage).setScale(2, RoundingMode.HALF_UP);
    }
    
    /**
     * Template method for subclasses to extract outcome from detail result
     */
    protected abstract OutcomeStatus getDetailOutcome(D detailResult);
    
    /**
     * Template method for subclasses to identify total period results
     */
    protected abstract boolean isDetailResultForTotalPeriod(D detailResult);
}