package com.nexigroup.pagopa.cruscotto.domain;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDate;

import com.nexigroup.pagopa.cruscotto.domain.enumeration.EvaluationType;
import com.nexigroup.pagopa.cruscotto.domain.enumeration.OutcomeStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class KpiA1DetailResultTest {

    private KpiA1DetailResult kpiA1DetailResult;

    @BeforeEach
    void setUp() {
        kpiA1DetailResult = new KpiA1DetailResult();
    }

    @Test
    void testGettersAndSetters() {
        Long id = 1L;
        Instance instance = new Instance();
        InstanceModule module = new InstanceModule();
        LocalDate analysisDate = LocalDate.now();
        EvaluationType evaluationType = EvaluationType.MESE;
        LocalDate startDate = LocalDate.now().minusDays(1);
        LocalDate endDate = LocalDate.now();
        Long totReq = 100L;
        Long reqTimeout = 5L;
        Double timeoutPercentage = 5.0;
        OutcomeStatus outcome = OutcomeStatus.OK;
        KpiA1Result kpiA1Result = new KpiA1Result();

        kpiA1DetailResult.setId(id);
        kpiA1DetailResult.setInstance(instance);
        kpiA1DetailResult.setInstanceModule(module);
        kpiA1DetailResult.setAnalysisDate(analysisDate);
        kpiA1DetailResult.setEvaluationType(evaluationType);
        kpiA1DetailResult.setEvaluationStartDate(startDate);
        kpiA1DetailResult.setEvaluationEndDate(endDate);
        kpiA1DetailResult.setTotReq(totReq);
        kpiA1DetailResult.setReqTimeout(reqTimeout);
        kpiA1DetailResult.setTimeoutPercentage(timeoutPercentage);
        kpiA1DetailResult.setOutcome(outcome);
        kpiA1DetailResult.setKpiA1Result(kpiA1Result);

        assertThat(kpiA1DetailResult.getId()).isEqualTo(id);
        assertThat(kpiA1DetailResult.getInstance()).isEqualTo(instance);
        assertThat(kpiA1DetailResult.getInstanceModule()).isEqualTo(module);
        assertThat(kpiA1DetailResult.getAnalysisDate()).isEqualTo(analysisDate);
        assertThat(kpiA1DetailResult.getEvaluationType()).isEqualTo(evaluationType);
        assertThat(kpiA1DetailResult.getEvaluationStartDate()).isEqualTo(startDate);
        assertThat(kpiA1DetailResult.getEvaluationEndDate()).isEqualTo(endDate);
        assertThat(kpiA1DetailResult.getTotReq()).isEqualTo(totReq);
        assertThat(kpiA1DetailResult.getReqTimeout()).isEqualTo(reqTimeout);
        assertThat(kpiA1DetailResult.getTimeoutPercentage()).isEqualTo(timeoutPercentage);
        assertThat(kpiA1DetailResult.getOutcome()).isEqualTo(outcome);
        assertThat(kpiA1DetailResult.getKpiA1Result()).isEqualTo(kpiA1Result);
    }

    @Test
    void testEqualsAndHashCode() {
        KpiA1DetailResult other = new KpiA1DetailResult();
        kpiA1DetailResult.setId(1L);
        other.setId(1L);

        assertThat(kpiA1DetailResult).isEqualTo(other);
        assertThat(kpiA1DetailResult.hashCode()).isEqualTo(other.hashCode());

        other.setId(2L);
        assertThat(kpiA1DetailResult).isNotEqualTo(other);
    }

    @Test
    void testToString() {
        kpiA1DetailResult.setId(1L);
        String toString = kpiA1DetailResult.toString();
        assertThat(toString).contains("KpiA1DetailResult");
        assertThat(toString).contains("id=1");
    }
}
