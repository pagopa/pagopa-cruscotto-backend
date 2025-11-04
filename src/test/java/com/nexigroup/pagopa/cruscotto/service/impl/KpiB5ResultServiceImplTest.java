package com.nexigroup.pagopa.cruscotto.service.impl;

import com.nexigroup.pagopa.cruscotto.domain.Instance;
import com.nexigroup.pagopa.cruscotto.domain.InstanceModule;
import com.nexigroup.pagopa.cruscotto.domain.KpiB5Result;
import com.nexigroup.pagopa.cruscotto.domain.enumeration.OutcomeStatus;
import com.nexigroup.pagopa.cruscotto.repository.InstanceModuleRepository;
import com.nexigroup.pagopa.cruscotto.repository.InstanceRepository;
import com.nexigroup.pagopa.cruscotto.repository.KpiB5ResultRepository;
import com.nexigroup.pagopa.cruscotto.service.dto.KpiB5ResultDTO;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class KpiB5ResultServiceImplTest {

    @Mock
    private InstanceRepository instanceRepository;

    @Mock
    private InstanceModuleRepository instanceModuleRepository;

    @Mock
    private KpiB5ResultRepository kpiB5ResultRepository;

    @InjectMocks
    private KpiB5ResultServiceImpl service;

    private Instance instance;
    private InstanceModule instanceModule;
    private KpiB5Result kpiB5Result;
    private KpiB5ResultDTO dto;

    @BeforeEach
    void setUp() {

        instance = new Instance();
        instance.setId(1L);

        instanceModule = new InstanceModule();
        instanceModule.setId(2L);

        kpiB5Result = new KpiB5Result();
        kpiB5Result.setId(10L);
        kpiB5Result.setInstance(instance);
        kpiB5Result.setInstanceModule(instanceModule);
        kpiB5Result.setOutcome(OutcomeStatus.OK);
        kpiB5Result.setAnalysisDate(LocalDate.now());
        kpiB5Result.setThresholdIndex(BigDecimal.valueOf(0.85));
        kpiB5Result.setToleranceIndex(BigDecimal.valueOf(0.1));

        dto = new KpiB5ResultDTO();
        dto.setInstanceId(1L);
        dto.setInstanceModuleId(2L);
        dto.setOutcome(OutcomeStatus.OK);
        dto.setAnalysisDate(LocalDate.now());
        dto.setEligibilityThreshold(BigDecimal.valueOf(0.85));
        dto.setTolerance(BigDecimal.valueOf(0.1));
    }

    @Test
    void save_ShouldPersistKpiB5ResultSuccessfully() {
        when(instanceRepository.findById(1L)).thenReturn(Optional.of(instance));
        when(instanceModuleRepository.findById(2L)).thenReturn(Optional.of(instanceModule));
        when(kpiB5ResultRepository.save(any(KpiB5Result.class))).thenAnswer(invocation -> {
            KpiB5Result saved = invocation.getArgument(0);
            saved.setId(10L);
            return saved;
        });

        KpiB5ResultDTO result = service.save(dto);

        assertNotNull(result);
        assertEquals(10L, result.getId());
        verify(kpiB5ResultRepository).save(any(KpiB5Result.class));
    }

    @Test
    void save_ShouldThrowWhenInstanceNotFound() {
        when(instanceRepository.findById(1L)).thenReturn(Optional.empty());

        IllegalArgumentException ex = assertThrows(
            IllegalArgumentException.class,
            () -> service.save(dto)
        );
        assertEquals("Instance not found", ex.getMessage());
    }

    @Test
    void save_ShouldThrowWhenInstanceModuleNotFound() {
        when(instanceRepository.findById(1L)).thenReturn(Optional.of(instance));
        when(instanceModuleRepository.findById(2L)).thenReturn(Optional.empty());

        IllegalArgumentException ex = assertThrows(
            IllegalArgumentException.class,
            () -> service.save(dto)
        );
        assertEquals("InstanceModule not found", ex.getMessage());
    }

    @Test
    void deleteAllByInstanceModule_ShouldReturnDeletedCount() {
        when(kpiB5ResultRepository.deleteAllByInstanceModuleId(2L)).thenReturn(3);

        int deleted = service.deleteAllByInstanceModule(2L);

        assertEquals(3, deleted);
        verify(kpiB5ResultRepository).deleteAllByInstanceModuleId(2L);
    }

    @Test
    void updateKpiB5ResultOutcome_ShouldUpdateWhenFound() {
        when(kpiB5ResultRepository.findById(10L)).thenReturn(Optional.of(kpiB5Result));

        service.updateKpiB5ResultOutcome(10L, OutcomeStatus.KO);

        verify(kpiB5ResultRepository).save(argThat(result ->
            result.getOutcome() == OutcomeStatus.KO
        ));
    }

    @Test
    void updateKpiB5ResultOutcome_ShouldDoNothingWhenNotFound() {
        when(kpiB5ResultRepository.findById(10L)).thenReturn(Optional.empty());

        service.updateKpiB5ResultOutcome(10L, OutcomeStatus.KO);

        verify(kpiB5ResultRepository, never()).save(any());
    }

    @Test
    void findByInstanceModuleId_ShouldReturnListOfDTOs() {
        when(kpiB5ResultRepository.findAllByInstanceModuleIdOrderByAnalysisDateDesc(2L))
            .thenReturn(List.of(kpiB5Result));

        List<KpiB5ResultDTO> resultList = service.findByInstanceModuleId(2L);

        assertEquals(1, resultList.size());
        assertEquals(OutcomeStatus.OK, resultList.get(0).getOutcome());
        verify(kpiB5ResultRepository).findAllByInstanceModuleIdOrderByAnalysisDateDesc(2L);
    }
}
