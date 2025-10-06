package com.nexigroup.pagopa.cruscotto.job.standin;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;

@Data
public class StandInEvent {
    @JsonProperty("id")
    private String eventId;
    
    @JsonProperty("station")
    private String stationCode;
    
    private String timestamp;
    
    @JsonProperty("type")
    private String eventType;
    
    @JsonProperty("info")
    private String description;
    
    private String date;
    
    // Computed properties for backward compatibility
    public String getFiscalCode() {
        // Extract fiscal code from station if needed, or return null
        return null;
    }
    
    public int getStandInCount() {
        // For ADD_TO_STANDIN events, count as 1 Stand-In event
        // This is the standard counting for KPI B.3
        return "ADD_TO_STANDIN".equals(eventType) ? 1 : 0;
    }
    
    /**
     * Converts the timestamp string to LocalDateTime for processing
     * The API returns timestamps in ISO format with timezone (e.g., "2025-10-01T23:55:16.272717700Z")
     */
    public LocalDateTime getTimestamp() {
        if (timestamp == null || timestamp.isEmpty()) {
            return null;
        }
        try {
            // Parse ISO timestamp with timezone and convert to LocalDateTime
            return OffsetDateTime.parse(timestamp, DateTimeFormatter.ISO_OFFSET_DATE_TIME).toLocalDateTime();
        } catch (Exception e) {
            // Fallback: try parsing as LocalDateTime directly
            try {
                return LocalDateTime.parse(timestamp, DateTimeFormatter.ISO_LOCAL_DATE_TIME);
            } catch (Exception fallbackException) {
                throw new RuntimeException("Failed to parse timestamp: " + timestamp, fallbackException);
            }
        }
    }
    
    /**
     * Gets the raw timestamp string from the API
     */
    public String getTimestampString() {
        return timestamp;
    }
}