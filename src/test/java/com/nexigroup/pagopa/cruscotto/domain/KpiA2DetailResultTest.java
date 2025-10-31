package com.nexigroup.pagopa.cruscotto.domain;

import static org.assertj.core.api.Assertions.assertThat;

import com.nexigroup.pagopa.cruscotto.domain.enumeration.OutcomeStatus;
import java.time.LocalDate;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class KpiA2DetailResultTest {

    private KpiA2DetailResult result1;
    private KpiA2DetailResult result2;

    private Instance instance;
    private InstanceModule module;
    private KpiA2Result kpiA2Result;

    @BeforeEach
    void setUp() {
        instance = new Instance();
        instance.setId(10L);

        module = new InstanceModule();
        module.setId(20L);

        kpiA2Result = new KpiA2Result();
        kpiA2Result.setId(30L);

        result1 = new KpiA2DetailResult();
        result1.setId(1L);
        result1.setInstance(instance);
        result1.setInstanceModule(module);
        result1.setAnalysisDate(LocalDate.of(2024, 1, 1));
        result1.setEvaluationStartDate(LocalDate.of(2024, 1, 2));
        result1.setEvaluationEndDate(LocalDate.of(2024, 1, 3));
        result1.setTotPayments(100L);
        result1.setTotIncorrectPayments(5L);
        result1.setErrorPercentage(5.0);
        result1.setOutcome(OutcomeStatus.OK);
        result1.setKpiA2Result(kpiA2Result);

        result2 = new KpiA2DetailResult();
        result2.setId(1L);
    }

    @Test
    void testGettersAndSetters() {
        assertThat(result1.getId()).isEqualTo(1L);
        assertThat(result1.getInstance()).isEqualTo(instance);
        assertThat(result1.getInstanceModule()).isEqualTo(module);
        assertThat(result1.getAnalysisDate()).isEqualTo(LocalDate.of(2024, 1, 1));
        assertThat(result1.getEvaluationStartDate()).isEqualTo(LocalDate.of(2024, 1, 2));
        assertThat(result1.getEvaluationEndDate()).isEqualTo(LocalDate.of(2024, 1, 3));
        assertThat(result1.getTotPayments()).isEqualTo(100L);
        assertThat(result1.getTotIncorrectPayments()).isEqualTo(5L);
        assertThat(result1.getErrorPercentage()).isEqualTo(5.0);
        assertThat(result1.getOutcome()).isEqualTo(OutcomeStatus.OK);
        assertThat(result1.getKpiA2Result()).isEqualTo(kpiA2Result);
    }

    @Test
    void testEqualsAndHashCode_sameId() {
        assertThat(result1).isEqualTo(result2);
        assertThat(result1.hashCode()).isEqualTo(result2.hashCode());
    }

    @Test
    void testEqualsAndHashCode_differentId() {
        result2.setId(2L);
        assertThat(result1).isNotEqualTo(result2);
        assertThat(result1.hashCode()).isNotEqualTo(result2.hashCode());
    }

    @Test
    void testEqualsAndHashCode_nullId() {
        KpiA2DetailResult result3 = new KpiA2DetailResult();
        assertThat(result3).isNotEqualTo(result1);
        assertThat(result3.hashCode()).isNotEqualTo(result1.hashCode());
    }

    @Test
    void testToStringContainsAllFields() {
        String text = result1.toString();
        assertThat(text).contains("KpiA2DetailResult");
        assertThat(text).contains("id=1");
        assertThat(text).contains("totPayments=100");
        assertThat(text).contains("errorPercentage=5.0");
        assertThat(text).contains("outcome=OK");
    }

    @Test
    void testEqualsWithDifferentTypeOrNull() {
        assertThat(result1.equals(null)).isFalse();
        assertThat(result1.equals("string")).isFalse();
        assertThat(result1.equals(result1)).isTrue();
    }
}
