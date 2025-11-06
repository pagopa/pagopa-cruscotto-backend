package com.nexigroup.pagopa.cruscotto.service.dto;

import com.nexigroup.pagopa.cruscotto.domain.enumeration.EvaluationType;
import com.nexigroup.pagopa.cruscotto.domain.enumeration.OutcomeStatus;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

class KpiB4ResultDTOTest {

    @Test
    void testGettersAndSetters() {
        KpiB4ResultDTO dto = new KpiB4ResultDTO();
        dto.setId(1L);
        dto.setInstanceId(2L);
        dto.setInstanceModuleId(3L);
        dto.setAnalysisDate(LocalDate.of(2025, 1, 1));
        dto.setExcludePlannedShutdown(true);
        dto.setExcludeUnplannedShutdown(false);
        dto.setEligibilityThreshold(0.8);
        dto.setTolerance(0.1);
        dto.setEvaluationType(EvaluationType.MESE);
        dto.setOutcome(OutcomeStatus.OK);

        assertThat(dto.getId()).isEqualTo(1L);
        assertThat(dto.getInstanceId()).isEqualTo(2L);
        assertThat(dto.getInstanceModuleId()).isEqualTo(3L);
        assertThat(dto.getAnalysisDate()).isEqualTo(LocalDate.of(2025, 1, 1));
        assertThat(dto.getExcludePlannedShutdown()).isTrue();
        assertThat(dto.getExcludeUnplannedShutdown()).isFalse();
        assertThat(dto.getEligibilityThreshold()).isEqualTo(0.8);
        assertThat(dto.getTolerance()).isEqualTo(0.1);
        assertThat(dto.getEvaluationType()).isEqualTo(EvaluationType.MESE);
        assertThat(dto.getOutcome()).isEqualTo(OutcomeStatus.OK);
    }

    @Test
    void testEqualsAndHashCode_sameId() {
        KpiB4ResultDTO dto1 = new KpiB4ResultDTO();
        dto1.setId(1L);

        KpiB4ResultDTO dto2 = new KpiB4ResultDTO();
        dto2.setId(1L);

        assertThat(dto1).isEqualTo(dto2);
        assertThat(dto1.hashCode()).isEqualTo(dto2.hashCode());
    }

    @Test
    void testEqualsAndHashCode_differentId() {
        KpiB4ResultDTO dto1 = new KpiB4ResultDTO();
        dto1.setId(1L);

        KpiB4ResultDTO dto2 = new KpiB4ResultDTO();
        dto2.setId(2L);

        assertThat(dto1).isNotEqualTo(dto2);
        assertThat(dto1.hashCode()).isNotEqualTo(dto2.hashCode());
    }

    @Test
    void testEquals_nullId() {
        KpiB4ResultDTO dto1 = new KpiB4ResultDTO();
        dto1.setId(null);

        KpiB4ResultDTO dto2 = new KpiB4ResultDTO();
        dto2.setId(1L);

        assertThat(dto1).isNotEqualTo(dto2);
    }

    @Test
    void testEquals_sameObject() {
        KpiB4ResultDTO dto = new KpiB4ResultDTO();
        dto.setId(10L);
        assertThat(dto).isEqualTo(dto);
    }

    @Test
    void testEquals_differentClass() {
        KpiB4ResultDTO dto = new KpiB4ResultDTO();
        dto.setId(10L);
        assertThat(dto.equals("string")).isFalse();
    }

    @Test
    void testToStringContainsFields() {
        KpiB4ResultDTO dto = new KpiB4ResultDTO();
        dto.setId(1L);
        dto.setInstanceId(2L);
        dto.setInstanceModuleId(3L);
        dto.setAnalysisDate(LocalDate.of(2025, 1, 1));
        dto.setExcludePlannedShutdown(true);
        dto.setExcludeUnplannedShutdown(false);
        dto.setEligibilityThreshold(0.5);
        dto.setTolerance(0.1);
        dto.setEvaluationType(EvaluationType.MESE);
        dto.setOutcome(OutcomeStatus.KO);

        String str = dto.toString();

        assertThat(str).contains("id=1");
        assertThat(str).contains("instanceId=2");
        assertThat(str).contains("evaluationType='MESE'");
        assertThat(str).contains("outcome='KO'");
    }
}
