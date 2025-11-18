package com.nexigroup.pagopa.cruscotto.domain;

import com.nexigroup.pagopa.cruscotto.domain.enumeration.EvaluationType;
import com.nexigroup.pagopa.cruscotto.domain.enumeration.OutcomeStatus;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

class KpiC2ResultTest {

    @Test
    void testGetterAndSetter() {
        KpiC2Result result = new KpiC2Result();

        // Sample dependencies
        Instance instance = new Instance();
        InstanceModule module = new InstanceModule();
        LocalDate date = LocalDate.of(2025, 11, 17);

        result.setId(1L);
        result.setInstance(instance);
        result.setInstanceModule(module);
        result.setAnalysisDate(date);
        result.setInstitutionTolerance(0.5);
        result.setNotificationTolerance(0.8);
        result.setEvaluationType(EvaluationType.MESE);
        result.setOutcome(OutcomeStatus.OK);

        // Assertions
        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getInstance()).isEqualTo(instance);
        assertThat(result.getInstanceModule()).isEqualTo(module);
        assertThat(result.getAnalysisDate()).isEqualTo(date);
        assertThat(result.getInstitutionTolerance()).isEqualTo(0.5);
        assertThat(result.getNotificationTolerance()).isEqualTo(0.8);
        assertThat(result.getEvaluationType()).isEqualTo(EvaluationType.MESE);
        assertThat(result.getOutcome()).isEqualTo(OutcomeStatus.OK);
    }

    @Test
    void testEqualsAndHashCode() {
        KpiC2Result result1 = new KpiC2Result();
        KpiC2Result result2 = new KpiC2Result();

        result1.setId(1L);
        result2.setId(1L);

        // equals should be true
        assertThat(result1).isEqualTo(result2);
        assertThat(result1.hashCode()).isEqualTo(result2.hashCode());

        result2.setId(2L);
        assertThat(result1).isNotEqualTo(result2);
    }

    @Test
    void testToString() {
        KpiC2Result result = new KpiC2Result();
        result.setId(1L);
        result.setInstance(new Instance());
        result.setInstanceModule(new InstanceModule());
        result.setAnalysisDate(LocalDate.of(2025, 11, 17));
        result.setInstitutionTolerance(0.5);
        result.setNotificationTolerance(0.8);
        result.setEvaluationType(EvaluationType.MESE);
        result.setOutcome(OutcomeStatus.OK);

        String toStringOutput = result.toString();

        assertThat(toStringOutput).contains(
            "id=1",
            "analysisDate=2025-11-17",
            "eligibilityThreshold=0.5",
            "tolerance=0.8",
            "evaluationType=MESE",
            "outcome=OK"
        );
    }
}
