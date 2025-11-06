package com.nexigroup.pagopa.cruscotto.service.dto;

import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;

class PagoPaPaymentReceiptDTOTest {

    @Test
    void testGettersAndSetters() {
        PagoPaPaymentReceiptDTO dto = new PagoPaPaymentReceiptDTO();

        dto.setId(1L);
        dto.setCfPartner("ABC123");
        dto.setStation("Station1");
        dto.setStartDate(Instant.parse("2025-10-31T10:15:30.00Z"));
        dto.setEndDate(Instant.parse("2025-11-01T10:15:30.00Z"));
        dto.setTotRes(100L);
        dto.setResOk(90L);
        dto.setResKo(10L);

        assertThat(dto.getId()).isEqualTo(1L);
        assertThat(dto.getCfPartner()).isEqualTo("ABC123");
        assertThat(dto.getStation()).isEqualTo("Station1");
        assertThat(dto.getStartDate()).isEqualTo(Instant.parse("2025-10-31T10:15:30.00Z"));
        assertThat(dto.getEndDate()).isEqualTo(Instant.parse("2025-11-01T10:15:30.00Z"));
        assertThat(dto.getTotRes()).isEqualTo(100L);
        assertThat(dto.getResOk()).isEqualTo(90L);
        assertThat(dto.getResKo()).isEqualTo(10L);
    }

    @Test
    void testEqualsAndHashCode() {
        PagoPaPaymentReceiptDTO dto1 = new PagoPaPaymentReceiptDTO();
        PagoPaPaymentReceiptDTO dto2 = new PagoPaPaymentReceiptDTO();

        dto1.setId(1L);
        dto2.setId(1L);

        assertThat(dto1).isEqualTo(dto2);
        assertThat(dto1.hashCode()).isEqualTo(dto2.hashCode());

        dto2.setId(2L);
        assertThat(dto1).isNotEqualTo(dto2);
    }

    @Test
    void testToString() {
        PagoPaPaymentReceiptDTO dto = new PagoPaPaymentReceiptDTO();
        dto.setId(1L);
        dto.setCfPartner("ABC123");
        dto.setStation("Station1");

        String toString = dto.toString();
        assertThat(toString).contains("id=1");
        assertThat(toString).contains("cfPartner=ABC123");
        assertThat(toString).contains("station=Station1");
    }
}
