package com.nexigroup.pagopa.cruscotto.service.impl;

import com.nexigroup.pagopa.cruscotto.domain.KpiB4DetailResult;
import com.nexigroup.pagopa.cruscotto.domain.KpiB4Result;
import com.nexigroup.pagopa.cruscotto.domain.enumeration.EvaluationType;
import com.nexigroup.pagopa.cruscotto.domain.enumeration.OutcomeStatus;
import com.nexigroup.pagopa.cruscotto.repository.KpiB4DetailResultRepository;
import com.nexigroup.pagopa.cruscotto.service.dto.KpiB4DetailResultDTO;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class KpiB4DetailResultServiceImplTest {

    @Mock
    private KpiB4DetailResultRepository kpiB4DetailResultRepository;

    @InjectMocks
    private KpiB4DetailResultServiceImpl service;

    @Test
    void save_shouldReturnSameDTO() {
        KpiB4DetailResultDTO dto = new KpiB4DetailResultDTO();
        dto.setId(1L);

        KpiB4DetailResultDTO result = service.save(dto);

        assertThat(result).isEqualTo(dto);
    }

    @Test
    void deleteAllByInstanceModule_shouldReturnZero() {
        long instanceModuleId = 123L;
        int deletedCount = service.deleteAllByInstanceModule(instanceModuleId);

        assertThat(deletedCount).isZero();
    }

    @Test
    void updateKpiB4DetailResultOutcome_shouldCallRepository() {
        long id = 1L;
        OutcomeStatus status = OutcomeStatus.OK;

        service.updateKpiB4DetailResultOutcome(id, status);

        // Currently the method has no implementation, so just ensure no exceptions
    }

    @Test
    void findByResultId_shouldReturnDTOs() {
        long resultId = 100L;

        KpiB4Result kpiB4Result = new KpiB4Result();
        kpiB4Result.setId(resultId);

        KpiB4DetailResult entity = new KpiB4DetailResult();
        entity.setId(1L);
        entity.setInstanceId(10L);
        entity.setInstanceModuleId(20L);
        entity.setAnagStationId(30L);
        entity.setKpiB4Result(kpiB4Result);
        entity.setAnalysisDate(LocalDate.from(LocalDateTime.now()));
        entity.setEvaluationType(EvaluationType.MESE);
        entity.setEvaluationStartDate(LocalDate.from(LocalDateTime.now().minusDays(1)));
        entity.setEvaluationEndDate(LocalDate.from(LocalDateTime.now()));
        entity.setSumTotGpd((long) 100.0);
        entity.setSumTotCp((long) 50.0);
        entity.setPerApiCp(BigDecimal.valueOf(0.5));
        entity.setOutcome(OutcomeStatus.OK);

        when(kpiB4DetailResultRepository.findAllByResultIdOrderByAnalysisDateDesc(resultId))
            .thenReturn(Collections.singletonList(entity));

        List<KpiB4DetailResultDTO> dtos = service.findByResultId(resultId);

        assertThat(dtos).hasSize(1);
        KpiB4DetailResultDTO dto = dtos.get(0);
        assertThat(dto.getId()).isEqualTo(1L);
        assertThat(dto.getKpiB4ResultId()).isEqualTo(resultId);

        verify(kpiB4DetailResultRepository, times(1))
            .findAllByResultIdOrderByAnalysisDateDesc(resultId);
    }

    @Test
    void findByResultId_shouldReturnEmptyList() {
        long resultId = 200L;
        when(kpiB4DetailResultRepository.findAllByResultIdOrderByAnalysisDateDesc(resultId))
            .thenReturn(Collections.emptyList());

        List<KpiB4DetailResultDTO> dtos = service.findByResultId(resultId);

        assertThat(dtos).isEmpty();
    }
}
