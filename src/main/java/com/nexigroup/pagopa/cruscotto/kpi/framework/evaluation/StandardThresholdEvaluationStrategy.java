package com.nexigroup.pagopa.cruscotto.kpi.framework.evaluation;

import com.nexigroup.pagopa.cruscotto.domain.enumeration.OutcomeStatus;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

/**
 * Standard evaluation strategy: primary threshold check with tolerance fallback
 * Logic: 
 * 1. If actual > threshold → OK
 * 2. Else if |differencePercentage| <= tolerance → OK  
 * 3. Else → KO
 */
@Component
public class StandardThresholdEvaluationStrategy implements KpiEvaluationStrategy<Number> {

    @Override
    public OutcomeStatus evaluate(Number actualValue, Number threshold, BigDecimal tolerance, BigDecimal differencePercentage) {
        // Primary check: meets threshold
        if (actualValue.doubleValue() > threshold.doubleValue()) {
            return OutcomeStatus.OK;
        }
        
        // Fallback check: within tolerance
        if (tolerance != null && differencePercentage != null) {
            if (differencePercentage.abs().compareTo(tolerance) <= 0) {
                return OutcomeStatus.OK;
            }
        }
        
        return OutcomeStatus.KO;
    }

    @Override
    public String getStrategyName() {
        return "StandardThresholdEvaluation";
    }
}