package com.nexigroup.pagopa.cruscotto.service.dto;

import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

class TaxonomyDTOTest {

    @Test
    void testGettersAndSetters() {
        TaxonomyDTO dto = new TaxonomyDTO();

        dto.setId(1L);
        dto.setInstitutionTypeCode("ITC");
        dto.setInstitutionType("Bank");
        dto.setAreaProgressiveCode("APC");
        dto.setAreaName("AreaName");
        dto.setAreaDescription("AreaDescription");
        dto.setServiceTypeCode("STC");
        dto.setServiceType("ServiceType");
        dto.setServiceTypeDescription("ServiceTypeDescription");
        dto.setVersion("v1");
        dto.setReasonCollection("Reason");
        dto.setTakingsIdentifier("TID");
        dto.setValidityStartDate(LocalDate.of(2025, 10, 27));
        dto.setValidityEndDate(LocalDate.of(2026, 10, 27));
        dto.setCreatedBy("Creator");
        dto.setCreatedDate(Instant.now());
        dto.setLastModifiedBy("Modifier");
        dto.setLastModifiedDate(Instant.now());

        assertThat(dto.getId()).isEqualTo(1L);
        assertThat(dto.getInstitutionTypeCode()).isEqualTo("ITC");
        assertThat(dto.getInstitutionType()).isEqualTo("Bank");
        assertThat(dto.getAreaProgressiveCode()).isEqualTo("APC");
        assertThat(dto.getAreaName()).isEqualTo("AreaName");
        assertThat(dto.getAreaDescription()).isEqualTo("AreaDescription");
        assertThat(dto.getServiceTypeCode()).isEqualTo("STC");
        assertThat(dto.getServiceType()).isEqualTo("ServiceType");
        assertThat(dto.getServiceTypeDescription()).isEqualTo("ServiceTypeDescription");
        assertThat(dto.getVersion()).isEqualTo("v1");
        assertThat(dto.getReasonCollection()).isEqualTo("Reason");
        assertThat(dto.getTakingsIdentifier()).isEqualTo("TID");
        assertThat(dto.getValidityStartDate()).isEqualTo(LocalDate.of(2025, 10, 27));
        assertThat(dto.getValidityEndDate()).isEqualTo(LocalDate.of(2026, 10, 27));
        assertThat(dto.getCreatedBy()).isEqualTo("Creator");
        assertThat(dto.getCreatedDate()).isNotNull();
        assertThat(dto.getLastModifiedBy()).isEqualTo("Modifier");
        assertThat(dto.getLastModifiedDate()).isNotNull();
    }

    @Test
    void testEqualsAndHashCode() {
        TaxonomyDTO dto1 = new TaxonomyDTO();
        dto1.setTakingsIdentifier("TID1");

        TaxonomyDTO dto2 = new TaxonomyDTO();
        dto2.setTakingsIdentifier("TID1");

        TaxonomyDTO dto3 = new TaxonomyDTO();
        dto3.setTakingsIdentifier("TID2");

        assertThat(dto1).isEqualTo(dto2);
        assertThat(dto1.hashCode()).isEqualTo(dto2.hashCode());
        assertThat(dto1).isNotEqualTo(dto3);
    }

    @Test
    void testToString() {
        TaxonomyDTO dto = new TaxonomyDTO();
        dto.setId(1L);
        dto.setTakingsIdentifier("TID");
        dto.setValidityStartDate(LocalDate.of(2025, 10, 27));
        dto.setValidityEndDate(LocalDate.of(2026, 10, 27));
        dto.setCreatedBy("Creator");
        dto.setCreatedDate(Instant.parse("2025-10-27T10:15:30.00Z"));
        dto.setLastModifiedBy("Modifier");
        dto.setLastModifiedDate(Instant.parse("2025-10-27T11:15:30.00Z"));

        String toString = dto.toString();

        assertThat(toString)
            .contains("id=1")
            .contains("takingsIdentifier='TID'")
            .contains("validityStartDate=2025-10-27")
            .contains("validityEndDate=2026-10-27")
            .contains("createdBy='Creator'")
            .contains("createdDate=2025-10-27T10:15:30Z")
            .contains("lastModifiedBy='Modifier'")
            .contains("lastModifiedDate=2025-10-27T11:15:30Z");
    }
}
