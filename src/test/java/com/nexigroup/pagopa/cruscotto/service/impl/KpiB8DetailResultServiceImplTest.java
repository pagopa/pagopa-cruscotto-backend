package com.nexigroup.pagopa.cruscotto.service.impl;

import com.nexigroup.pagopa.cruscotto.domain.KpiB8DetailResult;
import com.nexigroup.pagopa.cruscotto.domain.enumeration.EvaluationType;
import com.nexigroup.pagopa.cruscotto.domain.enumeration.OutcomeStatus;
import com.nexigroup.pagopa.cruscotto.repository.KpiB8DetailResultRepository;
import com.nexigroup.pagopa.cruscotto.service.dto.KpiB8DetailResultDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class KpiB8DetailResultServiceImplTest {

    private KpiB8DetailResultRepository repository;
    private KpiB8DetailResultServiceImpl service;

    @BeforeEach
    void setUp() {
        repository = mock(KpiB8DetailResultRepository.class);
        service = new KpiB8DetailResultServiceImpl(repository);
    }

    @Test
    void testSave() {
        KpiB8DetailResultDTO dto = new KpiB8DetailResultDTO();
        dto.setId(1L);
        // Since save is not implemented, it just returns the DTO
        KpiB8DetailResultDTO result = service.save(dto);
        assertThat(result).isEqualTo(dto);
    }

    @Test
    void testDeleteAllByInstanceModule() {
        long instanceModuleId = 123L;
        int result = service.deleteAllByInstanceModule(instanceModuleId);
        assertThat(result).isEqualTo(0); // default implementation
    }

    @Test
    void testUpdateKpiB8DetailResultOutcome() {
        long id = 1L;
        OutcomeStatus status = OutcomeStatus.OK;
        service.updateKpiB8DetailResultOutcome(id, status);
        // Method does not return anything yet, just ensure no exceptions
    }

    @Test
    void testFindByResultId() {
        long resultId = 1L;

        KpiB8DetailResult entity1 = new KpiB8DetailResult();
        entity1.setId(1L);
        entity1.setInstanceId(10L);
        entity1.setInstanceModuleId(100L);
        entity1.setAnagStationId(1000L);
        entity1.setKpiB8Result(new com.nexigroup.pagopa.cruscotto.domain.KpiB8Result() {{
            setId(999L);
        }});
        entity1.setAnalysisDate(LocalDate.of(2025, 10, 28));
        entity1.setEvaluationType(EvaluationType.MESE);
        entity1.setEvaluationStartDate(LocalDate.of(2025, 10, 1));
        entity1.setEvaluationEndDate(LocalDate.of(2025, 10, 28));
        entity1.setReqKO(5L);
        entity1.setTotReq(100L);
        entity1.setPerKO(BigDecimal.valueOf(5.0));
        entity1.setOutcome(OutcomeStatus.OK);

        when(repository.findAllByResultIdOrderByAnalysisDateDesc(resultId))
            .thenReturn(List.of(entity1));

        List<KpiB8DetailResultDTO> dtos = service.findByResultId(resultId);

        assertThat(dtos).hasSize(1);
        KpiB8DetailResultDTO dto = dtos.get(0);
        assertThat(dto.getId()).isEqualTo(entity1.getId());
        assertThat(dto.getKpiB8ResultId()).isEqualTo(entity1.getKpiB8Result().getId());
        assertThat(dto.getOutcome()).isEqualTo(entity1.getOutcome());

        verify(repository, times(1)).findAllByResultIdOrderByAnalysisDateDesc(resultId);
    }
}
