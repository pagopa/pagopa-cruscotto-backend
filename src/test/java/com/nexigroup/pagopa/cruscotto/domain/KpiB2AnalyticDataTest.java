package com.nexigroup.pagopa.cruscotto.domain;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDate;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class KpiB2AnalyticDataTest {

    private KpiB2AnalyticData kpiData;

    @BeforeEach
    void setUp() {
        kpiData = new KpiB2AnalyticData();
    }

    @Test
    void testGettersAndSetters() {
        Instance instance = new Instance();
        InstanceModule instanceModule = new InstanceModule();
        AnagStation station = new AnagStation();
        KpiB2DetailResult detailResult = new KpiB2DetailResult();

        LocalDate today = LocalDate.now();

        kpiData.setId(1L);
        kpiData.setInstance(instance);
        kpiData.setInstanceModule(instanceModule);
        kpiData.setAnalysisDate(today);
        kpiData.setStation(station);
        kpiData.setMethod("POST");
        kpiData.setEvaluationDate(today.minusDays(1));
        kpiData.setTotReq(100L);
        kpiData.setReqOk(95L);
        kpiData.setReqTimeout(5L);
        kpiData.setAvgTime(200.5);
        kpiData.setKpiB2DetailResult(detailResult);

        assertThat(kpiData.getId()).isEqualTo(1L);
        assertThat(kpiData.getInstance()).isSameAs(instance);
        assertThat(kpiData.getInstanceModule()).isSameAs(instanceModule);
        assertThat(kpiData.getAnalysisDate()).isEqualTo(today);
        assertThat(kpiData.getStation()).isSameAs(station);
        assertThat(kpiData.getMethod()).isEqualTo("POST");
        assertThat(kpiData.getEvaluationDate()).isEqualTo(today.minusDays(1));
        assertThat(kpiData.getTotReq()).isEqualTo(100L);
        assertThat(kpiData.getReqOk()).isEqualTo(95L);
        assertThat(kpiData.getReqTimeout()).isEqualTo(5L);
        assertThat(kpiData.getAvgTime()).isEqualTo(200.5);
        assertThat(kpiData.getKpiB2DetailResult()).isSameAs(detailResult);
    }

    @Test
    void testEqualsAndHashCode() {
        KpiB2AnalyticData data1 = new KpiB2AnalyticData();
        data1.setId(1L);

        KpiB2AnalyticData data2 = new KpiB2AnalyticData();
        data2.setId(1L);

        KpiB2AnalyticData data3 = new KpiB2AnalyticData();
        data3.setId(2L);

        // equals
        assertThat(data1)
            .isEqualTo(data2)
            .isNotEqualTo(data3)
            .isNotEqualTo(null)
            .isNotEqualTo(new Object());

        // hashCode
        assertThat(data1.hashCode()).isEqualTo(data2.hashCode());
        assertThat(data1.hashCode()).isNotEqualTo(data3.hashCode());
    }

    @Test
    void testToStringContainsAllFields() {
        kpiData.setId(1L);
        kpiData.setMethod("POST");
        kpiData.setTotReq(100L);
        kpiData.setReqOk(90L);
        kpiData.setReqTimeout(10L);
        kpiData.setAvgTime(250.0);
        kpiData.setAnalysisDate(LocalDate.of(2025, 1, 1));
        kpiData.setEvaluationDate(LocalDate.of(2025, 1, 2));
        kpiData.setInstance(new Instance());
        kpiData.setInstanceModule(new InstanceModule());
        kpiData.setStation(new AnagStation());
        kpiData.setKpiB2DetailResult(new KpiB2DetailResult());

        String result = kpiData.toString();

        assertThat(result).contains("KpiB2AnalyticData{");
        assertThat(result).contains("id=1");
        assertThat(result).contains("method='POST'");
        assertThat(result).contains("totReq=100");
        assertThat(result).contains("reqOk=90");
        assertThat(result).contains("reqTimeout=10");
        assertThat(result).contains("avgTime=250.0");
    }
}
