package com.nexigroup.pagopa.cruscotto.service.dto;

import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;

class PagoPaTaxonomyAggregatePositionDTOTest {

    @Test
    void testGettersAndSetters() {
        PagoPaTaxonomyAggregatePositionDTO dto = new PagoPaTaxonomyAggregatePositionDTO();

        Long id = 1L;
        String cfPartner = "CF123";
        String station = "Station1";
        String transferCategory = "CategoryA";
        Instant startDate = Instant.now();
        Instant endDate = Instant.now().plusSeconds(3600);
        Long total = 100L;

        dto.setId(id);
        dto.setCfPartner(cfPartner);
        dto.setStation(station);
        dto.setTransferCategory(transferCategory);
        dto.setStartDate(startDate);
        dto.setEndDate(endDate);
        dto.setTotal(total);

        assertThat(dto.getId()).isEqualTo(id);
        assertThat(dto.getCfPartner()).isEqualTo(cfPartner);
        assertThat(dto.getStation()).isEqualTo(station);
        assertThat(dto.getTransferCategory()).isEqualTo(transferCategory);
        assertThat(dto.getStartDate()).isEqualTo(startDate);
        assertThat(dto.getEndDate()).isEqualTo(endDate);
        assertThat(dto.getTotal()).isEqualTo(total);
    }

    @Test
    void testEqualsAndHashCode() {
        PagoPaTaxonomyAggregatePositionDTO dto1 = new PagoPaTaxonomyAggregatePositionDTO();
        PagoPaTaxonomyAggregatePositionDTO dto2 = new PagoPaTaxonomyAggregatePositionDTO();

        dto1.setId(1L);
        dto2.setId(1L);

        // Same object
        assertThat(dto1).isEqualTo(dto1);

        // Different object, same ID
        assertThat(dto1).isEqualTo(dto2);
        assertThat(dto1.hashCode()).isEqualTo(dto2.hashCode());

        // Different ID
        dto2.setId(2L);
        assertThat(dto1).isNotEqualTo(dto2);
    }

    @Test
    void testToString() {
        PagoPaTaxonomyAggregatePositionDTO dto = new PagoPaTaxonomyAggregatePositionDTO();
        dto.setId(1L);
        dto.setCfPartner("CF123");
        dto.setStation("Station1");
        dto.setTransferCategory("CategoryA");
        dto.setStartDate(Instant.parse("2025-01-01T00:00:00Z"));
        dto.setEndDate(Instant.parse("2025-12-31T23:59:59Z"));
        dto.setTotal(100L);

        String expected = "PagoPaTaxonomyAggregatePositionDTO [id=1, cfPartner=CF123, station=Station1, transferCategory=CategoryA, startDate=2025-01-01T00:00:00Z, endDate=2025-12-31T23:59:59Z, total=100]";
        assertThat(dto.toString()).isEqualTo(expected);
    }
}
