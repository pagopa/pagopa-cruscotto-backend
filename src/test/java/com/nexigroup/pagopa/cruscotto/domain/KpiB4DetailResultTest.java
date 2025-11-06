package com.nexigroup.pagopa.cruscotto.domain;

import static org.assertj.core.api.Assertions.assertThat;

import com.nexigroup.pagopa.cruscotto.domain.enumeration.EvaluationType;
import com.nexigroup.pagopa.cruscotto.domain.enumeration.OutcomeStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;

class KpiB4DetailResultTest {

    private KpiB4DetailResult detailResult;

    @BeforeEach
    void setUp() {
        detailResult = new KpiB4DetailResult();
    }

    @Test
    void testGettersAndSetters() {
        Long id = 1L;
        Long instanceId = 10L;
        Long instanceModuleId = 20L;
        Long stationId = 30L;
        LocalDate analysisDate = LocalDate.of(2025, 10, 30);
        EvaluationType evalType = EvaluationType.MESE;
        LocalDate startDate = LocalDate.of(2025, 1, 1);
        LocalDate endDate = LocalDate.of(2025, 12, 31);
        Long sumGpd = 100L;
        Long sumCp = 50L;
        BigDecimal perApiCp = BigDecimal.valueOf(50.0);
        OutcomeStatus outcome = OutcomeStatus.OK;

        detailResult.setId(id);
        detailResult.setInstanceId(instanceId);
        detailResult.setInstanceModuleId(instanceModuleId);
        detailResult.setAnagStationId(stationId);
        detailResult.setAnalysisDate(analysisDate);
        detailResult.setEvaluationType(evalType);
        detailResult.setEvaluationStartDate(startDate);
        detailResult.setEvaluationEndDate(endDate);
        detailResult.setSumTotGpd(sumGpd);
        detailResult.setSumTotCp(sumCp);
        detailResult.setPerApiCp(perApiCp);
        detailResult.setOutcome(outcome);

        assertThat(detailResult.getId()).isEqualTo(id);
        assertThat(detailResult.getInstanceId()).isEqualTo(instanceId);
        assertThat(detailResult.getInstanceModuleId()).isEqualTo(instanceModuleId);
        assertThat(detailResult.getAnagStationId()).isEqualTo(stationId);
        assertThat(detailResult.getAnalysisDate()).isEqualTo(analysisDate);
        assertThat(detailResult.getEvaluationType()).isEqualTo(evalType);
        assertThat(detailResult.getEvaluationStartDate()).isEqualTo(startDate);
        assertThat(detailResult.getEvaluationEndDate()).isEqualTo(endDate);
        assertThat(detailResult.getSumTotGpd()).isEqualTo(sumGpd);
        assertThat(detailResult.getSumTotCp()).isEqualTo(sumCp);
        assertThat(detailResult.getPerApiCp()).isEqualTo(perApiCp);
        assertThat(detailResult.getOutcome()).isEqualTo(outcome);
    }

    @Test
    void testEqualsAndHashCode() {
        KpiB4DetailResult result1 = new KpiB4DetailResult();
        KpiB4DetailResult result2 = new KpiB4DetailResult();

        result1.setId(1L);
        result2.setId(1L);

        assertThat(result1).isEqualTo(result2);
        assertThat(result1.hashCode()).isEqualTo(result2.hashCode());

        result2.setId(2L);
        assertThat(result1).isNotEqualTo(result2);
    }

    @Test
    void testToString() {
        detailResult.setId(1L);
        detailResult.setAnalysisDate(LocalDate.of(2025, 10, 30));
        detailResult.setEvaluationType(EvaluationType.MESE);
        detailResult.setEvaluationStartDate(LocalDate.of(2025, 1, 1));
        detailResult.setEvaluationEndDate(LocalDate.of(2025, 12, 31));
        detailResult.setSumTotGpd(100L);
        detailResult.setSumTotCp(50L);
        detailResult.setPerApiCp(BigDecimal.valueOf(50.0));
        detailResult.setOutcome(OutcomeStatus.OK);

        String toString = detailResult.toString();

        assertThat(toString).contains("KpiB4DetailResult{");
        assertThat(toString).contains("id=1");
        assertThat(toString).contains("analysisDate='2025-10-30'");
        assertThat(toString).contains("evaluationType='MESE'");
        assertThat(toString).contains("sumTotGpd=100");
        assertThat(toString).contains("sumTotCp=50");
        assertThat(toString).contains("perApiCp=50.0");
        assertThat(toString).contains("outcome='OK'");
    }
}
