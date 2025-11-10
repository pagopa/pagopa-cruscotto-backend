package com.nexigroup.pagopa.cruscotto.service.impl;

import com.nexigroup.pagopa.cruscotto.domain.*;
import com.nexigroup.pagopa.cruscotto.domain.enumeration.EvaluationType;
import com.nexigroup.pagopa.cruscotto.domain.enumeration.OutcomeStatus;
import com.nexigroup.pagopa.cruscotto.repository.*;
import com.nexigroup.pagopa.cruscotto.service.dto.KpiA1DetailResultDTO;
import com.nexigroup.pagopa.cruscotto.service.qdsl.QueryBuilder;
import com.querydsl.core.types.Expression;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Predicate;
import com.querydsl.jpa.impl.JPAQuery;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("KpiA1DetailResultServiceImpl Tests")
class KpiA1DetailResultServiceImplTest {

    private AnagStationRepository anagStationRepository; // Added
    private InstanceRepository instanceRepository;
    private InstanceModuleRepository instanceModuleRepository;
    private KpiA1DetailResultRepository kpiA1DetailResultRepository;
    private KpiA1ResultRepository kpiA1ResultRepository;
    private QueryBuilder queryBuilder;

    private KpiA1DetailResultServiceImpl service;

    @BeforeEach
    void setUp() {
        anagStationRepository = mock(AnagStationRepository.class); // Added
        instanceRepository = mock(InstanceRepository.class);
        instanceModuleRepository = mock(InstanceModuleRepository.class);
        kpiA1DetailResultRepository = mock(KpiA1DetailResultRepository.class);
        kpiA1ResultRepository = mock(KpiA1ResultRepository.class);
        queryBuilder = mock(QueryBuilder.class);

        service = new KpiA1DetailResultServiceImpl(
            anagStationRepository,      // Fixed constructor
            instanceRepository,
            instanceModuleRepository,
            kpiA1DetailResultRepository,
            kpiA1ResultRepository,
            queryBuilder
        );
    }

    @Test
    void save_shouldPersistKpiA1DetailResult() {
        // Arrange
        KpiA1DetailResultDTO dto = new KpiA1DetailResultDTO();
        dto.setInstanceId(1L);
        dto.setInstanceModuleId(2L);
        dto.setKpiA1ResultId(4L);
        dto.setAnalysisDate(LocalDate.now());
        dto.setEvaluationType(EvaluationType.MESE);
        dto.setEvaluationStartDate(LocalDate.now().minusDays(1));
        dto.setEvaluationEndDate(LocalDate.now());
        dto.setTotReq(10L);
        dto.setReqTimeout(2L);
        dto.setTimeoutPercentage(20.0);
        dto.setOutcome(OutcomeStatus.OK);

        Instance instance = new Instance();
        instance.setId(1L);
        InstanceModule module = new InstanceModule();
        module.setId(2L);
        KpiA1Result result = new KpiA1Result();
        result.setId(4L);

        lenient().when(instanceRepository.findById(1L)).thenReturn(Optional.of(instance));
        lenient().when(instanceModuleRepository.findById(2L)).thenReturn(Optional.of(module));
        lenient().when(kpiA1ResultRepository.findById(4L)).thenReturn(Optional.of(result));

        KpiA1DetailResult savedEntity = new KpiA1DetailResult();
        savedEntity.setId(100L);
        when(kpiA1DetailResultRepository.save(any(KpiA1DetailResult.class))).thenReturn(savedEntity);

        // Act
        KpiA1DetailResultDTO resultDto = service.save(dto);

        // Assert
        assertThat(resultDto.getId()).isEqualTo(100L);

        ArgumentCaptor<KpiA1DetailResult> captor = ArgumentCaptor.forClass(KpiA1DetailResult.class);
        verify(kpiA1DetailResultRepository).save(captor.capture());
        assertThat(captor.getValue().getInstance()).isEqualTo(instance);
        assertThat(captor.getValue().getInstanceModule()).isEqualTo(module);
        assertThat(captor.getValue().getKpiA1Result()).isEqualTo(result);
    }

    @Test
    void deleteAllByInstanceModule_shouldCallRepository() {
        when(kpiA1DetailResultRepository.deleteAllByInstanceModuleId(2L)).thenReturn(5);

        int deletedCount = service.deleteAllByInstanceModule(2L);

        assertThat(deletedCount).isEqualTo(5);
        verify(kpiA1DetailResultRepository).deleteAllByInstanceModuleId(2L);
    }

    @Test
    void findByResultId_shouldReturnDtoList() {
        QKpiA1DetailResult qDetail = QKpiA1DetailResult.kpiA1DetailResult;

        JPAQuery<Object> query = mock(JPAQuery.class);

        when(queryBuilder.createQuery()).thenReturn(query);
        when(query.from(qDetail)).thenReturn(query);
        when(query.where((Predicate) any())).thenReturn(query);

        // Correct stub for orderBy varargs
        lenient().when(query.orderBy(any(OrderSpecifier[].class))).thenReturn(query);

        when(query.select(any(Expression.class))).thenReturn(query);

        KpiA1DetailResultDTO dto = new KpiA1DetailResultDTO();
        List<KpiA1DetailResultDTO> expectedList = List.of(dto);
        when(query.fetch()).thenReturn((List) expectedList);

        List<KpiA1DetailResultDTO> resultList = service.findByResultId(1L);

        assertThat(resultList).isEqualTo(expectedList);
        verify(query).fetch();
    }

    @Test
    void save_shouldThrow_whenInstanceNotFound() {
        // Arrange
        KpiA1DetailResultDTO dto = new KpiA1DetailResultDTO();
        dto.setInstanceId(1L);

        when(instanceRepository.findById(1L)).thenReturn(Optional.empty());

        // Act & Assert
        org.junit.jupiter.api.Assertions.assertThrows(IllegalArgumentException.class,
            () -> service.save(dto),
            "Instance not found"
        );
    }

    @Test
    void save_shouldThrow_whenInstanceModuleNotFound() {
        // Arrange
        KpiA1DetailResultDTO dto = new KpiA1DetailResultDTO();
        dto.setInstanceId(1L);
        dto.setInstanceModuleId(2L);

        when(instanceRepository.findById(1L)).thenReturn(Optional.of(new Instance()));
        when(instanceModuleRepository.findById(2L)).thenReturn(Optional.empty());

        // Act & Assert
        org.junit.jupiter.api.Assertions.assertThrows(IllegalArgumentException.class,
            () -> service.save(dto),
            "InstanceModule not found"
        );
    }

    @Test
    void save_shouldThrow_whenKpiA1ResultNotFound() {
        // Arrange
        KpiA1DetailResultDTO dto = new KpiA1DetailResultDTO();
        dto.setInstanceId(1L);
        dto.setInstanceModuleId(2L);
        dto.setKpiA1ResultId(3L);

        when(instanceRepository.findById(1L)).thenReturn(Optional.of(new Instance()));
        when(instanceModuleRepository.findById(2L)).thenReturn(Optional.of(new InstanceModule()));
        when(kpiA1ResultRepository.findById(3L)).thenReturn(Optional.empty());

        // Act & Assert
        org.junit.jupiter.api.Assertions.assertThrows(IllegalArgumentException.class,
            () -> service.save(dto),
            "KpiA1Result not found"
        );
    }

}
