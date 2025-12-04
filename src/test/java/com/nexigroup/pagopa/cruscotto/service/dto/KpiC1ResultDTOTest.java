package com.nexigroup.pagopa.cruscotto.service.dto;

import com.nexigroup.pagopa.cruscotto.domain.enumeration.EvaluationType;
import com.nexigroup.pagopa.cruscotto.domain.enumeration.OutcomeStatus;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

class KpiC1ResultDTOTest {

    @Test
    void testGettersAndSetters() {
        KpiC1ResultDTO dto = new KpiC1ResultDTO();

        Long id = 1L;
        Long instanceId = 10L;
        Long instanceModuleId = 20L;
        LocalDate analysisDate = LocalDate.now();
        BigDecimal eligibilityThreshold = new BigDecimal("0.75");
        BigDecimal tolerance = new BigDecimal("0.05");
        EvaluationType evaluationType = EvaluationType.MESE;
        OutcomeStatus outcome = OutcomeStatus.OK;

        dto.setId(id);
        dto.setInstanceId(instanceId);
        dto.setInstanceModuleId(instanceModuleId);
        dto.setAnalysisDate(analysisDate);
        dto.setEligibilityThreshold(eligibilityThreshold);
        dto.setTolerance(tolerance);
        dto.setEvaluationType(evaluationType);
        dto.setOutcome(outcome);

        assertThat(dto.getId()).isEqualTo(id);
        assertThat(dto.getInstanceId()).isEqualTo(instanceId);
        assertThat(dto.getInstanceModuleId()).isEqualTo(instanceModuleId);
        assertThat(dto.getAnalysisDate()).isEqualTo(analysisDate);
        assertThat(dto.getEligibilityThreshold()).isEqualTo(eligibilityThreshold);
        assertThat(dto.getTolerance()).isEqualTo(tolerance);
        assertThat(dto.getEvaluationType()).isEqualTo(evaluationType);
        assertThat(dto.getOutcome()).isEqualTo(outcome);
    }

    @Test
    void testEqualsAndHashCode() {
        KpiC1ResultDTO dto1 = new KpiC1ResultDTO();
        dto1.setId(1L);
        dto1.setInstanceId(10L);
        dto1.setInstanceModuleId(20L);
        dto1.setAnalysisDate(LocalDate.of(2024, 1, 1));
        dto1.setEligibilityThreshold(new BigDecimal("0.5"));
        dto1.setTolerance(new BigDecimal("0.1"));
        dto1.setEvaluationType(EvaluationType.MESE);
        dto1.setOutcome(OutcomeStatus.OK);

        KpiC1ResultDTO dto2 = new KpiC1ResultDTO();
        dto2.setId(1L);
        dto2.setInstanceId(10L);
        dto2.setInstanceModuleId(20L);
        dto2.setAnalysisDate(LocalDate.of(2024, 1, 1));
        dto2.setEligibilityThreshold(new BigDecimal("0.5"));
        dto2.setTolerance(new BigDecimal("0.1"));
        dto2.setEvaluationType(EvaluationType.MESE);
        dto2.setOutcome(OutcomeStatus.OK);

        assertThat(dto1).isEqualTo(dto2);
        assertThat(dto1.hashCode()).isEqualTo(dto2.hashCode());
    }

    @Test
    void testNotEquals() {
        KpiC1ResultDTO dto1 = new KpiC1ResultDTO();
        dto1.setId(1L);

        KpiC1ResultDTO dto2 = new KpiC1ResultDTO();
        dto2.setId(2L);

        assertThat(dto1).isNotEqualTo(dto2);
    }

    @Test
    void testToStringContainsFields() {
        KpiC1ResultDTO dto = new KpiC1ResultDTO();
        dto.setId(99L);
        dto.setInstanceId(100L);
        dto.setInstanceModuleId(200L);
        dto.setAnalysisDate(LocalDate.of(2024, 5, 1));
        dto.setEligibilityThreshold(new BigDecimal("0.80"));
        dto.setTolerance(new BigDecimal("0.02"));
        dto.setEvaluationType(EvaluationType.TOTALE);
        dto.setOutcome(OutcomeStatus.KO);

        String toString = dto.toString();

        assertThat(toString).contains("id=99");
        assertThat(toString).contains("instanceId=100");
        assertThat(toString).contains("instanceModuleId=200");
        assertThat(toString).contains("analysisDate=2024-05-01");
        assertThat(toString).contains("eligibilityThreshold=0.80");
        assertThat(toString).contains("tolerance=0.02");
        assertThat(toString).contains("evaluationType=TOTAL");
        assertThat(toString).contains("outcome=KO");
    }
}
