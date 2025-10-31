package com.nexigroup.pagopa.cruscotto.service.impl;

import com.nexigroup.pagopa.cruscotto.domain.Instance;
import com.nexigroup.pagopa.cruscotto.domain.InstanceModule;
import com.nexigroup.pagopa.cruscotto.domain.KpiB3DetailResult;
import com.nexigroup.pagopa.cruscotto.domain.KpiB3Result;
import com.nexigroup.pagopa.cruscotto.domain.enumeration.EvaluationType;
import com.nexigroup.pagopa.cruscotto.domain.enumeration.OutcomeStatus;
import com.nexigroup.pagopa.cruscotto.repository.InstanceModuleRepository;
import com.nexigroup.pagopa.cruscotto.repository.InstanceRepository;
import com.nexigroup.pagopa.cruscotto.repository.KpiB3DetailResultRepository;
import com.nexigroup.pagopa.cruscotto.repository.KpiB3ResultRepository;
import com.nexigroup.pagopa.cruscotto.service.dto.KpiB3DetailResultDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class KpiB3DetailResultServiceImplTest {

    @Mock
    private InstanceRepository instanceRepository;

    @Mock
    private InstanceModuleRepository instanceModuleRepository;

    @Mock
    private KpiB3DetailResultRepository kpiB3DetailResultRepository;

    @Mock
    private KpiB3ResultRepository kpiB3ResultRepository;

    @InjectMocks
    private KpiB3DetailResultServiceImpl service;

    private Instance instance;
    private InstanceModule instanceModule;
    private KpiB3Result kpiB3Result;

    @BeforeEach
    void setUp() {
        instance = new Instance();
        instance.setId(1L);

        instanceModule = new InstanceModule();
        instanceModule.setId(1L);

        kpiB3Result = new KpiB3Result();
        kpiB3Result.setId(1L);
    }

    @Test
    void save_ShouldPersistKpiB3DetailResult() {
        KpiB3DetailResultDTO dto = new KpiB3DetailResultDTO();
        dto.setInstanceId(1L);
        dto.setInstanceModuleId(1L);
        dto.setKpiB3ResultId(1L);
        dto.setAnalysisDate(LocalDate.now());
        dto.setEvaluationType(EvaluationType.MESE);
        dto.setEvaluationStartDate(LocalDate.now().minusDays(1));
        dto.setEvaluationEndDate(LocalDate.now());
        dto.setTotalStandIn(100);
        dto.setOutcome(OutcomeStatus.OK);

        when(instanceRepository.findById(1L)).thenReturn(Optional.of(instance));
        when(instanceModuleRepository.findById(1L)).thenReturn(Optional.of(instanceModule));
        when(kpiB3ResultRepository.findById(1L)).thenReturn(Optional.of(kpiB3Result));

        KpiB3DetailResult savedEntity = new KpiB3DetailResult();
        savedEntity.setId(1L);
        when(kpiB3DetailResultRepository.save(any())).thenReturn(savedEntity);

        KpiB3DetailResultDTO result = service.save(dto);

        assertThat(result.getId()).isEqualTo(1L);

        ArgumentCaptor<KpiB3DetailResult> captor = ArgumentCaptor.forClass(KpiB3DetailResult.class);
        verify(kpiB3DetailResultRepository).save(captor.capture());
        assertThat(captor.getValue().getOutcome()).isEqualTo(OutcomeStatus.OK);
    }

    @Test
    void save_ShouldThrow_WhenInstanceNotFound() {
        KpiB3DetailResultDTO dto = new KpiB3DetailResultDTO();
        dto.setInstanceId(99L);

        when(instanceRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () -> service.save(dto));
    }

    @Test
    void deleteAllByInstanceModule_ShouldReturnDeletedCount() {
        when(kpiB3DetailResultRepository.deleteAllByInstanceModuleId(1L)).thenReturn(5);

        int deletedCount = service.deleteAllByInstanceModule(1L);

        assertThat(deletedCount).isEqualTo(5);
    }

    @Test
    void updateKpiB3DetailResultOutcome_ShouldUpdateOutcome() {
        KpiB3DetailResult entity = new KpiB3DetailResult();
        entity.setId(1L);

        when(kpiB3DetailResultRepository.findById(1L)).thenReturn(Optional.of(entity));
        when(kpiB3DetailResultRepository.save(any())).thenReturn(entity);

        service.updateKpiB3DetailResultOutcome(1L, OutcomeStatus.KO);

        assertThat(entity.getOutcome()).isEqualTo(OutcomeStatus.KO);
    }

    @Test
    void findByResultId_ShouldReturnDTOs() {
        KpiB3DetailResult entity = new KpiB3DetailResult();
        entity.setId(1L);
        entity.setInstance(instance);
        entity.setInstanceModule(instanceModule);
        entity.setKpiB3Result(kpiB3Result);
        entity.setAnalysisDate(LocalDate.now());
        entity.setEvaluationType(EvaluationType.MESE);
        entity.setEvaluationStartDate(LocalDate.now().minusDays(1));
        entity.setEvaluationEndDate(LocalDate.now());
        entity.setTotalStandIn(50);
        entity.setOutcome(OutcomeStatus.OK);

        when(kpiB3DetailResultRepository.findAllByResultIdOrderByAnalysisDateDesc(1L))
            .thenReturn(List.of(entity));

        List<KpiB3DetailResultDTO> dtos = service.findByResultId(1L);

        assertThat(dtos).hasSize(1);
        assertThat(dtos.get(0).getOutcome()).isEqualTo(OutcomeStatus.OK);
    }
}
