package com.nexigroup.pagopa.cruscotto.service.impl;

import com.nexigroup.pagopa.cruscotto.domain.Instance;
import com.nexigroup.pagopa.cruscotto.domain.InstanceModule;
import com.nexigroup.pagopa.cruscotto.domain.KpiA1Result;
import com.nexigroup.pagopa.cruscotto.domain.enumeration.EvaluationType;
import com.nexigroup.pagopa.cruscotto.domain.enumeration.OutcomeStatus;
import com.nexigroup.pagopa.cruscotto.repository.InstanceModuleRepository;
import com.nexigroup.pagopa.cruscotto.repository.InstanceRepository;
import com.nexigroup.pagopa.cruscotto.repository.KpiA1ResultRepository;
import com.nexigroup.pagopa.cruscotto.service.dto.KpiA1ResultDTO;
import com.nexigroup.pagopa.cruscotto.service.qdsl.QueryBuilder;
import com.querydsl.jpa.impl.JPAUpdateClause;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import java.util.List;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
@DisplayName("KpiA1ResultServiceImpl Tests")
class KpiA1ResultServiceImplTest {

    @Mock
    private InstanceRepository instanceRepository;

    @Mock
    private InstanceModuleRepository instanceModuleRepository;

    @Mock
    private KpiA1ResultRepository kpiA1ResultRepository;

    @Mock
    private QueryBuilder queryBuilder;

    @InjectMocks
    private KpiA1ResultServiceImpl service;

    @Test
    void testSave_success() {
        KpiA1ResultDTO dto = new KpiA1ResultDTO();
        dto.setInstanceId(1L);
        dto.setInstanceModuleId(2L);

        Instance instance = new Instance();
        instance.setId(1L);

        InstanceModule module = new InstanceModule();
        module.setId(2L);

        KpiA1Result savedResult = new KpiA1Result();
        savedResult.setId(100L);

        when(instanceRepository.findById(1L)).thenReturn(Optional.of(instance));
        when(instanceModuleRepository.findById(2L)).thenReturn(Optional.of(module));
        when(kpiA1ResultRepository.save(any(KpiA1Result.class))).thenReturn(savedResult);

        KpiA1ResultDTO result = service.save(dto);

        assertEquals(100L, result.getId());
        verify(kpiA1ResultRepository).save(any(KpiA1Result.class));
    }

    @Test
    void testSave_instanceNotFound() {
        KpiA1ResultDTO dto = new KpiA1ResultDTO();
        dto.setInstanceId(1L);
        dto.setInstanceModuleId(2L);

        // Caso 1: Instance non trovata
        when(instanceRepository.findById(1L)).thenReturn(Optional.empty());
        IllegalArgumentException ex1 = assertThrows(IllegalArgumentException.class, () -> service.save(dto));
        assertEquals("Instance not found", ex1.getMessage());

        // Caso 2: Instance trovata ma Module non trovato
        when(instanceRepository.findById(1L)).thenReturn(Optional.of(new Instance()));
        when(instanceModuleRepository.findById(2L)).thenReturn(Optional.empty());
        IllegalArgumentException ex2 = assertThrows(IllegalArgumentException.class, () -> service.save(dto));
        assertEquals("InstanceModule not found", ex2.getMessage());
    }

    @Test
    void testDeleteAllByInstanceModule() {
        when(kpiA1ResultRepository.deleteAllByInstanceModuleId(2L)).thenReturn(3);

        int deleted = service.deleteAllByInstanceModule(2L);

        assertEquals(3, deleted);
        verify(kpiA1ResultRepository).deleteAllByInstanceModuleId(2L);
    }

    @Test
    void testUpdateKpiA1ResultOutcome() {
        JPAUpdateClause updateClause = mock(JPAUpdateClause.class);
        when(queryBuilder.updateQuery(any())).thenReturn(updateClause);

        // cast esplicito del Path
        when(updateClause.set(any(), eq(OutcomeStatus.OK))).thenReturn(updateClause);
        when(updateClause.where(any())).thenReturn(updateClause);
        when(updateClause.execute()).thenReturn(1L);

        service.updateKpiA1ResultOutcome(10L, OutcomeStatus.OK);

        verify(updateClause).set(any(), eq(OutcomeStatus.OK));
        verify(updateClause).where(any());
        verify(updateClause).execute();
    }

    @Test
    void testFindByInstanceModuleId() {
        KpiA1Result kpi = new KpiA1Result();
        kpi.setId(1L);
        InstanceModule module = new InstanceModule();
        module.setId(2L);
        kpi.setInstanceModule(module);

        when(kpiA1ResultRepository.selectByInstanceModuleId(2L)).thenReturn(List.of(kpi));

        List<KpiA1ResultDTO> results = service.findByInstanceModuleId(2L);

        assertEquals(1, results.size());
        assertEquals(1L, results.get(0).getId());
    }

}
