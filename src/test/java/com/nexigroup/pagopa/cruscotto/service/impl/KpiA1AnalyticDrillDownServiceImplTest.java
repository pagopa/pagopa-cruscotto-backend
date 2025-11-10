package com.nexigroup.pagopa.cruscotto.service.impl;

import com.nexigroup.pagopa.cruscotto.domain.KpiA1AnalyticDrillDown;
import com.nexigroup.pagopa.cruscotto.repository.KpiA1AnalyticDrillDownRepository;
import com.nexigroup.pagopa.cruscotto.service.dto.KpiA1AnalyticDrillDownDTO;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("KpiA1AnalyticDrillDownServiceImpl Tests")
class KpiA1AnalyticDrillDownServiceImplTest {

    @Mock
    private KpiA1AnalyticDrillDownRepository repository;

    @InjectMocks
    private KpiA1AnalyticDrillDownServiceImpl service;

    @Test
    void testDeleteByKpiA1AnalyticDataIds() {
        List<Long> ids = Arrays.asList(1L, 2L, 3L);
        service.deleteByKpiA1AnalyticDataIds(ids);
        verify(repository, times(1)).deleteByKpiA1AnalyticDataIdIn(ids);
    }

    @Test
    void testFindByKpiA1AnalyticDataId() {
        KpiA1AnalyticDrillDown entity1 = new KpiA1AnalyticDrillDown();
        entity1.setId(1L);
        entity1.setKpiA1AnalyticDataId(10L);
        entity1.setFromHour(Instant.ofEpochSecond(0));
        entity1.setToHour(Instant.ofEpochSecond(1));
        entity1.setTotalRequests(100L);
        entity1.setOkRequests(90L);
        entity1.setReqTimeout(10L);

        when(repository.findByKpiA1AnalyticDataIdOrderByFromHourAsc(10L))
            .thenReturn(List.of(entity1));

        List<KpiA1AnalyticDrillDownDTO> result = service.findByKpiA1AnalyticDataId(10L);

        assertThat(result).hasSize(1);
        KpiA1AnalyticDrillDownDTO dto = result.get(0);
        assertThat(dto.getId()).isEqualTo(entity1.getId());
        assertThat(dto.getKpiA1AnalyticDataId()).isEqualTo(entity1.getKpiA1AnalyticDataId());
        assertThat(dto.getFromHour()).isEqualTo(entity1.getFromHour());
        assertThat(dto.getToHour()).isEqualTo(entity1.getToHour());
        assertThat(dto.getTotalRequests()).isEqualTo(entity1.getTotalRequests());
        assertThat(dto.getOkRequests()).isEqualTo(entity1.getOkRequests());
        assertThat(dto.getReqTimeout()).isEqualTo(entity1.getReqTimeout());

        verify(repository, times(1)).findByKpiA1AnalyticDataIdOrderByFromHourAsc(10L);
    }

    @Test
    void testSaveAll() {
        KpiA1AnalyticDrillDownDTO dto1 = new KpiA1AnalyticDrillDownDTO();
        dto1.setId(1L);
        dto1.setKpiA1AnalyticDataId(10L);
        dto1.setFromHour(Instant.ofEpochSecond(0));
        dto1.setToHour(Instant.ofEpochSecond(1));
        dto1.setTotalRequests(100L);
        dto1.setOkRequests(90L);
        dto1.setReqTimeout(10L);

        KpiA1AnalyticDrillDownDTO dto2 = new KpiA1AnalyticDrillDownDTO();
        dto2.setId(2L);
        dto2.setKpiA1AnalyticDataId(10L);
        dto2.setFromHour(Instant.ofEpochSecond(1));
        dto2.setToHour(Instant.ofEpochSecond(2));
        dto2.setTotalRequests(50L);
        dto2.setOkRequests(45L);
        dto2.setReqTimeout(5L);

        List<KpiA1AnalyticDrillDownDTO> dtos = Arrays.asList(dto1, dto2);

        service.saveAll(dtos);

        ArgumentCaptor<List<KpiA1AnalyticDrillDown>> captor = ArgumentCaptor.forClass(List.class);
        verify(repository, times(1)).saveAll(captor.capture());

        List<KpiA1AnalyticDrillDown> savedEntities = captor.getValue();
        assertThat(savedEntities).hasSize(2);
        assertThat(savedEntities.get(0).getId()).isEqualTo(dto1.getId());
        assertThat(savedEntities.get(1).getId()).isEqualTo(dto2.getId());
    }

    @Test
    void testDtoToEntityAndEntityToDtoMapping() {
        KpiA1AnalyticDrillDown entity = new KpiA1AnalyticDrillDown();
        entity.setId(5L);
        entity.setKpiA1AnalyticDataId(20L);
        entity.setFromHour(Instant.ofEpochSecond(2));
        entity.setToHour(Instant.ofEpochSecond(3));
        entity.setTotalRequests(200L);
        entity.setOkRequests(180L);
        entity.setReqTimeout(20L);

        // Using private methods via reflection is an option, but normally tested via public methods
        // This ensures mapping is exercised via public API (as we did above)

        when(repository.findByKpiA1AnalyticDataIdOrderByFromHourAsc(20L))
            .thenReturn(List.of(entity));

        List<KpiA1AnalyticDrillDownDTO> dtos = service.findByKpiA1AnalyticDataId(20L);

        assertThat(dtos).hasSize(1);
        KpiA1AnalyticDrillDownDTO dto = dtos.get(0);
        assertThat(dto.getId()).isEqualTo(entity.getId());
        assertThat(dto.getKpiA1AnalyticDataId()).isEqualTo(entity.getKpiA1AnalyticDataId());
        assertThat(dto.getFromHour()).isEqualTo(entity.getFromHour());
        assertThat(dto.getToHour()).isEqualTo(entity.getToHour());
        assertThat(dto.getTotalRequests()).isEqualTo(entity.getTotalRequests());
        assertThat(dto.getOkRequests()).isEqualTo(entity.getOkRequests());
        assertThat(dto.getReqTimeout()).isEqualTo(entity.getReqTimeout());

        verify(repository, times(1)).findByKpiA1AnalyticDataIdOrderByFromHourAsc(20L);
    }
}
