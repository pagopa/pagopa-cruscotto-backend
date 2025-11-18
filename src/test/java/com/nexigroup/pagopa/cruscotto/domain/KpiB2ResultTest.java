package com.nexigroup.pagopa.cruscotto.domain;

import static org.assertj.core.api.Assertions.assertThat;

import com.nexigroup.pagopa.cruscotto.domain.enumeration.EvaluationType;
import com.nexigroup.pagopa.cruscotto.domain.enumeration.OutcomeStatus;
import java.time.LocalDate;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

class KpiB2ResultTest {

    @Test
    void testGettersAndSetters() {
        Instance instance = Mockito.mock(Instance.class);
        InstanceModule module = Mockito.mock(InstanceModule.class);

        KpiB2Result result = new KpiB2Result();
        result.setId(1L);
        result.setInstance(instance);
        result.setInstanceModule(module);
        result.setAnalysisDate(LocalDate.of(2025, 1, 1));
        result.setExcludePlannedShutdown(true);
        result.setExcludeUnplannedShutdown(false);
        result.setEligibilityThreshold(95.5);
        result.setTolerance(2.5);
        result.setAverageTimeLimit(10.0);
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
        assertThat(result.getAverageTimeLimit()).isEqualTo(10.0);
        assertThat(result.getEvaluationType()).isEqualTo(EvaluationType.MESE);
        assertThat(result.getOutcome()).isEqualTo(OutcomeStatus.OK);
    }

    @Test
    void testEqualsAndHashCode() {
        KpiB2Result result1 = new KpiB2Result();
        result1.setId(1L);

        KpiB2Result result2 = new KpiB2Result();
        result2.setId(1L);

        KpiB2Result result3 = new KpiB2Result();
        result3.setId(2L);

        assertThat(result1).isEqualTo(result2);
        assertThat(result1).hasSameHashCodeAs(result2);
        assertThat(result1).isNotEqualTo(result3);
    }

    @Test
    void testToStringContainsFields() {
        KpiB2Result result = new KpiB2Result();
        result.setId(1L);
        result.setAnalysisDate(LocalDate.of(2025, 1, 1));
        result.setExcludePlannedShutdown(true);
        result.setExcludeUnplannedShutdown(false);
        result.setEligibilityThreshold(95.5);
        result.setTolerance(2.5);
        result.setAverageTimeLimit(10.0);
        result.setEvaluationType(EvaluationType.MESE);
        result.setOutcome(OutcomeStatus.OK);

        String toString = result.toString();
        assertThat(toString).contains("KpiB2Result");
        assertThat(toString).contains("id=1");
        assertThat(toString).contains("analysisDate=2025-01-01");
        assertThat(toString).contains("excludePlannedShutdown=true");
        assertThat(toString).contains("outcome=OK");
    }
}
