package com.nexigroup.pagopa.cruscotto.service.dto;

import com.nexigroup.pagopa.cruscotto.domain.enumeration.EvaluationType;
import com.nexigroup.pagopa.cruscotto.domain.enumeration.OutcomeStatus;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

class KpiB9DetailResultDTOTest {

    @Test
    void testGettersAndSetters() {
        KpiB9DetailResultDTO dto = new KpiB9DetailResultDTO();

        // Set fields
        dto.setId(1L);
        dto.setInstanceId(10L);
        dto.setInstanceModuleId(20L);
        dto.setAnalysisDate(LocalDate.of(2025, 10, 31));
        dto.setEvaluationType(EvaluationType.MESE);
        dto.setEvaluationStartDate(LocalDate.of(2025, 1, 1));
        dto.setEvaluationEndDate(LocalDate.of(2025, 12, 31));
        dto.setTotRes(100L);
        dto.setResKo(5L);
        dto.setResKoPercentage(5.0);
        dto.setOutcome(OutcomeStatus.OK);
        dto.setKpiB9ResultId(50L);

        // Assert getters
        assertThat(dto.getId()).isEqualTo(1L);
        assertThat(dto.getInstanceId()).isEqualTo(10L);
        assertThat(dto.getInstanceModuleId()).isEqualTo(20L);
        assertThat(dto.getAnalysisDate()).isEqualTo(LocalDate.of(2025, 10, 31));
        assertThat(dto.getEvaluationType()).isEqualTo(EvaluationType.MESE);
        assertThat(dto.getEvaluationStartDate()).isEqualTo(LocalDate.of(2025, 1, 1));
        assertThat(dto.getEvaluationEndDate()).isEqualTo(LocalDate.of(2025, 12, 31));
        assertThat(dto.getTotRes()).isEqualTo(100L);
        assertThat(dto.getResKo()).isEqualTo(5L);
        assertThat(dto.getResKoPercentage()).isEqualTo(5.0);
        assertThat(dto.getOutcome()).isEqualTo(OutcomeStatus.OK);
        assertThat(dto.getKpiB9ResultId()).isEqualTo(50L);
    }

    @Test
    void testToString() {
        KpiB9DetailResultDTO dto = new KpiB9DetailResultDTO();
        dto.setId(1L);
        dto.setInstanceId(10L);
        dto.setInstanceModuleId(20L);
        dto.setAnalysisDate(LocalDate.of(2025, 10, 31));
        dto.setEvaluationType(EvaluationType.MESE);
        dto.setEvaluationStartDate(LocalDate.of(2025, 1, 1));
        dto.setEvaluationEndDate(LocalDate.of(2025, 12, 31));
        dto.setTotRes(100L);
        dto.setResKo(5L);
        dto.setResKoPercentage(5.0);
        dto.setOutcome(OutcomeStatus.OK);
        dto.setKpiB9ResultId(50L);

        String toString = dto.toString();

        assertThat(toString).contains(
            "id=1",
            "instanceId=10",
            "instanceModuleId=20",
            "analysisDate=2025-10-31",
            "evaluationType=MESE",
            "evaluationStartDate=2025-01-01",
            "evaluationEndDate=2025-12-31",
            "totRes=100",
            "resKo=5",
            "resKoPercentage=5.0",
            "outcome=OK",
            "kpiB9ResultId=50"
        );
    }
}
