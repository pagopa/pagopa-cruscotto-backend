package com.nexigroup.pagopa.cruscotto.service.impl;

import com.nexigroup.pagopa.cruscotto.domain.Instance;
import com.nexigroup.pagopa.cruscotto.domain.InstanceModule;
import com.nexigroup.pagopa.cruscotto.domain.KpiB1Result;
import com.nexigroup.pagopa.cruscotto.domain.enumeration.EvaluationType;
import com.nexigroup.pagopa.cruscotto.domain.enumeration.OutcomeStatus;
import com.nexigroup.pagopa.cruscotto.repository.InstanceModuleRepository;
import com.nexigroup.pagopa.cruscotto.repository.InstanceRepository;
import com.nexigroup.pagopa.cruscotto.repository.KpiB1ResultRepository;
import com.nexigroup.pagopa.cruscotto.service.dto.KpiB1ResultDTO;
import com.nexigroup.pagopa.cruscotto.service.qdsl.QueryBuilder;
import com.querydsl.jpa.impl.JPAUpdateClause;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class KpiB1ResultServiceImplTest {

    private InstanceRepository instanceRepository;
    private InstanceModuleRepository instanceModuleRepository;
    private KpiB1ResultRepository kpiB1ResultRepository;
    private QueryBuilder queryBuilder;
    private KpiB1ResultServiceImpl service;

    @BeforeEach
    void setUp() {
        instanceRepository = mock(InstanceRepository.class);
        instanceModuleRepository = mock(InstanceModuleRepository.class);
        kpiB1ResultRepository = mock(KpiB1ResultRepository.class);
        queryBuilder = mock(QueryBuilder.class);

        service = new KpiB1ResultServiceImpl(
            instanceRepository,
            instanceModuleRepository,
            kpiB1ResultRepository,
            queryBuilder
        );
    }

    @Test
    void testSaveKpiB1Result() {
        Instance instance = new Instance();
        instance.setId(1L);
        InstanceModule module = new InstanceModule();
        module.setId(2L);

        KpiB1ResultDTO dto = new KpiB1ResultDTO();
        dto.setInstanceId(1L);
        dto.setInstanceModuleId(2L);
        dto.setAnalysisDate(LocalDate.now());
        dto.setEvaluationType(EvaluationType.MESE);
        dto.setInstitutionCount(10);
        dto.setTransactionCount(100);
        dto.setInstitutionTolerance(BigDecimal.valueOf(5));
        dto.setTransactionTolerance(BigDecimal.valueOf(10));
        dto.setOutcome(OutcomeStatus.OK);

        when(instanceRepository.findById(1L)).thenReturn(Optional.of(instance));
        when(instanceModuleRepository.findById(2L)).thenReturn(Optional.of(module));

        KpiB1Result savedResult = new KpiB1Result();
        savedResult.setId(100L);
        when(kpiB1ResultRepository.save(any())).thenReturn(savedResult);

        KpiB1ResultDTO resultDto = service.save(dto);

        assertThat(resultDto.getId()).isEqualTo(100L);
        verify(kpiB1ResultRepository, times(1)).save(any(KpiB1Result.class));
    }

    @Test
    void testDeleteAllByInstanceModule() {
        when(kpiB1ResultRepository.deleteAllByInstanceModuleId(2L)).thenReturn(5);

        int deleted = service.deleteAllByInstanceModule(2L);
        assertThat(deleted).isEqualTo(5);
        verify(kpiB1ResultRepository, times(1)).deleteAllByInstanceModuleId(2L);
    }

    @Test
    void testUpdateKpiB1ResultOutcome() {
        JPAUpdateClause updateClause = mock(JPAUpdateClause.class);
        when(queryBuilder.updateQuery(any())).thenReturn(updateClause);

        // Fix the ambiguity by specifying the types explicitly
        when(updateClause.set(any(com.querydsl.core.types.Path.class), any(OutcomeStatus.class)))
            .thenReturn(updateClause);

        when(updateClause.where(any())).thenReturn(updateClause);
        when(updateClause.execute()).thenReturn(1L);

        service.updateKpiB1ResultOutcome(123L, OutcomeStatus.KO);

        verify(updateClause).set(any(com.querydsl.core.types.Path.class), eq(OutcomeStatus.KO));
        verify(updateClause).where(any());
        verify(updateClause).execute();
    }

    @Test
    void testFindByInstanceModuleId() {
        KpiB1Result result = new KpiB1Result();
        Instance instance = new Instance();
        instance.setId(1L);
        InstanceModule module = new InstanceModule();
        module.setId(2L);
        result.setInstance(instance);
        result.setInstanceModule(module);
        result.setId(100L);
        result.setAnalysisDate(LocalDate.now());
        result.setEvaluationType(EvaluationType.MESE);
        result.setInstitutionCount(10);
        result.setTransactionCount(20);
        result.setInstitutionTolerance(BigDecimal.valueOf(5));
        result.setTransactionTolerance(BigDecimal.valueOf(10));
        result.setOutcome(OutcomeStatus.OK);

        when(kpiB1ResultRepository.selectByInstanceModuleId(2L)).thenReturn(List.of(result));

        List<KpiB1ResultDTO> dtos = service.findByInstanceModuleId(2L);

        assertThat(dtos).hasSize(1);
        assertThat(dtos.get(0).getId()).isEqualTo(100L);
    }

    @Test
    void testSaveKpiB1Result_instanceNotFound() {
        KpiB1ResultDTO dto = new KpiB1ResultDTO();
        dto.setInstanceId(1L);
        dto.setInstanceModuleId(2L);

        when(instanceRepository.findById(1L)).thenReturn(Optional.empty());

        // Verifica che l'eccezione sia lanciata
        org.junit.jupiter.api.Assertions.assertThrows(
            IllegalArgumentException.class,
            () -> service.save(dto),
            "Instance not found"
        );

        verify(instanceRepository, times(1)).findById(1L);
        verifyNoInteractions(instanceModuleRepository, kpiB1ResultRepository);
    }

    @Test
    void testSaveKpiB1Result_instanceModuleNotFound() {
        Instance instance = new Instance();
        instance.setId(1L);

        KpiB1ResultDTO dto = new KpiB1ResultDTO();
        dto.setInstanceId(1L);
        dto.setInstanceModuleId(2L);

        when(instanceRepository.findById(1L)).thenReturn(Optional.of(instance));
        when(instanceModuleRepository.findById(2L)).thenReturn(Optional.empty());

        org.junit.jupiter.api.Assertions.assertThrows(
            IllegalArgumentException.class,
            () -> service.save(dto),
            "InstanceModule not found"
        );

        verify(instanceRepository, times(1)).findById(1L);
        verify(instanceModuleRepository, times(1)).findById(2L);
        verifyNoInteractions(kpiB1ResultRepository);
    }

}
