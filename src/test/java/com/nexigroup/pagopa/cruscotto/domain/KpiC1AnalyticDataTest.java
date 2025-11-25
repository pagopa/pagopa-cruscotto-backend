package com.nexigroup.pagopa.cruscotto.domain;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

class KpiC1AnalyticDataTest {

    @Test
    void testDefaultConstructor_InitialValues() {
        KpiC1AnalyticData data = new KpiC1AnalyticData();

        assertThat(data.getPositionNumber()).isEqualTo(0L);
        assertThat(data.getMessageNumber()).isEqualTo(0L);
        assertThat(data.getId()).isNull();
    }

    @Test
    void testCustomConstructor_AssignsValues() {
        Instance instance = Mockito.mock(Instance.class);
        InstanceModule module = Mockito.mock(InstanceModule.class);
        LocalDate refDate = LocalDate.of(2024, 1, 10);
        LocalDate dataDate = LocalDate.of(2024, 1, 11);

        KpiC1AnalyticData data = new KpiC1AnalyticData(
            instance, module, refDate, dataDate, 5L, 7L
        );

        assertThat(data.getInstance()).isEqualTo(instance);
        assertThat(data.getInstanceModule()).isEqualTo(module);
        assertThat(data.getReferenceDate()).isEqualTo(refDate);
        assertThat(data.getData()).isEqualTo(dataDate);
        assertThat(data.getPositionNumber()).isEqualTo(5L);
        assertThat(data.getMessageNumber()).isEqualTo(7L);
    }

    @Test
    void testConstructor_NullCountsDefaultsToZero() {
        Instance instance = Mockito.mock(Instance.class);
        InstanceModule module = Mockito.mock(InstanceModule.class);
        LocalDate refDate = LocalDate.now();

        KpiC1AnalyticData data = new KpiC1AnalyticData(
            instance, module, refDate, refDate, null, null
        );

        assertThat(data.getPositionNumber()).isEqualTo(0L);
        assertThat(data.getMessageNumber()).isEqualTo(0L);
    }

    @Test
    void testEqualsAndHashCode() {
        KpiC1AnalyticData d1 = new KpiC1AnalyticData();
        d1.setId(100L);

        KpiC1AnalyticData d2 = new KpiC1AnalyticData();
        d2.setId(100L);

        KpiC1AnalyticData d3 = new KpiC1AnalyticData();
        d3.setId(200L);

        assertThat(d1).isEqualTo(d2);
        assertThat(d1.hashCode()).isEqualTo(d2.hashCode());
        assertThat(d1).isNotEqualTo(d3);
    }

    @Test
    void testToStringContainsKeyFields() {
        KpiC1AnalyticData data = new KpiC1AnalyticData();
        data.setId(10L);
        data.setReferenceDate(LocalDate.of(2024, 2, 1));
        data.setData(LocalDate.of(2024, 2, 2));
        data.setPositionNumber(3L);
        data.setMessageNumber(6L);

        String ts = data.toString();

        assertThat(ts).contains("id=10");
        assertThat(ts).contains("positionNumber=3");
        assertThat(ts).contains("messageNumber=6");
    }

    @Test
    void testRelationsSettersAndGetters() {
        KpiC1AnalyticData data = new KpiC1AnalyticData();

        Instance instance = Mockito.mock(Instance.class);
        InstanceModule module = Mockito.mock(InstanceModule.class);
        KpiC1DetailResult detail = Mockito.mock(KpiC1DetailResult.class);

        data.setInstance(instance);
        data.setInstanceModule(module);
        data.setDetailResult(detail);

        assertThat(data.getInstance()).isEqualTo(instance);
        assertThat(data.getInstanceModule()).isEqualTo(module);
        assertThat(data.getDetailResult()).isEqualTo(detail);
    }
}
