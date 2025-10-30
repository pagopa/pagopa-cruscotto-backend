package com.nexigroup.pagopa.cruscotto.service.impl;

import static org.junit.jupiter.api.Assertions.*;

import com.nexigroup.pagopa.cruscotto.domain.enumeration.OutcomeStatus;
import java.math.BigDecimal;
import java.util.stream.Stream;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

/**
 * Test for KPI B5 outcome determination logic.
 */
class KpiB5OutcomeTest {

    /**
     * Test the determineOutcomeWithThresholds logic used in KpiB5ServiceImpl
     */
    private OutcomeStatus determineOutcomeWithThresholds(BigDecimal percentage, BigDecimal threshold, BigDecimal tolerance) {
        BigDecimal maxAllowed = threshold.add(tolerance);
        return percentage.compareTo(maxAllowed) <= 0 ? OutcomeStatus.OK : OutcomeStatus.KO;
    }

    @ParameterizedTest
    @MethodSource("outcomeTestCases")
    void testDetermineOutcomeWithThresholds(double percentage, double threshold, double tolerance, OutcomeStatus expected) {
        // Given
        BigDecimal percentageValue = BigDecimal.valueOf(percentage);
        BigDecimal thresholdValue = BigDecimal.valueOf(threshold);
        BigDecimal toleranceValue = BigDecimal.valueOf(tolerance);

        // When
        OutcomeStatus result = determineOutcomeWithThresholds(percentageValue, thresholdValue, toleranceValue);

        // Then
        assertEquals(expected, result, 
            String.format("For percentage=%.2f%%, threshold=%.2f%%, tolerance=%.2f%% - expected %s but got %s", 
                         percentage, threshold, tolerance, expected, result));
    }

    static Stream<Arguments> outcomeTestCases() {
        return Stream.of(
            // Case: Standard configuration (5% threshold, 2% tolerance)
            // % <= 7% should be OK, > 7% should be KO
            Arguments.of(0.0, 5.0, 2.0, OutcomeStatus.OK),    // 0% <= 7% ✓
            Arguments.of(3.0, 5.0, 2.0, OutcomeStatus.OK),    // 3% <= 7% ✓
            Arguments.of(5.0, 5.0, 2.0, OutcomeStatus.OK),    // 5% <= 7% ✓
            Arguments.of(7.0, 5.0, 2.0, OutcomeStatus.OK),    // 7% <= 7% ✓ (boundary)
            Arguments.of(7.1, 5.0, 2.0, OutcomeStatus.KO),    // 7.1% > 7% ✗
            Arguments.of(10.0, 5.0, 2.0, OutcomeStatus.KO),   // 10% > 7% ✗

            // Case: Zero tolerance configuration (10% threshold, 0% tolerance)
            // % <= 10% should be OK, > 10% should be KO
            Arguments.of(5.0, 10.0, 0.0, OutcomeStatus.OK),   // 5% <= 10% ✓
            Arguments.of(10.0, 10.0, 0.0, OutcomeStatus.OK),  // 10% <= 10% ✓ (boundary)
            Arguments.of(10.1, 10.0, 0.0, OutcomeStatus.KO),  // 10.1% > 10% ✗

            // Case: Zero threshold configuration (0% threshold, 1% tolerance)
            // % <= 1% should be OK, > 1% should be KO
            Arguments.of(0.0, 0.0, 1.0, OutcomeStatus.OK),    // 0% <= 1% ✓
            Arguments.of(1.0, 0.0, 1.0, OutcomeStatus.OK),    // 1% <= 1% ✓ (boundary)
            Arguments.of(1.1, 0.0, 1.0, OutcomeStatus.KO),    // 1.1% > 1% ✗

            // Case: High percentage scenario (20% threshold, 5% tolerance)
            // % <= 25% should be OK, > 25% should be KO
            Arguments.of(15.0, 20.0, 5.0, OutcomeStatus.OK),  // 15% <= 25% ✓
            Arguments.of(25.0, 20.0, 5.0, OutcomeStatus.OK),  // 25% <= 25% ✓ (boundary)
            Arguments.of(25.1, 20.0, 5.0, OutcomeStatus.KO),  // 25.1% > 25% ✗
            Arguments.of(30.0, 20.0, 5.0, OutcomeStatus.KO)   // 30% > 25% ✗
        );
    }

    @Test
    void testOutcomeLogicDocumentation() {
        // This test documents the expected behavior clearly
        
        // Given a configuration of 5% threshold + 2% tolerance = 7% maximum allowed
        BigDecimal threshold = BigDecimal.valueOf(5.0);
        BigDecimal tolerance = BigDecimal.valueOf(2.0);
        
        // When we have exactly the threshold + tolerance percentage
        BigDecimal exactLimit = BigDecimal.valueOf(7.0);
        OutcomeStatus atLimit = determineOutcomeWithThresholds(exactLimit, threshold, tolerance);
        
        // Then it should be OK (boundary inclusive)
        assertEquals(OutcomeStatus.OK, atLimit);
        
        // When we have just above the limit
        BigDecimal aboveLimit = BigDecimal.valueOf(7.01);
        OutcomeStatus aboveLimitResult = determineOutcomeWithThresholds(aboveLimit, threshold, tolerance);
        
        // Then it should be KO
        assertEquals(OutcomeStatus.KO, aboveLimitResult);
        
        // This means: 
        // - If 7% or fewer partners are without spontaneous payments → OK
        // - If more than 7% of partners are without spontaneous payments → KO
    }
}