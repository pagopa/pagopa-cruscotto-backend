package com.nexigroup.pagopa.cruscotto.job.kpi.c1;

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
class KpiC1JobTest {

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
    private JobExecutionContext jobExecutionContext;

    @InjectMocks
    private KpiC1Job kpiC1Job;

    private ApplicationProperties.KpiC1Job kpiC1JobConfig;
    private ApplicationProperties.Job jobConfig;

    @BeforeEach
    void setUp() {
        kpiC1JobConfig = new ApplicationProperties.KpiC1Job();
        kpiC1JobConfig.setEnabled(true);
        kpiC1JobConfig.setLimit(10);

        jobConfig = new ApplicationProperties.Job();
        jobConfig.setKpiC1Job(kpiC1JobConfig);

        lenient().when(applicationProperties.getJob()).thenReturn(jobConfig);
    }

    @Test
    void testExecuteInternal_WhenJobDisabled() throws Exception {
        // Given
        kpiC1JobConfig.setEnabled(false);

        // When
        kpiC1Job.executeInternal(jobExecutionContext);

        // Then
        verify(instanceService, never()).findInstanceToCalculate(any(), anyInt());
    }

    @Test
    void testExecuteInternal_WhenNoInstancesToProcess() throws Exception {
        // Given
        when(instanceService.findInstanceToCalculate(ModuleCode.C1, 10))
            .thenReturn(Collections.emptyList());

        // When
        kpiC1Job.executeInternal(jobExecutionContext);

        // Then
        verify(instanceService).findInstanceToCalculate(ModuleCode.C1, 10);
        verify(kpiConfigurationService, never()).findKpiConfigurationByCode(anyString());
    }

    @Test
    void testExecuteInternal_WithInstancesToProcess() throws Exception {
        // Given
        InstanceDTO instanceDTO = createTestInstanceDTO();
        when(instanceService.findInstanceToCalculate(ModuleCode.C1, 10))
            .thenReturn(Arrays.asList(instanceDTO));

        KpiConfigurationDTO kpiConfigurationDTO = createTestKpiConfigurationDTO();
        when(kpiConfigurationService.findKpiConfigurationByCode(ModuleCode.C1.code))
            .thenReturn(Optional.of(kpiConfigurationDTO));

        InstanceModuleDTO instanceModuleDTO = createTestInstanceModuleDTO();
        when(instanceModuleService.findOne(instanceDTO.getId(), kpiConfigurationDTO.getModuleId()))
            .thenReturn(Optional.of(instanceModuleDTO));

        // When
        kpiC1Job.executeInternal(jobExecutionContext);

        // Then
        verify(instanceService).findInstanceToCalculate(ModuleCode.C1, 10);
        verify(kpiConfigurationService).findKpiConfigurationByCode(ModuleCode.C1.code);
        verify(instanceService).updateInstanceStatusInProgress(instanceDTO.getId());
        verify(instanceModuleService).findOne(instanceDTO.getId(), kpiConfigurationDTO.getModuleId());
        verify(instanceService).updateInstanceStatusCompleted(instanceDTO.getId());
    }

    @Test
    void testExecuteInternal_WhenKpiConfigurationNotFound() throws Exception {
        // Given
        InstanceDTO instanceDTO = createTestInstanceDTO();
        when(instanceService.findInstanceToCalculate(ModuleCode.C1, 10))
            .thenReturn(Arrays.asList(instanceDTO));

        when(kpiConfigurationService.findKpiConfigurationByCode(ModuleCode.C1.code))
            .thenReturn(Optional.empty());

        // When
        kpiC1Job.executeInternal(jobExecutionContext);

        // Then
        verify(instanceService).findInstanceToCalculate(ModuleCode.C1, 10);
        verify(kpiConfigurationService).findKpiConfigurationByCode(ModuleCode.C1.code);
        verify(instanceService, never()).updateInstanceStatusInProgress(any());
    }

    @Test
    void testExecuteInternal_WhenInstanceModuleNotFound() throws Exception {
        // Given
        InstanceDTO instanceDTO = createTestInstanceDTO();
        when(instanceService.findInstanceToCalculate(ModuleCode.C1, 10))
            .thenReturn(Arrays.asList(instanceDTO));

        KpiConfigurationDTO kpiConfigurationDTO = createTestKpiConfigurationDTO();
        when(kpiConfigurationService.findKpiConfigurationByCode(ModuleCode.C1.code))
            .thenReturn(Optional.of(kpiConfigurationDTO));

        when(instanceModuleService.findOne(instanceDTO.getId(), kpiConfigurationDTO.getModuleId()))
            .thenReturn(Optional.empty());

        // When
        kpiC1Job.executeInternal(jobExecutionContext);

        // Then
        verify(instanceService).updateInstanceStatusInProgress(instanceDTO.getId());
        verify(instanceService).updateInstanceStatusError(instanceDTO.getId());
    }

    @Test
    void testScheduleJobForSingleInstance() throws Exception {
        // Given
        String instanceIdentification = "TEST_INSTANCE_001";

        // When
        kpiC1Job.scheduleJobForSingleInstance(instanceIdentification);

        // Then
        verify(scheduler).scheduleJob(any(), any());
    }

    private InstanceDTO createTestInstanceDTO() {
        InstanceDTO instanceDTO = new InstanceDTO();
        instanceDTO.setId(1L);
        instanceDTO.setInstanceIdentification("TEST_INSTANCE_001");
        instanceDTO.setPartnerFiscalCode("12345678901");
        instanceDTO.setPartnerName("Test Partner");
        instanceDTO.setAnalysisPeriodStartDate(LocalDate.now().minusDays(30));
        instanceDTO.setAnalysisPeriodEndDate(LocalDate.now());
        return instanceDTO;
    }

    private KpiConfigurationDTO createTestKpiConfigurationDTO() {
        KpiConfigurationDTO kpiConfigurationDTO = new KpiConfigurationDTO();
        kpiConfigurationDTO.setId(1L);
        kpiConfigurationDTO.setModuleId(1L);
        kpiConfigurationDTO.setEligibilityThreshold(95.0);
        kpiConfigurationDTO.setExcludePlannedShutdown(false);
        kpiConfigurationDTO.setExcludeUnplannedShutdown(false);
        return kpiConfigurationDTO;
    }

    private InstanceModuleDTO createTestInstanceModuleDTO() {
        InstanceModuleDTO instanceModuleDTO = new InstanceModuleDTO();
        instanceModuleDTO.setId(1L);
        instanceModuleDTO.setInstanceId(1L);
        instanceModuleDTO.setModuleId(1L);
        return instanceModuleDTO;
    }
}