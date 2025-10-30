package com.nexigroup.pagopa.cruscotto.service.dto;

import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;

class PagoPaRecordedTimeoutDTOTest {

    @Test
    void testGettersAndSetters() {
        PagoPaRecordedTimeoutDTO dto = new PagoPaRecordedTimeoutDTO();

        dto.setId(1L);
        dto.setCfPartner("ABC123");
        dto.setStation("Station1");
        dto.setMethod("POST");
        Instant start = Instant.now();
        Instant end = start.plusSeconds(3600);
        dto.setStartDate(start);
        dto.setEndDate(end);
        dto.setTotReq(100L);
        dto.setReqOk(90L);
        dto.setReqTimeout(10L);
        dto.setAvgTime(123.45);

        assertThat(dto.getId()).isEqualTo(1L);
        assertThat(dto.getCfPartner()).isEqualTo("ABC123");
        assertThat(dto.getStation()).isEqualTo("Station1");
        assertThat(dto.getMethod()).isEqualTo("POST");
        assertThat(dto.getStartDate()).isEqualTo(start);
        assertThat(dto.getEndDate()).isEqualTo(end);
        assertThat(dto.getTotReq()).isEqualTo(100L);
        assertThat(dto.getReqOk()).isEqualTo(90L);
        assertThat(dto.getReqTimeout()).isEqualTo(10L);
        assertThat(dto.getAvgTime()).isEqualTo(123.45);
    }

    @Test
    void testEqualsAndHashCode() {
        PagoPaRecordedTimeoutDTO dto1 = new PagoPaRecordedTimeoutDTO();
        PagoPaRecordedTimeoutDTO dto2 = new PagoPaRecordedTimeoutDTO();

        dto1.setId(1L);
        dto2.setId(1L);

        assertThat(dto1).isEqualTo(dto2);
        assertThat(dto1.hashCode()).isEqualTo(dto2.hashCode());

        dto2.setId(2L);
        assertThat(dto1).isNotEqualTo(dto2);
    }

    @Test
    void testToString() {
        PagoPaRecordedTimeoutDTO dto = new PagoPaRecordedTimeoutDTO();
        dto.setId(1L);
        dto.setCfPartner("ABC123");
        dto.setStation("Station1");
        dto.setMethod("POST");

        String str = dto.toString();
        assertThat(str).contains("1");
        assertThat(str).contains("ABC123");
        assertThat(str).contains("Station1");
        assertThat(str).contains("POST");
    }
}
