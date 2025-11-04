package com.nexigroup.pagopa.cruscotto.service.dto;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

class PagopaNumeroStandinDTOTest {

    @Test
    void testGettersAndSetters() {
        PagopaNumeroStandinDTO dto = new PagopaNumeroStandinDTO();

        Long id = 1L;
        String stationCode = "ST001";
        LocalDateTime intervalStart = LocalDateTime.now().minusHours(1);
        LocalDateTime intervalEnd = LocalDateTime.now();
        Integer standInCount = 42;
        String eventType = "EVENT_X";
        LocalDateTime dataDate = LocalDateTime.now().minusDays(1);
        LocalDateTime dataOraEvento = LocalDateTime.now().minusMinutes(5);
        LocalDateTime loadTimestamp = LocalDateTime.now();
        Long partnerId = 999L;
        String partnerName = "Partner SPA";
        String partnerFiscalCode = "ABC123XYZ";

        dto.setId(id);
        dto.setStationCode(stationCode);
        dto.setIntervalStart(intervalStart);
        dto.setIntervalEnd(intervalEnd);
        dto.setStandInCount(standInCount);
        dto.setEventType(eventType);
        dto.setDataDate(dataDate);
        dto.setDataOraEvento(dataOraEvento);
        dto.setLoadTimestamp(loadTimestamp);
        dto.setPartnerId(partnerId);
        dto.setPartnerName(partnerName);
        dto.setPartnerFiscalCode(partnerFiscalCode);

        assertThat(dto.getId()).isEqualTo(id);
        assertThat(dto.getStationCode()).isEqualTo(stationCode);
        assertThat(dto.getIntervalStart()).isEqualTo(intervalStart);
        assertThat(dto.getIntervalEnd()).isEqualTo(intervalEnd);
        assertThat(dto.getStandInCount()).isEqualTo(standInCount);
        assertThat(dto.getEventType()).isEqualTo(eventType);
        assertThat(dto.getDataDate()).isEqualTo(dataDate);
        assertThat(dto.getDataOraEvento()).isEqualTo(dataOraEvento);
        assertThat(dto.getLoadTimestamp()).isEqualTo(loadTimestamp);
        assertThat(dto.getPartnerId()).isEqualTo(partnerId);
        assertThat(dto.getPartnerName()).isEqualTo(partnerName);
        assertThat(dto.getPartnerFiscalCode()).isEqualTo(partnerFiscalCode);
    }

    @Test
    void testEqualsAndHashCode() {
        PagopaNumeroStandinDTO dto1 = new PagopaNumeroStandinDTO();
        dto1.setId(1L);

        PagopaNumeroStandinDTO dto2 = new PagopaNumeroStandinDTO();
        dto2.setId(1L);

        PagopaNumeroStandinDTO dto3 = new PagopaNumeroStandinDTO();
        dto3.setId(2L);

        assertThat(dto1).isEqualTo(dto2);
        assertThat(dto1).hasSameHashCodeAs(dto2);
        assertThat(dto1).isNotEqualTo(dto3);
        assertThat(dto1).isNotEqualTo(null);
        assertThat(dto1).isNotEqualTo(new Object());
    }

    @Test
    void testToStringContainsAllFields() {
        PagopaNumeroStandinDTO dto = new PagopaNumeroStandinDTO();
        dto.setId(1L);
        dto.setStationCode("ST001");
        dto.setStandInCount(10);
        dto.setEventType("EVENT_X");
        dto.setPartnerName("Partner SPA");

        String result = dto.toString();

        assertThat(result)
            .contains("id=1")
            .contains("stationCode='ST001'")
            .contains("standInCount=10")
            .contains("eventType='EVENT_X'")
            .contains("partnerName='Partner SPA'");
    }
}
