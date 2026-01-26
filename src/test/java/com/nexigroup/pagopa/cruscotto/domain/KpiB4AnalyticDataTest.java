package com.nexigroup.pagopa.cruscotto.domain;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;

class KpiB4AnalyticDataTest {

    @Test
    void testGettersAndSetters() {
        KpiB4AnalyticData data = new KpiB4AnalyticData();
        data.setId(1L);
        data.setInstanceId(100L);
        data.setAnalysisDate(LocalDate.of(2025, 1, 1));
        data.setEvaluationDate(LocalDate.of(2025, 1, 2));
        data.setNumRequestGpd(50L);
        data.setNumRequestCp(20L);

        assertThat(data.getId()).isEqualTo(1L);
        assertThat(data.getInstanceId()).isEqualTo(100L);
        assertThat(data.getAnalysisDate()).isEqualTo(LocalDate.of(2025, 1, 1));
        assertThat(data.getEvaluationDate()).isEqualTo(LocalDate.of(2025, 1, 2));
        assertThat(data.getNumRequestGpd()).isEqualTo(50L);
        assertThat(data.getNumRequestCp()).isEqualTo(20L);
    }

    @Test
    void testEqualsAndHashCode() {
        KpiB4AnalyticData data1 = new KpiB4AnalyticData();
        data1.setId(1L);

        KpiB4AnalyticData data2 = new KpiB4AnalyticData();
        data2.setId(1L);

        KpiB4AnalyticData data3 = new KpiB4AnalyticData();
        data3.setId(2L);

        assertThat(data1).isEqualTo(data2);
        assertThat(data1).isNotEqualTo(data3);
        assertThat(data1.hashCode()).isEqualTo(data2.hashCode());
    }

    @Test
    void testToString() {
        KpiB4AnalyticData data = new KpiB4AnalyticData();
        data.setId(1L);
        data.setInstanceId(100L);
        data.setAnalysisDate(LocalDate.of(2025, 1, 1));
        data.setEvaluationDate(LocalDate.of(2025, 1, 2));
        data.setNumRequestGpd(50L);
        data.setNumRequestCp(20L);

        String toString = data.toString();
        assertThat(toString).contains("id=1");
        assertThat(toString).contains("instanceId=100");
        assertThat(toString).contains("analysisDate='2025-01-01'");
        assertThat(toString).contains("evaluationDate='2025-01-02'");
        assertThat(toString).contains("numRequestGpd=50");
        assertThat(toString).contains("numRequestCp=20");
    }

    @Test
    void testEqualsWithNullId() {
        KpiB4AnalyticData data1 = new KpiB4AnalyticData();
        KpiB4AnalyticData data2 = new KpiB4AnalyticData();

        assertThat(data1).isNotEqualTo(data2);
    }
}
