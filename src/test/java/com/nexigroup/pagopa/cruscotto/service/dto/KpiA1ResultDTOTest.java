package com.nexigroup.pagopa.cruscotto.service.dto;

import com.nexigroup.pagopa.cruscotto.domain.enumeration.EvaluationType;
import com.nexigroup.pagopa.cruscotto.domain.enumeration.OutcomeStatus;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

class KpiA1ResultDTOTest {

    @Test
    void testGettersAndSetters() {
        KpiA1ResultDTO dto = new KpiA1ResultDTO();

        dto.setId(1L);
        dto.setInstanceId(10L);
        dto.setInstanceModuleId(20L);
        dto.setAnalysisDate(LocalDate.of(2025, 10, 31));
        dto.setExcludePlannedShutdown(true);
        dto.setExcludeUnplannedShutdown(false);
        dto.setEligibilityThreshold(0.8);
        dto.setTolerance(0.1);
        dto.setEvaluationType(EvaluationType.MESE); // replace with actual enum value
        dto.setOutcome(OutcomeStatus.OK);           // replace with actual enum value

        assertThat(dto.getId()).isEqualTo(1L);
        assertThat(dto.getInstanceId()).isEqualTo(10L);
        assertThat(dto.getInstanceModuleId()).isEqualTo(20L);
        assertThat(dto.getAnalysisDate()).isEqualTo(LocalDate.of(2025, 10, 31));
        assertThat(dto.getExcludePlannedShutdown()).isTrue();
        assertThat(dto.getExcludeUnplannedShutdown()).isFalse();
        assertThat(dto.getEligibilityThreshold()).isEqualTo(0.8);
        assertThat(dto.getTolerance()).isEqualTo(0.1);
        assertThat(dto.getEvaluationType()).isEqualTo(EvaluationType.MESE);
        assertThat(dto.getOutcome()).isEqualTo(OutcomeStatus.OK);
    }

    @Test
    void testToStringContainsAllFields() {
        KpiA1ResultDTO dto = new KpiA1ResultDTO();
        dto.setId(1L);
        dto.setInstanceId(10L);
        dto.setInstanceModuleId(20L);
        dto.setAnalysisDate(LocalDate.of(2025, 10, 31));
        dto.setExcludePlannedShutdown(true);
        dto.setExcludeUnplannedShutdown(false);
        dto.setEligibilityThreshold(0.8);
        dto.setTolerance(0.1);
        dto.setEvaluationType(EvaluationType.MESE); // replace with actual enum value
        dto.setOutcome(OutcomeStatus.OK);           // replace with actual enum value

        String str = dto.toString();
        assertThat(str).contains(
            "id=1",
            "instanceId=10",
            "instanceModuleId=20",
            "analysisDate=2025-10-31",
            "excludePlannedShutdown=true",
            "excludeUnplannedShutdown=false",
            "eligibilityThreshold=0.8",
            "tolerance=0.1",
            "evaluationType=MESE",
            "outcome=OK"
        );
    }
}
