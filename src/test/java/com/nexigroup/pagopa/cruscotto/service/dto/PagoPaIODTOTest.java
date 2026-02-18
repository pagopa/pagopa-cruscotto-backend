package com.nexigroup.pagopa.cruscotto.service.dto;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;

class PagoPaIODTOTest {

    @Test
    void testGetPercentualeMessaggi_bothZero() {
        PagoPaIODTO dto = new PagoPaIODTO();
        dto.setNumeroPosizioni(0);
        dto.setNumeroMessaggi(0);

        assertEquals(0.0, dto.getPercentualeMessaggi());
    }

    @Test
    void testGetPercentualeMessaggi_zeroPosizioniWithMessaggi() {
        PagoPaIODTO dto = new PagoPaIODTO();
        dto.setNumeroPosizioni(0);
        dto.setNumeroMessaggi(10);

        assertEquals(100.0, dto.getPercentualeMessaggi());
    }

    @Test
    void testGetPercentualeMessaggi_normalCalculation() {
        PagoPaIODTO dto = new PagoPaIODTO();
        dto.setNumeroPosizioni(20);
        dto.setNumeroMessaggi(5);

        assertEquals(25.0, dto.getPercentualeMessaggi());
    }

    @Test
    void testGetPercentualeMessaggi_nullMessaggi() {
        PagoPaIODTO dto = new PagoPaIODTO();
        dto.setNumeroPosizioni(10);
        dto.setNumeroMessaggi(null);

        assertEquals(0.0, dto.getPercentualeMessaggi());
    }

    @Test
    void testGetPercentualeMessaggi_nullPosizioniAndNullMessaggi() {
        PagoPaIODTO dto = new PagoPaIODTO();
        dto.setNumeroPosizioni(null);
        dto.setNumeroMessaggi(null);

        assertEquals(0.0, dto.getPercentualeMessaggi());
    }

    @Test
    void testMeetsToleranceThreshold_trueWhenEqual() {
        PagoPaIODTO dto = new PagoPaIODTO();
        dto.setNumeroPosizioni(10);
        dto.setNumeroMessaggi(5); // 50%

        assertTrue(dto.meetsToleranceThreshold(50.0));
    }

    @Test
    void testMeetsToleranceThreshold_trueWhenGreater() {
        PagoPaIODTO dto = new PagoPaIODTO();
        dto.setNumeroPosizioni(10);
        dto.setNumeroMessaggi(8); // 80%

        assertTrue(dto.meetsToleranceThreshold(50.0));
    }

    @Test
    void testMeetsToleranceThreshold_falseWhenLower() {
        PagoPaIODTO dto = new PagoPaIODTO();
        dto.setNumeroPosizioni(10);
        dto.setNumeroMessaggi(2); // 20%

        assertFalse(dto.meetsToleranceThreshold(50.0));
    }

    @Test
    void testEquals_sameId() {
        PagoPaIODTO dto1 = new PagoPaIODTO();
        dto1.setId(1L);

        PagoPaIODTO dto2 = new PagoPaIODTO();
        dto2.setId(1L);

        assertEquals(dto1, dto2);
        assertEquals(dto1.hashCode(), dto2.hashCode());
    }

    @Test
    void testEquals_differentId() {
        PagoPaIODTO dto1 = new PagoPaIODTO();
        dto1.setId(1L);

        PagoPaIODTO dto2 = new PagoPaIODTO();
        dto2.setId(2L);

        assertNotEquals(dto1, dto2);
    }

    @Test
    void testEquals_nullId() {
        PagoPaIODTO dto1 = new PagoPaIODTO();
        PagoPaIODTO dto2 = new PagoPaIODTO();

        assertNotEquals(dto1, dto2);
    }

    @Test
    void testGettersAndSetters() {
        PagoPaIODTO dto = new PagoPaIODTO();
        LocalDate today = LocalDate.now();

        dto.setId(1L);
        dto.setCfPartner("CF123");
        dto.setEnte("Comune di Roma");
        dto.setData(today);
        dto.setNumeroPosizioni(10);
        dto.setNumeroMessaggi(7);

        assertEquals(1L, dto.getId());
        assertEquals("CF123", dto.getCfPartner());
        assertEquals("Comune di Roma", dto.getEnte());
        assertEquals(today, dto.getData());
        assertEquals(10, dto.getNumeroPosizioni());
        assertEquals(7, dto.getNumeroMessaggi());
    }
}
