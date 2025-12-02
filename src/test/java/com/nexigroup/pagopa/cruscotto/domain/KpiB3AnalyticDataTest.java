package com.nexigroup.pagopa.cruscotto.domain;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

class KpiB3AnalyticDataTest {

    @Test
    void testGettersAndSetters() {
        KpiB3AnalyticData data = new KpiB3AnalyticData();

        Long id = 1L;
        String eventId = "EVT123";
        String eventType = "ERROR";
        LocalDateTime timestamp = LocalDateTime.now();
        Integer standInCount = 5;

        data.setId(id);
        data.setEventId(eventId);
        data.setEventType(eventType);
        data.setEventTimestamp(timestamp);
        data.setStandInCount(standInCount);

        assertThat(data.getId()).isEqualTo(id);
        assertThat(data.getEventId()).isEqualTo(eventId);
        assertThat(data.getEventType()).isEqualTo(eventType);
        assertThat(data.getEventTimestamp()).isEqualTo(timestamp);
        assertThat(data.getStandInCount()).isEqualTo(standInCount);
    }

    @Test
    void testEqualsAndHashCode() {
        KpiB3AnalyticData data1 = new KpiB3AnalyticData();
        data1.setId(1L);

        KpiB3AnalyticData data2 = new KpiB3AnalyticData();
        data2.setId(1L);

        KpiB3AnalyticData data3 = new KpiB3AnalyticData();
        data3.setId(2L);

        assertThat(data1).isEqualTo(data2);
        assertThat(data1).isNotEqualTo(data3);
        assertThat(data1.hashCode()).isEqualTo(31); // fixed hashCode
    }

    @Test
    void testEqualsWithNullId() {
        KpiB3AnalyticData data1 = new KpiB3AnalyticData();
        KpiB3AnalyticData data2 = new KpiB3AnalyticData();

        assertThat(data1).isNotEqualTo(data2);
    }

    @Test
    void testToString() {
        KpiB3AnalyticData data = new KpiB3AnalyticData();
        data.setId(10L);
        data.setEventId("EVT999");
        data.setEventType("INFO");
        data.setEventTimestamp(LocalDateTime.of(2025, 1, 1, 12, 0));
        data.setStandInCount(3);

        String result = data.toString();

        assertThat(result).contains("id=10");
        assertThat(result).contains("eventId='EVT999'");
        assertThat(result).contains("eventType='INFO'");
        assertThat(result).contains("standInCount=3");
    }
}
