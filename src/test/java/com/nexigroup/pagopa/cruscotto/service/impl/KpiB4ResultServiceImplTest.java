package com.nexigroup.pagopa.cruscotto.service.impl;

import com.nexigroup.pagopa.cruscotto.domain.Instance;
import com.nexigroup.pagopa.cruscotto.domain.InstanceModule;
import com.nexigroup.pagopa.cruscotto.domain.KpiB4Result;
import com.nexigroup.pagopa.cruscotto.domain.enumeration.EvaluationType;
import com.nexigroup.pagopa.cruscotto.domain.enumeration.OutcomeStatus;
import com.nexigroup.pagopa.cruscotto.repository.InstanceModuleRepository;
import com.nexigroup.pagopa.cruscotto.repository.InstanceRepository;
import com.nexigroup.pagopa.cruscotto.repository.KpiB4ResultRepository;
import com.nexigroup.pagopa.cruscotto.service.dto.KpiB4ResultDTO;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class KpiB4ResultServiceImplTest {

    @Mock
    private InstanceRepository instanceRepository;

    @Mock
    private InstanceModuleRepository instanceModuleRepository;

    @Mock
    private KpiB4ResultRepository kpiB4ResultRepository;

    @InjectMocks
    private KpiB4ResultServiceImpl service;

    // -------------------------
    // Test for save()
    // -------------------------
    @Test
    void save_shouldPersistAndReturnDTO() {
        // given
        KpiB4ResultDTO dto = buildDTO();
        Instance instance = new Instance();
        instance.setId(1L);
        InstanceModule module = new InstanceModule();
        module.setId(2L);
        KpiB4Result savedResult = new KpiB4Result();
        savedResult.setId(10L);

        when(instanceRepository.findById(dto.getInstanceId())).thenReturn(Optional.of(instance));
        when(instanceModuleRepository.findById(dto.getInstanceModuleId())).thenReturn(Optional.of(module));
        when(kpiB4ResultRepository.save(any(KpiB4Result.class))).thenReturn(savedResult);

        // when
        KpiB4ResultDTO result = service.save(dto);

        // then
        assertNotNull(result);
        assertEquals(savedResult.getId(), result.getId());
        verify(kpiB4ResultRepository).save(any(KpiB4Result.class));
    }

    @Test
    void save_shouldThrowException_whenInstanceNotFound() {
        KpiB4ResultDTO dto = buildDTO();
        when(instanceRepository.findById(dto.getInstanceId())).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () -> service.save(dto));
        verify(instanceRepository).findById(dto.getInstanceId());
        verifyNoInteractions(instanceModuleRepository, kpiB4ResultRepository);
    }

    @Test
    void save_shouldThrowException_whenModuleNotFound() {
        KpiB4ResultDTO dto = buildDTO();
        Instance instance = new Instance();
        instance.setId(1L);

        when(instanceRepository.findById(dto.getInstanceId())).thenReturn(Optional.of(instance));
        when(instanceModuleRepository.findById(dto.getInstanceModuleId())).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () -> service.save(dto));
        verify(instanceModuleRepository).findById(dto.getInstanceModuleId());
        verifyNoInteractions(kpiB4ResultRepository);
    }

    // -------------------------
    // Test for deleteAllByInstanceModule()
    // -------------------------
    @Test
    void deleteAllByInstanceModule_shouldReturnDeletedCount() {
        when(kpiB4ResultRepository.deleteAllByInstanceModuleId(2L)).thenReturn(5);

        int deleted = service.deleteAllByInstanceModule(2L);

        assertEquals(5, deleted);
        verify(kpiB4ResultRepository).deleteAllByInstanceModuleId(2L);
    }

    // -------------------------
    // Test for updateKpiB4ResultOutcome()
    // -------------------------
    @Test
    void updateKpiB4ResultOutcome_shouldUpdateOutcomeWhenPresent() {
        KpiB4Result result = new KpiB4Result();
        result.setOutcome(OutcomeStatus.STANDBY);

        when(kpiB4ResultRepository.findById(10L)).thenReturn(Optional.of(result));
        when(kpiB4ResultRepository.save(result)).thenReturn(result);

        service.updateKpiB4ResultOutcome(10L, OutcomeStatus.OK);

        assertEquals(OutcomeStatus.OK, result.getOutcome());
        verify(kpiB4ResultRepository).save(result);
    }

    @Test
    void updateKpiB4ResultOutcome_shouldDoNothingIfNotFound() {
        when(kpiB4ResultRepository.findById(10L)).thenReturn(Optional.empty());

        service.updateKpiB4ResultOutcome(10L, OutcomeStatus.KO);

        verify(kpiB4ResultRepository, never()).save(any());
    }

    // -------------------------
    // Test for findByInstanceModuleId()
    // -------------------------
    @Test
    void findByInstanceModuleId_shouldReturnDTOList() {
        Instance instance = new Instance();
        instance.setId(1L);
        InstanceModule module = new InstanceModule();
        module.setId(2L);

        KpiB4Result result = new KpiB4Result();
        result.setId(5L);
        result.setInstance(instance);
        result.setInstanceModule(module);
        result.setAnalysisDate(LocalDate.now());
        result.setExcludePlannedShutdown(true);
        result.setExcludeUnplannedShutdown(false);
        result.setEligibilityThreshold(0.8);
        result.setTolerance(0.05);
        result.setEvaluationType(EvaluationType.MESE);
        result.setOutcome(OutcomeStatus.OK);

        when(kpiB4ResultRepository.findAllByInstanceModuleIdOrderByAnalysisDateDesc(2L))
            .thenReturn(List.of(result));

        List<KpiB4ResultDTO> dtoList = service.findByInstanceModuleId(2L);

        assertEquals(1, dtoList.size());
        assertEquals(result.getId(), dtoList.get(0).getId());
        verify(kpiB4ResultRepository).findAllByInstanceModuleIdOrderByAnalysisDateDesc(2L);
    }

    // -------------------------
    // Helper
    // -------------------------
    private KpiB4ResultDTO buildDTO() {
        KpiB4ResultDTO dto = new KpiB4ResultDTO();
        dto.setInstanceId(1L);
        dto.setInstanceModuleId(2L);
        dto.setAnalysisDate(LocalDate.now());
        dto.setExcludePlannedShutdown(true);
        dto.setExcludeUnplannedShutdown(false);
        dto.setEligibilityThreshold(0.8);
        dto.setTolerance(0.05);
        dto.setEvaluationType(EvaluationType.MESE);
        dto.setOutcome(OutcomeStatus.STANDBY);
        return dto;
    }
}
