package com.nexigroup.pagopa.cruscotto.kpi.framework.evaluation;

import com.nexigroup.pagopa.cruscotto.domain.enumeration.OutcomeStatus;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

/**
 * Tolerance-based evaluation strategy for percentage compliance
 * Logic: OK if percentage >= (100% - tolerance)
 * Example: tolerance=1.00 means 99% compliance is acceptable
 */
@Component
public class ToleranceBasedEvaluationStrategy implements KpiEvaluationStrategy<Number> {

    @Override
    public OutcomeStatus evaluate(Number actualPercentage, Number targetPercentage, BigDecimal tolerance, BigDecimal differencePercentage) {
        if (actualPercentage == null || tolerance == null) {
            return OutcomeStatus.KO;
        }
        
        // Convert to BigDecimal for precise calculations
        BigDecimal actual = new BigDecimal(actualPercentage.toString());
        BigDecimal target = new BigDecimal(targetPercentage.toString());
        
        // Calculate minimum acceptable percentage: target - tolerance
        BigDecimal minimumAcceptable = target.subtract(tolerance);
        
        return actual.compareTo(minimumAcceptable) >= 0 ? OutcomeStatus.OK : OutcomeStatus.KO;
    }

    @Override
    public String getStrategyName() {
        return "ToleranceBasedEvaluation";
    }
}