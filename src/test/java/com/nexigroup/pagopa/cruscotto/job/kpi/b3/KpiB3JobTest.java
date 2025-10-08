package com.nexigroup.pagopa.cruscotto.job.kpi.b3;

import com.nexigroup.pagopa.cruscotto.config.ApplicationProperties;
import com.nexigroup.pagopa.cruscotto.domain.enumeration.ModuleCode;
import com.nexigroup.pagopa.cruscotto.domain.enumeration.OutcomeStatus;
import com.nexigroup.pagopa.cruscotto.service.InstanceModuleService;
import com.nexigroup.pagopa.cruscotto.service.InstanceService;
import com.nexigroup.pagopa.cruscotto.service.KpiConfigurationService;
import com.nexigroup.pagopa.cruscotto.service.dto.InstanceDTO;
import com.nexigroup.pagopa.cruscotto.service.dto.KpiConfigurationDTO;
import com.nexigroup.pagopa.cruscotto.service.dto.InstanceModuleDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.quartz.JobExecutionContext;
import org.quartz.Scheduler;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.lenient;

@ExtendWith(MockitoExtension.class)
class KpiB3JobTest {

    @Mock
    private InstanceService instanceService;

    @Mock
    private InstanceModuleService instanceModuleService;

    @Mock
    private ApplicationProperties applicationProperties;

    @Mock
    private KpiConfigurationService kpiConfigurationService;

    @Mock
    private Scheduler scheduler;

    @Mock
    private com.nexigroup.pagopa.cruscotto.repository.KpiB3ResultRepository kpiB3ResultRepository;

    @Mock
    private com.nexigroup.pagopa.cruscotto.repository.KpiB3DetailResultRepository kpiB3DetailResultRepository;

    @Mock
    private com.nexigroup.pagopa.cruscotto.repository.KpiB3AnalyticDataRepository kpiB3AnalyticDataRepository;

    @Mock
    private com.nexigroup.pagopa.cruscotto.repository.InstanceRepository instanceRepository;

    @Mock
    private com.nexigroup.pagopa.cruscotto.repository.InstanceModuleRepository instanceModuleRepository;

    @Mock
    private com.nexigroup.pagopa.cruscotto.repository.AnagStationRepository anagStationRepository;

    @Mock
    private com.nexigroup.pagopa.cruscotto.repository.PagopaNumeroStandinRepository pagopaNumeroStandinRepository;

    @Mock
    private com.nexigroup.pagopa.cruscotto.repository.AnagPlannedShutdownRepository anagPlannedShutdownRepository;

    @Mock
    private com.nexigroup.pagopa.cruscotto.service.KpiB3DataService kpiB3DataService;

    @Mock
    private JobExecutionContext jobExecutionContext;

    @InjectMocks
    private KpiB3Job kpiB3Job;

    private ApplicationProperties.KpiB3Job kpiB3JobConfig;
    private ApplicationProperties.Job jobConfig;

    @BeforeEach
    void setUp() {
        kpiB3JobConfig = new ApplicationProperties.KpiB3Job();
        kpiB3JobConfig.setEnabled(true);
        kpiB3JobConfig.setLimit(10);

        jobConfig = new ApplicationProperties.Job();
        jobConfig.setKpiB3Job(kpiB3JobConfig);

        lenient().when(applicationProperties.getJob()).thenReturn(jobConfig);
    }

    @Test
    void testExecuteInternal_WhenJobDisabled() throws Exception {
        // Given
        kpiB3JobConfig.setEnabled(false);

        // When
        kpiB3Job.executeInternal(jobExecutionContext);

        // Then
        verify(instanceService, never()).findInstanceToCalculate(any(), anyInt());
    }

    @Test
    void testExecuteInternal_WhenNoInstancesToProcess() throws Exception {
        // Given
        when(instanceService.findInstanceToCalculate(ModuleCode.B3, 10))
            .thenReturn(Collections.emptyList());

        // When
        kpiB3Job.executeInternal(jobExecutionContext);

        // Then
        verify(instanceService).findInstanceToCalculate(ModuleCode.B3, 10);
        verify(kpiConfigurationService, never()).findKpiConfigurationByCode(anyString());
    }

    @Test
    void testExecuteInternal_WithInstancesToProcess() throws Exception {
        // Given
        InstanceDTO instance = new InstanceDTO();
        instance.setId(1L);
        instance.setInstanceIdentification("TEST_INSTANCE_001");
        instance.setPartnerFiscalCode("12345678901");
        instance.setPartnerName("Test Partner");
        instance.setPartnerId(100L);
        instance.setAnalysisPeriodStartDate(LocalDate.of(2024, 1, 1));
        instance.setAnalysisPeriodEndDate(LocalDate.of(2024, 1, 31));

        InstanceModuleDTO instanceModule = new InstanceModuleDTO();
        instanceModule.setId(10L);
        instanceModule.setInstanceId(1L);

        KpiConfigurationDTO kpiConfig = new KpiConfigurationDTO();
        kpiConfig.setModuleId(100L);
        kpiConfig.setEligibilityThreshold(90.0);
        kpiConfig.setTolerance(5.0);
        kpiConfig.setExcludePlannedShutdown(false);
        kpiConfig.setExcludeUnplannedShutdown(false);
        kpiConfig.setEvaluationType(com.nexigroup.pagopa.cruscotto.domain.enumeration.EvaluationType.TOTALE);



        when(instanceService.findInstanceToCalculate(ModuleCode.B3, 10))
            .thenReturn(Arrays.asList(instance));
        when(kpiConfigurationService.findKpiConfigurationByCode(ModuleCode.B3.code))
            .thenReturn(Optional.of(kpiConfig));
        when(instanceModuleService.findOne(instance.getId(), kpiConfig.getModuleId()))
            .thenReturn(Optional.of(instanceModule));
        
        // Mock database repository to return empty list (no stand-in events = OK)
        when(pagopaNumeroStandinRepository.findByDateRange(any(), any()))
            .thenReturn(Collections.emptyList());
        when(anagStationRepository.findByAnagPartnerFiscalCode(anyString()))
            .thenReturn(Collections.emptyList());
            


        // When
        kpiB3Job.executeInternal(jobExecutionContext);

        // Then
        verify(instanceService).findInstanceToCalculate(ModuleCode.B3, 10);
        verify(kpiConfigurationService).findKpiConfigurationByCode(ModuleCode.B3.code);
        verify(instanceService).updateInstanceStatusInProgress(instance.getId());
        verify(instanceModuleService, times(3)).findOne(instance.getId(), kpiConfig.getModuleId());
        verify(pagopaNumeroStandinRepository).findByDateRange(any(), any());
        verify(anagStationRepository).findByAnagPartnerFiscalCode(anyString());
        verify(kpiB3DataService).saveKpiB3Results(eq(instance), eq(instanceModule), eq(kpiConfig), any(), eq(OutcomeStatus.OK), any());
        verify(instanceModuleService).updateAutomaticOutcome(
            eq(instanceModule.getId()),
            eq(OutcomeStatus.OK)
        );
    }

    @Test
    void testExecuteInternal_WhenKpiConfigurationNotFound() throws Exception {
        // Given
        InstanceDTO instance = new InstanceDTO();
        instance.setId(1L);
        instance.setInstanceIdentification("TEST_INSTANCE_001");
        instance.setPartnerFiscalCode("12345678901");
        instance.setPartnerName("Test Partner");
        instance.setPartnerId(100L);

        when(instanceService.findInstanceToCalculate(ModuleCode.B3, 10))
            .thenReturn(Arrays.asList(instance));
        when(kpiConfigurationService.findKpiConfigurationByCode(ModuleCode.B3.code))
            .thenReturn(Optional.empty());

        // When
        kpiB3Job.executeInternal(jobExecutionContext);

        // Then
        verify(instanceService).findInstanceToCalculate(ModuleCode.B3, 10);
        verify(kpiConfigurationService).findKpiConfigurationByCode(ModuleCode.B3.code);
        // No instance module update should occur when configuration is not found
    }

    @Test
    void testScheduleJobForSingleInstance() throws Exception {
        // Given
        String instanceIdentification = "TEST_INSTANCE_001";

        // When
        kpiB3Job.scheduleJobForSingleInstance(instanceIdentification);

        // Then
        verify(scheduler).scheduleJob(any(), any());
    }
}