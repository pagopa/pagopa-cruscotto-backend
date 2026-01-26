package com.nexigroup.pagopa.cruscotto.domain;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class IoDrilldownTest {

    @Test
    void defaultConstructor_setsDefaultCounts() {
        IoDrilldown d = new IoDrilldown();
        assertNotNull(d);
        assertEquals(0L, d.getPositionsCount());
        assertEquals(0L, d.getMessagesCount());
        assertNull(d.getId());
    }

    @Test
    void parameterizedConstructor_andGetters() {
        LocalDate ref = LocalDate.of(2025, 1, 1);
        LocalDate data = LocalDate.of(2025, 1, 2);
        IoDrilldown d = new IoDrilldown(null, null, null, ref, data,
            "CF_INST", "CF_PART", 5L, 2L, 40.0, Boolean.TRUE);

        assertEquals(ref, d.getReferenceDate());
        assertEquals(data, d.getDataDate());
        assertEquals("CF_INST", d.getCfInstitution());
        assertEquals("CF_PART", d.getCfPartner());
        assertEquals(5L, d.getPositionsCount());
        assertEquals(2L, d.getMessagesCount());
        assertEquals(40.0, d.getPercentage());
        assertTrue(d.getMeetsTolerance());
    }

    @Test
    void equals_and_hashCode_behaviour() {
        IoDrilldown a = new IoDrilldown();
        IoDrilldown b = new IoDrilldown();

        a.setId(10L);
        b.setId(10L);
        assertEquals(a, b);
        assertEquals(a.hashCode(), b.hashCode());

        b.setId(11L);
        assertNotEquals(a, b);

        assertNotEquals(a, null);
        assertNotEquals(a, "some string");
        assertEquals(a, a); // reflexive
    }

    @Test
    void toString_containsKeyFields() {
        IoDrilldown d = new IoDrilldown();
        d.setCfInstitution("INST-X");
        d.setCfPartner("PART-Y");
        d.setDataDate(LocalDate.of(2025, 2, 3));
        d.setPositionsCount(7L);
        d.setMessagesCount(1L);
        d.setPercentage(14.2857);
        d.setMeetsTolerance(Boolean.FALSE);

        String s = d.toString();
        assertTrue(s.contains("INST-X"));
        assertTrue(s.contains("PART-Y"));
        assertTrue(s.contains("dataDate="));
        assertTrue(s.contains("positionsCount=7"));
        assertTrue(s.contains("messagesCount=1"));
        assertTrue(s.contains("percentage="));
        assertTrue(s.contains("meetsTolerance=false"));
    }
}
