package com.nexigroup.pagopa.cruscotto.domain;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import org.junit.jupiter.api.Test;

class PagopaNumeroStandinDrilldownTest {

    @Test
    void testGettersAndSetters() {
        PagopaNumeroStandinDrilldown entity = new PagopaNumeroStandinDrilldown();

        LocalDate analysisDate = LocalDate.now();
        LocalDateTime intervalStart = LocalDateTime.now().minusHours(1);
        LocalDateTime intervalEnd = LocalDateTime.now();
        LocalDateTime dataDate = LocalDateTime.now().minusDays(1);
        LocalDateTime dataOraEvento = LocalDateTime.now().minusMinutes(30);
        LocalDateTime loadTimestamp = LocalDateTime.now();

        entity.setId(1L);
        entity.setAnalysisDate(analysisDate);
        entity.setStationCode("ST123");
        entity.setIntervalStart(intervalStart);
        entity.setIntervalEnd(intervalEnd);
        entity.setStandInCount(5);
        entity.setEventType("TEST_EVENT");
        entity.setDataDate(dataDate);
        entity.setDataOraEvento(dataOraEvento);
        entity.setLoadTimestamp(loadTimestamp);
        entity.setOriginalStandinId(999L);

        assertEquals(1L, entity.getId());
        assertEquals(analysisDate, entity.getAnalysisDate());
        assertEquals("ST123", entity.getStationCode());
        assertEquals(intervalStart, entity.getIntervalStart());
        assertEquals(intervalEnd, entity.getIntervalEnd());
        assertEquals(5, entity.getStandInCount());
        assertEquals("TEST_EVENT", entity.getEventType());
        assertEquals(dataDate, entity.getDataDate());
        assertEquals(dataOraEvento, entity.getDataOraEvento());
        assertEquals(loadTimestamp, entity.getLoadTimestamp());
        assertEquals(999L, entity.getOriginalStandinId());
    }

    @Test
    void testEqualsAndHashCode() {
        PagopaNumeroStandinDrilldown entity1 = new PagopaNumeroStandinDrilldown();
        entity1.setId(123L);

        PagopaNumeroStandinDrilldown entity2 = new PagopaNumeroStandinDrilldown();
        entity2.setId(123L);

        PagopaNumeroStandinDrilldown entity3 = new PagopaNumeroStandinDrilldown();
        entity3.setId(456L);

        assertEquals(entity1, entity2);
        assertEquals(entity1.hashCode(), entity2.hashCode());

        assertNotEquals(entity1, entity3);
        assertNotEquals(entity1.hashCode(), entity3.hashCode());

        assertNotEquals(entity1, null);
        assertNotEquals(entity1, new Object());
    }

    @Test
    void testToStringContainsFields() {
        PagopaNumeroStandinDrilldown entity = new PagopaNumeroStandinDrilldown();
        entity.setId(10L);
        entity.setAnalysisDate(LocalDate.of(2025, 1, 1));
        entity.setStationCode("STA01");
        entity.setIntervalStart(LocalDateTime.of(2025, 1, 1, 10, 0));
        entity.setIntervalEnd(LocalDateTime.of(2025, 1, 1, 11, 0));
        entity.setStandInCount(3);
        entity.setDataDate(LocalDateTime.of(2025, 1, 1, 9, 0));
        entity.setDataOraEvento(LocalDateTime.of(2025, 1, 1, 9, 30));
        entity.setOriginalStandinId(77L);

        String result = entity.toString();

        assertTrue(result.contains("id=10"));
        assertTrue(result.contains("STA01"));
        assertTrue(result.contains("standInCount=3"));
        assertTrue(result.contains("originalStandinId=77"));
    }
}
