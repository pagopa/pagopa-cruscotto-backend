package com.nexigroup.pagopa.cruscotto.domain;

import static org.assertj.core.api.Assertions.assertThat;

import com.nexigroup.pagopa.cruscotto.domain.enumeration.EvaluationType;
import com.nexigroup.pagopa.cruscotto.domain.enumeration.OutcomeStatus;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

class KpiB2DetailResultTest {

    @Test
    void testGettersAndSetters() {
        KpiB2DetailResult result = new KpiB2DetailResult();

        result.setId(1L);
        result.setAnalysisDate(LocalDate.of(2025, 1, 1));
        result.setEvaluationType(EvaluationType.MESE);
        result.setEvaluationStartDate(LocalDate.of(2025, 1, 2));
        result.setEvaluationEndDate(LocalDate.of(2025, 1, 3));
        result.setTotReq(100L);
        result.setAvgTime(12.5);
        result.setOverTimeLimit(2.5);
        result.setOutcome(OutcomeStatus.OK);

        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getAnalysisDate()).isEqualTo(LocalDate.of(2025, 1, 1));
        assertThat(result.getEvaluationType()).isEqualTo(EvaluationType.MESE);
        assertThat(result.getEvaluationStartDate()).isEqualTo(LocalDate.of(2025, 1, 2));
        assertThat(result.getEvaluationEndDate()).isEqualTo(LocalDate.of(2025, 1, 3));
        assertThat(result.getTotReq()).isEqualTo(100L);
        assertThat(result.getAvgTime()).isEqualTo(12.5);
        assertThat(result.getOverTimeLimit()).isEqualTo(2.5);
        assertThat(result.getOutcome()).isEqualTo(OutcomeStatus.OK);
    }

    @Test
    void testEqualsAndHashCode() {
        KpiB2DetailResult result1 = new KpiB2DetailResult();
        result1.setId(1L);

        KpiB2DetailResult result2 = new KpiB2DetailResult();
        result2.setId(1L);

        KpiB2DetailResult result3 = new KpiB2DetailResult();
        result3.setId(2L);

        assertThat(result1).isEqualTo(result2);
        assertThat(result1).isNotEqualTo(result3);
        assertThat(result1.hashCode()).isEqualTo(result2.hashCode());
        assertThat(result1.hashCode()).isNotEqualTo(result3.hashCode());
    }

    @Test
    void testToStringContainsFields() {
        KpiB2DetailResult result = new KpiB2DetailResult();
        result.setId(1L);
        result.setAnalysisDate(LocalDate.of(2025, 1, 1));
        result.setEvaluationType(EvaluationType.MESE);
        result.setEvaluationStartDate(LocalDate.of(2025, 1, 2));
        result.setEvaluationEndDate(LocalDate.of(2025, 1, 3));
        result.setTotReq(100L);
        result.setAvgTime(12.5);
        result.setOverTimeLimit(2.5);
        result.setOutcome(OutcomeStatus.OK);

        String toString = result.toString();
        assertThat(toString).contains("id=1");
        assertThat(toString).contains("analysisDate=2025-01-01");
        assertThat(toString).contains("evaluationType=MESE");
        assertThat(toString).contains("totReq=100");
        assertThat(toString).contains("avgTime=12.5");
        assertThat(toString).contains("outcome=OK");
    }
}
