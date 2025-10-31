package com.nexigroup.pagopa.cruscotto.domain;

import static org.assertj.core.api.Assertions.assertThat;

import com.nexigroup.pagopa.cruscotto.domain.enumeration.OutcomeStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;

class KpiB5AnalyticDataTest {

    private KpiB5AnalyticData analyticData;

    @BeforeEach
    void setUp() {
        analyticData = new KpiB5AnalyticData();
    }

    @Test
    void testGettersAndSetters() {
        analyticData.setId(1L);
        assertThat(analyticData.getId()).isEqualTo(1L);

        Instance instance = new Instance();
        analyticData.setInstance(instance);
        assertThat(analyticData.getInstance()).isEqualTo(instance);

        InstanceModule module = new InstanceModule();
        analyticData.setInstanceModule(module);
        assertThat(analyticData.getInstanceModule()).isEqualTo(module);

        KpiB5DetailResult detailResult = new KpiB5DetailResult();
        analyticData.setKpiB5DetailResult(detailResult);
        assertThat(analyticData.getKpiB5DetailResult()).isEqualTo(detailResult);

        LocalDate date = LocalDate.of(2025, 10, 30);
        analyticData.setAnalysisDate(date);
        assertThat(analyticData.getAnalysisDate()).isEqualTo(date);

        analyticData.setStationsPresent(10);
        assertThat(analyticData.getStationsPresent()).isEqualTo(10);

        analyticData.setStationsWithoutSpontaneous(3);
        assertThat(analyticData.getStationsWithoutSpontaneous()).isEqualTo(3);

        BigDecimal percentage = new BigDecimal("30.5");
        analyticData.setPercentageNoSpontaneous(percentage);
        assertThat(analyticData.getPercentageNoSpontaneous()).isEqualTo(percentage);

        analyticData.setOutcome(OutcomeStatus.OK);
        assertThat(analyticData.getOutcome()).isEqualTo(OutcomeStatus.OK);
    }

    @Test
    void testEqualsAndHashCode() {
        KpiB5AnalyticData data1 = new KpiB5AnalyticData();
        data1.setId(1L);

        KpiB5AnalyticData data2 = new KpiB5AnalyticData();
        data2.setId(1L);

        KpiB5AnalyticData data3 = new KpiB5AnalyticData();
        data3.setId(2L);

        assertThat(data1).isEqualTo(data2);
        assertThat(data1.hashCode()).isEqualTo(data2.hashCode());
        assertThat(data1).isNotEqualTo(data3);
    }

    @Test
    void testToString() {
        analyticData.setId(1L);
        analyticData.setAnalysisDate(LocalDate.of(2025, 10, 30));
        analyticData.setStationsPresent(10);
        analyticData.setStationsWithoutSpontaneous(2);
        analyticData.setPercentageNoSpontaneous(new BigDecimal("20.0"));
        analyticData.setOutcome(OutcomeStatus.OK);

        String str = analyticData.toString();
        assertThat(str).contains("KpiB5AnalyticData{");
        assertThat(str).contains("id=1");
        assertThat(str).contains("analysisDate=2025-10-30");
        assertThat(str).contains("stationsPresent=10");
        assertThat(str).contains("stationsWithoutSpontaneous=2");
        assertThat(str).contains("percentageNoSpontaneous=20.0");
        assertThat(str).contains("outcome=OK");
    }
}
