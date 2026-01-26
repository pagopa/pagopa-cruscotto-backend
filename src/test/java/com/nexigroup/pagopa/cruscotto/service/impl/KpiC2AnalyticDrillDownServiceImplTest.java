package com.nexigroup.pagopa.cruscotto.service.impl;

import com.nexigroup.pagopa.cruscotto.domain.KpiC2AnalyticDrillDown;
import com.nexigroup.pagopa.cruscotto.repository.KpiC2AnalyticDrillDownRepository;
import com.nexigroup.pagopa.cruscotto.service.dto.KpiC2AnalyticDrillDownDTO;
import com.nexigroup.pagopa.cruscotto.service.mapper.KpiC2DrillDownMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class KpiC2AnalyticDrillDownServiceImplTest {

    @Mock
    private KpiC2AnalyticDrillDownRepository repository;

    @Mock
    private KpiC2DrillDownMapper mapper;

    @InjectMocks
    private KpiC2AnalyticDrillDownServiceImpl service;

    private KpiC2AnalyticDrillDown entity;
    private KpiC2AnalyticDrillDownDTO dto;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        entity = new KpiC2AnalyticDrillDown();
        entity.setId(1L);

        dto = new KpiC2AnalyticDrillDownDTO();
        dto.setId(1L);
    }

    @Test
    void testSave() {
        when(mapper.toEntity(dto)).thenReturn(entity);
        when(repository.save(entity)).thenReturn(entity);
        when(mapper.toDto(entity)).thenReturn(dto);

        KpiC2AnalyticDrillDownDTO result = service.save(dto);

        assertEquals(dto, result);
        verify(repository, times(1)).save(entity);
        verify(mapper, times(1)).toEntity(dto);
        verify(mapper, times(1)).toDto(entity);
    }

    @Test
    void testFindByKpiC2AnalyticDataId() {
        when(repository.selectByKpiC2AnalyticDataId(1L)).thenReturn(Arrays.asList(entity));
        when(mapper.toDto(entity)).thenReturn(dto);

        List<KpiC2AnalyticDrillDownDTO> result = service.findByKpiC2AnalyticDataId(1L);

        assertEquals(1, result.size());
        assertEquals(dto, result.get(0));
        verify(repository, times(1)).selectByKpiC2AnalyticDataId(1L);
        verify(mapper, times(1)).toDto(entity);
    }

    @Test
    void testFindByInstanceIdAndAnalysisDate() {
        LocalDate date = LocalDate.now();
        when(repository.findByInstanceIdAndAnalysisDate(1L, date)).thenReturn(Arrays.asList(entity));
        when(mapper.toDto(entity)).thenReturn(dto);

        List<KpiC2AnalyticDrillDownDTO> result = service.findByInstanceIdAndAnalysisDate(1L, date);

        assertEquals(1, result.size());
        assertEquals(dto, result.get(0));
        verify(repository, times(1)).findByInstanceIdAndAnalysisDate(1L, date);
        verify(mapper, times(1)).toDto(entity);
    }

    @Test
    void testDeleteAllByInstanceModuleId() {
        when(repository.deleteAllByInstanceModuleId(1L)).thenReturn(5);

        int deleted = service.deleteAllByInstanceModuleId(1L);

        assertEquals(5, deleted);
        verify(repository, times(1)).deleteAllByInstanceModuleId(1L);
    }

    @Test
    void testDeleteByInstanceModuleIdAndAnalysisDate() {
        LocalDate date = LocalDate.now();
        when(repository.deleteByInstanceModuleIdAndAnalysisDate(1L, date)).thenReturn(3);

        int deleted = service.deleteByInstanceModuleIdAndAnalysisDate(1L, date);

        assertEquals(3, deleted);
        verify(repository, times(1)).deleteByInstanceModuleIdAndAnalysisDate(1L, date);
    }
}
