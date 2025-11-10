package com.nexigroup.pagopa.cruscotto.service.impl;

import com.nexigroup.pagopa.cruscotto.domain.Instance;
import com.nexigroup.pagopa.cruscotto.domain.InstanceModule;
import com.nexigroup.pagopa.cruscotto.domain.KpiA2Result;
import com.nexigroup.pagopa.cruscotto.domain.enumeration.OutcomeStatus;
import com.nexigroup.pagopa.cruscotto.repository.InstanceModuleRepository;
import com.nexigroup.pagopa.cruscotto.repository.InstanceRepository;
import com.nexigroup.pagopa.cruscotto.repository.KpiA2ResultRepository;
import com.nexigroup.pagopa.cruscotto.service.dto.KpiA2ResultDTO;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
@DisplayName("KpiA2ResultServiceImpl Tests")
class KpiA2ResultServiceImplTest {

    @Mock
    private InstanceRepository instanceRepository;

    @Mock
    private InstanceModuleRepository instanceModuleRepository;

    @Mock
    private KpiA2ResultRepository kpiA2ResultRepository;


    @InjectMocks
    private KpiA2ResultServiceImpl service;

    @Test
    void testSaveSuccess() {
        // Arrange
        Instance instance = new Instance();
        instance.setId(1L);
        InstanceModule module = new InstanceModule();
        module.setId(2L);

        KpiA2ResultDTO dto = new KpiA2ResultDTO();
        dto.setInstanceId(1L);
        dto.setInstanceModuleId(2L);
        dto.setAnalysisDate(LocalDate.now());
        dto.setTolerance(0.5);
        dto.setOutcome(OutcomeStatus.OK);

        KpiA2Result savedResult = new KpiA2Result();
        savedResult.setId(100L);
        savedResult.setInstance(instance);
        savedResult.setInstanceModule(module);
        savedResult.setAnalysisDate(dto.getAnalysisDate());
        savedResult.setTolerance(dto.getTolerance());
        savedResult.setOutcome(dto.getOutcome());

        when(instanceRepository.findById(1L)).thenReturn(Optional.of(instance));
        when(instanceModuleRepository.findById(2L)).thenReturn(Optional.of(module));
        when(kpiA2ResultRepository.save(any(KpiA2Result.class))).thenReturn(savedResult);

        // Act
        KpiA2ResultDTO result = service.save(dto);

        // Assert
        assertEquals(100L, result.getId());
        verify(kpiA2ResultRepository, times(1)).save(any(KpiA2Result.class));
    }

    @Test
    void testSaveInstanceNotFound() {
        KpiA2ResultDTO dto = new KpiA2ResultDTO();
        dto.setInstanceId(1L);
        dto.setInstanceModuleId(2L);

        when(instanceRepository.findById(1L)).thenReturn(Optional.empty());

        Exception exception = assertThrows(IllegalArgumentException.class, () -> service.save(dto));
        assertEquals("Instance not found", exception.getMessage());
    }

    @Test
    void testDeleteAllByInstanceModule() {
        when(kpiA2ResultRepository.deleteAllByInstanceModuleId(2L)).thenReturn(3);

        int deletedCount = service.deleteAllByInstanceModule(2L);

        assertEquals(3, deletedCount);
        verify(kpiA2ResultRepository, times(1)).deleteAllByInstanceModuleId(2L);
    }

    @Test
    void testFindByInstanceModuleId() {
        InstanceModule module = new InstanceModule();
        module.setId(2L);
        KpiA2Result result = new KpiA2Result();
        result.setId(100L);
        result.setInstanceModule(module);
        result.setAnalysisDate(LocalDate.now());
        result.setTolerance(0.5);
        result.setOutcome(OutcomeStatus.OK);

        when(kpiA2ResultRepository.selectByInstanceModuleId(2L)).thenReturn(List.of(result));

        List<KpiA2ResultDTO> dtoList = service.findByInstanceModuleId(2L);

        assertEquals(1, dtoList.size());
        assertEquals(100L, dtoList.get(0).getId());
        assertEquals(2L, dtoList.get(0).getInstanceModuleId());
    }
}
