package com.nexigroup.pagopa.cruscotto.domain;

import static org.junit.jupiter.api.Assertions.*;

import com.nexigroup.pagopa.cruscotto.domain.enumeration.SpontaneousPayments;
import org.junit.jupiter.api.Test;

class SpontaneousDrilldownTest {

    @Test
    void testEqualsAndHashCode_sameId() {
        SpontaneousDrilldown d1 = new SpontaneousDrilldown();
        d1.setId(1L);

        SpontaneousDrilldown d2 = new SpontaneousDrilldown();
        d2.setId(1L);

        assertEquals(d1, d2);
        assertEquals(d1.hashCode(), d2.hashCode());
    }

    @Test
    void testEqualsAndHashCode_differentId() {
        SpontaneousDrilldown d1 = new SpontaneousDrilldown();
        d1.setId(1L);

        SpontaneousDrilldown d2 = new SpontaneousDrilldown();
        d2.setId(2L);

        assertNotEquals(d1, d2);
        assertNotEquals(d1.hashCode(), d2.hashCode());
    }

    @Test
    void testEqualsWithDifferentType() {
        SpontaneousDrilldown d1 = new SpontaneousDrilldown();
        d1.setId(1L);

        assertNotEquals(d1, "some string");
    }

    @Test
    void testGetSpontaneousPayments_true() {
        SpontaneousDrilldown drilldown = new SpontaneousDrilldown();
        drilldown.setSpontaneousPayment(true);

        assertEquals(SpontaneousPayments.ATTIVI, drilldown.getSpontaneousPayments());
    }

    @Test
    void testGetSpontaneousPayments_false() {
        SpontaneousDrilldown drilldown = new SpontaneousDrilldown();
        drilldown.setSpontaneousPayment(false);

        assertEquals(SpontaneousPayments.NON_ATTIVI, drilldown.getSpontaneousPayments());
    }

    @Test
    void testToStringContainsFields() {
        SpontaneousDrilldown drilldown = new SpontaneousDrilldown();
        drilldown.setId(10L);
        drilldown.setPartnerId(20L);
        drilldown.setPartnerName("PartnerX");
        drilldown.setPartnerFiscalCode("PF123");
        drilldown.setStationCode("ST456");
        drilldown.setFiscalCode("FC789");
        drilldown.setSpontaneousPayment(true);

        String result = drilldown.toString();

        assertTrue(result.contains("id=10"));
        assertTrue(result.contains("partnerId=20"));
        assertTrue(result.contains("partnerName='PartnerX'"));
        assertTrue(result.contains("partnerFiscalCode='PF123'"));
        assertTrue(result.contains("stationCode='ST456'"));
        assertTrue(result.contains("fiscalCode='FC789'"));
        assertTrue(result.contains("spontaneousPayment=true"));
    }

    @Test
    void testGetterSetter() {
        SpontaneousDrilldown drilldown = new SpontaneousDrilldown();
        drilldown.setId(100L);
        drilldown.setPartnerId(200L);
        drilldown.setPartnerName("TestPartner");
        drilldown.setPartnerFiscalCode("Fiscal123");
        drilldown.setStationCode("StationABC");
        drilldown.setFiscalCode("FiscalXYZ");
        drilldown.setSpontaneousPayment(false);

        assertEquals(100L, drilldown.getId());
        assertEquals(200L, drilldown.getPartnerId());
        assertEquals("TestPartner", drilldown.getPartnerName());
        assertEquals("Fiscal123", drilldown.getPartnerFiscalCode());
        assertEquals("StationABC", drilldown.getStationCode());
        assertEquals("FiscalXYZ", drilldown.getFiscalCode());
        assertFalse(drilldown.getSpontaneousPayment());
    }
}
