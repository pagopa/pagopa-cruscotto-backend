package com.nexigroup.pagopa.cruscotto.domain;

import static org.assertj.core.api.Assertions.assertThat;

import com.nexigroup.pagopa.cruscotto.domain.enumeration.EvaluationType;
import com.nexigroup.pagopa.cruscotto.domain.enumeration.OutcomeStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

class KpiB9DetailResultTest {

    private KpiB9DetailResult result;

    @BeforeEach
    void setUp() {
        result = new KpiB9DetailResult();
    }

    @Test
    void testGettersAndSetters() {
        Instance instance = new Instance();
        InstanceModule module = new InstanceModule();
        KpiB9Result kpiB9Result = new KpiB9Result();
        LocalDate today = LocalDate.now();

        result.setId(1L);
        result.setInstance(instance);
        result.setInstanceModule(module);
        result.setAnalysisDate(today);
        result.setEvaluationType(EvaluationType.MESE);
        result.setEvaluationStartDate(today.minusDays(1));
        result.setEvaluationEndDate(today);
        result.setTotRes(100L);
        result.setResKo(10L);
        result.setResKoPercentage(10.0);
        result.setOutcome(OutcomeStatus.KO);
        result.setKpiB9Result(kpiB9Result);

        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getInstance()).isEqualTo(instance);
        assertThat(result.getInstanceModule()).isEqualTo(module);
        assertThat(result.getAnalysisDate()).isEqualTo(today);
        assertThat(result.getEvaluationType()).isEqualTo(EvaluationType.MESE);
        assertThat(result.getEvaluationStartDate()).isEqualTo(today.minusDays(1));
        assertThat(result.getEvaluationEndDate()).isEqualTo(today);
        assertThat(result.getTotRes()).isEqualTo(100L);
        assertThat(result.getResKo()).isEqualTo(10L);
        assertThat(result.getResKoPercentage()).isEqualTo(10.0);
        assertThat(result.getOutcome()).isEqualTo(OutcomeStatus.KO);
        assertThat(result.getKpiB9Result()).isEqualTo(kpiB9Result);
    }

    @Test
    void testEqualsAndHashCode() {
        KpiB9DetailResult result2 = new KpiB9DetailResult();
        result.setId(1L);
        result2.setId(1L);

        assertThat(result).isEqualTo(result2);
        assertThat(result.hashCode()).isEqualTo(result2.hashCode());

        result2.setId(2L);
        assertThat(result).isNotEqualTo(result2);
    }

    @Test
    void testToString() {
        result.setId(1L);
        result.setTotRes(50L);
        result.setResKo(5L);
        result.setResKoPercentage(10.0);
        result.setEvaluationType(EvaluationType.MESE);
        result.setOutcome(OutcomeStatus.KO);
        String str = result.toString();

        assertThat(str).contains("id=1");
        assertThat(str).contains("totRes=50");
        assertThat(str).contains("resKo=5");
        assertThat(str).contains("resKoPercentage=10.0");
        assertThat(str).contains("evaluationType=MESE");
        assertThat(str).contains("outcome=KO");
    }
}
