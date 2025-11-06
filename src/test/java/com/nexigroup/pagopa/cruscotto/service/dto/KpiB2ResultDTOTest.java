package com.nexigroup.pagopa.cruscotto.service.dto;

import com.nexigroup.pagopa.cruscotto.domain.enumeration.EvaluationType;
import com.nexigroup.pagopa.cruscotto.domain.enumeration.OutcomeStatus;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

class KpiB2ResultDTOTest {

    @Test
    void testGettersAndSetters() {
        KpiB2ResultDTO dto = new KpiB2ResultDTO();

        dto.setId(1L);
        dto.setInstanceId(2L);
        dto.setInstanceModuleId(3L);
        dto.setAnalysisDate(LocalDate.of(2025, 10, 31));
        dto.setExcludePlannedShutdown(true);
        dto.setExcludeUnplannedShutdown(false);
        dto.setEligibilityThreshold(75.0);
        dto.setTolerance(5.0);
        dto.setAverageTimeLimit(120.0);
        dto.setEvaluationType(EvaluationType.MESE);
        dto.setOutcome(OutcomeStatus.OK);

        assertThat(dto.getId()).isEqualTo(1L);
        assertThat(dto.getInstanceId()).isEqualTo(2L);
        assertThat(dto.getInstanceModuleId()).isEqualTo(3L);
        assertThat(dto.getAnalysisDate()).isEqualTo(LocalDate.of(2025, 10, 31));
        assertThat(dto.getExcludePlannedShutdown()).isTrue();
        assertThat(dto.getExcludeUnplannedShutdown()).isFalse();
        assertThat(dto.getEligibilityThreshold()).isEqualTo(75.0);
        assertThat(dto.getTolerance()).isEqualTo(5.0);
        assertThat(dto.getAverageTimeLimit()).isEqualTo(120.0);
        assertThat(dto.getEvaluationType()).isEqualTo(EvaluationType.MESE);
        assertThat(dto.getOutcome()).isEqualTo(OutcomeStatus.OK);
    }

    @Test
    void testToStringContainsAllFields() {
        KpiB2ResultDTO dto = new KpiB2ResultDTO();
        dto.setId(1L);
        dto.setInstanceId(2L);
        dto.setInstanceModuleId(3L);
        dto.setAnalysisDate(LocalDate.of(2025, 10, 31));
        dto.setExcludePlannedShutdown(true);
        dto.setExcludeUnplannedShutdown(false);
        dto.setEligibilityThreshold(75.0);
        dto.setTolerance(5.0);
        dto.setAverageTimeLimit(120.0);
        dto.setEvaluationType(EvaluationType.MESE);
        dto.setOutcome(OutcomeStatus.OK);

        String toString = dto.toString();

        assertThat(toString).contains(
            "id=1",
            "instanceId=2",
            "instanceModuleId=3",
            "analysisDate=2025-10-31",
            "excludePlannedShutdown=true",
            "excludeUnplannedShutdown=false",
            "eligibilityThreshold=75.0",
            "tolerance=5.0",
            "averageTimeLimit=120.0",
            "evaluationType=MESE",
            "outcome=OK"
        );
    }
}
