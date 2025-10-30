package com.nexigroup.pagopa.cruscotto.service.impl;

import com.nexigroup.pagopa.cruscotto.domain.enumeration.EvaluationType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import com.nexigroup.pagopa.cruscotto.domain.*;
import com.nexigroup.pagopa.cruscotto.domain.enumeration.OutcomeStatus;
import com.nexigroup.pagopa.cruscotto.repository.*;
import com.nexigroup.pagopa.cruscotto.service.dto.KpiB9ResultDTO;
import com.nexigroup.pagopa.cruscotto.service.qdsl.QueryBuilder;
import com.querydsl.jpa.impl.JPAUpdateClause;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.time.LocalDate;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("KpiB9ResultServiceImpl Tests")
class KpiB9ResultServiceImplTest {

    @Mock
    private InstanceRepository instanceRepository;

    @Mock
    private InstanceModuleRepository instanceModuleRepository;

    @Mock
    private KpiB9ResultRepository kpiB9ResultRepository;

    @Mock
    private QueryBuilder queryBuilder;

    @InjectMocks
    private KpiB9ResultServiceImpl kpiB9ResultService;

    @Test
    void testSave() {
        // Arrange
        Instance instance = new Instance();
        instance.setId(1L);

        InstanceModule module = new InstanceModule();
        module.setId(10L);

        KpiB9Result savedResult = new KpiB9Result();
        savedResult.setId(100L);

        KpiB9ResultDTO dto = new KpiB9ResultDTO();
        dto.setInstanceId(1L);
        dto.setInstanceModuleId(10L);
        dto.setAnalysisDate(LocalDate.now());
        dto.setExcludePlannedShutdown(false);
        dto.setExcludeUnplannedShutdown(true);
        dto.setEligibilityThreshold(90.0);
        dto.setTolerance(5.0);
        dto.setEvaluationType(EvaluationType.MESE);
        dto.setOutcome(OutcomeStatus.OK);

        when(instanceRepository.findById(1L)).thenReturn(Optional.of(instance));
        when(instanceModuleRepository.findById(10L)).thenReturn(Optional.of(module));
        when(kpiB9ResultRepository.save(any(KpiB9Result.class))).thenReturn(savedResult);

        // Act
        KpiB9ResultDTO result = kpiB9ResultService.save(dto);

        // Assert
        assertEquals(100L, result.getId());
        verify(kpiB9ResultRepository, times(1)).save(any(KpiB9Result.class));
    }

    @Test
    void testDeleteAllByInstanceModule() {
        when(kpiB9ResultRepository.deleteAllByInstanceModuleId(10L)).thenReturn(3);

        int deleted = kpiB9ResultService.deleteAllByInstanceModule(10L);

        assertEquals(3, deleted);
        verify(kpiB9ResultRepository, times(1)).deleteAllByInstanceModuleId(10L);
    }

    @Test
    void testUpdateKpiB9ResultOutcome() {
        // Mock di JPAUpdateClause
        JPAUpdateClause updateClause = mock(JPAUpdateClause.class);

        // Il queryBuilder restituisce il mock
        when(queryBuilder.updateQuery(any())).thenReturn(updateClause);

        // Usa doAnswer per aggirare l'ambiguità degli overload di set
        doAnswer(invocation -> updateClause)
            .when(updateClause)
            .set(any(), Optional.ofNullable(any()));

        // Stub per where ed execute
        when(updateClause.where(any())).thenReturn(updateClause);
        when(updateClause.execute()).thenReturn(1L);

        // Chiamata del metodo sotto test
        kpiB9ResultService.updateKpiB9ResultOutcome(50L, OutcomeStatus.KO);

        // Verifiche
        verify(updateClause).set(any(), eq(OutcomeStatus.KO));
        verify(updateClause).where(any());
        verify(updateClause).execute();
    }


    @Test
    void testFindByInstanceModuleId() {
        InstanceModule module = new InstanceModule();
        module.setId(10L);

        KpiB9Result result = new KpiB9Result();
        result.setId(1L);
        result.setInstanceModule(module);
        result.setAnalysisDate(LocalDate.now());
        result.setExcludePlannedShutdown(false);
        result.setExcludeUnplannedShutdown(false);
        result.setEligibilityThreshold(90.0);
        result.setTolerance(5.0);
        result.setEvaluationType(EvaluationType.MESE);
        result.setOutcome(OutcomeStatus.OK);

        when(kpiB9ResultRepository.selectByInstanceModuleId(10L))
            .thenReturn(Collections.singletonList(result));

        List<KpiB9ResultDTO> dtos = kpiB9ResultService.findByInstanceModuleId(10L);

        assertEquals(1, dtos.size());
        assertEquals(1L, dtos.get(0).getId());
    }

}
