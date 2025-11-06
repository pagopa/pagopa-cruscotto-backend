package com.nexigroup.pagopa.cruscotto.service.dto;

import com.nexigroup.pagopa.cruscotto.domain.enumeration.StationStatus;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

class AnagStationDTOTest {

    @Test
    void testGettersAndSetters() {
        AnagStationDTO dto = new AnagStationDTO();

        // ID
        dto.setId(1L);
        assertThat(dto.getId()).isEqualTo(1L);

        // Name
        dto.setName("Station A");
        assertThat(dto.getName()).isEqualTo("Station A");

        // Activation Date
        LocalDate activationDate = LocalDate.now();
        dto.setActivationDate(activationDate);
        assertThat(dto.getActivationDate()).isEqualTo(activationDate);

        // Partner
        dto.setPartnerId(10L);
        assertThat(dto.getPartnerId()).isEqualTo(10L);

        dto.setPartnerFiscalCode("ABC123");
        assertThat(dto.getPartnerFiscalCode()).isEqualTo("ABC123");

        dto.setPartnerName("Partner X");
        assertThat(dto.getPartnerName()).isEqualTo("Partner X");

        // Connection type
        dto.setTypeConnection("TYPE1");
        assertThat(dto.getTypeConnection()).isEqualTo("TYPE1");

        // Primitive Version
        dto.setPrimitiveVersion(1);
        assertThat(dto.getPrimitiveVersion()).isEqualTo(1);

        // Payment Option
        dto.setPaymentOption(true);
        assertThat(dto.getPaymentOption()).isTrue();

        // Associated Institutes
        dto.setAssociatedInstitutes(5);
        assertThat(dto.getAssociatedInstitutes()).isEqualTo(5);

        // Status
        dto.setStatus(StationStatus.ATTIVA);
        assertThat(dto.getStatus()).isEqualTo(StationStatus.ATTIVA);

        // Deactivation Date
        LocalDate deactivationDate = LocalDate.now();
        dto.setDeactivationDate(deactivationDate);
        assertThat(dto.getDeactivationDate()).isEqualTo(deactivationDate);

        // Created / Modified
        dto.setCreatedBy("user1");
        dto.setCreatedDate(Instant.now());
        dto.setLastModifiedBy("user2");
        dto.setLastModifiedDate(Instant.now());

        assertThat(dto.getCreatedBy()).isEqualTo("user1");
        assertThat(dto.getCreatedDate()).isNotNull();
        assertThat(dto.getLastModifiedBy()).isEqualTo("user2");
        assertThat(dto.getLastModifiedDate()).isNotNull();
    }

    @Test
    void testEqualsAndHashCode() {
        AnagStationDTO dto1 = new AnagStationDTO();
        dto1.setId(1L);

        AnagStationDTO dto2 = new AnagStationDTO();
        dto2.setId(1L);

        assertThat(dto1).isEqualTo(dto2);
        assertThat(dto1.hashCode()).isEqualTo(dto2.hashCode());

        dto2.setId(2L);
        assertThat(dto1).isNotEqualTo(dto2);
    }

    @Test
    void testDefaultPaymentOption() {
        AnagStationDTO dto = new AnagStationDTO();
        // Default value should be FALSE
        assertThat(dto.getPaymentOption()).isFalse();
    }
}
