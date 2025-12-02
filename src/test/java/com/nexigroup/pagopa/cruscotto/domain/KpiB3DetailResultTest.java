package com.nexigroup.pagopa.cruscotto.domain;

import static org.assertj.core.api.Assertions.assertThat;

import com.nexigroup.pagopa.cruscotto.domain.enumeration.EvaluationType;
import com.nexigroup.pagopa.cruscotto.domain.enumeration.OutcomeStatus;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

class KpiB3DetailResultTest {

    @Test
    void testEqualsAndHashCode() {
        KpiB3DetailResult result1 = new KpiB3DetailResult();
        result1.setId(1L);

        KpiB3DetailResult result2 = new KpiB3DetailResult();
        result2.setId(1L);

        KpiB3DetailResult result3 = new KpiB3DetailResult();
        result3.setId(2L);

        assertThat(result1).isEqualTo(result2);
        assertThat(result1).isNotEqualTo(result3);
        assertThat(result1.hashCode()).isEqualTo(result2.hashCode());
    }

    @Test
    void testToStringContainsFields() {
        KpiB3DetailResult result = new KpiB3DetailResult();
        result.setId(99L);
        result.setAnalysisDate(LocalDate.of(2025, 11, 18));
        result.setEvaluationType(EvaluationType.MESE);
        result.setEvaluationStartDate(LocalDate.of(2025, 11, 1));
        result.setEvaluationEndDate(LocalDate.of(2025, 11, 30));
        result.setTotalStandIn(5);
        result.setOutcome(OutcomeStatus.OK);

        String toString = result.toString();

        assertThat(toString).contains("id=99");
        assertThat(toString).contains("analysisDate='2025-11-18'");
        assertThat(toString).contains("evaluationType='MESE'");
        assertThat(toString).contains("evaluationStartDate='2025-11-01'");
        assertThat(toString).contains("evaluationEndDate='2025-11-30'");
        assertThat(toString).contains("totalStandIn=5");
        assertThat(toString).contains("outcome='OK'");
    }

    @Test
    void testGettersAndSetters() {
        KpiB3DetailResult result = new KpiB3DetailResult();

        LocalDate now = LocalDate.now();
        result.setAnalysisDate(now);
        result.setEvaluationType(EvaluationType.TOTALE);
        result.setEvaluationStartDate(now.minusDays(10));
        result.setEvaluationEndDate(now);
        result.setTotalStandIn(42);
        result.setOutcome(OutcomeStatus.KO);

        assertThat(result.getAnalysisDate()).isEqualTo(now);
        assertThat(result.getEvaluationType()).isEqualTo(EvaluationType.TOTALE);
        assertThat(result.getEvaluationStartDate()).isEqualTo(now.minusDays(10));
        assertThat(result.getEvaluationEndDate()).isEqualTo(now);
        assertThat(result.getTotalStandIn()).isEqualTo(42);
        assertThat(result.getOutcome()).isEqualTo(OutcomeStatus.KO);
    }
}
