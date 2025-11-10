package com.nexigroup.pagopa.cruscotto.service.impl;

import com.nexigroup.pagopa.cruscotto.domain.Instance;
import com.nexigroup.pagopa.cruscotto.domain.InstanceModule;
import com.nexigroup.pagopa.cruscotto.domain.KpiB8Result;
import com.nexigroup.pagopa.cruscotto.domain.enumeration.EvaluationType;
import com.nexigroup.pagopa.cruscotto.domain.enumeration.OutcomeStatus;
import com.nexigroup.pagopa.cruscotto.repository.InstanceModuleRepository;
import com.nexigroup.pagopa.cruscotto.repository.InstanceRepository;
import com.nexigroup.pagopa.cruscotto.repository.KpiB8ResultRepository;
import com.nexigroup.pagopa.cruscotto.service.dto.KpiB8ResultDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataAccessException;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class KpiB8ResultServiceImplTest {

    @Mock
    private InstanceRepository instanceRepository;

    @Mock
    private InstanceModuleRepository instanceModuleRepository;

    @Mock
    private KpiB8ResultRepository kpiB8ResultRepository;

    @InjectMocks
    private KpiB8ResultServiceImpl service;

    private Instance instance;
    private InstanceModule module;
    private KpiB8ResultDTO dto;
    private KpiB8Result entity;

    @BeforeEach
    void setUp() {

        instance = new Instance();
        instance.setId(1L);

        module = new InstanceModule();
        module.setId(2L);

        dto = new KpiB8ResultDTO();
        dto.setInstanceId(1L);
        dto.setInstanceModuleId(2L);
        dto.setAnalysisDate(LocalDate.now());
        dto.setEligibilityThreshold(95.0);
        dto.setTolerance(0.05);
        dto.setEvaluationType(EvaluationType.MESE);
        dto.setOutcome(OutcomeStatus.OK);

        entity = new KpiB8Result();
        entity.setId(10L);
        entity.setInstance(instance);
        entity.setInstanceModule(module);
        entity.setAnalysisDate(dto.getAnalysisDate());
        entity.setEligibilityThreshold(dto.getEligibilityThreshold());
        entity.setTolerance(dto.getTolerance());
        entity.setEvaluationType(dto.getEvaluationType());
        entity.setOutcome(dto.getOutcome());
    }

    @Test
    void save_shouldPersistAndReturnDTO() {
        when(instanceRepository.findById(1L)).thenReturn(Optional.of(instance));
        when(instanceModuleRepository.findById(2L)).thenReturn(Optional.of(module));
        when(kpiB8ResultRepository.save(any(KpiB8Result.class))).thenReturn(entity);

        KpiB8ResultDTO result = service.save(dto);

        assertNotNull(result.getId());
        assertEquals(10L, result.getId());
        verify(kpiB8ResultRepository, times(1)).save(any(KpiB8Result.class));
    }

    @Test
    void save_shouldThrowWhenInstanceNotFound() {
        when(instanceRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () -> service.save(dto));
        verify(kpiB8ResultRepository, never()).save(any());
    }

    @Test
    void save_shouldThrowWhenInstanceModuleNotFound() {
        when(instanceRepository.findById(1L)).thenReturn(Optional.of(instance));
        when(instanceModuleRepository.findById(2L)).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () -> service.save(dto));
        verify(kpiB8ResultRepository, never()).save(any());
    }

    @Test
    void deleteAllByInstanceModule_shouldDelegateToRepository() {
        when(kpiB8ResultRepository.deleteAllByInstanceModuleId(2L)).thenReturn(3);

        int deleted = service.deleteAllByInstanceModule(2L);

        assertEquals(3, deleted);
        verify(kpiB8ResultRepository, times(1)).deleteAllByInstanceModuleId(2L);
    }

    @Test
    void updateKpiB8ResultOutcome_shouldUpdateWhenEntityExists() {
        when(kpiB8ResultRepository.findById(10L)).thenReturn(Optional.of(entity));
        when(kpiB8ResultRepository.save(any(KpiB8Result.class))).thenReturn(entity);

        service.updateKpiB8ResultOutcome(10L, OutcomeStatus.KO);

        assertEquals(OutcomeStatus.KO, entity.getOutcome());
        verify(kpiB8ResultRepository, times(1)).save(entity);
    }

    @Test
    void updateKpiB8ResultOutcome_shouldNotFailWhenEntityNotFound() {
        when(kpiB8ResultRepository.findById(10L)).thenReturn(Optional.empty());

        assertDoesNotThrow(() -> service.updateKpiB8ResultOutcome(10L, OutcomeStatus.KO));
        verify(kpiB8ResultRepository, never()).save(any());
    }

    @Test
    void findByInstanceModuleId_shouldReturnMappedDTOs() {
        KpiB8Result another = new KpiB8Result();
        another.setId(11L);
        another.setInstance(instance);
        another.setInstanceModule(module);
        another.setAnalysisDate(LocalDate.now().minusDays(1));
        another.setEligibilityThreshold(90.0);
        another.setTolerance(0.1);
        another.setEvaluationType(EvaluationType.TOTALE);
        another.setOutcome(OutcomeStatus.KO);

        when(kpiB8ResultRepository.findAllByInstanceModuleIdOrderByAnalysisDateDesc(2L))
            .thenReturn(List.of(entity, another));

        List<KpiB8ResultDTO> results = service.findByInstanceModuleId(2L);

        assertEquals(2, results.size());
        assertEquals(entity.getId(), results.get(0).getId());
        assertEquals(OutcomeStatus.KO, results.get(1).getOutcome());
        verify(kpiB8ResultRepository, times(1))
            .findAllByInstanceModuleIdOrderByAnalysisDateDesc(2L);
    }

    @Test
    void save_shouldHandleRepositoryExceptionGracefully() {
        when(instanceRepository.findById(1L)).thenReturn(Optional.of(instance));
        when(instanceModuleRepository.findById(2L)).thenReturn(Optional.of(module));
        when(kpiB8ResultRepository.save(any(KpiB8Result.class)))
            .thenThrow(new DataAccessException("DB error") {});

        assertThrows(DataAccessException.class, () -> service.save(dto));
    }
}
