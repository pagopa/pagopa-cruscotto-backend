package com.nexigroup.pagopa.cruscotto.service.dto;

import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

class PagoPaPaymentReceiptDrilldownDTOTest {

    @Test
    void testGettersAndSetters() {
        PagoPaPaymentReceiptDrilldownDTO dto = new PagoPaPaymentReceiptDrilldownDTO();

        dto.setId(1L);
        dto.setInstanceId(10L);
        dto.setInstanceModuleId(100L);
        dto.setStationId(5L);
        dto.setStationName("Station A");
        dto.setAnalysisDate(LocalDate.of(2025, 10, 30));
        dto.setEvaluationDate(LocalDate.of(2025, 11, 1));
        dto.setStartTime(Instant.parse("2025-10-30T08:00:00Z"));
        dto.setEndTime(Instant.parse("2025-10-30T08:15:00Z"));
        dto.setTimeSlot("08:00-08:15");
        dto.setTotRes(50L);
        dto.setResOk(40L);
        dto.setResKo(10L);
        dto.setResKoPercentage(20.0);

        assertThat(dto.getId()).isEqualTo(1L);
        assertThat(dto.getInstanceId()).isEqualTo(10L);
        assertThat(dto.getInstanceModuleId()).isEqualTo(100L);
        assertThat(dto.getStationId()).isEqualTo(5L);
        assertThat(dto.getStationName()).isEqualTo("Station A");
        assertThat(dto.getAnalysisDate()).isEqualTo(LocalDate.of(2025, 10, 30));
        assertThat(dto.getEvaluationDate()).isEqualTo(LocalDate.of(2025, 11, 1));
        assertThat(dto.getStartTime()).isEqualTo(Instant.parse("2025-10-30T08:00:00Z"));
        assertThat(dto.getEndTime()).isEqualTo(Instant.parse("2025-10-30T08:15:00Z"));
        assertThat(dto.getTimeSlot()).isEqualTo("08:00-08:15");
        assertThat(dto.getTotRes()).isEqualTo(50L);
        assertThat(dto.getResOk()).isEqualTo(40L);
        assertThat(dto.getResKo()).isEqualTo(10L);
        assertThat(dto.getResKoPercentage()).isEqualTo(20.0);
    }

    @Test
    void testEqualsAndHashCode() {
        PagoPaPaymentReceiptDrilldownDTO dto1 = new PagoPaPaymentReceiptDrilldownDTO();
        PagoPaPaymentReceiptDrilldownDTO dto2 = new PagoPaPaymentReceiptDrilldownDTO();

        dto1.setId(1L);
        dto2.setId(1L);

        assertThat(dto1).isEqualTo(dto2);
        assertThat(dto1.hashCode()).isEqualTo(dto2.hashCode());

        dto2.setId(2L);
        assertThat(dto1).isNotEqualTo(dto2);
    }

    @Test
    void testToString() {
        PagoPaPaymentReceiptDrilldownDTO dto = new PagoPaPaymentReceiptDrilldownDTO();
        dto.setId(1L);
        dto.setInstanceId(10L);
        dto.setStationId(5L);
        dto.setStationName("Station A");
        dto.setEvaluationDate(LocalDate.of(2025, 11, 1));
        dto.setTimeSlot("08:00-08:15");
        dto.setTotRes(50L);
        dto.setResOk(40L);
        dto.setResKo(10L);
        dto.setResKoPercentage(20.0);

        String str = dto.toString();
        assertThat(str).contains("id=1");
        assertThat(str).contains("instanceId=10");
        assertThat(str).contains("stationId=5");
        assertThat(str).contains("stationName='Station A'");
        assertThat(str).contains("evaluationDate=2025-11-01");
        assertThat(str).contains("timeSlot='08:00-08:15'");
        assertThat(str).contains("totRes=50");
        assertThat(str).contains("resOk=40");
        assertThat(str).contains("resKo=10");
        assertThat(str).contains("resKoPercentage=20.0");
    }
}
