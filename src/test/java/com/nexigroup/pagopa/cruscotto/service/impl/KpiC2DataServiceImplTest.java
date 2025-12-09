package com.nexigroup.pagopa.cruscotto.service.impl;

import com.nexigroup.pagopa.cruscotto.domain.Instance;
import com.nexigroup.pagopa.cruscotto.domain.enumeration.EvaluationType;
import com.nexigroup.pagopa.cruscotto.domain.enumeration.OutcomeStatus;
import com.nexigroup.pagopa.cruscotto.repository.InstanceRepository;
import com.nexigroup.pagopa.cruscotto.repository.KpiC2DetailResultRepository;
import com.nexigroup.pagopa.cruscotto.service.KpiC2Service;
import com.nexigroup.pagopa.cruscotto.service.dto.InstanceDTO;
import com.nexigroup.pagopa.cruscotto.service.dto.InstanceModuleDTO;
import com.nexigroup.pagopa.cruscotto.service.dto.KpiC2ResultDTO;
import com.nexigroup.pagopa.cruscotto.service.dto.KpiConfigurationDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import java.time.LocalDate;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class KpiC2DataServiceImplTest {

    @InjectMocks
    private KpiC2DataServiceImpl kpiC2DataService;

    @Mock
    private KpiC2Service kpiC2Service;

    @Mock
    private InstanceRepository instanceRepository;

    @Mock
    private KpiC2DetailResultRepository kpiC2DetailResultRepository;

    private InstanceDTO instanceDTO;
    private InstanceModuleDTO instanceModuleDTO;
    private KpiConfigurationDTO kpiConfigurationDTO;
    private Instance instance;
    private KpiC2ResultDTO kpiC2ResultDTO;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        instanceDTO = new InstanceDTO();
        instanceDTO.setId(1L);

        instanceModuleDTO = new InstanceModuleDTO();
        instanceModuleDTO.setId(1L);

        kpiConfigurationDTO = new KpiConfigurationDTO();
        kpiConfigurationDTO.setEvaluationType(EvaluationType.MESE);

        instance = new Instance();

        kpiC2ResultDTO = new KpiC2ResultDTO();
        kpiC2ResultDTO.setId(1L);
        kpiC2ResultDTO.setOutcome(OutcomeStatus.OK);
    }

    @Test
    void testSaveKpiC2Results_HappyPath_AllOk() {
        when(instanceRepository.findById(instanceDTO.getId())).thenReturn(Optional.of(instance));
        when(kpiC2Service.executeKpiC2Calculation(instance)).thenReturn(kpiC2ResultDTO);
        when(kpiC2DetailResultRepository.existsKoOutcomeByResultId(kpiC2ResultDTO.getId())).thenReturn(false);

        OutcomeStatus outcome = OutcomeStatus.OK;
        OutcomeStatus resultOutcome = kpiC2DataService.saveKpiC2Results(
            instanceDTO, instanceModuleDTO, kpiConfigurationDTO, LocalDate.now(), outcome
        );

        assertEquals(outcome, resultOutcome);
        verify(kpiC2Service).updateKpiC2ResultOutcome(kpiC2ResultDTO.getId(), outcome);
    }

    @Test
    void testSaveKpiC2Results_HasKoDetailResults() {
        when(instanceRepository.findById(instanceDTO.getId())).thenReturn(Optional.of(instance));
        when(kpiC2Service.executeKpiC2Calculation(instance)).thenReturn(kpiC2ResultDTO);
        when(kpiC2DetailResultRepository.existsKoOutcomeByResultId(kpiC2ResultDTO.getId())).thenReturn(true);

        OutcomeStatus outcome = OutcomeStatus.OK;
        OutcomeStatus resultOutcome = kpiC2DataService.saveKpiC2Results(
            instanceDTO, instanceModuleDTO, kpiConfigurationDTO, LocalDate.now(), outcome
        );

        assertEquals(OutcomeStatus.KO, resultOutcome);
        verify(kpiC2Service).updateKpiC2ResultOutcome(kpiC2ResultDTO.getId(), OutcomeStatus.KO);
    }

    @Test
    void testSaveKpiC2Results_NonMonthlyEvaluationType() {
        kpiConfigurationDTO.setEvaluationType(EvaluationType.TOTALE);
        when(instanceRepository.findById(instanceDTO.getId())).thenReturn(Optional.of(instance));
        when(kpiC2Service.executeKpiC2Calculation(instance)).thenReturn(kpiC2ResultDTO);

        OutcomeStatus outcome = OutcomeStatus.OK;
        OutcomeStatus resultOutcome = kpiC2DataService.saveKpiC2Results(
            instanceDTO, instanceModuleDTO, kpiConfigurationDTO, LocalDate.now(), outcome
        );

        assertEquals(outcome, resultOutcome);
        verify(kpiC2Service).updateKpiC2ResultOutcome(kpiC2ResultDTO.getId(), outcome);
        verifyNoInteractions(kpiC2DetailResultRepository);
    }

    @Test
    void testSaveKpiC2Results_InstanceNotFound() {
        when(instanceRepository.findById(instanceDTO.getId())).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class, () ->
            kpiC2DataService.saveKpiC2Results(
                instanceDTO, instanceModuleDTO, kpiConfigurationDTO, LocalDate.now(), OutcomeStatus.OK
            )
        );

        assertTrue(exception.getMessage().contains("Instance not found"));
    }

    @Test
    void testSaveKpiC2Results_ExceptionDuringCalculation() {
        when(instanceRepository.findById(instanceDTO.getId())).thenReturn(Optional.of(instance));
        when(kpiC2Service.executeKpiC2Calculation(instance)).thenThrow(new RuntimeException("Calculation failed"));

        RuntimeException exception = assertThrows(RuntimeException.class, () ->
            kpiC2DataService.saveKpiC2Results(
                instanceDTO, instanceModuleDTO, kpiConfigurationDTO, LocalDate.now(), OutcomeStatus.OK
            )
        );

        assertTrue(exception.getMessage().contains("KPI C.2 calculation failed"));
    }
}
