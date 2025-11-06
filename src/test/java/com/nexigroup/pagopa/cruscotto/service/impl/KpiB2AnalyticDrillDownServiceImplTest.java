package com.nexigroup.pagopa.cruscotto.service.impl;

import com.nexigroup.pagopa.cruscotto.domain.KpiB2AnalyticDrillDown;
import com.nexigroup.pagopa.cruscotto.repository.KpiB2AnalyticDrillDownRepository;
import com.nexigroup.pagopa.cruscotto.service.dto.KpiB2AnalyticDrillDownDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("KpiB2AnalyticDrillDownServiceImpl Tests")
class KpiB2AnalyticDrillDownServiceImplTest {

    private KpiB2AnalyticDrillDownRepository repository;
    private KpiB2AnalyticDrillDownServiceImpl service;

    @BeforeEach
    void setUp() {
        repository = mock(KpiB2AnalyticDrillDownRepository.class);
        service = new KpiB2AnalyticDrillDownServiceImpl(repository);
    }

    @Test
    void testSaveAll() {
        KpiB2AnalyticDrillDownDTO dto1 = new KpiB2AnalyticDrillDownDTO();
        dto1.setKpiB2AnalyticDataId(1L);
        dto1.setTotalRequests(10L);
        dto1.setOkRequests(9L);
        dto1.setAverageTimeMs(100.0);
        dto1.setFromHour(Instant.ofEpochSecond(1));
        dto1.setEndHour(Instant.ofEpochSecond(2));

        KpiB2AnalyticDrillDownDTO dto2 = new KpiB2AnalyticDrillDownDTO();
        dto2.setKpiB2AnalyticDataId(2L);
        dto2.setTotalRequests(20L);
        dto2.setOkRequests(18L);
        dto2.setAverageTimeMs(200.0);
        dto2.setFromHour(Instant.ofEpochSecond(2));
        dto2.setEndHour(Instant.ofEpochSecond(3));

        List<KpiB2AnalyticDrillDownDTO> dtos = Arrays.asList(dto1, dto2);

        service.saveAll(dtos);

        ArgumentCaptor<List<KpiB2AnalyticDrillDown>> captor = ArgumentCaptor.forClass(List.class);
        verify(repository, times(1)).saveAll(captor.capture());

        List<KpiB2AnalyticDrillDown> savedEntities = captor.getValue();
        assertEquals(2, savedEntities.size());
        assertEquals(dto1.getKpiB2AnalyticDataId(), savedEntities.get(0).getKpiB2AnalyticDataId());
        assertEquals(dto2.getTotalRequests(), savedEntities.get(1).getTotalRequests());
    }

    @Test
    void testFindByKpiB2AnalyticDataId() {
        KpiB2AnalyticDrillDown entity = new KpiB2AnalyticDrillDown();
        entity.setId(1L);
        entity.setKpiB2AnalyticDataId(100L);
        entity.setTotalRequests(50L);
        entity.setOkRequests(45L);
        entity.setAverageTimeMs(120.0);
        entity.setFromHour(Instant.ofEpochSecond(0));
        entity.setEndHour(Instant.ofEpochSecond(1));

        when(repository.findByKpiB2AnalyticDataIdOrderByFromHourAsc(100L))
            .thenReturn(Collections.singletonList(entity));

        List<KpiB2AnalyticDrillDownDTO> result = service.findByKpiB2AnalyticDataId(100L);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(entity.getId(), result.get(0).getId());
        assertEquals(entity.getTotalRequests(), result.get(0).getTotalRequests());
    }

    @Test
    void testDeleteByKpiB2AnalyticDataIds() {
        List<Long> ids = Arrays.asList(1L, 2L, 3L);
        service.deleteByKpiB2AnalyticDataIds(ids);
        verify(repository, times(1)).deleteByKpiB2AnalyticDataIdIn(ids);
    }

    @Test
    void testDeleteByKpiB2AnalyticDataIdsEmptyOrNull() {
        service.deleteByKpiB2AnalyticDataIds(null);
        service.deleteByKpiB2AnalyticDataIds(Collections.emptyList());
        verify(repository, never()).deleteByKpiB2AnalyticDataIdIn(anyList());
    }
}
