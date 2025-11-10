package com.nexigroup.pagopa.cruscotto.service.dto;

import com.nexigroup.pagopa.cruscotto.domain.enumeration.TypePlanned;
import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;

class AnagPlannedShutdownDTOTest {

    @Test
    void testGettersAndSetters() {
        AnagPlannedShutdownDTO dto = new AnagPlannedShutdownDTO();

        dto.setId(1L);
        dto.setTypePlanned(TypePlanned.PROGRAMMATO);
        dto.setStandInd(true);
        Instant now = Instant.now();
        dto.setShutdownStartDate(now);
        dto.setShutdownEndDate(now.plusSeconds(3600));
        dto.setYear(2025L);
        dto.setExternalId(100L);
        dto.setPartnerId(200L);
        dto.setPartnerFiscalCode("ABC123");
        dto.setPartnerName("PartnerName");
        dto.setStationId(300L);
        dto.setStationName("StationName");

        assertThat(dto.getId()).isEqualTo(1L);
        assertThat(dto.getTypePlanned()).isEqualTo(TypePlanned.PROGRAMMATO);
        assertThat(dto.isStandInd()).isTrue();
        assertThat(dto.getShutdownStartDate()).isEqualTo(now);
        assertThat(dto.getShutdownEndDate()).isEqualTo(now.plusSeconds(3600));
        assertThat(dto.getYear()).isEqualTo(2025L);
        assertThat(dto.getExternalId()).isEqualTo(100L);
        assertThat(dto.getPartnerId()).isEqualTo(200L);
        assertThat(dto.getPartnerFiscalCode()).isEqualTo("ABC123");
        assertThat(dto.getPartnerName()).isEqualTo("PartnerName");
        assertThat(dto.getStationId()).isEqualTo(300L);
        assertThat(dto.getStationName()).isEqualTo("StationName");
    }

    @Test
    void testEqualsAndHashCode() {
        AnagPlannedShutdownDTO dto1 = new AnagPlannedShutdownDTO();
        dto1.setId(1L);

        AnagPlannedShutdownDTO dto2 = new AnagPlannedShutdownDTO();
        dto2.setId(1L);

        assertThat(dto1).isEqualTo(dto2);
        assertThat(dto1.hashCode()).isEqualTo(dto2.hashCode());

        dto2.setId(2L);
        assertThat(dto1).isNotEqualTo(dto2);
    }

    @Test
    void testToString() {
        AnagPlannedShutdownDTO dto = new AnagPlannedShutdownDTO();
        dto.setId(1L);
        dto.setPartnerFiscalCode("ABC123");
        dto.setStationName("StationName");

        String toString = dto.toString();
        assertThat(toString).contains("id=1");
        assertThat(toString).contains("partnerFiscalCode='ABC123'");
        assertThat(toString).contains("stationName='StationName'");
    }
}
