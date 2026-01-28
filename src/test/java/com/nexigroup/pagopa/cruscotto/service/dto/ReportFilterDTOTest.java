package com.nexigroup.pagopa.cruscotto.service.dto;

import static org.junit.jupiter.api.Assertions.*;

import java.io.*;

import org.junit.jupiter.api.Test;

class ReportFilterDTOTest {

    @Test
    void testDefaultConstructor() {
        ReportFilterDTO dto = new ReportFilterDTO();

        assertNull(dto.getInstanceId());
        assertNull(dto.getStatus());
    }

    @Test
    void testAllArgsConstructor() {
        Long instanceId = 10L;
        String status = "COMPLETED";

        ReportFilterDTO dto = new ReportFilterDTO(instanceId, status);

        assertEquals(instanceId, dto.getInstanceId());
        assertEquals(status, dto.getStatus());
    }

    @Test
    void testSettersAndGetters() {
        ReportFilterDTO dto = new ReportFilterDTO();

        dto.setInstanceId(20L);
        dto.setStatus("FAILED");

        assertEquals(20L, dto.getInstanceId());
        assertEquals("FAILED", dto.getStatus());
    }

    @Test
    void testToString() {
        ReportFilterDTO dto = new ReportFilterDTO(5L, "IN_PROGRESS");

        String result = dto.toString();

        assertTrue(result.contains("instanceId=5"));
        assertTrue(result.contains("status='IN_PROGRESS'"));
    }

    @Test
    void testSerializable() throws IOException, ClassNotFoundException {
        ReportFilterDTO original = new ReportFilterDTO(1L, "OK");

        // Serialize
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        ObjectOutputStream out = new ObjectOutputStream(bos);
        out.writeObject(original);
        out.flush();

        // Deserialize
        ByteArrayInputStream bis = new ByteArrayInputStream(bos.toByteArray());
        ObjectInputStream in = new ObjectInputStream(bis);
        ReportFilterDTO deserialized = (ReportFilterDTO) in.readObject();

        assertEquals(original.getInstanceId(), deserialized.getInstanceId());
        assertEquals(original.getStatus(), deserialized.getStatus());
    }
}
