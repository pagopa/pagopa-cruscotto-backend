package com.nexigroup.pagopa.cruscotto.service.dto;

import com.nexigroup.pagopa.cruscotto.domain.enumeration.PartnerStatus;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

class AnagPartnerDTOTest {

    @Test
    void testGettersAndSetters() {
        // Arrange
        AnagPartnerDTO dto = new AnagPartnerDTO();
        PartnerIdentificationDTO partnerIdentification = new PartnerIdentificationDTO();
        PartnerStatus status = PartnerStatus.ATTIVO;
        Boolean qualified = true;
        LocalDate deactivationDate = LocalDate.of(2025, 10, 27);
        LocalDate lastAnalysisDate = LocalDate.of(2025, 10, 20);
        LocalDate analysisPeriodStartDate = LocalDate.of(2025, 10, 1);
        LocalDate analysisPeriodEndDate = LocalDate.of(2025, 10, 15);
        Long stationsCount = 5L;
        Long associatedInstitutes = 2L;

        // Act
        dto.setPartnerIdentification(partnerIdentification);
        dto.setStatus(status);
        dto.setQualified(qualified);
        dto.setDeactivationDate(deactivationDate);
        dto.setLastAnalysisDate(lastAnalysisDate);
        dto.setAnalysisPeriodStartDate(analysisPeriodStartDate);
        dto.setAnalysisPeriodEndDate(analysisPeriodEndDate);
        dto.setStationsCount(stationsCount);
        dto.setAssociatedInstitutes(associatedInstitutes);

        // Assert
        assertThat(dto.getPartnerIdentification()).isEqualTo(partnerIdentification);
        assertThat(dto.getStatus()).isEqualTo(status);
        assertThat(dto.getQualified()).isEqualTo(qualified);
        assertThat(dto.getDeactivationDate()).isEqualTo(deactivationDate);
        assertThat(dto.getLastAnalysisDate()).isEqualTo(lastAnalysisDate);
        assertThat(dto.getAnalysisPeriodStartDate()).isEqualTo(analysisPeriodStartDate);
        assertThat(dto.getAnalysisPeriodEndDate()).isEqualTo(analysisPeriodEndDate);
        assertThat(dto.getStationsCount()).isEqualTo(stationsCount);
        assertThat(dto.getAssociatedInstitutes()).isEqualTo(associatedInstitutes);
    }

    @Test
    void testEqualsAndHashCode() {
        // Arrange
        AnagPartnerDTO dto1 = new AnagPartnerDTO();
        AnagPartnerDTO dto2 = new AnagPartnerDTO();

        // Act & Assert
        assertThat(dto1).isEqualTo(dto2);
        assertThat(dto1.hashCode()).isEqualTo(dto2.hashCode());

        dto1.setQualified(true);
        assertThat(dto1).isNotEqualTo(dto2);
    }
}
