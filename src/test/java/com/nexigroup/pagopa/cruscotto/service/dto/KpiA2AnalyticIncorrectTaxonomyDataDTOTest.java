package com.nexigroup.pagopa.cruscotto.service.dto;

import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.*;

class KpiA2AnalyticIncorrectTaxonomyDataDTOTest {

    @Test
    void testGettersAndSetters() {
        KpiA2AnalyticIncorrectTaxonomyDataDTO dto = new KpiA2AnalyticIncorrectTaxonomyDataDTO();

        dto.setId(1L);
        dto.setKpiA2AnalyticDataId(2L);
        dto.setTransferCategory("Category1");
        dto.setTotal(100L);
        Instant from = Instant.now();
        Instant end = from.plusSeconds(3600);
        dto.setFromHour(from);
        dto.setEndHour(end);

        assertEquals(1L, dto.getId());
        assertEquals(2L, dto.getKpiA2AnalyticDataId());
        assertEquals("Category1", dto.getTransferCategory());
        assertEquals(100L, dto.getTotal());
        assertEquals(from, dto.getFromHour());
        assertEquals(end, dto.getEndHour());
    }

    @Test
    void testEqualsAndHashCode() {
        Instant now = Instant.now();

        KpiA2AnalyticIncorrectTaxonomyDataDTO dto1 = new KpiA2AnalyticIncorrectTaxonomyDataDTO();
        dto1.setId(1L);
        dto1.setKpiA2AnalyticDataId(2L);
        dto1.setTransferCategory("Category1");
        dto1.setTotal(100L);
        dto1.setFromHour(now);
        dto1.setEndHour(now.plusSeconds(3600));

        KpiA2AnalyticIncorrectTaxonomyDataDTO dto2 = new KpiA2AnalyticIncorrectTaxonomyDataDTO();
        dto2.setId(1L);
        dto2.setKpiA2AnalyticDataId(2L);
        dto2.setTransferCategory("Category1");
        dto2.setTotal(100L);
        dto2.setFromHour(now);
        dto2.setEndHour(now.plusSeconds(3600));

        assertEquals(dto1, dto2);
        assertEquals(dto1.hashCode(), dto2.hashCode());

        dto2.setId(99L);
        assertNotEquals(dto1, dto2);
    }

    @Test
    void testToString() {
        KpiA2AnalyticIncorrectTaxonomyDataDTO dto = new KpiA2AnalyticIncorrectTaxonomyDataDTO();
        dto.setId(1L);
        dto.setTransferCategory("Category1");

        String str = dto.toString();
        assertNotNull(str);
        assertTrue(str.contains("1"));
        assertTrue(str.contains("Category1"));
    }
}
