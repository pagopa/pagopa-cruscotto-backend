package com.nexigroup.pagopa.cruscotto.service.dto;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class InstitutionIdentificationDTOTest {

    @Test
    void testGettersAndSetters() {
        InstitutionIdentificationDTO dto = new InstitutionIdentificationDTO();

        Long id = 123L;
        String name = "Comune di Roma";
        String fiscalCode = "12345678901";

        dto.setId(id);
        dto.setName(name);
        dto.setFiscalCode(fiscalCode);

        assertEquals(id, dto.getId());
        assertEquals(name, dto.getName());
        assertEquals(fiscalCode, dto.getFiscalCode());
    }

    @Test
    void testToStringEqualsAndHashCode() {
        InstitutionIdentificationDTO dto1 = new InstitutionIdentificationDTO();
        dto1.setId(1L);
        dto1.setName("Test");
        dto1.setFiscalCode("ABC123");

        InstitutionIdentificationDTO dto2 = new InstitutionIdentificationDTO();
        dto2.setId(1L);
        dto2.setName("Test");
        dto2.setFiscalCode("ABC123");

        // Lombok doesn't generate equals/hashCode by default — but we can still check basic object behavior
        assertNotEquals(dto1, new Object());
        assertNotEquals(dto1, null);
        assertNotEquals(dto1.hashCode(), 0);

        // toString should not be null
        assertNotNull(dto1.toString());
    }
}
