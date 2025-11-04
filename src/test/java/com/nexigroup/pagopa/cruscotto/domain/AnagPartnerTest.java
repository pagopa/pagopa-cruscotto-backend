package com.nexigroup.pagopa.cruscotto.domain;

import static org.junit.jupiter.api.Assertions.*;

import com.nexigroup.pagopa.cruscotto.domain.enumeration.PartnerStatus;
import java.time.LocalDate;
import java.util.HashSet;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class AnagPartnerTest {

    private AnagPartner partner;

    @BeforeEach
    void setUp() {
        partner = new AnagPartner();
    }

    @Test
    void testDefaultValues() {
        assertNotNull(partner);
        assertFalse(partner.getQualified(), "Default 'qualified' should be false");
        assertNotNull(partner.getAnagStations());
        assertTrue(partner.getAnagStations().isEmpty());
        assertNotNull(partner.getAnagPlannedShutdowns());
        assertTrue(partner.getAnagPlannedShutdowns().isEmpty());
    }

    @Test
    void testGettersAndSetters() {
        LocalDate now = LocalDate.now();

        partner.setId(1L);
        partner.setFiscalCode("FISC123");
        partner.setName("PartnerName");
        partner.setStatus(PartnerStatus.ATTIVO);
        partner.setQualified(true);
        partner.setDeactivationDate(now.minusDays(10));
        partner.setLastAnalysisDate(now);
        partner.setAnalysisPeriodStartDate(now.minusMonths(1));
        partner.setAnalysisPeriodEndDate(now.plusMonths(1));
        partner.setStationsCount(5L);
        partner.setInstitutionsCount(3L);

        assertEquals(1L, partner.getId());
        assertEquals("FISC123", partner.getFiscalCode());
        assertEquals("PartnerName", partner.getName());
        assertEquals(PartnerStatus.ATTIVO, partner.getStatus());
        assertTrue(partner.getQualified());
        assertEquals(now.minusDays(10), partner.getDeactivationDate());
        assertEquals(now, partner.getLastAnalysisDate());
        assertEquals(now.minusMonths(1), partner.getAnalysisPeriodStartDate());
        assertEquals(now.plusMonths(1), partner.getAnalysisPeriodEndDate());
        assertEquals(5L, partner.getStationsCount());
        assertEquals(3L, partner.getInstitutionsCount());
    }

    @Test
    void testEqualsAndHashCode() {
        AnagPartner p1 = new AnagPartner();
        p1.setId(1L);

        AnagPartner p2 = new AnagPartner();
        p2.setId(1L);

        AnagPartner p3 = new AnagPartner();
        p3.setId(2L);

        assertEquals(p1, p2);
        assertNotEquals(p1, p3);
        assertNotEquals(p1, null);
        assertNotEquals(p1, new Object());
        assertEquals(p1.hashCode(), p2.hashCode());
    }

    @Test
    void testToStringContainsFields() {
        partner.setId(10L);
        partner.setFiscalCode("ABC123");
        partner.setName("PartnerName");
        partner.setStatus(PartnerStatus.NON_ATTIVO);
        partner.setQualified(false);

        String str = partner.toString();
        assertTrue(str.contains("ABC123"));
        assertTrue(str.contains("PartnerName"));
        assertTrue(str.contains("NON_ATTIVO"));
    }

    @Test
    void testRelationshipsCollections() {
        AnagStation station = new AnagStation();
        AnagPlannedShutdown shutdown = new AnagPlannedShutdown();

        partner.setAnagStations(new HashSet<>());
        partner.getAnagStations().add(station);

        partner.setAnagPlannedShutdowns(new HashSet<>());
        partner.getAnagPlannedShutdowns().add(shutdown);

        assertEquals(1, partner.getAnagStations().size());
        assertEquals(1, partner.getAnagPlannedShutdowns().size());
    }
}
