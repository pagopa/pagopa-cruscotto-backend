package com.nexigroup.pagopa.cruscotto.domain;

import com.nexigroup.pagopa.cruscotto.domain.enumeration.EvaluationType;
import com.nexigroup.pagopa.cruscotto.domain.enumeration.OutcomeStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

class KpiB8DetailResultTest {

    private KpiB8DetailResult detailResult;

    @BeforeEach
    void setUp() {
        detailResult = new KpiB8DetailResult();
    }

    @Test
    void testGetterAndSetter() {
        detailResult.setId(1L);
        detailResult.setInstanceId(10L);
        detailResult.setInstanceModuleId(20L);
        detailResult.setAnalysisDate(LocalDate.of(2025, 10, 30));
        detailResult.setEvaluationType(EvaluationType.MESE);
        detailResult.setEvaluationStartDate(LocalDate.of(2025, 10, 1));
        detailResult.setEvaluationEndDate(LocalDate.of(2025, 10, 31));
        detailResult.setTotReq(100L);
        detailResult.setReqKO(5L);
        detailResult.setPerKO(BigDecimal.valueOf(5.0));
        detailResult.setOutcome(OutcomeStatus.OK);

        assertThat(detailResult.getId()).isEqualTo(1L);
        assertThat(detailResult.getInstanceId()).isEqualTo(10L);
        assertThat(detailResult.getInstanceModuleId()).isEqualTo(20L);
        assertThat(detailResult.getAnalysisDate()).isEqualTo(LocalDate.of(2025, 10, 30));
        assertThat(detailResult.getEvaluationType()).isEqualTo(EvaluationType.MESE);
        assertThat(detailResult.getEvaluationStartDate()).isEqualTo(LocalDate.of(2025, 10, 1));
        assertThat(detailResult.getEvaluationEndDate()).isEqualTo(LocalDate.of(2025, 10, 31));
        assertThat(detailResult.getTotReq()).isEqualTo(100L);
        assertThat(detailResult.getReqKO()).isEqualTo(5L);
        assertThat(detailResult.getPerKO()).isEqualTo(BigDecimal.valueOf(5.0));
        assertThat(detailResult.getOutcome()).isEqualTo(OutcomeStatus.OK);
    }

    @Test
    void testEqualsAndHashCode() {
        KpiB8DetailResult another = new KpiB8DetailResult();

        detailResult.setId(1L);
        another.setId(1L);
        assertThat(detailResult).isEqualTo(another);
        assertThat(detailResult.hashCode()).isEqualTo(another.hashCode());

        another.setId(2L);
        assertThat(detailResult).isNotEqualTo(another);
    }

    @Test
    void testToString() {
        detailResult.setId(1L);
        detailResult.setTotReq(100L);
        String str = detailResult.toString();
        assertThat(str).contains("id=1");
        assertThat(str).contains("totReq=100");
    }
}
