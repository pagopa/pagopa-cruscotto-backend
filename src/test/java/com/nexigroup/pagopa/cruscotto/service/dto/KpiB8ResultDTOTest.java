package com.nexigroup.pagopa.cruscotto.service.dto;

import com.nexigroup.pagopa.cruscotto.domain.enumeration.EvaluationType;
import com.nexigroup.pagopa.cruscotto.domain.enumeration.OutcomeStatus;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

class KpiB8ResultDTOTest {

    @Test
    void testGetterAndSetter() {
        KpiB8ResultDTO dto = new KpiB8ResultDTO();

        dto.setId(1L);
        dto.setInstanceId(2L);
        dto.setInstanceModuleId(3L);
        dto.setAnalysisDate(LocalDate.of(2025, 10, 31));
        dto.setEligibilityThreshold(0.75);
        dto.setTolerance(0.05);
        dto.setEvaluationType(EvaluationType.MESE);
        dto.setOutcome(OutcomeStatus.OK);

        assertThat(dto.getId()).isEqualTo(1L);
        assertThat(dto.getInstanceId()).isEqualTo(2L);
        assertThat(dto.getInstanceModuleId()).isEqualTo(3L);
        assertThat(dto.getAnalysisDate()).isEqualTo(LocalDate.of(2025, 10, 31));
        assertThat(dto.getEligibilityThreshold()).isEqualTo(0.75);
        assertThat(dto.getTolerance()).isEqualTo(0.05);
        assertThat(dto.getEvaluationType()).isEqualTo(EvaluationType.MESE);
        assertThat(dto.getOutcome()).isEqualTo(OutcomeStatus.OK);
    }

    @Test
    void testEqualsAndHashCode() {
        KpiB8ResultDTO dto1 = new KpiB8ResultDTO();
        dto1.setId(1L);

        KpiB8ResultDTO dto2 = new KpiB8ResultDTO();
        dto2.setId(1L);

        KpiB8ResultDTO dto3 = new KpiB8ResultDTO();
        dto3.setId(2L);

        assertThat(dto1).isEqualTo(dto2);
        assertThat(dto1.hashCode()).isEqualTo(dto2.hashCode());
        assertThat(dto1).isNotEqualTo(dto3);
    }

    @Test
    void testToString() {
        KpiB8ResultDTO dto = new KpiB8ResultDTO();
        dto.setId(1L);
        dto.setInstanceId(2L);

        String toString = dto.toString();
        assertThat(toString).contains("id=1", "instanceId=2");
    }
}
