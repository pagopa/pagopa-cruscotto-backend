package com.nexigroup.pagopa.cruscotto.service.impl;

import com.nexigroup.pagopa.cruscotto.domain.*;
import com.nexigroup.pagopa.cruscotto.domain.enumeration.EvaluationType;
import com.nexigroup.pagopa.cruscotto.domain.enumeration.OutcomeStatus;
import com.nexigroup.pagopa.cruscotto.repository.*;
import com.nexigroup.pagopa.cruscotto.service.dto.KpiB2DetailResultDTO;
import com.nexigroup.pagopa.cruscotto.service.qdsl.QueryBuilder;
import com.querydsl.core.types.*;
import com.querydsl.core.types.dsl.EntityPathBase;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("KpiB2DetailResultServiceImpl Tests")
class KpiB2DetailResultServiceImplTest {

    @Mock private AnagStationRepository anagStationRepository;
    @Mock private InstanceRepository instanceRepository;
    @Mock private InstanceModuleRepository instanceModuleRepository;
    @Mock private KpiB2DetailResultRepository kpiB2DetailResultRepository;
    @Mock private KpiB2ResultRepository kpiB2ResultRepository;
    @Mock private QueryBuilder queryBuilder;

    @InjectMocks
    private KpiB2DetailResultServiceImpl service;

    private KpiB2DetailResultDTO buildDto() {
        KpiB2DetailResultDTO dto = new KpiB2DetailResultDTO();
        dto.setId(1L);
        dto.setInstanceId(2L);
        dto.setInstanceModuleId(3L);
        dto.setKpiB2ResultId(5L);
        dto.setAnalysisDate(LocalDate.now());
        dto.setEvaluationType(EvaluationType.MESE);
        dto.setEvaluationStartDate(LocalDate.now().minusDays(1));
        dto.setEvaluationEndDate(LocalDate.now());
        dto.setTotReq(100L);
        dto.setAvgTime(20.0);
        dto.setOverTimeLimit(5.0);
        dto.setOutcome(OutcomeStatus.OK);
        return dto;
    }

    @Test
    void save_shouldPersistAndReturnDto() {
        KpiB2DetailResultDTO dto = buildDto();

        Instance instance = new Instance();
        instance.setId(2L);
        InstanceModule module = new InstanceModule();
        module.setId(3L);
        AnagStation station = new AnagStation();
        station.setId(4L);
        KpiB2Result result = new KpiB2Result();
        result.setId(5L);
        KpiB2DetailResult entity = new KpiB2DetailResult();
        entity.setId(99L);

        lenient().when(instanceRepository.findById(2L)).thenReturn(Optional.of(instance));
        lenient().when(instanceModuleRepository.findById(3L)).thenReturn(Optional.of(module));
        lenient().when(anagStationRepository.findById(4L)).thenReturn(Optional.of(station));
        lenient().when(kpiB2ResultRepository.findById(5L)).thenReturn(Optional.of(result));

        when(kpiB2DetailResultRepository.save(any())).thenReturn(entity);

        KpiB2DetailResultDTO saved = service.save(dto);

        assertThat(saved.getId()).isEqualTo(99L);
        verify(kpiB2DetailResultRepository).save(any(KpiB2DetailResult.class));
    }

    @Test
    void save_shouldThrowWhenInstanceNotFound() {
        KpiB2DetailResultDTO dto = buildDto();
        when(instanceRepository.findById(2L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.save(dto))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("Instance not found");
    }

    @Test
    void save_shouldThrowWhenModuleNotFound() {
        KpiB2DetailResultDTO dto = buildDto();
        when(instanceRepository.findById(2L)).thenReturn(Optional.of(new Instance()));
        when(instanceModuleRepository.findById(3L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.save(dto))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("InstanceModule not found");
    }

    @Test
    void deleteAllByInstanceModule_shouldDelegateToRepository() {
        when(kpiB2DetailResultRepository.deleteAllByInstanceModuleId(3L)).thenReturn(7);

        int deleted = service.deleteAllByInstanceModule(3L);

        assertThat(deleted).isEqualTo(7);
        verify(kpiB2DetailResultRepository).deleteAllByInstanceModuleId(3L);
    }

    @Test
    void findByResultId_shouldReturnList() {
        KpiB2DetailResultDTO dto = buildDto();

        @SuppressWarnings("unchecked")
        com.querydsl.jpa.impl.JPAQuery<Object> jpaQueryMock = mock(com.querydsl.jpa.impl.JPAQuery.class);

        lenient().when(queryBuilder.createQuery()).thenReturn(jpaQueryMock);
        lenient().when(jpaQueryMock.from((EntityPath<?>) any())).thenReturn(jpaQueryMock);
        lenient().when(jpaQueryMock.leftJoin(any(EntityPathBase.class), any(EntityPathBase.class))).thenReturn(jpaQueryMock);
        lenient().when(jpaQueryMock.where((Predicate) any())).thenReturn(jpaQueryMock);
        lenient().when(jpaQueryMock.orderBy(any(OrderSpecifier[].class))).thenReturn(jpaQueryMock);
        lenient().when(jpaQueryMock.select(any(Expression.class))).thenReturn(jpaQueryMock);
        when(jpaQueryMock.fetch()).thenReturn(List.of(dto));

        List<KpiB2DetailResultDTO> results = service.findByResultId(5L);

        assertThat(results).hasSize(1);
        assertThat(results.get(0).getId()).isEqualTo(dto.getId());

        verify(queryBuilder).createQuery();
        verify(jpaQueryMock).fetch();
    }
}
