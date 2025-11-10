package com.nexigroup.pagopa.cruscotto.service.impl;

import com.nexigroup.pagopa.cruscotto.domain.Instance;
import com.nexigroup.pagopa.cruscotto.domain.enumeration.EvaluationType;
import com.nexigroup.pagopa.cruscotto.domain.enumeration.OutcomeStatus;
import com.nexigroup.pagopa.cruscotto.repository.InstanceRepository;
import com.nexigroup.pagopa.cruscotto.repository.KpiB8DetailResultRepository;
import com.nexigroup.pagopa.cruscotto.service.KpiB8Service;
import com.nexigroup.pagopa.cruscotto.service.dto.InstanceDTO;
import com.nexigroup.pagopa.cruscotto.service.dto.InstanceModuleDTO;
import com.nexigroup.pagopa.cruscotto.service.dto.KpiB8ResultDTO;
import com.nexigroup.pagopa.cruscotto.service.dto.KpiConfigurationDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class KpiB8DataServiceImplTest {

    @Mock
    private KpiB8Service kpiB8Service;

    @Mock
    private InstanceRepository instanceRepository;

    @Mock
    private KpiB8DetailResultRepository kpiB8DetailResultRepository;

    @InjectMocks
    private KpiB8DataServiceImpl kpiB8DataService;

    private Instance instance;
    private InstanceDTO instanceDTO;
    private InstanceModuleDTO instanceModuleDTO;
    private KpiConfigurationDTO configDTO;
    private KpiB8ResultDTO resultDTO;

    @BeforeEach
    void setUp() {
        instance = new Instance();
        instance.setId(1L);

        instanceDTO = new InstanceDTO();
        instanceDTO.setId(1L);

        instanceModuleDTO = new InstanceModuleDTO();
        instanceModuleDTO.setId(10L);

        configDTO = new KpiConfigurationDTO();
        configDTO.setEvaluationType(EvaluationType.MESE);

        resultDTO = new KpiB8ResultDTO();
        resultDTO.setId(100L);
        resultDTO.setOutcome(OutcomeStatus.OK);
    }

    @Test
    void shouldSaveResultsSuccessfully_whenEvaluationTypeIsNotMonthly() {
        configDTO.setEvaluationType(EvaluationType.TOTALE);

        when(instanceRepository.findById(1L)).thenReturn(Optional.of(instance));
        when(kpiB8Service.executeKpiB8Calculation(instance)).thenReturn(resultDTO);

        OutcomeStatus outcome = kpiB8DataService.saveKpiB8Results(
            instanceDTO, instanceModuleDTO, configDTO,
            LocalDate.now(), OutcomeStatus.OK);

        assertEquals(OutcomeStatus.OK, outcome);
        verify(kpiB8Service).updateKpiB8ResultOutcome(resultDTO.getId(), OutcomeStatus.OK);

        // Ensure KO check is never called for non-monthly evaluation
        verify(kpiB8DetailResultRepository, never()).existsKoOutcomeByResultId(anyLong());
    }

    @Test
    void shouldForceOutcomeToKO_whenMonthlyEvaluationAndHasKoDetailResults() {
        configDTO.setEvaluationType(EvaluationType.MESE);
        when(instanceRepository.findById(1L)).thenReturn(Optional.of(instance));
        when(kpiB8Service.executeKpiB8Calculation(instance)).thenReturn(resultDTO);
        when(kpiB8DetailResultRepository.existsKoOutcomeByResultId(100L)).thenReturn(true);

        OutcomeStatus outcome = kpiB8DataService.saveKpiB8Results(
            instanceDTO, instanceModuleDTO, configDTO,
            LocalDate.now(), OutcomeStatus.OK);

        assertEquals(OutcomeStatus.KO, outcome);
        verify(kpiB8Service).updateKpiB8ResultOutcome(100L, OutcomeStatus.KO);
    }

    @Test
    void shouldKeepOutcomeOK_whenMonthlyEvaluationAndAllDetailResultsAreOK() {
        configDTO.setEvaluationType(EvaluationType.MESE);
        when(instanceRepository.findById(1L)).thenReturn(Optional.of(instance));
        when(kpiB8Service.executeKpiB8Calculation(instance)).thenReturn(resultDTO);
        when(kpiB8DetailResultRepository.existsKoOutcomeByResultId(100L)).thenReturn(false);

        OutcomeStatus outcome = kpiB8DataService.saveKpiB8Results(
            instanceDTO, instanceModuleDTO, configDTO,
            LocalDate.now(), OutcomeStatus.OK);

        assertEquals(OutcomeStatus.OK, outcome);
        verify(kpiB8Service).updateKpiB8ResultOutcome(100L, OutcomeStatus.OK);
    }

    @Test
    void shouldThrowException_whenInstanceNotFound() {
        when(instanceRepository.findById(1L)).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class, () ->
            kpiB8DataService.saveKpiB8Results(
                instanceDTO, instanceModuleDTO, configDTO,
                LocalDate.now(), OutcomeStatus.OK)
        );

        assertTrue(exception.getMessage().contains("Instance not found"));
        verify(kpiB8Service, never()).executeKpiB8Calculation(any());
    }

    @Test
    void shouldThrowException_whenUnexpectedErrorOccurs() {
        when(instanceRepository.findById(1L)).thenThrow(new RuntimeException("DB error"));

        RuntimeException exception = assertThrows(RuntimeException.class, () ->
            kpiB8DataService.saveKpiB8Results(
                instanceDTO, instanceModuleDTO, configDTO,
                LocalDate.now(), OutcomeStatus.OK)
        );

        assertTrue(exception.getMessage().contains("KPI B.8 calculation failed"));
    }
}
