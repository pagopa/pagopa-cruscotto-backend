package com.nexigroup.pagopa.cruscotto.service.impl;

import com.nexigroup.pagopa.cruscotto.domain.Instance;
import com.nexigroup.pagopa.cruscotto.domain.InstanceModule;
import com.nexigroup.pagopa.cruscotto.domain.KpiB3Result;
import com.nexigroup.pagopa.cruscotto.domain.enumeration.EvaluationType;
import com.nexigroup.pagopa.cruscotto.domain.enumeration.OutcomeStatus;
import com.nexigroup.pagopa.cruscotto.repository.InstanceModuleRepository;
import com.nexigroup.pagopa.cruscotto.repository.InstanceRepository;
import com.nexigroup.pagopa.cruscotto.repository.KpiB3ResultRepository;
import com.nexigroup.pagopa.cruscotto.service.dto.KpiB3ResultDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class KpiB3ResultServiceImplTest {

    private InstanceRepository instanceRepository;
    private InstanceModuleRepository instanceModuleRepository;
    private KpiB3ResultRepository kpiB3ResultRepository;
    private KpiB3ResultServiceImpl service;

    @BeforeEach
    void setUp() {
        instanceRepository = mock(InstanceRepository.class);
        instanceModuleRepository = mock(InstanceModuleRepository.class);
        kpiB3ResultRepository = mock(KpiB3ResultRepository.class);
        service = new KpiB3ResultServiceImpl(instanceRepository, instanceModuleRepository, kpiB3ResultRepository);
    }

    @Test
    void save_ShouldSaveKpiB3Result() {
        KpiB3ResultDTO dto = new KpiB3ResultDTO();
        dto.setInstanceId(1L);
        dto.setInstanceModuleId(2L);
        dto.setAnalysisDate(LocalDate.now());
        dto.setExcludePlannedShutdown(true);
        dto.setExcludeUnplannedShutdown(false);
        dto.setEligibilityThreshold(90.0);
        dto.setTolerance(5.0);
        dto.setEvaluationType(EvaluationType.MESE);
        dto.setOutcome(OutcomeStatus.STANDBY);

        Instance instance = new Instance();
        instance.setId(1L);
        InstanceModule module = new InstanceModule();
        module.setId(2L);

        when(instanceRepository.findById(1L)).thenReturn(Optional.of(instance));
        when(instanceModuleRepository.findById(2L)).thenReturn(Optional.of(module));

        KpiB3Result saved = new KpiB3Result();
        saved.setId(100L);
        when(kpiB3ResultRepository.save(any(KpiB3Result.class))).thenReturn(saved);

        KpiB3ResultDTO result = service.save(dto);

        assertEquals(100L, result.getId());
        verify(kpiB3ResultRepository).save(any(KpiB3Result.class));
    }

    @Test
    void save_ShouldThrowWhenInstanceNotFound() {
        KpiB3ResultDTO dto = new KpiB3ResultDTO();
        dto.setInstanceId(1L);
        when(instanceRepository.findById(1L)).thenReturn(Optional.empty());

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> service.save(dto));
        assertEquals("Instance not found", exception.getMessage());
    }

    @Test
    void save_ShouldThrowWhenInstanceModuleNotFound() {
        KpiB3ResultDTO dto = new KpiB3ResultDTO();
        dto.setInstanceId(1L);
        dto.setInstanceModuleId(2L);

        when(instanceRepository.findById(1L)).thenReturn(Optional.of(new Instance()));
        when(instanceModuleRepository.findById(2L)).thenReturn(Optional.empty());

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> service.save(dto));
        assertEquals("InstanceModule not found", exception.getMessage());
    }

    @Test
    void deleteAllByInstanceModule_ShouldCallRepository() {
        when(kpiB3ResultRepository.deleteAllByInstanceModuleId(2L)).thenReturn(3);

        int deleted = service.deleteAllByInstanceModule(2L);

        assertEquals(3, deleted);
        verify(kpiB3ResultRepository).deleteAllByInstanceModuleId(2L);
    }

    @Test
    void updateKpiB3ResultOutcome_ShouldUpdateOutcome() {
        KpiB3Result result = new KpiB3Result();
        result.setOutcome(OutcomeStatus.STANDBY);
        when(kpiB3ResultRepository.findById(1L)).thenReturn(Optional.of(result));

        service.updateKpiB3ResultOutcome(1L, OutcomeStatus.OK);

        assertEquals(OutcomeStatus.OK, result.getOutcome());
        verify(kpiB3ResultRepository).save(result);
    }

    @Test
    void updateKpiB3ResultOutcome_ShouldDoNothingIfNotFound() {
        when(kpiB3ResultRepository.findById(1L)).thenReturn(Optional.empty());

        service.updateKpiB3ResultOutcome(1L, OutcomeStatus.OK);

        verify(kpiB3ResultRepository, never()).save(any());
    }

    @Test
    void findByInstanceModuleId_ShouldReturnDTOList() {
        KpiB3Result result = new KpiB3Result();
        result.setId(1L);
        Instance instance = new Instance();
        instance.setId(10L);
        InstanceModule module = new InstanceModule();
        module.setId(20L);
        result.setInstance(instance);
        result.setInstanceModule(module);
        result.setAnalysisDate(LocalDate.now());
        result.setExcludePlannedShutdown(true);
        result.setExcludeUnplannedShutdown(false);
        result.setEligibilityThreshold(90.0);
        result.setTolerance(5.0);
        result.setEvaluationType(EvaluationType.MESE);
        result.setOutcome(OutcomeStatus.STANDBY);

        when(kpiB3ResultRepository.findAllByInstanceModuleIdOrderByAnalysisDateDesc(20L))
            .thenReturn(List.of(result));

        List<KpiB3ResultDTO> dtos = service.findByInstanceModuleId(20L);

        assertEquals(1, dtos.size());
        assertEquals(1L, dtos.get(0).getId());
        assertEquals(10L, dtos.get(0).getInstanceId());
        assertEquals(20L, dtos.get(0).getInstanceModuleId());
    }
}
