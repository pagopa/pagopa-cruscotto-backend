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
public class ToleranceBasedEvaluationStrategy implements KpiEvaluationStrategy<BigDecimal> {

    @Override
    public OutcomeStatus evaluate(BigDecimal actualPercentage, BigDecimal targetPercentage, BigDecimal tolerance, BigDecimal differencePercentage) {
        if (actualPercentage == null || tolerance == null) {
            return OutcomeStatus.KO;
        }
        
        // Calculate minimum acceptable percentage: target - tolerance
        BigDecimal minimumAcceptable = targetPercentage.subtract(tolerance);
        
        return actualPercentage.compareTo(minimumAcceptable) >= 0 ? OutcomeStatus.OK : OutcomeStatus.KO;
    }

    @Override
    public String getStrategyName() {
        return "ToleranceBasedEvaluation";
    }
}