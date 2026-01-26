package com.nexigroup.pagopa.cruscotto.domain;

import static org.assertj.core.api.Assertions.assertThat;

import com.nexigroup.pagopa.cruscotto.domain.enumeration.EvaluationType;
import com.nexigroup.pagopa.cruscotto.domain.enumeration.OutcomeStatus;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

class KpiB9ResultTest {

    @Test
    void testGettersAndSetters() {
        KpiB9Result result = new KpiB9Result();

        Instance instance = new Instance();
        InstanceModule module = new InstanceModule();

        result.setId(1L);
        result.setInstance(instance);
        result.setInstanceModule(module);
        result.setAnalysisDate(LocalDate.of(2025, 1, 1));
        result.setExcludePlannedShutdown(true);
        result.setExcludeUnplannedShutdown(false);
        result.setEligibilityThreshold(95.5);
        result.setTolerance(0.05);
        result.setEvaluationType(EvaluationType.MESE);
        result.setOutcome(OutcomeStatus.OK);

        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getInstance()).isEqualTo(instance);
        assertThat(result.getInstanceModule()).isEqualTo(module);
        assertThat(result.getAnalysisDate()).isEqualTo(LocalDate.of(2025, 1, 1));
        assertThat(result.getExcludePlannedShutdown()).isTrue();
        assertThat(result.getExcludeUnplannedShutdown()).isFalse();
        assertThat(result.getEligibilityThreshold()).isEqualTo(95.5);
        assertThat(result.getTolerance()).isEqualTo(0.05);
        assertThat(result.getEvaluationType()).isEqualTo(EvaluationType.MESE);
        assertThat(result.getOutcome()).isEqualTo(OutcomeStatus.OK);
    }

    @Test
    void testEqualsAndHashCode() {
        KpiB9Result result1 = new KpiB9Result();
        result1.setId(1L);

        KpiB9Result result2 = new KpiB9Result();
        result2.setId(1L);

        KpiB9Result result3 = new KpiB9Result();
        result3.setId(2L);

        assertThat(result1).isEqualTo(result2);
        assertThat(result1).hasSameHashCodeAs(result2);

        assertThat(result1).isNotEqualTo(result3);
        assertThat(result1.hashCode()).isNotEqualTo(result3.hashCode());
    }

    @Test
    void testToStringContainsFields() {
        KpiB9Result result = new KpiB9Result();
        result.setId(10L);
        result.setAnalysisDate(LocalDate.of(2025, 1, 1));
        result.setExcludePlannedShutdown(true);
        result.setExcludeUnplannedShutdown(false);
        result.setEligibilityThreshold(90.0);
        result.setTolerance(0.1);
        result.setEvaluationType(EvaluationType.TOTALE);
        result.setOutcome(OutcomeStatus.KO);

        String toString = result.toString();

        assertThat(toString).contains("KpiB9Result");
        assertThat(toString).contains("id=10");
        assertThat(toString).contains("analysisDate=2025-01-01");
        assertThat(toString).contains("excludePlannedShutdown=true");
        assertThat(toString).contains("excludeUnplannedShutdown=false");
        assertThat(toString).contains("eligibilityThreshold=90.0");
        assertThat(toString).contains("tolerance=0.1");
        assertThat(toString).contains("evaluationType=TOTALE");
        assertThat(toString).contains("outcome=KO");
    }
}
