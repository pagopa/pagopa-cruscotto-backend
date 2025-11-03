package com.nexigroup.pagopa.cruscotto.service.dto;

import com.nexigroup.pagopa.cruscotto.domain.enumeration.EvaluationType;
import com.nexigroup.pagopa.cruscotto.domain.enumeration.OutcomeStatus;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

class KpiB3ResultDTOTest {

    @Test
    void testGetterAndSetter() {
        KpiB3ResultDTO dto = new KpiB3ResultDTO();

        dto.setId(1L);
        dto.setInstanceId(2L);
        dto.setInstanceModuleId(3L);
        dto.setAnalysisDate(LocalDate.of(2025, 10, 31));
        dto.setExcludePlannedShutdown(true);
        dto.setExcludeUnplannedShutdown(false);
        dto.setEligibilityThreshold(90.5);
        dto.setTolerance(5.0);
        dto.setEvaluationType(EvaluationType.MESE); // Replace with real enum value
        dto.setOutcome(OutcomeStatus.OK); // Replace with real enum value

        assertThat(dto.getId()).isEqualTo(1L);
        assertThat(dto.getInstanceId()).isEqualTo(2L);
        assertThat(dto.getInstanceModuleId()).isEqualTo(3L);
        assertThat(dto.getAnalysisDate()).isEqualTo(LocalDate.of(2025, 10, 31));
        assertThat(dto.getExcludePlannedShutdown()).isTrue();
        assertThat(dto.getExcludeUnplannedShutdown()).isFalse();
        assertThat(dto.getEligibilityThreshold()).isEqualTo(90.5);
        assertThat(dto.getTolerance()).isEqualTo(5.0);
        assertThat(dto.getEvaluationType()).isEqualTo(EvaluationType.MESE);
        assertThat(dto.getOutcome()).isEqualTo(OutcomeStatus.OK);
    }

    @Test
    void testEqualsAndHashCode() {
        KpiB3ResultDTO dto1 = new KpiB3ResultDTO();
        KpiB3ResultDTO dto2 = new KpiB3ResultDTO();

        dto1.setId(1L);
        dto2.setId(1L);

        assertThat(dto1).isEqualTo(dto2);
        assertThat(dto1.hashCode()).isEqualTo(dto2.hashCode());

        dto2.setId(2L);
        assertThat(dto1).isNotEqualTo(dto2);
    }

    @Test
    void testToString() {
        KpiB3ResultDTO dto = new KpiB3ResultDTO();
        dto.setId(1L);
        String toString = dto.toString();
        assertThat(toString).contains("id=1");
    }
}
