package com.nexigroup.pagopa.cruscotto.job.kpi.a1;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.quartz.JobExecutionContext;
import org.quartz.Scheduler;

import com.nexigroup.pagopa.cruscotto.config.ApplicationProperties;
import com.nexigroup.pagopa.cruscotto.domain.enumeration.EvaluationType;
import com.nexigroup.pagopa.cruscotto.domain.enumeration.ModuleCode;
import com.nexigroup.pagopa.cruscotto.domain.enumeration.TypePlanned;
import com.nexigroup.pagopa.cruscotto.service.AnagPlannedShutdownService;
import com.nexigroup.pagopa.cruscotto.service.AnagStationService;
import com.nexigroup.pagopa.cruscotto.service.InstanceModuleService;
import com.nexigroup.pagopa.cruscotto.service.InstanceService;
import com.nexigroup.pagopa.cruscotto.service.KpiA1AnalyticDataService;
import com.nexigroup.pagopa.cruscotto.service.KpiA1DetailResultService;
import com.nexigroup.pagopa.cruscotto.service.KpiA1ResultService;
import com.nexigroup.pagopa.cruscotto.service.KpiConfigurationService;
import com.nexigroup.pagopa.cruscotto.service.PagoPaRecordedTimeoutService;
import com.nexigroup.pagopa.cruscotto.service.dto.InstanceDTO;
import com.nexigroup.pagopa.cruscotto.service.dto.InstanceModuleDTO;
import com.nexigroup.pagopa.cruscotto.service.dto.KpiA1DetailResultDTO;
import com.nexigroup.pagopa.cruscotto.service.dto.KpiA1ResultDTO;
import com.nexigroup.pagopa.cruscotto.service.dto.KpiConfigurationDTO;
import com.nexigroup.pagopa.cruscotto.service.dto.PagoPaRecordedTimeoutDTO;

/**
 * Simple test for {@link KpiA1Job} focusing on basic aggregation functionality.
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("KpiA1Job Simple Tests")
class KpiA1JobSimpleTest {

    @Mock
    private InstanceService instanceService;

    @Mock
    private InstanceModuleService instanceModuleService;

    @Mock
    private ApplicationProperties applicationProperties;

    @Mock
    private PagoPaRecordedTimeoutService pagoPaRecordedTimeoutService;

    @Mock
    private AnagStationService anagStationService;

    @Mock
    private KpiConfigurationService kpiConfigurationService;

    @Mock
    private AnagPlannedShutdownService anagPlannedShutdownService;

    @Mock
    private KpiA1AnalyticDataService kpiA1AnalyticDataService;

    @Mock
    private KpiA1DetailResultService kpiA1DetailResultService;

    @Mock
    private KpiA1ResultService kpiA1ResultService;

    @Mock
    private Scheduler scheduler;

    @Mock
    private JobExecutionContext jobExecutionContext;

    @InjectMocks
    private KpiA1Job kpiA1Job;

    private ApplicationProperties.KpiA1Job jobConfig;
    private InstanceDTO instanceDTO;
    private InstanceModuleDTO instanceModuleDTO;
    private KpiConfigurationDTO kpiConfigurationDTO;
    private KpiA1ResultDTO kpiA1ResultDTO;

    @BeforeEach
    void setUp() {
        setupJobConfiguration();
        setupInstance();
        setupInstanceModule();
        setupKpiConfiguration();
        setupKpiResult();
    }

    @Test
    @DisplayName("Should process simple aggregation with single station and method")
    void shouldProcessSimpleAggregationWithSingleStationAndMethod() {
        // Given
        setupBasicMocks();
        
        // Setup single station and method
        Map<String, List<String>> stationsAndMethods = new HashMap<>();
        stationsAndMethods.put("TEST_STATION", List.of("POST"));
        
        lenient().when(pagoPaRecordedTimeoutService.findAllStationAndMethodIntoPeriodForPartner(
            eq("12345678901"), eq(LocalDate.of(2025, 1, 1)), eq(LocalDate.of(2025, 1, 31))))
            .thenReturn(stationsAndMethods);

        // Setup timeout data for single day
        PagoPaRecordedTimeoutDTO timeoutData = new PagoPaRecordedTimeoutDTO();
        timeoutData.setTotReq(1000L);
        timeoutData.setReqOk(950L);
        timeoutData.setReqTimeout(50L);
        timeoutData.setStartDate(LocalDate.of(2025, 1, 1).atStartOfDay().toInstant(java.time.ZoneOffset.UTC));
        timeoutData.setEndDate(LocalDate.of(2025, 1, 1).atTime(23, 59, 59).toInstant(java.time.ZoneOffset.UTC));

        lenient().when(pagoPaRecordedTimeoutService.findAllRecordIntoDayForPartnerStationAndMethod(
            eq("12345678901"), eq("TEST_STATION"), eq("POST"), any(LocalDate.class)))
            .thenReturn(List.of(timeoutData));

        lenient().when(anagStationService.findIdByNameOrCreate("TEST_STATION", 1L)).thenReturn(100L);

        // Mock empty maintenance
        lenient().when(anagPlannedShutdownService.findAllByTypePlannedIntoPeriod(
            eq(1L), eq(100L), any(TypePlanned.class), any(LocalDate.class), any(LocalDate.class)))
            .thenReturn(new ArrayList<>());

        // Mock save operations
        KpiA1DetailResultDTO savedDetailResult = new KpiA1DetailResultDTO();
        savedDetailResult.setId(1L);
        lenient().when(kpiA1DetailResultService.save(any(KpiA1DetailResultDTO.class)))
            .thenReturn(savedDetailResult);

        // When
        kpiA1Job.executeInternal(jobExecutionContext);

        // Then
        // Verify that save was called at least once (monthly + total)
        verify(kpiA1DetailResultService, times(2)).save(any(KpiA1DetailResultDTO.class));
        
        // Capture the saved results
        ArgumentCaptor<KpiA1DetailResultDTO> captor = ArgumentCaptor.forClass(KpiA1DetailResultDTO.class);
        verify(kpiA1DetailResultService, times(2)).save(captor.capture());
        
        List<KpiA1DetailResultDTO> savedResults = captor.getAllValues();
        assertThat(savedResults).hasSize(2);
        
        // First should be monthly aggregation
        KpiA1DetailResultDTO monthlyResult = savedResults.get(0);
        assertThat(monthlyResult.getStationId()).isEqualTo(-1L); // Aggregated placeholder
        assertThat(monthlyResult.getMethod()).isEqualTo("AGGREGATED"); // Aggregated placeholder
        assertThat(monthlyResult.getEvaluationType()).isEqualTo(EvaluationType.MESE);
        assertThat(monthlyResult.getTotReq()).isEqualTo(31000L); // 1000 * 31 days
        assertThat(monthlyResult.getReqTimeout()).isEqualTo(1550L); // 50 * 31 days
        
        // Second should be total period aggregation
        KpiA1DetailResultDTO totalResult = savedResults.get(1);
        assertThat(totalResult.getStationId()).isEqualTo(-1L); // Aggregated placeholder
        assertThat(totalResult.getMethod()).isEqualTo("AGGREGATED"); // Aggregated placeholder
        assertThat(totalResult.getEvaluationType()).isEqualTo(EvaluationType.TOTALE);
        assertThat(totalResult.getTotReq()).isEqualTo(31000L); // Same as monthly since it's only one month
        assertThat(totalResult.getReqTimeout()).isEqualTo(1550L); // Same as monthly since it's only one month
    }

    @Test
    @DisplayName("Should handle empty stations list")
    void shouldHandleEmptyStationsList() {
        // Given
        setupBasicMocks();
        
        // Setup empty stations map
        when(pagoPaRecordedTimeoutService.findAllStationAndMethodIntoPeriodForPartner(
            eq("12345678901"), eq(LocalDate.of(2025, 1, 1)), eq(LocalDate.of(2025, 1, 31))))
            .thenReturn(new HashMap<>());

        // When
        kpiA1Job.executeInternal(jobExecutionContext);

        // Then
        // Should not call save when no stations are found
        verify(kpiA1DetailResultService, times(0)).save(any(KpiA1DetailResultDTO.class));
    }

    @Test
    @DisplayName("Should handle null stations and methods")
    void shouldHandleNullStationsAndMethods() {
        // Given
        setupBasicMocks();
        
        // Setup null return
        when(pagoPaRecordedTimeoutService.findAllStationAndMethodIntoPeriodForPartner(
            eq("12345678901"), eq(LocalDate.of(2025, 1, 1)), eq(LocalDate.of(2025, 1, 31))))
            .thenReturn(null);

        // When
        kpiA1Job.executeInternal(jobExecutionContext);

        // Then
        // Should not call save when stations map is null
        verify(kpiA1DetailResultService, times(0)).save(any(KpiA1DetailResultDTO.class));
    }

    private void setupJobConfiguration() {
        jobConfig = new ApplicationProperties.KpiA1Job();
        jobConfig.setEnabled(true);
        jobConfig.setLimit(10);

        ApplicationProperties.Job job = new ApplicationProperties.Job();
        job.setKpiA1Job(jobConfig);

        lenient().when(applicationProperties.getJob()).thenReturn(job);
    }

    private void setupInstance() {
        instanceDTO = new InstanceDTO();
        instanceDTO.setId(1L);
        instanceDTO.setInstanceIdentification("TEST-INSTANCE");
        instanceDTO.setPartnerFiscalCode("12345678901");
        instanceDTO.setPartnerName("Test Partner");
        instanceDTO.setPartnerId(1L);
        instanceDTO.setAnalysisPeriodStartDate(LocalDate.of(2025, 1, 1));
        instanceDTO.setAnalysisPeriodEndDate(LocalDate.of(2025, 1, 31));
    }

    private void setupInstanceModule() {
        instanceModuleDTO = new InstanceModuleDTO();
        instanceModuleDTO.setId(1L);
    }

    private void setupKpiConfiguration() {
        kpiConfigurationDTO = new KpiConfigurationDTO();
        kpiConfigurationDTO.setModuleId(1L);
        kpiConfigurationDTO.setEligibilityThreshold(5.0); // 5%
        kpiConfigurationDTO.setTolerance(1.0); // 1%
        kpiConfigurationDTO.setEvaluationType(EvaluationType.MESE);
        kpiConfigurationDTO.setExcludePlannedShutdown(false);
        kpiConfigurationDTO.setExcludeUnplannedShutdown(false);
    }

    private void setupKpiResult() {
        kpiA1ResultDTO = new KpiA1ResultDTO();
        kpiA1ResultDTO.setId(1L);
    }

    private void setupBasicMocks() {
        List<InstanceDTO> instances = List.of(instanceDTO);
        lenient().when(instanceService.findInstanceToCalculate(ModuleCode.A1, 10))
            .thenReturn(instances);
        lenient().when(kpiConfigurationService.findKpiConfigurationByCode(ModuleCode.A1.code))
            .thenReturn(Optional.of(kpiConfigurationDTO));
        lenient().when(instanceModuleService.findOne(1L, 1L))
            .thenReturn(Optional.of(instanceModuleDTO));
        lenient().when(kpiA1ResultService.save(any(KpiA1ResultDTO.class)))
            .thenReturn(kpiA1ResultDTO);
        lenient().when(kpiA1AnalyticDataService.deleteAllByInstanceModule(anyLong()))
            .thenReturn(0);
        lenient().when(kpiA1DetailResultService.deleteAllByInstanceModule(anyLong()))
            .thenReturn(0);
        lenient().when(kpiA1ResultService.deleteAllByInstanceModule(anyLong()))
            .thenReturn(0);
    }
}
