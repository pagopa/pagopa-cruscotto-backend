package com.nexigroup.pagopa.cruscotto.service.impl;

import com.nexigroup.pagopa.cruscotto.domain.PagopaApiLogDrilldown;
import com.nexigroup.pagopa.cruscotto.repository.PagopaApiLogDrilldownRepository;
import com.nexigroup.pagopa.cruscotto.service.dto.PagopaAPILogDTO;
import com.nexigroup.pagopa.cruscotto.service.mapper.PagopaApiLogDrilldownMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PagopaApiLogDrilldownServiceImplTest {

    @Mock
    private PagopaApiLogDrilldownRepository pagopaApiLogDrilldownRepository;

    @Mock
    private PagopaApiLogDrilldownMapper pagopaApiLogDrilldownMapper;

    @InjectMocks
    private PagopaApiLogDrilldownServiceImpl service;

    private PagopaApiLogDrilldown entity;
    private PagopaAPILogDTO dto;

    @BeforeEach
    void setUp() {
        entity = new PagopaApiLogDrilldown();
        dto = new PagopaAPILogDTO();
    }

    @Test
    void testSave() {
        when(pagopaApiLogDrilldownMapper.toEntity(dto)).thenReturn(entity);
        when(pagopaApiLogDrilldownRepository.save(entity)).thenReturn(entity);
        when(pagopaApiLogDrilldownMapper.toDto(entity)).thenReturn(dto);

        PagopaAPILogDTO result = service.save(dto);

        assertNotNull(result);
        verify(pagopaApiLogDrilldownMapper).toEntity(dto);
        verify(pagopaApiLogDrilldownRepository).save(entity);
        verify(pagopaApiLogDrilldownMapper).toDto(entity);
    }

    @Test
    void testFindByKpiB4AnalyticDataId() {
        Long analyticDataId = 1L;
        when(pagopaApiLogDrilldownRepository.findByKpiB4AnalyticDataId(analyticDataId))
            .thenReturn(List.of(entity));
        when(pagopaApiLogDrilldownMapper.toDto(entity)).thenReturn(dto);

        List<PagopaAPILogDTO> result = service.findByKpiB4AnalyticDataId(analyticDataId);

        assertEquals(1, result.size());
        verify(pagopaApiLogDrilldownRepository).findByKpiB4AnalyticDataId(analyticDataId);
        verify(pagopaApiLogDrilldownMapper).toDto(entity);
    }

    @Test
    void testFindByKpiB8AnalyticDataId() {
        Long analyticDataId = 2L;
        when(pagopaApiLogDrilldownRepository.findByKpiB8AnalyticDataId(analyticDataId))
            .thenReturn(List.of(entity));
        when(pagopaApiLogDrilldownMapper.toDto(entity)).thenReturn(dto);

        List<PagopaAPILogDTO> result = service.findByKpiB8AnalyticDataId(analyticDataId);

        assertEquals(1, result.size());
        verify(pagopaApiLogDrilldownRepository).findByKpiB8AnalyticDataId(analyticDataId);
        verify(pagopaApiLogDrilldownMapper).toDto(entity);
    }

    @Test
    void testFindByInstanceIdAndAnalysisDate() {
        Long instanceId = 10L;
        LocalDate analysisDate = LocalDate.now();

        when(pagopaApiLogDrilldownRepository.findByInstanceIdAndAnalysisDate(instanceId, analysisDate))
            .thenReturn(List.of(entity));
        when(pagopaApiLogDrilldownMapper.toDto(entity)).thenReturn(dto);

        List<PagopaAPILogDTO> result = service.findByInstanceIdAndAnalysisDate(instanceId, analysisDate);

        assertEquals(1, result.size());
        verify(pagopaApiLogDrilldownRepository).findByInstanceIdAndAnalysisDate(instanceId, analysisDate);
        verify(pagopaApiLogDrilldownMapper).toDto(entity);
    }

    @Test
    void testDeleteAllByInstanceModuleId() {
        Long moduleId = 99L;
        when(pagopaApiLogDrilldownRepository.deleteAllByInstanceModuleId(moduleId)).thenReturn(3);

        int deleted = service.deleteAllByInstanceModuleId(moduleId);

        assertEquals(3, deleted);
        verify(pagopaApiLogDrilldownRepository).deleteAllByInstanceModuleId(moduleId);
    }

    @Test
    void testDeleteByInstanceModuleIdAndAnalysisDate() {
        Long moduleId = 5L;
        LocalDate date = LocalDate.now();
        when(pagopaApiLogDrilldownRepository.deleteByInstanceModuleIdAndAnalysisDate(moduleId, date))
            .thenReturn(2);

        int deleted = service.deleteByInstanceModuleIdAndAnalysisDate(moduleId, date);

        assertEquals(2, deleted);
        verify(pagopaApiLogDrilldownRepository).deleteByInstanceModuleIdAndAnalysisDate(moduleId, date);
    }
}
