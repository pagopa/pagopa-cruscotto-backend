package com.nexigroup.pagopa.cruscotto.service.impl;

import com.nexigroup.pagopa.cruscotto.domain.*;
import com.nexigroup.pagopa.cruscotto.repository.*;
import com.nexigroup.pagopa.cruscotto.service.dto.KpiB1AnalyticDataDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class KpiB1AnalyticDataServiceImplTest {

    @Mock
    private InstanceRepository instanceRepository;

    @Mock
    private InstanceModuleRepository instanceModuleRepository;

    @Mock
    private KpiB1AnalyticDataRepository kpiB1AnalyticDataRepository;

    @Mock
    private KpiB1DetailResultRepository kpiB1DetailResultRepository;

    @InjectMocks
    private KpiB1AnalyticDataServiceImpl service;

    private Instance instance;
    private InstanceModule instanceModule;
    private KpiB1DetailResult detailResult;
    private KpiB1AnalyticDataDTO dto;

    @BeforeEach
    void setUp() {

        instance = new Instance();
        instance.setId(1L);

        instanceModule = new InstanceModule();
        instanceModule.setId(2L);

        detailResult = new KpiB1DetailResult();
        detailResult.setId(3L);

        dto = new KpiB1AnalyticDataDTO();
        dto.setInstanceId(1L);
        dto.setInstanceModuleId(2L);
        dto.setKpiB1DetailResultId(3L);
        dto.setAnalysisDate(LocalDate.now());
        dto.setDataDate(LocalDate.now().minusDays(1));
        dto.setInstitutionCount(10);
        dto.setTransactionCount(100);
    }

    @Test
    void testSave() {
        when(instanceRepository.findById(1L)).thenReturn(Optional.of(instance));
        when(instanceModuleRepository.findById(2L)).thenReturn(Optional.of(instanceModule));
        when(kpiB1DetailResultRepository.findById(3L)).thenReturn(Optional.of(detailResult));

        KpiB1AnalyticData savedEntity = new KpiB1AnalyticData();
        savedEntity.setId(5L);
        when(kpiB1AnalyticDataRepository.save(any())).thenReturn(savedEntity);

        KpiB1AnalyticDataDTO result = service.save(dto);

        assertNotNull(result);
        assertEquals(5L, result.getId());

        verify(kpiB1AnalyticDataRepository, times(1)).save(any(KpiB1AnalyticData.class));
    }

    @Test
    void testSaveAll() {
        when(instanceRepository.findById(anyLong())).thenReturn(Optional.of(instance));
        when(instanceModuleRepository.findById(anyLong())).thenReturn(Optional.of(instanceModule));
        when(kpiB1DetailResultRepository.findById(anyLong())).thenReturn(Optional.of(detailResult));

        service.saveAll(Arrays.asList(dto, dto));

        verify(kpiB1AnalyticDataRepository, times(2)).save(any(KpiB1AnalyticData.class));
    }

    @Test
    void testDeleteAllByInstanceModule() {
        when(kpiB1AnalyticDataRepository.deleteAllByInstanceModuleId(2L)).thenReturn(3);

        int deleted = service.deleteAllByInstanceModule(2L);

        assertEquals(3, deleted);
        verify(kpiB1AnalyticDataRepository, times(1)).deleteAllByInstanceModuleId(2L);
    }

}
