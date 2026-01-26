package com.nexigroup.pagopa.cruscotto.domain;

import static org.assertj.core.api.Assertions.assertThat;

import com.nexigroup.pagopa.cruscotto.domain.enumeration.OutcomeStatus;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;

class KpiB5ResultTest {

    @Test
    void testGettersAndSetters() {
        KpiB5Result result = new KpiB5Result();

        Instance instance = new Instance();
        InstanceModule module = new InstanceModule();

        result.setId(1L);
        result.setInstance(instance);
        result.setInstanceModule(module);
        result.setAnalysisDate(LocalDate.of(2025, 1, 1));
        result.setThresholdIndex(BigDecimal.valueOf(10.5));
        result.setToleranceIndex(BigDecimal.valueOf(5.5));
        result.setOutcome(OutcomeStatus.OK);

        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getInstance()).isEqualTo(instance);
        assertThat(result.getInstanceModule()).isEqualTo(module);
        assertThat(result.getAnalysisDate()).isEqualTo(LocalDate.of(2025, 1, 1));
        assertThat(result.getThresholdIndex()).isEqualByComparingTo("10.5");
        assertThat(result.getToleranceIndex()).isEqualByComparingTo("5.5");
        assertThat(result.getOutcome()).isEqualTo(OutcomeStatus.OK);
    }

    @Test
    void testEqualsAndHashCode() {
        KpiB5Result result1 = new KpiB5Result();
        result1.setId(1L);

        KpiB5Result result2 = new KpiB5Result();
        result2.setId(1L);

        KpiB5Result result3 = new KpiB5Result();
        result3.setId(2L);

        assertThat(result1).isEqualTo(result2);
        assertThat(result1).hasSameHashCodeAs(result2);

        assertThat(result1).isNotEqualTo(result3);
        assertThat(result1.hashCode()).isNotEqualTo(result3.hashCode());
    }

    @Test
    void testToStringContainsFields() {
        KpiB5Result result = new KpiB5Result();
        result.setId(99L);
        result.setAnalysisDate(LocalDate.of(2025, 11, 18));
        result.setThresholdIndex(BigDecimal.TEN);
        result.setToleranceIndex(BigDecimal.ONE);
        result.setOutcome(OutcomeStatus.KO);

        String toString = result.toString();

        assertThat(toString).contains("id=99");
        assertThat(toString).contains("analysisDate=2025-11-18");
        assertThat(toString).contains("thresholdIndex=10");
        assertThat(toString).contains("toleranceIndex=1");
        assertThat(toString).contains("outcome=KO");
    }

    @Test
    void testEqualsWithSameReference() {
        KpiB5Result result = new KpiB5Result();
        result.setId(1L);

        assertThat(result.equals(result)).isTrue();
    }

    @Test
    void testEqualsWithDifferentClass() {
        KpiB5Result result = new KpiB5Result();
        result.setId(1L);

        assertThat(result.equals("string")).isFalse();
    }
}
