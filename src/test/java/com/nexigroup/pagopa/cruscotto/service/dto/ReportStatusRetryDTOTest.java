package com.nexigroup.pagopa.cruscotto.service.dto;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ReportStatusRetryDTOTest {

    @Test
    void testNoArgsConstructorAndSetters() {
        ReportStatusRetryDTO dto = new ReportStatusRetryDTO();

        dto.setLastError("Some error");

        assertEquals("Some error", dto.getLastError());
    }

    @Test
    void testAllArgsConstructor() {
        ReportStatusRetryDTO dto = new ReportStatusRetryDTO("Error message");

        assertEquals("Error message", dto.getLastError());
    }

    @Test
    void testEqualsAndHashCode() {
        ReportStatusRetryDTO dto1 = new ReportStatusRetryDTO("Error");
        ReportStatusRetryDTO dto2 = new ReportStatusRetryDTO("Error");
        ReportStatusRetryDTO dto3 = new ReportStatusRetryDTO("Different error");

        assertEquals(dto1, dto2);
        assertEquals(dto1.hashCode(), dto2.hashCode());

        assertNotEquals(dto1, dto3);
    }

    @Test
    void testEqualsWithSameInstance() {
        ReportStatusRetryDTO dto = new ReportStatusRetryDTO("Error");

        assertEquals(dto, dto);
    }

    @Test
    void testEqualsWithNullAndDifferentClass() {
        ReportStatusRetryDTO dto = new ReportStatusRetryDTO("Error");

        assertNotEquals(dto, null);
        assertNotEquals(dto, "Error");
    }

    @Test
    void testToString() {
        ReportStatusRetryDTO dto = new ReportStatusRetryDTO("Error");

        String toString = dto.toString();

        assertNotNull(toString);
        assertTrue(toString.contains("lastError"));
        assertTrue(toString.contains("Error"));
    }
}
