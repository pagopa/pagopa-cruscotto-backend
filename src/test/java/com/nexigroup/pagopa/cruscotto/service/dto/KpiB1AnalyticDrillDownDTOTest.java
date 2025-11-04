package com.nexigroup.pagopa.cruscotto.service.dto;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

class KpiB1AnalyticDrillDownDTOTest {

    @Test
    void testGetterAndSetter() {
        KpiB1AnalyticDrillDownDTO dto = new KpiB1AnalyticDrillDownDTO();

        dto.setId(1L);
        dto.setKpiB1AnalyticDataId(10L);
        dto.setDataDate(LocalDate.of(2025, 10, 30));
        dto.setPartnerFiscalCode("P123");
        dto.setInstitutionFiscalCode("I456");
        dto.setTransactionCount(100);
        dto.setStationCode("S789");

        assertThat(dto.getId()).isEqualTo(1L);
        assertThat(dto.getKpiB1AnalyticDataId()).isEqualTo(10L);
        assertThat(dto.getDataDate()).isEqualTo(LocalDate.of(2025, 10, 30));
        assertThat(dto.getPartnerFiscalCode()).isEqualTo("P123");
        assertThat(dto.getInstitutionFiscalCode()).isEqualTo("I456");
        assertThat(dto.getTransactionCount()).isEqualTo(100);
        assertThat(dto.getStationCode()).isEqualTo("S789");
    }

    @Test
    void testEqualsAndHashCode() {
        KpiB1AnalyticDrillDownDTO dto1 = new KpiB1AnalyticDrillDownDTO();
        dto1.setId(1L);

        KpiB1AnalyticDrillDownDTO dto2 = new KpiB1AnalyticDrillDownDTO();
        dto2.setId(1L);

        KpiB1AnalyticDrillDownDTO dto3 = new KpiB1AnalyticDrillDownDTO();
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
        KpiB1AnalyticDrillDownDTO dto = new KpiB1AnalyticDrillDownDTO();
        dto.setId(1L);
        dto.setPartnerFiscalCode("P123");

        String toString = dto.toString();

        assertThat(toString).contains("id=1");
        assertThat(toString).contains("partnerFiscalCode=P123");
    }
}
