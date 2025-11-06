package com.nexigroup.pagopa.cruscotto.service.impl;

import com.nexigroup.pagopa.cruscotto.domain.enumeration.EvaluationType;
import com.querydsl.core.types.Path;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import com.nexigroup.pagopa.cruscotto.domain.Instance;
import com.nexigroup.pagopa.cruscotto.domain.InstanceModule;
import com.nexigroup.pagopa.cruscotto.domain.KpiB2Result;
import com.nexigroup.pagopa.cruscotto.domain.enumeration.OutcomeStatus;
import com.nexigroup.pagopa.cruscotto.repository.InstanceModuleRepository;
import com.nexigroup.pagopa.cruscotto.repository.InstanceRepository;
import com.nexigroup.pagopa.cruscotto.repository.KpiB2ResultRepository;
import com.nexigroup.pagopa.cruscotto.service.dto.KpiB2ResultDTO;
import com.nexigroup.pagopa.cruscotto.service.qdsl.QueryBuilder;
import com.querydsl.jpa.impl.JPAUpdateClause;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("KpiB9ResultServiceImpl Tests")
class KpiB2ResultServiceImplTest {

@Mock
private InstanceRepository instanceRepository;
@Mock
private InstanceModuleRepository instanceModuleRepository;
@Mock
private KpiB2ResultRepository kpiB2ResultRepository;
@Mock
private QueryBuilder queryBuilder;
@Mock
private JPAUpdateClause jpaUpdateClause;

@InjectMocks
private KpiB2ResultServiceImpl service;

private KpiB2ResultDTO buildDto() {
    KpiB2ResultDTO dto = new KpiB2ResultDTO();
    dto.setInstanceId(1L);
    dto.setInstanceModuleId(2L);
    dto.setAnalysisDate(LocalDate.now());
    dto.setExcludePlannedShutdown(true);
    dto.setExcludeUnplannedShutdown(false);
    dto.setEligibilityThreshold(90.0);
    dto.setTolerance(5.0);
    dto.setAverageTimeLimit(120.0);
    dto.setEvaluationType(EvaluationType.MESE);
    dto.setOutcome(OutcomeStatus.OK);
    return dto;
}

@Test
void save_success() {
    KpiB2ResultDTO dto = buildDto();
    Instance instance = new Instance();
    instance.setId(1L);
    InstanceModule module = new InstanceModule();
    module.setId(2L);

    KpiB2Result saved = new KpiB2Result();
    saved.setId(99L);
    saved.setInstance(instance);
    saved.setInstanceModule(module);

    when(instanceRepository.findById(1L)).thenReturn(Optional.of(instance));
    when(instanceModuleRepository.findById(2L)).thenReturn(Optional.of(module));
    when(kpiB2ResultRepository.save(any())).thenReturn(saved);

    KpiB2ResultDTO result = service.save(dto);

    assertThat(result.getId()).isEqualTo(99L);
    verify(instanceRepository).findById(1L);
    verify(instanceModuleRepository).findById(2L);
    verify(kpiB2ResultRepository).save(any(KpiB2Result.class));
}

@Test
void save_instanceNotFound() {
    KpiB2ResultDTO dto = buildDto();
    when(instanceRepository.findById(1L)).thenReturn(Optional.empty());

    assertThatThrownBy(() -> service.save(dto))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("Instance not found");
}

@Test
void save_instanceModuleNotFound() {
    KpiB2ResultDTO dto = buildDto();
    Instance instance = new Instance();
    instance.setId(1L);

    when(instanceRepository.findById(1L)).thenReturn(Optional.of(instance));
    when(instanceModuleRepository.findById(2L)).thenReturn(Optional.empty());

    assertThatThrownBy(() -> service.save(dto))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("InstanceModule not found");
}

@Test
void deleteAllByInstanceModule_success() {
    when(kpiB2ResultRepository.deleteAllByInstanceModuleId(2L)).thenReturn(3);

    int deleted = service.deleteAllByInstanceModule(2L);

    assertThat(deleted).isEqualTo(3);
    verify(kpiB2ResultRepository).deleteAllByInstanceModuleId(2L);
}

@Test
void updateKpiB2ResultOutcome_success() {
    when(queryBuilder.updateQuery(any())).thenReturn(jpaUpdateClause);
    when(jpaUpdateClause.set((Path<Object>) any(), (Object) any())).thenReturn(jpaUpdateClause);
    when(jpaUpdateClause.where(any())).thenReturn(jpaUpdateClause);

    service.updateKpiB2ResultOutcome(10L, OutcomeStatus.KO);

    verify(queryBuilder).updateQuery(any());
    verify(jpaUpdateClause).set(any(), eq(OutcomeStatus.KO));
    verify(jpaUpdateClause).where(any());
    verify(jpaUpdateClause).execute();
}

@Test
void findByInstanceModuleId_success() {
    KpiB2Result result = new KpiB2Result();
    result.setId(1L);
    Instance instance = new Instance();
    instance.setId(1L);
    InstanceModule module = new InstanceModule();
    module.setId(2L);
    result.setInstance(instance);
    result.setInstanceModule(module);
    result.setAnalysisDate(LocalDate.now());
    result.setOutcome(OutcomeStatus.OK);

    when(kpiB2ResultRepository.selectByInstanceModuleId(2L))
        .thenReturn(Collections.singletonList(result));

    List<KpiB2ResultDTO> dtos = service.findByInstanceModuleId(2L);

    assertThat(dtos).hasSize(1);
    assertThat(dtos.get(0).getId()).isEqualTo(1L);
    assertThat(dtos.get(0).getInstanceModuleId()).isEqualTo(2L);
}
}
