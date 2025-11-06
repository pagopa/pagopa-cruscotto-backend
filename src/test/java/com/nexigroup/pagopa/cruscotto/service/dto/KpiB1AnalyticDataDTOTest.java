package com.nexigroup.pagopa.cruscotto.service.dto;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

class KpiB1AnalyticDataDTOTest {

    @Test
    void testGettersAndSetters() {
        KpiB1AnalyticDataDTO dto = new KpiB1AnalyticDataDTO();

        dto.setId(1L);
        dto.setInstanceId(2L);
        dto.setInstanceModuleId(3L);
        dto.setAnalysisDate(LocalDate.of(2025, 10, 31));
        dto.setDataDate(LocalDate.of(2025, 10, 30));
        dto.setInstitutionCount(100);
        dto.setTransactionCount(200);
        dto.setKpiB1DetailResultId(4L);

        assertThat(dto.getId()).isEqualTo(1L);
        assertThat(dto.getInstanceId()).isEqualTo(2L);
        assertThat(dto.getInstanceModuleId()).isEqualTo(3L);
        assertThat(dto.getAnalysisDate()).isEqualTo(LocalDate.of(2025, 10, 31));
        assertThat(dto.getDataDate()).isEqualTo(LocalDate.of(2025, 10, 30));
        assertThat(dto.getInstitutionCount()).isEqualTo(100);
        assertThat(dto.getTransactionCount()).isEqualTo(200);
        assertThat(dto.getKpiB1DetailResultId()).isEqualTo(4L);
    }

    @Test
    void testEqualsAndHashCode() {
        KpiB1AnalyticDataDTO dto1 = new KpiB1AnalyticDataDTO();
        dto1.setId(1L);

        KpiB1AnalyticDataDTO dto2 = new KpiB1AnalyticDataDTO();
        dto2.setId(1L);

        KpiB1AnalyticDataDTO dto3 = new KpiB1AnalyticDataDTO();
        dto3.setId(2L);

        // Equals
        assertThat(dto1).isEqualTo(dto2);
        assertThat(dto1).isNotEqualTo(dto3);

        // HashCode
        assertThat(dto1.hashCode()).isEqualTo(dto2.hashCode());
        assertThat(dto1.hashCode()).isNotEqualTo(dto3.hashCode());
    }

    @Test
    void testToString() {
        KpiB1AnalyticDataDTO dto = new KpiB1AnalyticDataDTO();
        dto.setId(1L);
        dto.setInstanceId(2L);

        String toString = dto.toString();
        assertThat(toString).contains("id=1");
        assertThat(toString).contains("instanceId=2");
    }
}
