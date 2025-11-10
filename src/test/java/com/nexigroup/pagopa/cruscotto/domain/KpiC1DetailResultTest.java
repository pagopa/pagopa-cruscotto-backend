package com.nexigroup.pagopa.cruscotto.domain;

import static org.assertj.core.api.Assertions.assertThat;

import com.nexigroup.pagopa.cruscotto.domain.enumeration.EvaluationType;
import com.nexigroup.pagopa.cruscotto.domain.enumeration.OutcomeStatus;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Unit tests for {@link KpiC1DetailResult} entity.
 */
class KpiC1DetailResultTest {

    @Test
    void testUpdateDetails() {
        KpiC1DetailResult detail = new KpiC1DetailResult();
        detail.setConfiguredThreshold(new BigDecimal("75.00"));
        
        // Test normal case: 80 messages out of 100 positions = 80%
        detail.updateDetails(100L, 80L);
        
        assertThat(detail.getTotalPositions()).isEqualTo(100L);
        assertThat(detail.getSentMessages()).isEqualTo(80L);
        assertThat(detail.getSendingPercentage()).isEqualTo(new BigDecimal("80.00"));
    }

    @Test
    void testUpdateDetails_ZeroPositions() {
        KpiC1DetailResult detail = new KpiC1DetailResult();
        detail.setConfiguredThreshold(new BigDecimal("75.00"));
        
        // Test edge case: 0 positions
        detail.updateDetails(0L, 10L);
        
        assertThat(detail.getTotalPositions()).isEqualTo(0L);
        assertThat(detail.getSentMessages()).isEqualTo(10L);
        assertThat(detail.getSendingPercentage()).isNull(); // Can't calculate percentage with 0 positions
    }

    @Test
    void testUpdateInstitutions() {
        KpiC1DetailResult detail = new KpiC1DetailResult();
        
        // Test normal case: 2 compliant out of 3 total = 66.67%
        detail.updateInstitutions(3L, 2L);
        
        assertThat(detail.getTotalInstitutions()).isEqualTo(3L);
        assertThat(detail.getCompliantInstitutions()).isEqualTo(2L);
        assertThat(detail.getPercentageCompliantInstitutions()).isEqualTo(new BigDecimal("66.67"));
    }

    @Test
    void testUpdateInstitutions_ZeroInstitutions() {
        KpiC1DetailResult detail = new KpiC1DetailResult();
        
        // Test edge case: 0 institutions
        detail.updateInstitutions(0L, 0L);
        
        assertThat(detail.getTotalInstitutions()).isEqualTo(0L);
        assertThat(detail.getCompliantInstitutions()).isEqualTo(0L);
        assertThat(detail.getPercentageCompliantInstitutions()).isNull(); // Can't calculate percentage with 0 institutions
    }

    @Test
    void testConstructor() {
        Instance instance = new Instance();
        InstanceModule instanceModule = new InstanceModule();
        LocalDate referenceDate = LocalDate.of(2024, 1, 15);
        EvaluationType evaluationType = EvaluationType.MESE;
        LocalDate evaluationStartDate = LocalDate.of(2024, 1, 1);
        LocalDate evaluationEndDate = LocalDate.of(2024, 1, 31);
        String cfInstitution = "12345678901";
        // Test with successful outcome
        OutcomeStatus outcome = OutcomeStatus.OK;
        Boolean compliant = true;
        BigDecimal soglia = new BigDecimal("75.00");
        KpiC1Result kpiC1Result = new KpiC1Result();

        KpiC1DetailResult detail = new KpiC1DetailResult(
            instance, instanceModule, referenceDate, evaluationType,
            evaluationStartDate, evaluationEndDate, cfInstitution, 
            outcome, compliant, soglia, kpiC1Result);

        assertThat(detail.getInstance()).isEqualTo(instance);
        assertThat(detail.getInstanceModule()).isEqualTo(instanceModule);
        assertThat(detail.getReferenceDate()).isEqualTo(referenceDate);
        assertThat(detail.getEvaluationType()).isEqualTo(evaluationType);
        assertThat(detail.getEvaluationStartDate()).isEqualTo(evaluationStartDate);
        assertThat(detail.getEvaluationEndDate()).isEqualTo(evaluationEndDate);
        assertThat(detail.getCfInstitution()).isEqualTo(cfInstitution);
        assertThat(detail.getOutcome()).isEqualTo(outcome);
        assertThat(detail.getCompliant()).isEqualTo(compliant);
        assertThat(detail.getConfiguredThreshold()).isEqualTo(soglia);
        assertThat(detail.getKpiC1Result()).isEqualTo(kpiC1Result);
        assertThat(detail.getTotalPositions()).isEqualTo(0L);
        assertThat(detail.getSentMessages()).isEqualTo(0L);
        assertThat(detail.getTotalInstitutions()).isEqualTo(0L);
        assertThat(detail.getCompliantInstitutions()).isEqualTo(0L);
    }

    @Test
    void testDefaultConstructor() {
        KpiC1DetailResult detail = new KpiC1DetailResult();
        
        assertThat(detail.getTotalPositions()).isEqualTo(0L);
        assertThat(detail.getSentMessages()).isEqualTo(0L);
        assertThat(detail.getTotalInstitutions()).isEqualTo(0L);
        assertThat(detail.getCompliantInstitutions()).isEqualTo(0L);
    }
}