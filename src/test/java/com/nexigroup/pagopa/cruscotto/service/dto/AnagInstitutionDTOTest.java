package com.nexigroup.pagopa.cruscotto.service.dto;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class AnagInstitutionDTOTest {

    @Test
    void testGettersAndSetters() {
        // Arrange
        AnagInstitutionDTO dto = new AnagInstitutionDTO();
        InstitutionIdentificationDTO institutionIdentification = new InstitutionIdentificationDTO();
        String stationName = "Station A";
        String partnerName = "Partner X";
        String partnerFiscalCode = "12345678901";
        Boolean enabled = true;
        Boolean aca = false;
        Boolean standIn = true;

        // Act
        dto.setInstitutionIdentification(institutionIdentification);
        dto.setStationName(stationName);
        dto.setPartnerName(partnerName);
        dto.setPartnerFiscalCode(partnerFiscalCode);
        dto.setEnabled(enabled);
        dto.setAca(aca);
        dto.setStandIn(standIn);

        // Assert
        assertThat(dto.getInstitutionIdentification()).isEqualTo(institutionIdentification);
        assertThat(dto.getStationName()).isEqualTo(stationName);
        assertThat(dto.getPartnerName()).isEqualTo(partnerName);
        assertThat(dto.getPartnerFiscalCode()).isEqualTo(partnerFiscalCode);
        assertThat(dto.getEnabled()).isEqualTo(enabled);
        assertThat(dto.getAca()).isEqualTo(aca);
        assertThat(dto.getStandIn()).isEqualTo(standIn);
    }
}
