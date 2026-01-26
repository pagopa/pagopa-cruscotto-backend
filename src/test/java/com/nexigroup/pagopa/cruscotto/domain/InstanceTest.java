package com.nexigroup.pagopa.cruscotto.domain;

import static org.assertj.core.api.Assertions.assertThat;

import com.nexigroup.pagopa.cruscotto.domain.enumeration.AnalysisOutcome;
import com.nexigroup.pagopa.cruscotto.domain.enumeration.InstanceStatus;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.time.LocalDate;

class InstanceTest {

    @Test
    void testGettersAndSetters() {
        Instance instance = new Instance();

        Long id = 1L;
        String identification = "INST-001";
        LocalDate predictedDate = LocalDate.now().plusDays(1);
        Instant applicationDate = Instant.now();
        LocalDate startDate = LocalDate.now();
        LocalDate endDate = LocalDate.now().plusDays(30);

        instance.setId(id);
        instance.setInstanceIdentification(identification);
        instance.setPredictedDateAnalysis(predictedDate);
        instance.setApplicationDate(applicationDate);
        instance.setAnalysisPeriodStartDate(startDate);
        instance.setAnalysisPeriodEndDate(endDate);
        instance.setStatus(InstanceStatus.ESEGUITA);
        instance.setLastAnalysisDate(applicationDate);
        instance.setLastAnalysisOutcome(AnalysisOutcome.OK);
        instance.setChangePartnerQualified(true);

        assertThat(instance.getId()).isEqualTo(id);
        assertThat(instance.getInstanceIdentification()).isEqualTo(identification);
        assertThat(instance.getPredictedDateAnalysis()).isEqualTo(predictedDate);
        assertThat(instance.getApplicationDate()).isEqualTo(applicationDate);
        assertThat(instance.getAnalysisPeriodStartDate()).isEqualTo(startDate);
        assertThat(instance.getAnalysisPeriodEndDate()).isEqualTo(endDate);
        assertThat(instance.getStatus()).isEqualTo(InstanceStatus.ESEGUITA);
        assertThat(instance.getLastAnalysisDate()).isEqualTo(applicationDate);
        assertThat(instance.getLastAnalysisOutcome()).isEqualTo(AnalysisOutcome.OK);
        assertThat(instance.getChangePartnerQualified()).isTrue();
    }

    @Test
    void testEqualsAndHashCode() {
        Instance instance1 = new Instance();
        instance1.setId(1L);

        Instance instance2 = new Instance();
        instance2.setId(1L);

        Instance instance3 = new Instance();
        instance3.setId(2L);

        assertThat(instance1).isEqualTo(instance2);
        assertThat(instance1).isNotEqualTo(instance3);
        assertThat(instance1.hashCode()).isEqualTo(instance2.hashCode());
    }

    @Test
    void testToStringContainsFields() {
        Instance instance = new Instance();
        instance.setId(1L);
        instance.setInstanceIdentification("INST-001");
        instance.setPredictedDateAnalysis(LocalDate.now());
        instance.setApplicationDate(Instant.now());
        instance.setAnalysisPeriodStartDate(LocalDate.now());
        instance.setAnalysisPeriodEndDate(LocalDate.now().plusDays(10));
        instance.setStatus(InstanceStatus.ESEGUITA);
        instance.setChangePartnerQualified(false);

        String toString = instance.toString();

        assertThat(toString).contains("id=1");
        assertThat(toString).contains("instanceIdentification='INST-001'");
        assertThat(toString).contains("status=ESEGUITA");
        assertThat(toString).contains("changePartnerQualified=false");
    }

    @Test
    void testDefaultValues() {
        Instance instance = new Instance();
        assertThat(instance.getChangePartnerQualified()).isFalse();
        assertThat(instance.getInstanceModules()).isEmpty();
    }
}
