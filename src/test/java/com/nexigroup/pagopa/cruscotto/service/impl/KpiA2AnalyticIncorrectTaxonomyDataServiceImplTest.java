package com.nexigroup.pagopa.cruscotto.service.impl;

import com.nexigroup.pagopa.cruscotto.domain.KpiA2AnalyticIncorrectTaxonomyData;
import com.nexigroup.pagopa.cruscotto.repository.KpiA2AnalyticIncorrectTaxonomyDataRepository;
import com.nexigroup.pagopa.cruscotto.service.dto.KpiA2AnalyticIncorrectTaxonomyDataDTO;
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

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("KpiA2AnalyticIncorrectTaxonomyDataServiceImpl Tests")
class KpiA2AnalyticIncorrectTaxonomyDataServiceImplTest {

    @Mock
    private KpiA2AnalyticIncorrectTaxonomyDataRepository repository;

    @InjectMocks
    private KpiA2AnalyticIncorrectTaxonomyDataServiceImpl service;

    @Test
    void testSaveAll() {
        KpiA2AnalyticIncorrectTaxonomyDataDTO dto1 = new KpiA2AnalyticIncorrectTaxonomyDataDTO();
        dto1.setKpiA2AnalyticDataId(1L);
        dto1.setTransferCategory("Category1");
        dto1.setCoTotalIncorrectPayments(10L);
        dto1.setCoTotalPayments(100L);
        dto1.setFromHour(Instant.ofEpochSecond(8));
        dto1.setEndHour(Instant.ofEpochSecond(10));

        KpiA2AnalyticIncorrectTaxonomyDataDTO dto2 = new KpiA2AnalyticIncorrectTaxonomyDataDTO();
        dto2.setKpiA2AnalyticDataId(1L);
        dto2.setTransferCategory("Category2");
        dto2.setCoTotalIncorrectPayments(5L);
        dto2.setCoTotalPayments(50L);
        dto2.setFromHour(Instant.ofEpochSecond(10));
        dto2.setEndHour(Instant.ofEpochSecond(12));

        List<KpiA2AnalyticIncorrectTaxonomyDataDTO> dtos = Arrays.asList(dto1, dto2);

        service.saveAll(dtos);

        // Capture the saved entities
        ArgumentCaptor<List<KpiA2AnalyticIncorrectTaxonomyData>> captor = ArgumentCaptor.forClass(List.class);
        verify(repository, times(1)).saveAll(captor.capture());

        List<KpiA2AnalyticIncorrectTaxonomyData> savedEntities = captor.getValue();
        assertEquals(2, savedEntities.size());
        assertEquals("Category1", savedEntities.get(0).getTransferCategory());
        assertEquals("Category2", savedEntities.get(1).getTransferCategory());
        assertEquals(Instant.ofEpochSecond(8), savedEntities.get(0).getFromHour());
        assertEquals(Instant.ofEpochSecond(12), savedEntities.get(1).getEndHour());
    }

    @Test
    void testFindByKpiA2AnalyticDataId() {
        KpiA2AnalyticIncorrectTaxonomyData entity1 = new KpiA2AnalyticIncorrectTaxonomyData();
        entity1.setId(1L);
        entity1.setKpiA2AnalyticDataId(1L);
        entity1.setTransferCategory("Category1");
        entity1.setCoTotalIncorrectPayments(10L);
        entity1.setTotPayments(100L);
        entity1.setFromHour(Instant.ofEpochSecond(8));
        entity1.setEndHour(Instant.ofEpochSecond(10));

        KpiA2AnalyticIncorrectTaxonomyData entity2 = new KpiA2AnalyticIncorrectTaxonomyData();
        entity2.setId(2L);
        entity2.setKpiA2AnalyticDataId(1L);
        entity2.setTransferCategory("Category2");
        entity2.setCoTotalIncorrectPayments(5L);
        entity2.setTotPayments(50L);
        entity2.setFromHour(Instant.ofEpochSecond(10));
        entity2.setEndHour(Instant.ofEpochSecond(12));

        when(repository.findByKpiA2AnalyticDataIdOrderByFromHourAsc(1L))
            .thenReturn(Arrays.asList(entity1, entity2));

        List<KpiA2AnalyticIncorrectTaxonomyDataDTO> dtos = service.findByKpiA2AnalyticDataId(1L);

        assertEquals(2, dtos.size());
        assertEquals("Category1", dtos.get(0).getTransferCategory());
        assertEquals("Category2", dtos.get(1).getTransferCategory());

        // Correctly compare Instant epoch seconds
        assertEquals(8L, dtos.get(0).getFromHour().getEpochSecond());
        assertEquals(12L, dtos.get(1).getEndHour().getEpochSecond());

        verify(repository, times(1)).findByKpiA2AnalyticDataIdOrderByFromHourAsc(1L);
    }
}
