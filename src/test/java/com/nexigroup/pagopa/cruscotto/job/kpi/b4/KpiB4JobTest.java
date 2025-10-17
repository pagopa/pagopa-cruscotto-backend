package com.nexigroup.pagopa.cruscotto.job.kpi.b4;

import com.nexigroup.pagopa.cruscotto.config.ApplicationProperties;
import com.nexigroup.pagopa.cruscotto.domain.enumeration.ModuleCode;
import com.nexigroup.pagopa.cruscotto.service.InstanceService;
import com.nexigroup.pagopa.cruscotto.service.InstanceModuleService;
import com.nexigroup.pagopa.cruscotto.service.KpiConfigurationService;
import com.nexigroup.pagopa.cruscotto.service.KpiB4DataService;
import com.nexigroup.pagopa.cruscotto.service.dto.InstanceDTO;
import com.nexigroup.pagopa.cruscotto.service.dto.InstanceModuleDTO;
import com.nexigroup.pagopa.cruscotto.service.dto.KpiConfigurationDTO;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.quartz.JobExecutionContext;
import org.quartz.Scheduler;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class KpiB4JobTest {

    @Mock
    private InstanceService instanceService;

    @Mock
    private InstanceModuleService instanceModuleService;

    @Mock
    private ApplicationProperties applicationProperties;

    @Mock
    private KpiConfigurationService kpiConfigurationService;

    @Mock
    private KpiB4DataService kpiB4DataService;

    @Mock
    private Scheduler scheduler;

    @Mock
    private JobExecutionContext jobExecutionContext;

    @InjectMocks
    private KpiB4Job kpiB4Job;

    private ApplicationProperties.KpiB4Job kpiB4JobConfig;
    private ApplicationProperties.Job jobConfig;

    @BeforeEach
    void setUp() {
        kpiB4JobConfig = new ApplicationProperties.KpiB4Job();
        kpiB4JobConfig.setEnabled(true);
        kpiB4JobConfig.setLimit(10);

        jobConfig = new ApplicationProperties.Job();
        jobConfig.setKpiB4Job(kpiB4JobConfig);

        lenient().when(applicationProperties.getJob()).thenReturn(jobConfig);
    }

    @Test
    void executeInternal_whenJobDisabled_shouldExitEarly() throws Exception {
        // Given
        kpiB4JobConfig.setEnabled(false);

        // When
        kpiB4Job.executeInternal(jobExecutionContext);

        // Then
        verify(instanceService, never()).findInstanceToCalculate(any(), any());
    }

    @Test
    void executeInternal_whenNoInstancesToCalculate_shouldExitEarly() throws Exception {
        // Given
        when(instanceService.findInstanceToCalculate(ModuleCode.B4, 10))
                .thenReturn(Collections.emptyList());

        // When
        kpiB4Job.executeInternal(jobExecutionContext);

        // Then
        verify(instanceService).findInstanceToCalculate(ModuleCode.B4, 10);
        verify(kpiConfigurationService, never()).findKpiConfigurationByCode(anyString());
    }

    @Test
    void executeInternal_whenKpiConfigurationNotFound_shouldExitEarly() throws Exception {
        // Given
        List<InstanceDTO> instanceDTOs = Arrays.asList(createTestInstanceDTO());
        when(instanceService.findInstanceToCalculate(ModuleCode.B4, 10))
                .thenReturn(instanceDTOs);
        when(kpiConfigurationService.findKpiConfigurationByCode(ModuleCode.B4.code))
                .thenReturn(Optional.empty());

        // When
        kpiB4Job.executeInternal(jobExecutionContext);

        // Then
        verify(instanceService).findInstanceToCalculate(ModuleCode.B4, 10);
        verify(kpiConfigurationService).findKpiConfigurationByCode(ModuleCode.B4.code);
        verify(instanceModuleService, never()).findOne(any(), any());
    }

    @Test
    void executeInternal_whenInstanceModulesNotFound_shouldContinue() throws Exception {
        // Given
        List<InstanceDTO> instanceDTOs = Arrays.asList(createTestInstanceDTO());
        KpiConfigurationDTO kpiConfig = createTestKpiConfigurationDTO();

        when(instanceService.findInstanceToCalculate(ModuleCode.B4, 10))
                .thenReturn(instanceDTOs);
        when(kpiConfigurationService.findKpiConfigurationByCode(ModuleCode.B4.code))
                .thenReturn(Optional.of(kpiConfig));
        when(instanceModuleService.findOne(1L, kpiConfig.getModuleId()))
                .thenReturn(Optional.empty());

        // When
        kpiB4Job.executeInternal(jobExecutionContext);

        // Then
        verify(instanceService).findInstanceToCalculate(ModuleCode.B4, 10);
        verify(kpiConfigurationService).findKpiConfigurationByCode(ModuleCode.B4.code);
        verify(instanceModuleService).findOne(1L, kpiConfig.getModuleId());
        verify(kpiB4DataService, never()).saveKpiB4Results(any(), any(), any(), any(), any());
    }

    @Test
    void executeInternal_withValidData_shouldProcessSuccessfully() throws Exception {
        // Given
        List<InstanceDTO> instanceDTOs = Arrays.asList(createTestInstanceDTO());
        KpiConfigurationDTO kpiConfig = createTestKpiConfigurationDTO();
        List<InstanceModuleDTO> instanceModules = Arrays.asList(createTestInstanceModuleDTO());

        when(instanceService.findInstanceToCalculate(ModuleCode.B4, 10))
                .thenReturn(instanceDTOs);
        when(kpiConfigurationService.findKpiConfigurationByCode(ModuleCode.B4.code))
                .thenReturn(Optional.of(kpiConfig));
        when(instanceModuleService.findOne(1L, kpiConfig.getModuleId()))
                .thenReturn(Optional.of(instanceModules.get(0)));

        // When
        kpiB4Job.executeInternal(jobExecutionContext);

        // Then
        verify(instanceService).findInstanceToCalculate(ModuleCode.B4, 10);
        verify(kpiConfigurationService).findKpiConfigurationByCode(ModuleCode.B4.code);
        verify(instanceModuleService).findOne(1L, kpiConfig.getModuleId());
        verify(kpiB4DataService).saveKpiB4Results(any(), any(), any(), any(), any());
    }

    @Test
    void scheduleJobForSingleInstance_shouldScheduleJob() {
        // Given
        String instanceIdentification = "TEST_INSTANCE";

        // When
        kpiB4Job.scheduleJobForSingleInstance(instanceIdentification);

        // Then - No exception should be thrown
        // Il test verifica che il metodo non lanci eccezioni
    }

    private InstanceDTO createTestInstanceDTO() {
        InstanceDTO instanceDTO = new InstanceDTO();
        instanceDTO.setId(1L);
        instanceDTO.setInstanceIdentification("TEST_INSTANCE");
        return instanceDTO;
    }

    private KpiConfigurationDTO createTestKpiConfigurationDTO() {
        KpiConfigurationDTO kpiConfigDTO = new KpiConfigurationDTO();
        kpiConfigDTO.setId(1L);
        kpiConfigDTO.setModuleId(1L);
        kpiConfigDTO.setModuleCode(ModuleCode.B4.code);
        return kpiConfigDTO;
    }

    private InstanceModuleDTO createTestInstanceModuleDTO() {
        InstanceModuleDTO instanceModuleDTO = new InstanceModuleDTO();
        instanceModuleDTO.setId(1L);
        instanceModuleDTO.setInstanceId(1L);
        instanceModuleDTO.setModuleCode(ModuleCode.B4.code);
        return instanceModuleDTO;
    }
}