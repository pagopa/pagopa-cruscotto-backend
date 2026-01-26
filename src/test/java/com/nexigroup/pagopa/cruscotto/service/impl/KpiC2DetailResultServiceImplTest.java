package com.nexigroup.pagopa.cruscotto.service.impl;

import com.nexigroup.pagopa.cruscotto.domain.KpiC2DetailResult;
import com.nexigroup.pagopa.cruscotto.domain.KpiC2Result;
import com.nexigroup.pagopa.cruscotto.domain.enumeration.EvaluationType;
import com.nexigroup.pagopa.cruscotto.domain.enumeration.OutcomeStatus;
import com.nexigroup.pagopa.cruscotto.repository.KpiC2DetailResultRepository;
import com.nexigroup.pagopa.cruscotto.service.dto.KpiC2DetailResultDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class KpiC2DetailResultServiceImplTest {

    @Mock
    private KpiC2DetailResultRepository repository;

    @InjectMocks
    private KpiC2DetailResultServiceImpl service;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testSave() {
        KpiC2DetailResultDTO dto = new KpiC2DetailResultDTO();
        dto.setId(1L);

        KpiC2DetailResultDTO result = service.save(dto);

        assertThat(result).isEqualTo(dto);
    }

    @Test
    void testDeleteAllByInstanceModule() {
        long instanceModuleId = 123L;
        int deletedCount = service.deleteAllByInstanceModule(instanceModuleId);
        assertThat(deletedCount).isEqualTo(0);
    }

    @Test
    void testUpdateKpiC2DetailResultOutcome() {
        long id = 1L;
        OutcomeStatus status = OutcomeStatus.OK;

        // Act
        service.updateKpiC2DetailResultOutcome(id, status);

        // Assert — method should not call the repository at all
        verifyNoInteractions(repository);
    }

    @Test
    void testFindByResultId() {
        long resultId = 10L;

        KpiC2Result kpiC2Result = new KpiC2Result();
        kpiC2Result.setId(resultId);

        KpiC2DetailResult entity = new KpiC2DetailResult();
        entity.setId(1L);
        entity.setInstanceId(100L);
        entity.setInstanceModuleId(200L);
        entity.setKpiC2Result(kpiC2Result);
        entity.setAnalysisDate(LocalDate.from(LocalDateTime.now()));
        entity.setEvaluationType(EvaluationType.MESE);
        entity.setEvaluationStartDate(LocalDate.from(LocalDateTime.now().minusDays(1)));
        entity.setEvaluationEndDate(LocalDate.from(LocalDateTime.now()));
        entity.setTotalInstitution(5L);
        entity.setTotalInstitutionSend(3L);
        entity.setPercentInstitutionSend(BigDecimal.valueOf(60.0));
        entity.setTotalPayment(10L);
        entity.setTotalNotification(8L);
        entity.setPercentEntiOk(BigDecimal.valueOf(80.0));
        entity.setOutcome(OutcomeStatus.OK);

        when(repository.findAllByResultIdOrderByAnalysisDateDesc(resultId))
            .thenReturn(List.of(entity));

        List<KpiC2DetailResultDTO> dtos = service.findByResultId(resultId);

        assertThat(dtos).hasSize(1);
        KpiC2DetailResultDTO dto = dtos.get(0);
        assertThat(dto.getId()).isEqualTo(entity.getId());
        assertThat(dto.getInstanceId()).isEqualTo(entity.getInstanceId());
        assertThat(dto.getKpiC2ResultId()).isEqualTo(entity.getKpiC2Result().getId());
        assertThat(dto.getOutcome()).isEqualTo(entity.getOutcome());

        verify(repository, times(1)).findAllByResultIdOrderByAnalysisDateDesc(resultId);
    }
}
