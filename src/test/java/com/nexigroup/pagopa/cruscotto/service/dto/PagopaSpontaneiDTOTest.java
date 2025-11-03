package com.nexigroup.pagopa.cruscotto.service.dto;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PagopaSpontaneiDTOTest {

    @Test
    void testGettersAndSetters() {
        PagopaSpontaneiDTO dto = new PagopaSpontaneiDTO();

        dto.setId(1L);
        dto.setKpiB5AnalyticDataId(2L);
        dto.setPartnerId(3L);
        dto.setPartnerName("Partner Name");
        dto.setPartnerFiscalCode("ABC123");
        dto.setStationCode("ST123");
        dto.setFiscalCode("FC456");
        dto.setSpontaneousPayments("ATTIVI");

        assertEquals(1L, dto.getId());
        assertEquals(2L, dto.getKpiB5AnalyticDataId());
        assertEquals(3L, dto.getPartnerId());
        assertEquals("Partner Name", dto.getPartnerName());
        assertEquals("ABC123", dto.getPartnerFiscalCode());
        assertEquals("ST123", dto.getStationCode());
        assertEquals("FC456", dto.getFiscalCode());
        assertEquals("ATTIVI", dto.getSpontaneousPayments());
    }

    @Test
    void testEqualsAndHashCode() {
        PagopaSpontaneiDTO dto1 = new PagopaSpontaneiDTO();
        dto1.setId(1L);

        PagopaSpontaneiDTO dto2 = new PagopaSpontaneiDTO();
        dto2.setId(1L);

        PagopaSpontaneiDTO dto3 = new PagopaSpontaneiDTO();
        dto3.setId(2L);

        assertEquals(dto1, dto2);
        assertNotEquals(dto1, dto3);
        assertNotEquals(dto1, null);
        assertNotEquals(dto1, new Object());
        assertEquals(dto1.hashCode(), dto2.hashCode());
    }

    @Test
    void testEqualsWhenIdIsNull() {
        PagopaSpontaneiDTO dto1 = new PagopaSpontaneiDTO();
        PagopaSpontaneiDTO dto2 = new PagopaSpontaneiDTO();

        assertNotEquals(dto1, dto2);
    }

    @Test
    void testToStringContainsAllFields() {
        PagopaSpontaneiDTO dto = new PagopaSpontaneiDTO();
        dto.setId(1L);
        dto.setKpiB5AnalyticDataId(2L);
        dto.setPartnerId(3L);
        dto.setPartnerName("Partner");
        dto.setPartnerFiscalCode("FISCAL");
        dto.setStationCode("ST001");
        dto.setFiscalCode("FC123");
        dto.setSpontaneousPayments("ATTIVI");

        String result = dto.toString();

        assertTrue(result.contains("id=1"));
        assertTrue(result.contains("kpiB5AnalyticDataId=2"));
        assertTrue(result.contains("partnerId=3"));
        assertTrue(result.contains("partnerName='Partner'"));
        assertTrue(result.contains("partnerFiscalCode='FISCAL'"));
        assertTrue(result.contains("stationCode='ST001'"));
        assertTrue(result.contains("fiscalCode='FC123'"));
        assertTrue(result.contains("spontaneousPayments='ATTIVI'"));
    }
}
