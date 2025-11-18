package com.nexigroup.pagopa.cruscotto.domain;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDate;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

class KpiB9AnalyticDataTest {

    @Test
    void testGettersAndSetters() {
        KpiB9AnalyticData data = new KpiB9AnalyticData();

        Instance instance = Mockito.mock(Instance.class);
        InstanceModule module = Mockito.mock(InstanceModule.class);
        AnagStation station = Mockito.mock(AnagStation.class);
        KpiB9DetailResult detailResult = Mockito.mock(KpiB9DetailResult.class);

        data.setId(1L);
        data.setInstance(instance);
        data.setInstanceModule(module);
        data.setAnalysisDate(LocalDate.of(2025, 1, 1));
        data.setStation(station);
        data.setEvaluationDate(LocalDate.of(2025, 2, 1));
        data.setTotRes(100L);
        data.setResOk(80L);
        data.setResKoReal(15L);
        data.setResKoValid(5L);
        data.setKpiB9DetailResult(detailResult);

        assertThat(data.getId()).isEqualTo(1L);
        assertThat(data.getInstance()).isEqualTo(instance);
        assertThat(data.getInstanceModule()).isEqualTo(module);
        assertThat(data.getAnalysisDate()).isEqualTo(LocalDate.of(2025, 1, 1));
        assertThat(data.getStation()).isEqualTo(station);
        assertThat(data.getEvaluationDate()).isEqualTo(LocalDate.of(2025, 2, 1));
        assertThat(data.getTotRes()).isEqualTo(100L);
        assertThat(data.getResOk()).isEqualTo(80L);
        assertThat(data.getResKoReal()).isEqualTo(15L);
        assertThat(data.getResKoValid()).isEqualTo(5L);
        assertThat(data.getKpiB9DetailResult()).isEqualTo(detailResult);
    }

    @Test
    void testEqualsAndHashCode() {
        KpiB9AnalyticData data1 = new KpiB9AnalyticData();
        data1.setId(1L);

        KpiB9AnalyticData data2 = new KpiB9AnalyticData();
        data2.setId(1L);

        KpiB9AnalyticData data3 = new KpiB9AnalyticData();
        data3.setId(2L);

        assertThat(data1).isEqualTo(data2);
        assertThat(data1).isNotEqualTo(data3);
        assertThat(data1.hashCode()).isEqualTo(data2.hashCode());
        assertThat(data1.hashCode()).isNotEqualTo(data3.hashCode());
    }

    @Test
    void testToString() {
        KpiB9AnalyticData data = new KpiB9AnalyticData();
        data.setId(1L);
        data.setTotRes(100L);
        data.setResOk(80L);
        data.setResKoReal(15L);
        data.setResKoValid(5L);

        String toString = data.toString();

        assertThat(toString).contains("KpiB9AnalyticData");
        assertThat(toString).contains("id=1");
        assertThat(toString).contains("totRes=100");
        assertThat(toString).contains("resOk=80");
        assertThat(toString).contains("resKoReal=15");
        assertThat(toString).contains("resKoValid=5");
    }
}
