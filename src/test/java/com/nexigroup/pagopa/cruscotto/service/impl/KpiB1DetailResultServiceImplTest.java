package com.nexigroup.pagopa.cruscotto.service.impl;

import com.nexigroup.pagopa.cruscotto.domain.*;
import com.nexigroup.pagopa.cruscotto.domain.enumeration.EvaluationType;
import com.nexigroup.pagopa.cruscotto.domain.enumeration.OutcomeStatus;
import com.nexigroup.pagopa.cruscotto.repository.*;
import com.nexigroup.pagopa.cruscotto.service.dto.KpiB1DetailResultDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class KpiB1DetailResultServiceImplTest {

    @Mock
    private InstanceRepository instanceRepository;

    @Mock
    private InstanceModuleRepository instanceModuleRepository;

    @Mock
    private KpiB1DetailResultRepository kpiB1DetailResultRepository;

    @Mock
    private KpiB1ResultRepository kpiB1ResultRepository;

    @InjectMocks
    private KpiB1DetailResultServiceImpl service;

    private Instance instance;
    private InstanceModule instanceModule;
    private KpiB1Result kpiB1Result;
    private KpiB1DetailResultDTO dto;

    @BeforeEach
    void setUp() {
        instance = new Instance();
        instance.setId(1L);

        instanceModule = new InstanceModule();
        instanceModule.setId(2L);

        kpiB1Result = new KpiB1Result();
        kpiB1Result.setId(3L);

        KpiB1DetailResult kpiB1DetailResult = new KpiB1DetailResult();
        kpiB1DetailResult.setId(4L);
        kpiB1DetailResult.setInstance(instance);
        kpiB1DetailResult.setInstanceModule(instanceModule);
        kpiB1DetailResult.setKpiB1Result(kpiB1Result);

        dto = new KpiB1DetailResultDTO();
        dto.setInstanceId(1L);
        dto.setInstanceModuleId(2L);
        dto.setKpiB1ResultId(3L);
        dto.setAnalysisDate(LocalDate.now());
        dto.setEvaluationType(EvaluationType.MESE);
        dto.setEvaluationStartDate(LocalDate.now());
        dto.setEvaluationEndDate(LocalDate.now());
        dto.setTotalInstitutions(10);
        dto.setInstitutionDifference(2);
        dto.setInstitutionDifferencePercentage(BigDecimal.valueOf(20.0));
        dto.setInstitutionOutcome(OutcomeStatus.OK);
        dto.setTotalTransactions(100);
        dto.setTransactionDifference(10);
        dto.setTransactionDifferencePercentage(BigDecimal.valueOf(10.0));
        dto.setTransactionOutcome(OutcomeStatus.OK);
    }

    @Test
    void save_ShouldReturnSavedDto() {
        when(instanceRepository.findById(dto.getInstanceId())).thenReturn(Optional.of(instance));
        when(instanceModuleRepository.findById(dto.getInstanceModuleId())).thenReturn(Optional.of(instanceModule));
        when(kpiB1ResultRepository.findById(dto.getKpiB1ResultId())).thenReturn(Optional.of(kpiB1Result));
        when(kpiB1DetailResultRepository.save(any(KpiB1DetailResult.class))).thenAnswer(invocation -> {
            KpiB1DetailResult arg = invocation.getArgument(0);
            arg.setId(999L);
            return arg;
        });

        KpiB1DetailResultDTO result = service.save(dto);

        assertNotNull(result);
        assertEquals(999L, result.getId());
        verify(kpiB1DetailResultRepository, times(1)).save(any(KpiB1DetailResult.class));
    }

    @Test
    void save_ShouldThrowException_WhenInstanceNotFound() {
        when(instanceRepository.findById(dto.getInstanceId())).thenReturn(Optional.empty());

        Exception exception = assertThrows(IllegalArgumentException.class, () -> service.save(dto));
        assertEquals("Instance not found", exception.getMessage());
    }

    @Test
    void deleteAllByInstanceModule_ShouldReturnDeletedCount() {
        when(kpiB1DetailResultRepository.deleteAllByInstanceModuleId(2L)).thenReturn(5);

        int deleted = service.deleteAllByInstanceModule(2L);
        assertEquals(5, deleted);
        verify(kpiB1DetailResultRepository, times(1)).deleteAllByInstanceModuleId(2L);
    }
}
