package com.nexigroup.pagopa.cruscotto.domain;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

class KpiA2AnalyticDataTest {

    @Test
    void testGetterAndSetter() {
        KpiA2AnalyticData data = new KpiA2AnalyticData();

        data.setId(1L);
        assertThat(data.getId()).isEqualTo(1L);

        Instance instance = new Instance();
        data.setInstance(instance);
        assertThat(data.getInstance()).isEqualTo(instance);

        InstanceModule instanceModule = new InstanceModule();
        data.setInstanceModule(instanceModule);
        assertThat(data.getInstanceModule()).isEqualTo(instanceModule);

        LocalDate analysisDate = LocalDate.of(2025, 10, 30);
        data.setAnalysisDate(analysisDate);
        assertThat(data.getAnalysisDate()).isEqualTo(analysisDate);

        LocalDate evaluationDate = LocalDate.of(2025, 11, 1);
        data.setEvaluationDate(evaluationDate);
        assertThat(data.getEvaluationDate()).isEqualTo(evaluationDate);

        data.setTotPayments(100L);
        assertThat(data.getTotPayments()).isEqualTo(100L);

        data.setTotIncorrectPayments(5L);
        assertThat(data.getTotIncorrectPayments()).isEqualTo(5L);

        KpiA2DetailResult result = new KpiA2DetailResult();
        data.setKpiA2DetailResult(result);
        assertThat(data.getKpiA2DetailResult()).isEqualTo(result);
    }

    @Test
    void testEqualsAndHashCode() {
        KpiA2AnalyticData data1 = new KpiA2AnalyticData();
        KpiA2AnalyticData data2 = new KpiA2AnalyticData();

        data1.setId(1L);
        data2.setId(1L);

        assertThat(data1).isEqualTo(data2);
        assertThat(data1.hashCode()).isEqualTo(data2.hashCode());

        data2.setId(2L);
        assertThat(data1).isNotEqualTo(data2);
        assertThat(data1.hashCode()).isNotEqualTo(data2.hashCode());
    }

    @Test
    void testToString() {
        KpiA2AnalyticData data = new KpiA2AnalyticData();
        data.setId(1L);
        data.setAnalysisDate(LocalDate.of(2025, 10, 30));
        data.setEvaluationDate(LocalDate.of(2025, 11, 1));
        data.setTotPayments(100L);
        data.setTotIncorrectPayments(5L);

        String str = data.toString();
        assertThat(str).contains("KpiA2AnalyticData [id=1");
        assertThat(str).contains("analysisDate=2025-10-30");
        assertThat(str).contains("evaluationDate=2025-11-01");
        assertThat(str).contains("totPayments=100");
        assertThat(str).contains("totIncorrectPayments=5");
    }
}
