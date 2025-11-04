package com.nexigroup.pagopa.cruscotto.service.dto;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class PagopaAPILogDTOTest {

    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        // Registriamo il modulo per Java 8 date/time
        objectMapper.registerModule(new JavaTimeModule());
        // Serializzare le date come stringhe ISO, non come timestamp
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    }

    @Test
    void testGetterSetter() {
        PagopaAPILogDTO dto = new PagopaAPILogDTO();

        dto.setCfPartner("ABC123");
        dto.setDate(LocalDate.of(2025, 10, 29));
        dto.setStation("Station1");
        dto.setCfEnte("ENTE456");
        dto.setApi("PAYMENT_API");
        dto.setTotReq(100);
        dto.setReqOk(80);
        dto.setReqKo(20);

        assertEquals("ABC123", dto.getCfPartner());
        assertEquals(LocalDate.of(2025, 10, 29), dto.getDate());
        assertEquals("Station1", dto.getStation());
        assertEquals("ENTE456", dto.getCfEnte());
        assertEquals("PAYMENT_API", dto.getApi());
        assertEquals(100, dto.getTotReq());
        assertEquals(80, dto.getReqOk());
        assertEquals(20, dto.getReqKo());
    }

    @Test
    void testAllArgsConstructor() {
        LocalDate date = LocalDate.of(2025, 10, 29);
        PagopaAPILogDTO dto = new PagopaAPILogDTO(
            "ABC123",
            date,
            "Station1",
            "ENTE456",
            "PAYMENT_API",
            100,
            80,
            20
        );

        assertEquals("ABC123", dto.getCfPartner());
        assertEquals(date, dto.getDate());
        assertEquals("Station1", dto.getStation());
        assertEquals("ENTE456", dto.getCfEnte());
        assertEquals("PAYMENT_API", dto.getApi());
        assertEquals(100, dto.getTotReq());
        assertEquals(80, dto.getReqOk());
        assertEquals(20, dto.getReqKo());
    }

    @Test
    void testJsonSerialization() throws Exception {
        LocalDate date = LocalDate.of(2025, 10, 29);
        PagopaAPILogDTO dto = new PagopaAPILogDTO(
            "ABC123",
            date,
            "Station1",
            "ENTE456",
            "PAYMENT_API",
            100,
            80,
            20
        );

        String json = objectMapper.writeValueAsString(dto);

        assertTrue(json.contains("\"partnerFiscalCode\":\"ABC123\""));
        assertTrue(json.contains("\"dataDate\":\"2025-10-29\""));
        assertTrue(json.contains("\"stationCode\":\"Station1\""));
        assertTrue(json.contains("\"fiscalCode\":\"ENTE456\""));
        assertTrue(json.contains("\"api\":\"PAYMENT_API\""));
        assertTrue(json.contains("\"totalRequests\":100"));
        assertTrue(json.contains("\"okRequests\":80"));
        assertTrue(json.contains("\"koRequests\":20"));
    }

    @Test
    void testJsonDeserialization() throws Exception {
        String json = """
                {
                  "partnerFiscalCode": "ABC123",
                  "dataDate": "2025-10-29",
                  "stationCode": "Station1",
                  "fiscalCode": "ENTE456",
                  "api": "PAYMENT_API",
                  "totalRequests": 100,
                  "okRequests": 80,
                  "koRequests": 20
                }
                """;

        PagopaAPILogDTO dto = objectMapper.readValue(json, PagopaAPILogDTO.class);

        assertEquals("ABC123", dto.getCfPartner());
        assertEquals(LocalDate.of(2025, 10, 29), dto.getDate());
        assertEquals("Station1", dto.getStation());
        assertEquals("ENTE456", dto.getCfEnte());
        assertEquals("PAYMENT_API", dto.getApi());
        assertEquals(100, dto.getTotReq());
        assertEquals(80, dto.getReqOk());
        assertEquals(20, dto.getReqKo());
    }
}
