package com.nexigroup.pagopa.cruscotto.domain;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class TaxonomyTest {

    private Taxonomy taxonomy;

    @BeforeEach
    void setUp() {
        taxonomy = new Taxonomy();
    }

    @Test
    void testGettersAndSetters() {
        taxonomy.setId(1L);
        taxonomy.setInstitutionTypeCode("INST");
        taxonomy.setInstitutionType("Institution");
        taxonomy.setAreaProgressiveCode("AR001");
        taxonomy.setAreaName("AreaName");
        taxonomy.setAreaDescription("AreaDescription");
        taxonomy.setServiceTypeCode("SRV01");
        taxonomy.setServiceType("ServiceType");
        taxonomy.setServiceTypeDescription("ServiceTypeDescription");
        taxonomy.setVersion("v1");
        taxonomy.setReasonCollection("RC01");
        taxonomy.setTakingsIdentifier("TK1234567890");
        taxonomy.setValidityStartDate(LocalDate.of(2025, 1, 1));
        taxonomy.setValidityEndDate(LocalDate.of(2025, 12, 31));

        assertEquals(1L, taxonomy.getId());
        assertEquals("INST", taxonomy.getInstitutionTypeCode());
        assertEquals("Institution", taxonomy.getInstitutionType());
        assertEquals("AR001", taxonomy.getAreaProgressiveCode());
        assertEquals("AreaName", taxonomy.getAreaName());
        assertEquals("AreaDescription", taxonomy.getAreaDescription());
        assertEquals("SRV01", taxonomy.getServiceTypeCode());
        assertEquals("ServiceType", taxonomy.getServiceType());
        assertEquals("ServiceTypeDescription", taxonomy.getServiceTypeDescription());
        assertEquals("v1", taxonomy.getVersion());
        assertEquals("RC01", taxonomy.getReasonCollection());
        assertEquals("TK1234567890", taxonomy.getTakingsIdentifier());
        assertEquals(LocalDate.of(2025, 1, 1), taxonomy.getValidityStartDate());
        assertEquals(LocalDate.of(2025, 12, 31), taxonomy.getValidityEndDate());
    }

    @Test
    void testEqualsAndHashCode() {
        Taxonomy t1 = new Taxonomy();
        t1.setId(1L);

        Taxonomy t2 = new Taxonomy();
        t2.setId(1L);

        Taxonomy t3 = new Taxonomy();
        t3.setId(2L);

        assertEquals(t1, t2);
        assertNotEquals(t1, t3);
        assertNotEquals(t1, null);
        assertNotEquals(t1, new Object());
        assertEquals(t1.hashCode(), t2.hashCode());
    }

    @Test
    void testEqualsWithNullId() {
        Taxonomy t1 = new Taxonomy();
        Taxonomy t2 = new Taxonomy();
        assertNotEquals(t1, t2);
    }

    @Test
    void testToStringContainsFields() {
        taxonomy.setId(5L);
        taxonomy.setInstitutionType("Institution");
        taxonomy.setTakingsIdentifier("TK9999999999");

        String result = taxonomy.toString();
        assertNotNull(result);
        assertTrue(result.contains("Institution"));
        assertTrue(result.contains("TK9999999999"));
    }
}
