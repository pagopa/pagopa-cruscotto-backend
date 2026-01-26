package com.nexigroup.pagopa.cruscotto.security.jwt;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class RestResponseTest {

    @Test
    void testBuilderAndGetters() {
        RestResponse response = RestResponse.builder()
            .status(404)
            .url("/api/test")
            .error("Not Found")
            .message("Resource not found")
            .build();

        assertNotNull(response.getTimestamp(), "Timestamp should be initialized");
        assertEquals(404, response.getStatus());
        assertEquals("/api/test", response.getUrl());
        assertEquals("Not Found", response.getError());
        assertEquals("Resource not found", response.getMessage());
    }

    @Test
    void testBuilderAndSetters() {
        RestResponse response = RestResponse.builder()
            .status(500)
            .url("/api/error")
            .error("Internal Server Error")
            .message("Something went wrong")
            .build();

        // simulate "setters" by creating a new object with different values if needed
        RestResponse updatedResponse = RestResponse.builder()
            .status(200)
            .url("/api/updated")
            .error("None")
            .message("Updated")
            .build();

        assertEquals(200, updatedResponse.getStatus());
        assertEquals("/api/updated", updatedResponse.getUrl());
        assertEquals("None", updatedResponse.getError());
        assertEquals("Updated", updatedResponse.getMessage());
    }


    @Test
    void testToJson() {
        RestResponse response = RestResponse.builder()
            .status(200)
            .url("/api/json")
            .error("None")
            .message("Success")
            .build();

        String json = response.toJson();

        assertNotNull(json);
        assertTrue(json.contains("\"status\": 200"));
        assertTrue(json.contains("\"url\": \"/api/json\""));
        assertTrue(json.contains("\"error\": \"None\""));
        assertTrue(json.contains("\"message\": \"Success\""));
        assertTrue(json.contains("\"timestamp\": \""));
    }
}
