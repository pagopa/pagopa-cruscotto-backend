package com.nexigroup.pagopa.cruscotto.service.dto;

import com.nexigroup.pagopa.cruscotto.domain.enumeration.EvaluationType;
import com.nexigroup.pagopa.cruscotto.domain.enumeration.OutcomeStatus;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

class KpiB9ResultDTOTest {

    @Test
    void testGettersAndSetters() {
        KpiB9ResultDTO dto = new KpiB9ResultDTO();

        dto.setId(1L);
        dto.setInstanceId(100L);
        dto.setInstanceModuleId(200L);
        dto.setAnalysisDate(LocalDate.of(2025, 10, 31));
        dto.setExcludePlannedShutdown(true);
        dto.setExcludeUnplannedShutdown(false);
        dto.setEligibilityThreshold(95.5);
        dto.setTolerance(5.0);
        dto.setEvaluationType(EvaluationType.MESE);
        dto.setOutcome(OutcomeStatus.OK);

        assertThat(dto.getId()).isEqualTo(1L);
        assertThat(dto.getInstanceId()).isEqualTo(100L);
        assertThat(dto.getInstanceModuleId()).isEqualTo(200L);
        assertThat(dto.getAnalysisDate()).isEqualTo(LocalDate.of(2025, 10, 31));
        assertThat(dto.getExcludePlannedShutdown()).isTrue();
        assertThat(dto.getExcludeUnplannedShutdown()).isFalse();
        assertThat(dto.getEligibilityThreshold()).isEqualTo(95.5);
        assertThat(dto.getTolerance()).isEqualTo(5.0);
        assertThat(dto.getEvaluationType()).isEqualTo(EvaluationType.MESE);
        assertThat(dto.getOutcome()).isEqualTo(OutcomeStatus.OK);
    }

    @Test
    void testToString() {
        KpiB9ResultDTO dto = new KpiB9ResultDTO();
        dto.setId(1L);
        dto.setInstanceId(100L);
        dto.setInstanceModuleId(200L);
        dto.setAnalysisDate(LocalDate.of(2025, 10, 31));
        dto.setExcludePlannedShutdown(true);
        dto.setExcludeUnplannedShutdown(false);
        dto.setEligibilityThreshold(95.5);
        dto.setTolerance(5.0);
        dto.setEvaluationType(EvaluationType.MESE);
        dto.setOutcome(OutcomeStatus.OK);

        String expected = "KpiB9ResultDTO [id=1, instanceId=100, instanceModuleId=200, analysisDate=2025-10-31, excludePlannedShutdown=true, excludeUnplannedShutdown=false, eligibilityThreshold=95.5, tolerance=5.0, evaluationType=MESE, outcome=OK]";

        assertThat(dto.toString()).isEqualTo(expected);
    }
}
