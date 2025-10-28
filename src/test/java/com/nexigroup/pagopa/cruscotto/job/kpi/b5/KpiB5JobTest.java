package com.nexigroup.pagopa.cruscotto.job.kpi.b5;

import com.nexigroup.pagopa.cruscotto.config.ApplicationProperties;
import com.nexigroup.pagopa.cruscotto.domain.enumeration.ModuleCode;
import com.nexigroup.pagopa.cruscotto.service.InstanceService;
import com.nexigroup.pagopa.cruscotto.service.dto.InstanceDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.quartz.JobExecutionContext;

import java.util.Arrays;
import java.util.Collections;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class KpiB5JobTest {

    @Mock
    private InstanceService instanceService;

    @Mock
    private ApplicationProperties applicationProperties;

    @Mock
    private JobExecutionContext jobExecutionContext;

    @InjectMocks
    private KpiB5Job kpiB5Job;

    private ApplicationProperties.KpiB5Job kpiB5JobConfig;
    private ApplicationProperties.Job jobConfig;

    @BeforeEach
    void setUp() {
        kpiB5JobConfig = new ApplicationProperties.KpiB5Job();
        kpiB5JobConfig.setEnabled(true);
        kpiB5JobConfig.setLimit(10);

        jobConfig = new ApplicationProperties.Job();
        jobConfig.setKpiB5Job(kpiB5JobConfig);

        lenient().when(applicationProperties.getJob()).thenReturn(jobConfig);
    }

    @Test
    void testExecuteInternal_WhenJobDisabled() throws Exception {
        // Given
        kpiB5JobConfig.setEnabled(false);

        // When
        kpiB5Job.executeInternal(jobExecutionContext);

        // Then
        verify(instanceService, never()).findInstanceToCalculate(any(), anyInt());
    }

    @Test
    void testExecuteInternal_WhenNoInstancesFound() throws Exception {
        // Given
        when(instanceService.findInstanceToCalculate(eq(ModuleCode.B5), eq(10)))
            .thenReturn(Collections.emptyList());

        // When
        kpiB5Job.executeInternal(jobExecutionContext);

        // Then
        verify(instanceService).findInstanceToCalculate(eq(ModuleCode.B5), eq(10));
    }

    @Test
    void testExecuteInternal_WhenInstancesFound() throws Exception {
        // Given
        InstanceDTO instance1 = new InstanceDTO();
        instance1.setId(1L);
        instance1.setInstanceIdentification("INST001");
        instance1.setPartnerName("Partner 1");

        InstanceDTO instance2 = new InstanceDTO();
        instance2.setId(2L);
        instance2.setInstanceIdentification("INST002");
        instance2.setPartnerName("Partner 2");

        when(instanceService.findInstanceToCalculate(eq(ModuleCode.B5), eq(10)))
            .thenReturn(Arrays.asList(instance1, instance2));

        // When
        kpiB5Job.executeInternal(jobExecutionContext);

        // Then
        verify(instanceService).findInstanceToCalculate(eq(ModuleCode.B5), eq(10));
    }

    @Test
    void testExecuteInternal_WhenExceptionOccurs() throws Exception {
        // Given
        when(instanceService.findInstanceToCalculate(eq(ModuleCode.B5), eq(10)))
            .thenThrow(new RuntimeException("Test exception"));

        // When
        kpiB5Job.executeInternal(jobExecutionContext);

        // Then
        verify(instanceService).findInstanceToCalculate(eq(ModuleCode.B5), eq(10));
        // The job should handle the exception gracefully and continue
    }
}