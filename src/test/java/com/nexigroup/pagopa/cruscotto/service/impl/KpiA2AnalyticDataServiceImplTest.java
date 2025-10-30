package com.nexigroup.pagopa.cruscotto.service.impl;

import com.nexigroup.pagopa.cruscotto.domain.*;
import com.nexigroup.pagopa.cruscotto.repository.*;
import com.nexigroup.pagopa.cruscotto.service.dto.KpiA2AnalyticDataDTO;
import com.nexigroup.pagopa.cruscotto.service.qdsl.QueryBuilder;
import com.querydsl.core.types.EntityPath;
import com.querydsl.core.types.Expression;
import com.querydsl.core.types.Predicate;
import com.querydsl.jpa.impl.JPAQuery;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("KpiA2AnalyticDataServiceImpl Tests")
class KpiA2AnalyticDataServiceImplTest {

    @Mock private InstanceRepository instanceRepository;
    @Mock private InstanceModuleRepository instanceModuleRepository;
    @Mock private KpiA2AnalyticDataRepository kpiA2AnalyticDataRepository;
    @Mock private KpiA2DetailResultRepository kpiA2DetailResultRepository;
    @Mock private QueryBuilder queryBuilder;
    @Mock private JPAQuery<Object> jpaQueryMock;

    @InjectMocks private KpiA2AnalyticDataServiceImpl service;

    @Test
    void testSaveAll_success() {
        KpiA2AnalyticDataDTO dto = new KpiA2AnalyticDataDTO();
        dto.setInstanceId(1L);
        dto.setInstanceModuleId(10L);
        dto.setKpiA2DetailResultId(100L);
        dto.setAnalysisDate(LocalDate.now());
        dto.setEvaluationDate(LocalDate.now());
        dto.setTotPayments(50L);
        dto.setTotIncorrectPayments(5L);

        Instance instance = new Instance(); instance.setId(1L);
        InstanceModule module = new InstanceModule(); module.setId(10L);
        KpiA2DetailResult detailResult = new KpiA2DetailResult(); detailResult.setId(100L);

        when(instanceRepository.findById(1L)).thenReturn(Optional.of(instance));
        when(instanceModuleRepository.findById(10L)).thenReturn(Optional.of(module));
        when(kpiA2DetailResultRepository.findById(100L)).thenReturn(Optional.of(detailResult));

        // <-- Mock save to return the saved entity
        when(kpiA2AnalyticDataRepository.save(any(KpiA2AnalyticData.class)))
            .thenAnswer(invocation -> invocation.getArgument(0));

        service.save(dto);

        verify(kpiA2AnalyticDataRepository, times(1)).save(any(KpiA2AnalyticData.class));
    }


    @Test
    void testDeleteAllByInstanceModule() {
        when(kpiA2AnalyticDataRepository.deleteAllByInstanceModuleId(10L)).thenReturn(3);
        int deleted = service.deleteAllByInstanceModule(10L);

        assertEquals(3, deleted);
        verify(kpiA2AnalyticDataRepository, times(1)).deleteAllByInstanceModuleId(10L);
    }

    @Test
    void testFindByDetailResultId() {
        when(queryBuilder.createQuery()).thenReturn(jpaQueryMock);
        when(jpaQueryMock.from((EntityPath<?>) any())).thenReturn(jpaQueryMock);
        when(jpaQueryMock.where((Predicate) any())).thenReturn(jpaQueryMock);
        when(jpaQueryMock.select((Expression<Object>) any())).thenReturn(jpaQueryMock);

        List<KpiA2AnalyticDataDTO> fakeList = List.of(new KpiA2AnalyticDataDTO());
        when(jpaQueryMock.fetch()).thenReturn(Collections.singletonList(fakeList));

        List<KpiA2AnalyticDataDTO> result = service.findByDetailResultId(100L);

        assertEquals(1, result.size());
        verify(jpaQueryMock).fetch();
    }

    @Test
    void testSaveAll_instanceNotFound() {
        KpiA2AnalyticDataDTO dto = new KpiA2AnalyticDataDTO();
        dto.setInstanceId(1L);

        when(instanceRepository.findById(1L)).thenReturn(Optional.empty());

        try {
            service.save(dto);
        } catch (IllegalArgumentException ex) {
            assertEquals("Instance not found", ex.getMessage());
        }
    }

    @Test
    void testSaveAll_instanceModuleNotFound() {
        KpiA2AnalyticDataDTO dto = new KpiA2AnalyticDataDTO();
        dto.setInstanceId(1L);
        dto.setInstanceModuleId(10L);

        when(instanceRepository.findById(1L)).thenReturn(Optional.of(new Instance()));
        when(instanceModuleRepository.findById(10L)).thenReturn(Optional.empty());

        try {
            service.save(dto);
        } catch (IllegalArgumentException ex) {
            assertEquals("InstanceModule not found", ex.getMessage());
        }
    }

    @Test
    void testSaveAll_detailResultNotFound() {
        KpiA2AnalyticDataDTO dto = new KpiA2AnalyticDataDTO();
        dto.setInstanceId(1L);
        dto.setInstanceModuleId(10L);
        dto.setKpiA2DetailResultId(100L);

        when(instanceRepository.findById(1L)).thenReturn(Optional.of(new Instance()));
        when(instanceModuleRepository.findById(10L)).thenReturn(Optional.of(new InstanceModule()));
        when(kpiA2DetailResultRepository.findById(100L)).thenReturn(Optional.empty());

        try {
            service.save(dto);
        } catch (IllegalArgumentException ex) {
            assertEquals("KpiA2DetailResult not found", ex.getMessage());
        }
    }
}
