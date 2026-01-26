package com.nexigroup.pagopa.cruscotto.service.impl;

import com.nexigroup.pagopa.cruscotto.domain.Instance;
import com.nexigroup.pagopa.cruscotto.domain.InstanceModule;
import com.nexigroup.pagopa.cruscotto.domain.KpiC2Result;
import com.nexigroup.pagopa.cruscotto.domain.enumeration.EvaluationType;
import com.nexigroup.pagopa.cruscotto.domain.enumeration.OutcomeStatus;
import com.nexigroup.pagopa.cruscotto.repository.InstanceModuleRepository;
import com.nexigroup.pagopa.cruscotto.repository.InstanceRepository;
import com.nexigroup.pagopa.cruscotto.repository.KpiC2ResultRepository;
import com.nexigroup.pagopa.cruscotto.service.dto.KpiC2ResultDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

class KpiC2ResultServiceImplTest {

    private InstanceRepository instanceRepository;
    private InstanceModuleRepository instanceModuleRepository;
    private KpiC2ResultRepository kpiC2ResultRepository;
    private KpiC2ResultServiceImpl service;

    @BeforeEach
    void setUp() {
        instanceRepository = mock(InstanceRepository.class);
        instanceModuleRepository = mock(InstanceModuleRepository.class);
        kpiC2ResultRepository = mock(KpiC2ResultRepository.class);

        service = new KpiC2ResultServiceImpl(
            instanceRepository,
            instanceModuleRepository,
            kpiC2ResultRepository
        );
    }

    @Test
    void testSave_success() {
        KpiC2ResultDTO dto = new KpiC2ResultDTO();
        dto.setInstanceId(1L);
        dto.setInstanceModuleId(2L);
        dto.setAnalysisDate(LocalDate.now());
        dto.setInstitutionTolerance(10.0);
        dto.setNotificationTolerance(5.0);
        dto.setEvaluationType(EvaluationType.MESE);
        dto.setOutcome(OutcomeStatus.STANDBY);

        Instance instance = new Instance();
        instance.setId(1L);

        InstanceModule module = new InstanceModule();
        module.setId(2L);

        when(instanceRepository.findById(1L)).thenReturn(Optional.of(instance));
        when(instanceModuleRepository.findById(2L)).thenReturn(Optional.of(module));

        KpiC2Result savedResult = new KpiC2Result();
        savedResult.setId(100L);

        when(kpiC2ResultRepository.save(any(KpiC2Result.class))).thenReturn(savedResult);

        KpiC2ResultDTO result = service.save(dto);

        assertThat(result.getId()).isEqualTo(100L);

        ArgumentCaptor<KpiC2Result> captor = ArgumentCaptor.forClass(KpiC2Result.class);
        verify(kpiC2ResultRepository).save(captor.capture());

        KpiC2Result captured = captor.getValue();
        assertThat(captured.getInstance()).isEqualTo(instance);
        assertThat(captured.getInstanceModule()).isEqualTo(module);
        assertThat(captured.getAnalysisDate()).isEqualTo(dto.getAnalysisDate());
        assertThat(captured.getInstitutionTolerance()).isEqualTo(dto.getInstitutionTolerance());
        assertThat(captured.getNotificationTolerance()).isEqualTo(dto.getNotificationTolerance());
        assertThat(captured.getEvaluationType()).isEqualTo(dto.getEvaluationType());
        assertThat(captured.getOutcome()).isEqualTo(dto.getOutcome());
    }

    @Test
    void testSave_instanceNotFound() {
        KpiC2ResultDTO dto = new KpiC2ResultDTO();
        dto.setInstanceId(1L);
        dto.setInstanceModuleId(2L);

        when(instanceRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () -> service.save(dto));
    }

    @Test
    void testDeleteAllByInstanceModule() {
        when(kpiC2ResultRepository.deleteAllByInstanceModuleId(2L)).thenReturn(3);

        int deletedCount = service.deleteAllByInstanceModule(2L);

        assertThat(deletedCount).isEqualTo(3);
        verify(kpiC2ResultRepository).deleteAllByInstanceModuleId(2L);
    }

    @Test
    void testUpdateKpiC2ResultOutcome() {
        KpiC2Result result = new KpiC2Result();
        result.setOutcome(OutcomeStatus.STANDBY);

        when(kpiC2ResultRepository.findById(1L)).thenReturn(Optional.of(result));

        service.updateKpiC2ResultOutcome(1L, OutcomeStatus.OK);

        assertThat(result.getOutcome()).isEqualTo(OutcomeStatus.OK);
        verify(kpiC2ResultRepository).save(result);
    }

    @Test
    void testFindByInstanceModuleId() {
        KpiC2Result result = new KpiC2Result();
        result.setId(10L);
        Instance instance = new Instance();
        instance.setId(1L);
        result.setInstance(instance);
        InstanceModule module = new InstanceModule();
        module.setId(2L);
        result.setInstanceModule(module);
        result.setAnalysisDate(LocalDate.now());
        result.setInstitutionTolerance(5.0);
        result.setNotificationTolerance(3.0);
        result.setEvaluationType(EvaluationType.MESE);
        result.setOutcome(OutcomeStatus.OK);

        when(kpiC2ResultRepository.findAllByInstanceModuleIdOrderByAnalysisDateDesc(2L))
            .thenReturn(List.of(result));

        List<KpiC2ResultDTO> dtos = service.findByInstanceModuleId(2L);

        assertThat(dtos).hasSize(1);
        assertThat(dtos.get(0).getId()).isEqualTo(10L);
    }
}
