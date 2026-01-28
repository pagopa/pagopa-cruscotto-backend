package com.nexigroup.pagopa.cruscotto.service.dto;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ReportStatusFailedDTOTest {

    @Test
    void testNoArgsConstructorAndSetters() {
        ReportStatusFailedDTO dto = new ReportStatusFailedDTO();

        dto.setLastError("Some error");
        dto.setCanRetry(true);

        assertEquals("Some error", dto.getLastError());
        assertTrue(dto.getCanRetry());
    }

    @Test
    void testAllArgsConstructorAndGetters() {
        ReportStatusFailedDTO dto =
            new ReportStatusFailedDTO("Failure reason", false);

        assertEquals("Failure reason", dto.getLastError());
        assertFalse(dto.getCanRetry());
    }

    @Test
    void testEqualsAndHashCode() {
        ReportStatusFailedDTO dto1 =
            new ReportStatusFailedDTO("Error", true);
        ReportStatusFailedDTO dto2 =
            new ReportStatusFailedDTO("Error", true);
        ReportStatusFailedDTO dto3 =
            new ReportStatusFailedDTO("Different error", false);

        assertEquals(dto1, dto2);
        assertEquals(dto1.hashCode(), dto2.hashCode());

        assertNotEquals(dto1, dto3);
    }

    @Test
    void testEqualsWithNullAndDifferentClass() {
        ReportStatusFailedDTO dto =
            new ReportStatusFailedDTO("Error", true);

        assertNotEquals(dto, null);
        assertNotEquals(dto, "not a dto");
    }

    @Test
    void testToString() {
        ReportStatusFailedDTO dto =
            new ReportStatusFailedDTO("Error", true);

        String toString = dto.toString();

        assertNotNull(toString);
        assertTrue(toString.contains("lastError"));
        assertTrue(toString.contains("canRetry"));
    }
}
