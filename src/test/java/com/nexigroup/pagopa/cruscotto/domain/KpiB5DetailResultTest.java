package com.nexigroup.pagopa.cruscotto.domain;

import com.nexigroup.pagopa.cruscotto.domain.enumeration.OutcomeStatus;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

class KpiB5DetailResultTest {

    @Test
    void testGettersAndSetters() {
        KpiB5DetailResult result = new KpiB5DetailResult();
        Instance instance = new Instance();
        InstanceModule module = new InstanceModule();
        KpiB5Result kpiB5Result = new KpiB5Result();
        LocalDate analysisDate = LocalDate.now();
        Integer stationsPresent = 10;
        Integer stationsWithoutSpontaneous = 2;
        BigDecimal percentage = BigDecimal.valueOf(20.5);
        OutcomeStatus outcome = OutcomeStatus.OK;

        result.setId(1L);
        result.setInstance(instance);
        result.setInstanceModule(module);
        result.setKpiB5Result(kpiB5Result);
        result.setAnalysisDate(analysisDate);
        result.setStationsPresent(stationsPresent);
        result.setStationsWithoutSpontaneous(stationsWithoutSpontaneous);
        result.setPercentageNoSpontaneous(percentage);
        result.setOutcome(outcome);

        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getInstance()).isEqualTo(instance);
        assertThat(result.getInstanceModule()).isEqualTo(module);
        assertThat(result.getKpiB5Result()).isEqualTo(kpiB5Result);
        assertThat(result.getAnalysisDate()).isEqualTo(analysisDate);
        assertThat(result.getStationsPresent()).isEqualTo(stationsPresent);
        assertThat(result.getStationsWithoutSpontaneous()).isEqualTo(stationsWithoutSpontaneous);
        assertThat(result.getPercentageNoSpontaneous()).isEqualTo(percentage);
        assertThat(result.getOutcome()).isEqualTo(outcome);
    }

    @Test
    void testEqualsAndHashCode() {
        KpiB5DetailResult result1 = new KpiB5DetailResult();
        KpiB5DetailResult result2 = new KpiB5DetailResult();

        // Equal if same id
        result1.setId(1L);
        result2.setId(1L);
        assertThat(result1).isEqualTo(result2);
        assertThat(result1.hashCode()).isEqualTo(result2.hashCode());

        // Not equal if different id
        result2.setId(2L);
        assertThat(result1).isNotEqualTo(result2);
        assertThat(result1.hashCode()).isNotEqualTo(result2.hashCode());

        // Not equal to null or different type
        assertThat(result1).isNotEqualTo(null);
        assertThat(result1).isNotEqualTo("Some String");
    }

    @Test
    void testToStringContainsAllFields() {
        KpiB5DetailResult result = new KpiB5DetailResult();
        result.setId(1L);
        result.setAnalysisDate(LocalDate.of(2025, 10, 30));
        result.setStationsPresent(10);
        result.setStationsWithoutSpontaneous(2);
        result.setPercentageNoSpontaneous(BigDecimal.valueOf(20.5));
        result.setOutcome(OutcomeStatus.OK);

        String toString = result.toString();

        assertThat(toString).contains("id=1");
        assertThat(toString).contains("analysisDate=2025-10-30");
        assertThat(toString).contains("stationsPresent=10");
        assertThat(toString).contains("stationsWithoutSpontaneous=2");
        assertThat(toString).contains("percentageNoSpontaneous=20.5");
        assertThat(toString).contains("outcome=OK");
    }
}
