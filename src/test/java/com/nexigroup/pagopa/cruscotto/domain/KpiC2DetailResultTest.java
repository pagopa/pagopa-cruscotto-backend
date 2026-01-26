package com.nexigroup.pagopa.cruscotto.domain;

import com.nexigroup.pagopa.cruscotto.domain.enumeration.EvaluationType;
import com.nexigroup.pagopa.cruscotto.domain.enumeration.OutcomeStatus;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

class KpiC2DetailResultTest {

    @Test
    void testEqualsAndHashCode() {
        KpiC2DetailResult r1 = new KpiC2DetailResult();
        r1.setId(100L);

        KpiC2DetailResult r2 = new KpiC2DetailResult();
        r2.setId(100L);

        KpiC2DetailResult r3 = new KpiC2DetailResult();
        r3.setId(200L);

        assertThat(r1).isEqualTo(r2);
        assertThat(r1).hasSameHashCodeAs(r2);

        assertThat(r1).isNotEqualTo(r3);
        assertThat(r1).isNotEqualTo(null);
        assertThat(r1).isNotEqualTo(new Object());
    }

    @Test
    void testToStringContainsImportantFields() {
        KpiC2DetailResult result = new KpiC2DetailResult();
        result.setId(1L);
        result.setInstanceId(10L);
        result.setInstanceModuleId(20L);
        result.setAnalysisDate(LocalDate.of(2025, 1, 1));
        result.setEvaluationType(EvaluationType.MESE);
        result.setEvaluationStartDate(LocalDate.of(2025, 1, 2));
        result.setEvaluationEndDate(LocalDate.of(2025, 1, 3));
        result.setTotalInstitution(100L);
        result.setTotalInstitutionSend(90L);
        result.setPercentInstitutionSend(new BigDecimal("90.00"));
        result.setTotalPayment(50L);
        result.setTotalNotification(45L);
        result.setPercentEntiOk(new BigDecimal("88.50"));
        result.setOutcome(OutcomeStatus.OK);

        String toString = result.toString();

        assertThat(toString).contains(
            "id=1",
            "instanceId=10",
            "instanceModuleId=20",
            "analysisDate=2025-01-01",
            "evaluationType='MESE'",
            "totalInstitution=100",
            "outcome='OK'"
        );
    }

    @Test
    void testGettersAndSetters() {
        KpiC2DetailResult result = new KpiC2DetailResult();

        LocalDate d1 = LocalDate.now();
        LocalDate d2 = LocalDate.now().minusDays(7);
        LocalDate d3 = LocalDate.now().minusDays(1);

        result.setId(1L);
        result.setInstanceId(2L);
        result.setInstanceModuleId(3L);
        result.setAnalysisDate(d1);
        result.setEvaluationStartDate(d2);
        result.setEvaluationEndDate(d3);
        result.setEvaluationType(EvaluationType.TOTALE);
        result.setTotalInstitution(10L);
        result.setTotalInstitutionSend(9L);
        result.setPercentInstitutionSend(BigDecimal.TEN);
        result.setTotalPayment(5L);
        result.setTotalNotification(4L);
        result.setPercentEntiOk(BigDecimal.ONE);
        result.setOutcome(OutcomeStatus.KO);

        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getInstanceId()).isEqualTo(2L);
        assertThat(result.getInstanceModuleId()).isEqualTo(3L);
        assertThat(result.getAnalysisDate()).isEqualTo(d1);
        assertThat(result.getEvaluationStartDate()).isEqualTo(d2);
        assertThat(result.getEvaluationEndDate()).isEqualTo(d3);
        assertThat(result.getEvaluationType()).isEqualTo(EvaluationType.TOTALE);
        assertThat(result.getTotalInstitution()).isEqualTo(10L);
        assertThat(result.getTotalInstitutionSend()).isEqualTo(9L);
        assertThat(result.getPercentInstitutionSend()).isEqualTo(BigDecimal.TEN);
        assertThat(result.getTotalPayment()).isEqualTo(5L);
        assertThat(result.getTotalNotification()).isEqualTo(4L);
        assertThat(result.getPercentEntiOk()).isEqualTo(BigDecimal.ONE);
        assertThat(result.getOutcome()).isEqualTo(OutcomeStatus.KO);
    }

    @Test
    void testEnumAssignments() {
        KpiC2DetailResult result = new KpiC2DetailResult();

        result.setEvaluationType(EvaluationType.MESE);
        result.setOutcome(OutcomeStatus.OK);

        assertThat(result.getEvaluationType()).isEqualTo(EvaluationType.MESE);
        assertThat(result.getOutcome()).isEqualTo(OutcomeStatus.OK);
    }
}
