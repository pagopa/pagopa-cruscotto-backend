package com.nexigroup.pagopa.cruscotto.service.impl;

import com.nexigroup.pagopa.cruscotto.domain.*;
import com.nexigroup.pagopa.cruscotto.repository.*;
import com.nexigroup.pagopa.cruscotto.service.dto.KpiA2DetailResultDTO;
import com.nexigroup.pagopa.cruscotto.service.qdsl.QueryBuilder;
import com.querydsl.core.types.EntityPath;
import com.querydsl.core.types.Expression;
import com.querydsl.core.types.Predicate;
import com.querydsl.jpa.impl.JPAQuery;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("KpiA2DetailResultServiceImpl Tests")
class KpiA2DetailResultServiceImplTest {

    @Mock
    private InstanceRepository instanceRepository;

    @Mock
    private InstanceModuleRepository instanceModuleRepository;

    @Mock
    private KpiA2DetailResultRepository kpiA2DetailResultRepository;

    @Mock
    private KpiA2ResultRepository kpiA2ResultRepository;

    @Mock
    private QueryBuilder queryBuilder;

    @Mock
    @SuppressWarnings("rawtypes")
    private JPAQuery jpqlQuery;

    @InjectMocks
    private KpiA2DetailResultServiceImpl service;

    private Instance instance;
    private InstanceModule instanceModule;
    private KpiA2Result kpiA2Result;

    @BeforeEach
    void setUp() {
        instance = new Instance();
        instance.setId(1L);

        instanceModule = new InstanceModule();
        instanceModule.setId(2L);

        kpiA2Result = new KpiA2Result();
        kpiA2Result.setId(3L);
    }

    @Test
    void save_shouldPersistAndReturnDto() {
        KpiA2DetailResultDTO dto = new KpiA2DetailResultDTO();
        dto.setInstanceId(1L);
        dto.setInstanceModuleId(2L);
        dto.setKpiA2ResultId(3L);
        dto.setAnalysisDate(LocalDate.now());
        dto.setTotPayments(10L);

        when(instanceRepository.findById(1L)).thenReturn(Optional.of(instance));
        when(instanceModuleRepository.findById(2L)).thenReturn(Optional.of(instanceModule));
        when(kpiA2ResultRepository.findById(3L)).thenReturn(Optional.of(kpiA2Result));

        KpiA2DetailResult savedEntity = new KpiA2DetailResult();
        savedEntity.setId(99L);
        when(kpiA2DetailResultRepository.save(any())).thenReturn(savedEntity);

        KpiA2DetailResultDTO result = service.save(dto);

        assertThat(result.getId()).isEqualTo(99L);
        verify(kpiA2DetailResultRepository).save(any(KpiA2DetailResult.class));
    }

    @Test
    void save_shouldThrowIfInstanceNotFound() {
        KpiA2DetailResultDTO dto = new KpiA2DetailResultDTO();
        dto.setInstanceId(1L);
        when(instanceRepository.findById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.save(dto))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("Instance not found");
    }

    @Test
    void save_shouldThrowIfInstanceModuleNotFound() {
        KpiA2DetailResultDTO dto = new KpiA2DetailResultDTO();
        dto.setInstanceId(1L);
        dto.setInstanceModuleId(2L);
        when(instanceRepository.findById(1L)).thenReturn(Optional.of(instance));
        when(instanceModuleRepository.findById(2L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.save(dto))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("InstanceModule not found");
    }

    @Test
    void save_shouldThrowIfKpiA2ResultNotFound() {
        KpiA2DetailResultDTO dto = new KpiA2DetailResultDTO();
        dto.setInstanceId(1L);
        dto.setInstanceModuleId(2L);
        dto.setKpiA2ResultId(3L);

        when(instanceRepository.findById(1L)).thenReturn(Optional.of(instance));
        when(instanceModuleRepository.findById(2L)).thenReturn(Optional.of(instanceModule));
        when(kpiA2ResultRepository.findById(3L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.save(dto))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("KpiA2Result not found");
    }

    @Test
    void deleteAllByInstanceModule_shouldDelegateToRepository() {
        when(kpiA2DetailResultRepository.deleteAllByInstanceModuleId(2L)).thenReturn(5);

        int deleted = service.deleteAllByInstanceModule(2L);

        assertThat(deleted).isEqualTo(5);
        verify(kpiA2DetailResultRepository).deleteAllByInstanceModuleId(2L);
    }

    @Test
    @SuppressWarnings("unchecked")
    void findByResultId_shouldReturnQueryResults() {
        when(queryBuilder.createQuery()).thenReturn(jpqlQuery);
        when(jpqlQuery.from((EntityPath<?>) any())).thenReturn(jpqlQuery);
        when(jpqlQuery.where((Predicate) any())).thenReturn(jpqlQuery);
        when(jpqlQuery.select((Expression) any())).thenReturn(jpqlQuery);
        when(jpqlQuery.fetch()).thenReturn((List) List.of(new KpiA2DetailResultDTO()));

        List<KpiA2DetailResultDTO> results = service.findByResultId(3L);

        assertThat(results).hasSize(1);
        verify(queryBuilder).createQuery();
        verify(jpqlQuery).fetch();
        verify(jpqlQuery).select((Expression) any());
        verify(jpqlQuery).from((EntityPath<?>) any());
        verify(jpqlQuery).where((Predicate) any());
    }

}
