package com.nexigroup.pagopa.cruscotto.domain;

import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;

class PagoPaRecordedTimeoutTest {

    @Test
    void testGettersAndSetters() {
        PagoPaRecordedTimeout timeout = new PagoPaRecordedTimeout();

        Instant start = Instant.now();
        Instant end = start.plusSeconds(60);

        timeout.setId(1L);
        timeout.setCfPartner("partner123");
        timeout.setStation("stationABC");
        timeout.setMethod("METHOD1");
        timeout.setStartDate(start);
        timeout.setEndDate(end);
        timeout.setTotReq(100L);
        timeout.setReqOk(80L);
        timeout.setReqTimeout(20L);
        timeout.setAvgTime(5.5);

        assertThat(timeout.getId()).isEqualTo(1L);
        assertThat(timeout.getCfPartner()).isEqualTo("partner123");
        assertThat(timeout.getStation()).isEqualTo("stationABC");
        assertThat(timeout.getMethod()).isEqualTo("METHOD1");
        assertThat(timeout.getStartDate()).isEqualTo(start);
        assertThat(timeout.getEndDate()).isEqualTo(end);
        assertThat(timeout.getTotReq()).isEqualTo(100L);
        assertThat(timeout.getReqOk()).isEqualTo(80L);
        assertThat(timeout.getReqTimeout()).isEqualTo(20L);
        assertThat(timeout.getAvgTime()).isEqualTo(5.5);
    }

    @Test
    void testEqualsAndHashCode() {
        Instant start = Instant.now();
        Instant end = start.plusSeconds(60);

        PagoPaRecordedTimeout timeout1 = new PagoPaRecordedTimeout();
        timeout1.setCfPartner("partner123");
        timeout1.setStation("stationABC");
        timeout1.setMethod("METHOD1");
        timeout1.setStartDate(start);
        timeout1.setEndDate(end);

        PagoPaRecordedTimeout timeout2 = new PagoPaRecordedTimeout();
        timeout2.setCfPartner("partner123");
        timeout2.setStation("stationABC");
        timeout2.setMethod("METHOD1");
        timeout2.setStartDate(start);
        timeout2.setEndDate(end);

        assertThat(timeout1).
            isEqualTo(timeout2)
            .hasSameHashCodeAs(timeout2);

        // Change a field and verify inequality
        timeout2.setMethod("METHOD2");
        assertThat(timeout1).isNotEqualTo(timeout2);
    }

    @Test
    void testToString() {
        PagoPaRecordedTimeout timeout = new PagoPaRecordedTimeout();
        timeout.setCfPartner("partner123");
        timeout.setStation("stationABC");
        timeout.setMethod("METHOD1");
        timeout.setStartDate(Instant.parse("2025-10-07T10:00:00Z"));
        timeout.setEndDate(Instant.parse("2025-10-07T10:01:00Z"));
        timeout.setTotReq(100L);
        timeout.setReqOk(80L);
        timeout.setReqTimeout(20L);
        timeout.setAvgTime(5.5);

        String expected = "PagoPaRecorderTimeout{cfPartner='partner123', station='stationABC', method='METHOD1', startDate=2025-10-07T10:00:00Z, endDate=2025-10-07T10:01:00Z, totReq=100, reqOk=80, reqTimeout=20, avgTime=5.5}";
        assertThat(timeout).hasToString(expected);
    }
}
