package com.nexigroup.pagopa.cruscotto.service.dto;

import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;

class PagoPaTaxonomyIncorrectDTOTest {

    @Test
    void testGettersAndSetters() {
        PagoPaTaxonomyIncorrectDTO dto = new PagoPaTaxonomyIncorrectDTO();

        Instant fromHour = Instant.parse("2025-10-27T08:00:00Z");
        Instant endHour = Instant.parse("2025-10-27T18:00:00Z");
        String transferCategory = "PAYMENT";
        Long total = 123L;

        dto.setFromHour(fromHour);
        dto.setEndHour(endHour);
        dto.setTransferCategory(transferCategory);
        dto.setTotal(total);

        assertThat(dto.getFromHour()).isEqualTo(fromHour);
        assertThat(dto.getEndHour()).isEqualTo(endHour);
        assertThat(dto.getTransferCategory()).isEqualTo(transferCategory);
        assertThat(dto.getTotal()).isEqualTo(total);
    }

    @Test
    void testEqualsAndHashCode() {
        PagoPaTaxonomyIncorrectDTO dto1 = new PagoPaTaxonomyIncorrectDTO();
        PagoPaTaxonomyIncorrectDTO dto2 = new PagoPaTaxonomyIncorrectDTO();

        dto1.setFromHour(Instant.parse("2025-10-27T08:00:00Z"));
        dto1.setEndHour(Instant.parse("2025-10-27T18:00:00Z"));
        dto1.setTransferCategory("PAYMENT");
        dto1.setTotal(123L);

        dto2.setFromHour(Instant.parse("2025-10-27T08:00:00Z"));
        dto2.setEndHour(Instant.parse("2025-10-27T18:00:00Z"));
        dto2.setTransferCategory("PAYMENT");
        dto2.setTotal(123L);

        assertThat(dto1).isEqualTo(dto2);
        assertThat(dto1.hashCode()).isEqualTo(dto2.hashCode());

        // Change a field and verify inequality
        dto2.setTotal(456L);
        assertThat(dto1).isNotEqualTo(dto2);
    }

    @Test
    void testToString() {
        PagoPaTaxonomyIncorrectDTO dto = new PagoPaTaxonomyIncorrectDTO();
        dto.setFromHour(Instant.parse("2025-10-27T08:00:00Z"));
        dto.setEndHour(Instant.parse("2025-10-27T18:00:00Z"));
        dto.setTransferCategory("PAYMENT");
        dto.setTotal(123L);

        String str = dto.toString();

        assertThat(str).contains("fromHour=2025-10-27T08:00:00Z");
        assertThat(str).contains("endHour=2025-10-27T18:00:00Z");
        assertThat(str).contains("transferCategory=PAYMENT");
        assertThat(str).contains("total=123");
    }
}
