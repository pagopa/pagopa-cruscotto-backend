package com.nexigroup.pagopa.cruscotto.service.impl;

import com.nexigroup.pagopa.cruscotto.domain.*;
import com.nexigroup.pagopa.cruscotto.domain.enumeration.EvaluationType;
import com.nexigroup.pagopa.cruscotto.domain.enumeration.OutcomeStatus;
import com.nexigroup.pagopa.cruscotto.repository.*;
import com.nexigroup.pagopa.cruscotto.service.dto.KpiB9DetailResultDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import static org.mockito.ArgumentMatchers.any;

@ExtendWith(MockitoExtension.class)
@DisplayName("KpiB9DetailResultServiceImpl Tests")
class KpiB9DetailResultServiceImplTest {

    @Mock
    private InstanceRepository instanceRepository;

    @Mock
    private InstanceModuleRepository instanceModuleRepository;

    @Mock
    private KpiB9DetailResultRepository kpiB9DetailResultRepository;

    @Mock
    private KpiB9ResultRepository kpiB9ResultRepository;

    @InjectMocks
    private KpiB9DetailResultServiceImpl service;

    private Instance instance;
    private InstanceModule instanceModule;
    private KpiB9Result kpiB9Result;
    private KpiB9DetailResult detailResult;
    private KpiB9DetailResultDTO dto;

    @BeforeEach
    void setUp() {

        instance = new Instance();
        instance.setId(1L);

        instanceModule = new InstanceModule();
        instanceModule.setId(2L);

        kpiB9Result = new KpiB9Result();
        kpiB9Result.setId(3L);

        detailResult = new KpiB9DetailResult();
        detailResult.setId(100L);
        detailResult.setInstance(instance);
        detailResult.setInstanceModule(instanceModule);
        detailResult.setKpiB9Result(kpiB9Result);

        dto = new KpiB9DetailResultDTO();
        dto.setInstanceId(1L);
        dto.setInstanceModuleId(2L);
        dto.setKpiB9ResultId(3L);
        dto.setAnalysisDate(LocalDate.now());
        dto.setEvaluationType(EvaluationType.MESE);
        dto.setEvaluationStartDate(LocalDate.now().minusDays(1));
        dto.setEvaluationEndDate(LocalDate.now());
        dto.setTotRes(10L);
        dto.setResKo(2L);
        dto.setResKoPercentage(20.0);
        dto.setOutcome(OutcomeStatus.OK);
    }

    @Test
    void save_ShouldPersistEntity_WhenValidInput() {
        when(instanceRepository.findById(1L)).thenReturn(Optional.of(instance));
        when(instanceModuleRepository.findById(2L)).thenReturn(Optional.of(instanceModule));
        when(kpiB9ResultRepository.findById(3L)).thenReturn(Optional.of(kpiB9Result));
        when(kpiB9DetailResultRepository.save(any())).thenReturn(detailResult);

        KpiB9DetailResultDTO result = service.save(dto);

        assertThat(result.getId()).isEqualTo(100L);
        verify(kpiB9DetailResultRepository).save(any(KpiB9DetailResult.class));
    }

    @Test
    void save_ShouldThrow_WhenInstanceNotFound() {
        when(instanceRepository.findById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.save(dto))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("Instance not found");
    }

    @Test
    void save_ShouldThrow_WhenInstanceModuleNotFound() {
        when(instanceRepository.findById(1L)).thenReturn(Optional.of(instance));
        when(instanceModuleRepository.findById(2L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.save(dto))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("InstanceModule not found");
    }

    @Test
    void save_ShouldThrow_WhenKpiB9ResultNotFound() {
        when(instanceRepository.findById(1L)).thenReturn(Optional.of(instance));
        when(instanceModuleRepository.findById(2L)).thenReturn(Optional.of(instanceModule));
        when(kpiB9ResultRepository.findById(3L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.save(dto))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("KpiB9Result not found");
    }

    @Test
    void deleteAllByInstanceModule_ShouldReturnCount() {
        when(kpiB9DetailResultRepository.deleteAllByInstanceModuleId(2L)).thenReturn(5);

        int deleted = service.deleteAllByInstanceModule(2L);

        assertThat(deleted).isEqualTo(5);
        verify(kpiB9DetailResultRepository).deleteAllByInstanceModuleId(2L);
    }

}
