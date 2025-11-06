package com.nexigroup.pagopa.cruscotto.service.dto;

import com.nexigroup.pagopa.cruscotto.domain.enumeration.EvaluationType;
import com.nexigroup.pagopa.cruscotto.domain.enumeration.OutcomeStatus;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

class KpiB1DetailResultDTOTest {

    @Test
    void testGetterAndSetter() {
        KpiB1DetailResultDTO dto = new KpiB1DetailResultDTO();

        Long id = 1L;
        Long instanceId = 2L;
        Long instanceModuleId = 3L;
        Long kpiB1ResultId = 4L;
        LocalDate analysisDate = LocalDate.now();
        EvaluationType evaluationType = EvaluationType.MESE; // replace with actual enum
        LocalDate evaluationStartDate = LocalDate.now().minusDays(1);
        LocalDate evaluationEndDate = LocalDate.now().plusDays(1);
        Integer totalInstitutions = 10;
        Integer institutionDifference = 2;
        BigDecimal institutionDifferencePercentage = BigDecimal.valueOf(20.5);
        OutcomeStatus institutionOutcome = OutcomeStatus.OK; // replace with actual enum
        Integer totalTransactions = 100;
        Integer transactionDifference = 5;
        BigDecimal transactionDifferencePercentage = BigDecimal.valueOf(5.0);
        OutcomeStatus transactionOutcome = OutcomeStatus.KO; // replace with actual enum

        dto.setId(id);
        dto.setInstanceId(instanceId);
        dto.setInstanceModuleId(instanceModuleId);
        dto.setKpiB1ResultId(kpiB1ResultId);
        dto.setAnalysisDate(analysisDate);
        dto.setEvaluationType(evaluationType);
        dto.setEvaluationStartDate(evaluationStartDate);
        dto.setEvaluationEndDate(evaluationEndDate);
        dto.setTotalInstitutions(totalInstitutions);
        dto.setInstitutionDifference(institutionDifference);
        dto.setInstitutionDifferencePercentage(institutionDifferencePercentage);
        dto.setInstitutionOutcome(institutionOutcome);
        dto.setTotalTransactions(totalTransactions);
        dto.setTransactionDifference(transactionDifference);
        dto.setTransactionDifferencePercentage(transactionDifferencePercentage);
        dto.setTransactionOutcome(transactionOutcome);

        assertThat(dto.getId()).isEqualTo(id);
        assertThat(dto.getInstanceId()).isEqualTo(instanceId);
        assertThat(dto.getInstanceModuleId()).isEqualTo(instanceModuleId);
        assertThat(dto.getKpiB1ResultId()).isEqualTo(kpiB1ResultId);
        assertThat(dto.getAnalysisDate()).isEqualTo(analysisDate);
        assertThat(dto.getEvaluationType()).isEqualTo(evaluationType);
        assertThat(dto.getEvaluationStartDate()).isEqualTo(evaluationStartDate);
        assertThat(dto.getEvaluationEndDate()).isEqualTo(evaluationEndDate);
        assertThat(dto.getTotalInstitutions()).isEqualTo(totalInstitutions);
        assertThat(dto.getInstitutionDifference()).isEqualTo(institutionDifference);
        assertThat(dto.getInstitutionDifferencePercentage()).isEqualByComparingTo(institutionDifferencePercentage);
        assertThat(dto.getInstitutionOutcome()).isEqualTo(institutionOutcome);
        assertThat(dto.getTotalTransactions()).isEqualTo(totalTransactions);
        assertThat(dto.getTransactionDifference()).isEqualTo(transactionDifference);
        assertThat(dto.getTransactionDifferencePercentage()).isEqualByComparingTo(transactionDifferencePercentage);
        assertThat(dto.getTransactionOutcome()).isEqualTo(transactionOutcome);
    }

    @Test
    void testEqualsAndHashCode() {
        KpiB1DetailResultDTO dto1 = new KpiB1DetailResultDTO();
        KpiB1DetailResultDTO dto2 = new KpiB1DetailResultDTO();

        dto1.setId(1L);
        dto2.setId(1L);

        assertThat(dto1).isEqualTo(dto2);
        assertThat(dto1.hashCode()).isEqualTo(dto2.hashCode());

        dto2.setId(2L);
        assertThat(dto1).isNotEqualTo(dto2);
    }

    @Test
    void testToString() {
        KpiB1DetailResultDTO dto = new KpiB1DetailResultDTO();
        dto.setId(1L);
        dto.setInstanceId(2L);

        String str = dto.toString();
        assertThat(str).contains("id=1", "instanceId=2");
    }
}
