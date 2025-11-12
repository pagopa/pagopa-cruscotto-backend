package com.nexigroup.pagopa.cruscotto.domain;

import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;

class KpiB2AnalyticDrillDownTest {

    @Test
    void testGettersAndSetters() {
        KpiB2AnalyticDrillDown entity = new KpiB2AnalyticDrillDown();

        Long id = 1L;
        Long kpiB2AnalyticDataId = 2L;
        Instant fromHour = Instant.now();
        Instant endHour = Instant.now().plusSeconds(3600);
        Long totalRequests = 100L;
        Long okRequests = 95L;
        Double averageTimeMs = 123.45;

        entity.setId(id);
        entity.setKpiB2AnalyticDataId(kpiB2AnalyticDataId);
        entity.setFromHour(fromHour);
        entity.setEndHour(endHour);
        entity.setTotalRequests(totalRequests);
        entity.setOkRequests(okRequests);
        entity.setAverageTimeMs(averageTimeMs);

        assertThat(entity.getId()).isEqualTo(id);
        assertThat(entity.getKpiB2AnalyticDataId()).isEqualTo(kpiB2AnalyticDataId);
        assertThat(entity.getFromHour()).isEqualTo(fromHour);
        assertThat(entity.getEndHour()).isEqualTo(endHour);
        assertThat(entity.getTotalRequests()).isEqualTo(totalRequests);
        assertThat(entity.getOkRequests()).isEqualTo(okRequests);
        assertThat(entity.getAverageTimeMs()).isEqualTo(averageTimeMs);
    }

    @Test
    void testEqualsAndHashCode() {
        KpiB2AnalyticDrillDown entity1 = new KpiB2AnalyticDrillDown();
        KpiB2AnalyticDrillDown entity2 = new KpiB2AnalyticDrillDown();

        // Same ID -> should be equal
        entity1.setId(1L);
        entity2.setId(1L);

        assertThat(entity1).isEqualTo(entity2);
        assertThat(entity1.hashCode()).isEqualTo(entity2.hashCode());

        // Different ID -> should not be equal
        entity2.setId(2L);
        assertThat(entity1).isNotEqualTo(entity2);
        assertThat(entity1.hashCode()).isNotEqualTo(entity2.hashCode());
    }

    @Test
    void testToString() {
        KpiB2AnalyticDrillDown entity = new KpiB2AnalyticDrillDown();
        entity.setId(1L);
        entity.setKpiB2AnalyticDataId(2L);

        String toString = entity.toString();

        assertThat(toString).contains("id=1");
        assertThat(toString).contains("kpiB2AnalyticDataId=2");
    }
}
