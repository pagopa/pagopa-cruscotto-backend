package com.nexigroup.pagopa.cruscotto.domain;

import static org.assertj.core.api.Assertions.assertThat;

import com.nexigroup.pagopa.cruscotto.domain.enumeration.EvaluationType;
import com.nexigroup.pagopa.cruscotto.domain.enumeration.OutcomeStatus;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

class KpiA1ResultTest {

    @Test
    void testGettersAndSetters() {
        KpiA1Result result = new KpiA1Result();

        Instance instance = new Instance();
        InstanceModule module = new InstanceModule();

        result.setId(1L);
        result.setInstance(instance);
        result.setInstanceModule(module);
        result.setAnalysisDate(LocalDate.of(2025, 1, 1));
        result.setExcludePlannedShutdown(true);
        result.setExcludeUnplannedShutdown(false);
        result.setEligibilityThreshold(95.5);
        result.setTolerance(2.5);
        result.setEvaluationType(EvaluationType.MESE);
        result.setOutcome(OutcomeStatus.OK);

        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getInstance()).isEqualTo(instance);
        assertThat(result.getInstanceModule()).isEqualTo(module);
        assertThat(result.getAnalysisDate()).isEqualTo(LocalDate.of(2025, 1, 1));
        assertThat(result.getExcludePlannedShutdown()).isTrue();
        assertThat(result.getExcludeUnplannedShutdown()).isFalse();
        assertThat(result.getEligibilityThreshold()).isEqualTo(95.5);
        assertThat(result.getTolerance()).isEqualTo(2.5);
        assertThat(result.getEvaluationType()).isEqualTo(EvaluationType.MESE);
        assertThat(result.getOutcome()).isEqualTo(OutcomeStatus.OK);
    }

    @Test
    void testEqualsAndHashCode() {
        KpiA1Result result1 = new KpiA1Result();
        result1.setId(1L);

        KpiA1Result result2 = new KpiA1Result();
        result2.setId(1L);

        KpiA1Result result3 = new KpiA1Result();
        result3.setId(2L);

        assertThat(result1).isEqualTo(result2);
        assertThat(result1).hasSameHashCodeAs(result2);

        assertThat(result1).isNotEqualTo(result3);
        assertThat(result1.hashCode()).isNotEqualTo(result3.hashCode());
    }

    @Test
    void testToString() {
        KpiA1Result result = new KpiA1Result();
        result.setId(10L);
        result.setAnalysisDate(LocalDate.of(2025, 11, 18));
        result.setExcludePlannedShutdown(true);
        result.setExcludeUnplannedShutdown(true);
        result.setEligibilityThreshold(80.0);
        result.setTolerance(5.0);
        result.setEvaluationType(EvaluationType.TOTALE);
        result.setOutcome(OutcomeStatus.KO);

        String toString = result.toString();

        assertThat(toString).contains("KpiA1Result");
        assertThat(toString).contains("id=10");
        assertThat(toString).contains("analysisDate=2025-11-18");
        assertThat(toString).contains("evaluationType=TOTALE");
        assertThat(toString).contains("outcome=KO");
    }
}
