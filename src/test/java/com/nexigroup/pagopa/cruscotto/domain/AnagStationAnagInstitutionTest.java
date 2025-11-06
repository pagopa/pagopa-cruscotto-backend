package com.nexigroup.pagopa.cruscotto.domain;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class AnagStationAnagInstitutionTest {

    @Test
    void testEqualsAndHashCode() {
        AnagStation station1 = new AnagStation();
        station1.setId(1L);
        AnagInstitution institution1 = new AnagInstitution();
        institution1.setId(100L);

        AnagStation station2 = new AnagStation();
        station2.setId(1L);
        AnagInstitution institution2 = new AnagInstitution();
        institution2.setId(100L);

        AnagStationAnagInstitution rel1 = new AnagStationAnagInstitution();
        rel1.setAnagStation(station1);
        rel1.setAnagInstitution(institution1);
        rel1.setAca(true);
        rel1.setStandin(false);

        AnagStationAnagInstitution rel2 = new AnagStationAnagInstitution();
        rel2.setAnagStation(station2);
        rel2.setAnagInstitution(institution2);
        rel2.setAca(true);
        rel2.setStandin(false);

        assertEquals(rel2, rel1);
        assertEquals(rel2.hashCode(), rel1.hashCode());
    }

    @Test
    void testEqualsWithDifferentObjects() {
        AnagStation station1 = new AnagStation();
        station1.setId(1L);
        AnagInstitution institution1 = new AnagInstitution();
        institution1.setId(100L);

        AnagStationAnagInstitution rel1 = new AnagStationAnagInstitution();
        rel1.setAnagStation(station1);
        rel1.setAnagInstitution(institution1);

        AnagStationAnagInstitution rel2 = new AnagStationAnagInstitution();
        rel2.setAnagStation(null);
        rel2.setAnagInstitution(institution1);

        assertNotEquals(rel2, rel1);
        assertNotEquals(rel2.hashCode(), rel1.hashCode());
    }

    @Test
    void testGettersAndSetters() {
        AnagStation station = new AnagStation();
        AnagInstitution institution = new AnagInstitution();

        AnagStationAnagInstitution rel = new AnagStationAnagInstitution();
        rel.setAnagStation(station);
        rel.setAnagInstitution(institution);
        rel.setAca(true);
        rel.setStandin(false);

        assertSame(station, rel.getAnagStation());
        assertSame(institution, rel.getAnagInstitution());
        assertTrue(rel.getAca());
        assertFalse(rel.getStandin());
    }

    @Test
    void testEqualsWithNullAndDifferentClass() {
        AnagStationAnagInstitution rel = new AnagStationAnagInstitution();
        assertNotEquals(null, rel);
        assertNotEquals("someString", rel);
    }
}
