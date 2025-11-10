package com.nexigroup.pagopa.cruscotto.domain;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

class KpiA1AnalyticDataTest {

    private KpiA1AnalyticData analyticData;
    private KpiA1AnalyticData otherAnalyticData;

    @BeforeEach
    void setUp() {
        analyticData = new KpiA1AnalyticData();
        otherAnalyticData = new KpiA1AnalyticData();
    }

    @Test
    void testGettersAndSetters() {
        // Setting fields
        analyticData.setId(1L);
        analyticData.setAnalysisDate(LocalDate.of(2025, 10, 30));
        analyticData.setEvaluationDate(LocalDate.of(2025, 11, 1));
        analyticData.setMethod("TestMethod");
        analyticData.setTotReq(100L);
        analyticData.setReqOk(80L);
        analyticData.setReqTimeoutReal(10L);
        analyticData.setReqTimeoutValid(5L);

        Instance instance = new Instance();
        InstanceModule module = new InstanceModule();
        AnagStation station = new AnagStation();
        KpiA1DetailResult detailResult = new KpiA1DetailResult();

        analyticData.setInstance(instance);
        analyticData.setInstanceModule(module);
        analyticData.setStation(station);
        analyticData.setKpiA1DetailResult(detailResult);

        // Assertions
        assertThat(analyticData.getId()).isEqualTo(1L);
        assertThat(analyticData.getAnalysisDate()).isEqualTo(LocalDate.of(2025, 10, 30));
        assertThat(analyticData.getEvaluationDate()).isEqualTo(LocalDate.of(2025, 11, 1));
        assertThat(analyticData.getMethod()).isEqualTo("TestMethod");
        assertThat(analyticData.getTotReq()).isEqualTo(100L);
        assertThat(analyticData.getReqOk()).isEqualTo(80L);
        assertThat(analyticData.getReqTimeoutReal()).isEqualTo(10L);
        assertThat(analyticData.getReqTimeoutValid()).isEqualTo(5L);
        assertThat(analyticData.getInstance()).isEqualTo(instance);
        assertThat(analyticData.getInstanceModule()).isEqualTo(module);
        assertThat(analyticData.getStation()).isEqualTo(station);
        assertThat(analyticData.getKpiA1DetailResult()).isEqualTo(detailResult);
    }

    @Test
    void testEqualsAndHashCode() {
        analyticData.setId(1L);
        otherAnalyticData.setId(1L);

        // Should be equal because IDs match
        assertThat(analyticData).isEqualTo(otherAnalyticData);
        assertThat(analyticData.hashCode()).isEqualTo(otherAnalyticData.hashCode());

        otherAnalyticData.setId(2L);

        // Should not be equal now
        assertThat(analyticData).isNotEqualTo(otherAnalyticData);
        assertThat(analyticData.hashCode()).isNotEqualTo(otherAnalyticData.hashCode());
    }

    @Test
    void testToStringContainsAllFields() {
        analyticData.setId(1L);
        analyticData.setMethod("TestMethod");

        String toString = analyticData.toString();

        assertThat(toString).contains("id=1");
        assertThat(toString).contains("method='TestMethod'");
    }
}
