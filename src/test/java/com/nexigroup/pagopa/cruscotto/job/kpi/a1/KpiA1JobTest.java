package com.nexigroup.pagopa.cruscotto.job.kpi.a1;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
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
import org.junit.jupiter.api.Nested;
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
import com.nexigroup.pagopa.cruscotto.domain.enumeration.OutcomeStatus;
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
import com.nexigroup.pagopa.cruscotto.service.dto.AnagPlannedShutdownDTO;
import com.nexigroup.pagopa.cruscotto.service.dto.InstanceDTO;
import com.nexigroup.pagopa.cruscotto.service.dto.InstanceModuleDTO;
import com.nexigroup.pagopa.cruscotto.service.dto.KpiA1DetailResultDTO;
import com.nexigroup.pagopa.cruscotto.service.dto.KpiA1ResultDTO;
import com.nexigroup.pagopa.cruscotto.service.dto.KpiConfigurationDTO;
import com.nexigroup.pagopa.cruscotto.service.dto.PagoPaRecordedTimeoutDTO;

/**
 * Unit tests for {@link KpiA1Job}.
 * These tests ensure comprehensive coverage of the KPI A1 job execution,
 * including aggregation logic and all business rules.
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("KpiA1Job Tests")
class KpiA1JobTest {

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
        // Setup job configuration
        jobConfig = new ApplicationProperties.KpiA1Job();
        jobConfig.setEnabled(true);
        jobConfig.setLimit(10);

        ApplicationProperties.Job job = new ApplicationProperties.Job();
        job.setKpiA1Job(jobConfig);

        when(applicationProperties.getJob()).thenReturn(job);

        // Setup instance
        instanceDTO = new InstanceDTO();
        instanceDTO.setId(1L);
        instanceDTO.setInstanceIdentification("TEST-INSTANCE");
        instanceDTO.setPartnerFiscalCode("12345678901");
        instanceDTO.setPartnerName("Test Partner");
        instanceDTO.setPartnerId(1L);
        instanceDTO.setAnalysisPeriodStartDate(LocalDate.of(2025, 1, 1));
        instanceDTO.setAnalysisPeriodEndDate(LocalDate.of(2025, 1, 31));

        // Setup instance module
        instanceModuleDTO = new InstanceModuleDTO();
        instanceModuleDTO.setId(1L);

        // Setup KPI configuration
        kpiConfigurationDTO = new KpiConfigurationDTO();
        kpiConfigurationDTO.setModuleId(1L);
        kpiConfigurationDTO.setEligibilityThreshold(2.0);
        kpiConfigurationDTO.setTolerance(0.5);
        kpiConfigurationDTO.setEvaluationType(EvaluationType.MESE);
        kpiConfigurationDTO.setExcludePlannedShutdown(false);
        kpiConfigurationDTO.setExcludeUnplannedShutdown(false);

        // Setup KPI result
        kpiA1ResultDTO = new KpiA1ResultDTO();
        kpiA1ResultDTO.setId(1L);
    }

    @Nested
    @DisplayName("Job Execution Tests")
    class JobExecutionTests {

        @Test
        @DisplayName("Should skip execution when job is disabled")
        void shouldSkipExecutionWhenJobIsDisabled() {
            // Given
            jobConfig.setEnabled(false);

            // When
            kpiA1Job.executeInternal(jobExecutionContext);

            // Then
            verify(instanceService, never()).findInstanceToCalculate(any(), any());
        }

        @Test
        @DisplayName("Should skip execution when no instances to calculate")
        void shouldSkipExecutionWhenNoInstancesToCalculate() {
            // Given
            when(instanceService.findInstanceToCalculate(ModuleCode.A1, 10))
                .thenReturn(new ArrayList<>());

            // When
            kpiA1Job.executeInternal(jobExecutionContext);

            // Then
            verify(kpiConfigurationService, never()).findKpiConfigurationByCode(anyString());
        }

        @Test
        @DisplayName("Should process instance when configuration is found")
        void shouldProcessInstanceWhenConfigurationIsFound() {
            // Given
            List<InstanceDTO> instances = List.of(instanceDTO);
            when(instanceService.findInstanceToCalculate(ModuleCode.A1, 10))
                .thenReturn(instances);
            when(kpiConfigurationService.findKpiConfigurationByCode(ModuleCode.A1.code))
                .thenReturn(Optional.of(kpiConfigurationDTO));
            when(instanceModuleService.findOne(1L, 1L))
                .thenReturn(Optional.of(instanceModuleDTO));
            when(kpiA1ResultService.save(any(KpiA1ResultDTO.class)))
                .thenReturn(kpiA1ResultDTO);
            when(pagoPaRecordedTimeoutService.findAllStationAndMethodIntoPeriodForPartner(
                anyString(), any(LocalDate.class), any(LocalDate.class)))
                .thenReturn(new HashMap<>());

            // When
            kpiA1Job.executeInternal(jobExecutionContext);

            // Then
            verify(instanceService).updateInstanceStatusInProgress(1L);
            verify(kpiA1AnalyticDataService).deleteAllByInstanceModule(1L);
            verify(kpiA1DetailResultService).deleteAllByInstanceModule(1L);
            verify(kpiA1ResultService).deleteAllByInstanceModule(1L);
        }
    }

    @Nested
    @DisplayName("Data Aggregation Tests")
    class DataAggregationTests {

        @Test
        @DisplayName("Should aggregate data correctly for single month with multiple stations and methods")
        void shouldAggregateDataForSingleMonthWithMultipleStationsAndMethods() {
            // Given
            setupBasicMocks();
            
            Map<String, List<String>> stations = new HashMap<>();
            stations.put("STATION1", List.of("METHOD1", "METHOD2"));
            stations.put("STATION2", List.of("METHOD1"));
            
            when(pagoPaRecordedTimeoutService.findAllStationAndMethodIntoPeriodForPartner(
                anyString(), any(LocalDate.class), any(LocalDate.class)))
                .thenReturn(stations);
            when(anagStationService.findIdByNameOrCreate(anyString(), anyLong()))
                .thenReturn(1L);
            when(anagPlannedShutdownService.findAllByTypePlannedIntoPeriod(
                anyLong(), anyLong(), any(TypePlanned.class), any(LocalDate.class), any(LocalDate.class)))
                .thenReturn(new ArrayList<>());

            // Setup timeout data for different days
            setupTimeoutDataForDay(LocalDate.of(2025, 1, 1), 100L, 10L);
            setupTimeoutDataForDay(LocalDate.of(2025, 1, 2), 200L, 20L);
            setupTimeoutDataForDay(LocalDate.of(2025, 1, 3), 150L, 15L);

            KpiA1DetailResultDTO savedDetailResult = new KpiA1DetailResultDTO();
            savedDetailResult.setId(1L);
            when(kpiA1DetailResultService.save(any(KpiA1DetailResultDTO.class)))
                .thenReturn(savedDetailResult);

            // When
            kpiA1Job.executeInternal(jobExecutionContext);

            // Then
            ArgumentCaptor<KpiA1DetailResultDTO> detailResultCaptor = 
                ArgumentCaptor.forClass(KpiA1DetailResultDTO.class);
            verify(kpiA1DetailResultService, times(2)).save(detailResultCaptor.capture());

            List<KpiA1DetailResultDTO> capturedResults = detailResultCaptor.getAllValues();
            
            // Find monthly result
            KpiA1DetailResultDTO monthlyResult = capturedResults.stream()
                .filter(r -> r.getEvaluationType() == EvaluationType.MESE)
                .findFirst()
                .orElseThrow();

            // Verify aggregation: 3 stations/methods * 3 days each = total aggregated values
            assertThat(monthlyResult.getTotReq()).isEqualTo(1350L); // (100+200+150) * 3 combinations
            assertThat(monthlyResult.getReqTimeout()).isEqualTo(135L); // (10+20+15) * 3 combinations
            assertThat(monthlyResult.getTimeoutPercentage()).isEqualTo(10.0); // 135/1350 * 100
            assertThat(monthlyResult.getStationId()).isEqualTo(-1L); // Aggregated placeholder
            assertThat(monthlyResult.getMethod()).isEqualTo("AGGREGATED"); // Aggregated placeholder
        }

        @Test
        @DisplayName("Should calculate outcome as KO when any individual outcome is KO")
        void shouldCalculateOutcomeAsKOWhenAnyIndividualOutcomeIsKO() {
            // Given
            setupBasicMocks();
            
            // Set threshold low enough that high timeout percentage will result in KO
            kpiConfigurationDTO.setEligibilityThreshold(1.0);
            kpiConfigurationDTO.setTolerance(0.0);
            
            Map<String, List<String>> stations = new HashMap<>();
            stations.put("STATION1", List.of("METHOD1"));
            
            when(pagoPaRecordedTimeoutService.findAllStationAndMethodIntoPeriodForPartner(
                anyString(), any(LocalDate.class), any(LocalDate.class)))
                .thenReturn(stations);
            when(anagStationService.findIdByNameOrCreate(anyString(), anyLong()))
                .thenReturn(1L);
            when(anagPlannedShutdownService.findAllByTypePlannedIntoPeriod(
                anyLong(), anyLong(), any(TypePlanned.class), any(LocalDate.class), any(LocalDate.class)))
                .thenReturn(new ArrayList<>());

            // Setup data with high timeout percentage (will trigger KO)
            setupTimeoutDataForDay(LocalDate.of(2025, 1, 1), 100L, 50L); // 50% timeout

            KpiA1DetailResultDTO savedDetailResult = new KpiA1DetailResultDTO();
            savedDetailResult.setId(1L);
            when(kpiA1DetailResultService.save(any(KpiA1DetailResultDTO.class)))
                .thenReturn(savedDetailResult);

            // When
            kpiA1Job.executeInternal(jobExecutionContext);

            // Then
            ArgumentCaptor<KpiA1DetailResultDTO> detailResultCaptor = 
                ArgumentCaptor.forClass(KpiA1DetailResultDTO.class);
            verify(kpiA1DetailResultService, times(2)).save(detailResultCaptor.capture());

            List<KpiA1DetailResultDTO> capturedResults = detailResultCaptor.getAllValues();
            
            // Monthly result should be KO due to high timeout percentage
            KpiA1DetailResultDTO monthlyResult = capturedResults.stream()
                .filter(r -> r.getEvaluationType() == EvaluationType.MESE)
                .findFirst()
                .orElseThrow();

            assertThat(monthlyResult.getOutcome()).isEqualTo(OutcomeStatus.KO);
            
            // Total period result should also be KO since monthly result is KO
            KpiA1DetailResultDTO totalResult = capturedResults.stream()
                .filter(r -> r.getEvaluationType() == EvaluationType.TOTALE)
                .findFirst()
                .orElseThrow();

            assertThat(totalResult.getOutcome()).isEqualTo(OutcomeStatus.KO);
        }

        @Test
        @DisplayName("Should handle multiple months correctly")
        void shouldHandleMultipleMonthsCorrectly() {
            // Given
            setupBasicMocks();
            
            // Set period to span two months
            instanceDTO.setAnalysisPeriodStartDate(LocalDate.of(2025, 1, 15));
            instanceDTO.setAnalysisPeriodEndDate(LocalDate.of(2025, 2, 15));
            
            Map<String, List<String>> stations = new HashMap<>();
            stations.put("STATION1", List.of("METHOD1"));
            
            when(pagoPaRecordedTimeoutService.findAllStationAndMethodIntoPeriodForPartner(
                anyString(), any(LocalDate.class), any(LocalDate.class)))
                .thenReturn(stations);
            when(anagStationService.findIdByNameOrCreate(anyString(), anyLong()))
                .thenReturn(1L);
            when(anagPlannedShutdownService.findAllByTypePlannedIntoPeriod(
                anyLong(), anyLong(), any(TypePlanned.class), any(LocalDate.class), any(LocalDate.class)))
                .thenReturn(new ArrayList<>());

            // Setup timeout data for different months
            setupTimeoutDataForDay(LocalDate.of(2025, 1, 20), 100L, 5L); // January
            setupTimeoutDataForDay(LocalDate.of(2025, 2, 10), 200L, 10L); // February

            KpiA1DetailResultDTO savedDetailResult = new KpiA1DetailResultDTO();
            savedDetailResult.setId(1L);
            when(kpiA1DetailResultService.save(any(KpiA1DetailResultDTO.class)))
                .thenReturn(savedDetailResult);

            // When
            kpiA1Job.executeInternal(jobExecutionContext);

            // Then
            ArgumentCaptor<KpiA1DetailResultDTO> detailResultCaptor = 
                ArgumentCaptor.forClass(KpiA1DetailResultDTO.class);
            verify(kpiA1DetailResultService, times(3)).save(detailResultCaptor.capture()); // 2 months + 1 total

            List<KpiA1DetailResultDTO> capturedResults = detailResultCaptor.getAllValues();
            
            // Should have 2 monthly results and 1 total result
            long monthlyCount = capturedResults.stream()
                .filter(r -> r.getEvaluationType() == EvaluationType.MESE)
                .count();
            long totalCount = capturedResults.stream()
                .filter(r -> r.getEvaluationType() == EvaluationType.TOTALE)
                .count();

            assertThat(monthlyCount).isEqualTo(2);
            assertThat(totalCount).isEqualTo(1);
        }
    }

    @Nested
    @DisplayName("Maintenance Exclusion Tests")
    class MaintenanceExclusionTests {

        @Test
        @DisplayName("Should exclude planned maintenance when configured")
        void shouldExcludePlannedMaintenanceWhenConfigured() {
            // Given
            setupBasicMocks();
            kpiConfigurationDTO.setExcludePlannedShutdown(true);
            
            Map<String, List<String>> stations = new HashMap<>();
            stations.put("STATION1", List.of("METHOD1"));
            
            when(pagoPaRecordedTimeoutService.findAllStationAndMethodIntoPeriodForPartner(
                anyString(), any(LocalDate.class), any(LocalDate.class)))
                .thenReturn(stations);
            when(anagStationService.findIdByNameOrCreate(anyString(), anyLong()))
                .thenReturn(1L);

            // Setup planned maintenance
            List<AnagPlannedShutdownDTO> maintenance = new ArrayList<>();
            AnagPlannedShutdownDTO plannedShutdown = new AnagPlannedShutdownDTO();
            maintenance.add(plannedShutdown);
            
            when(anagPlannedShutdownService.findAllByTypePlannedIntoPeriod(
                anyLong(), anyLong(), eq(TypePlanned.PROGRAMMATO), any(LocalDate.class), any(LocalDate.class)))
                .thenReturn(maintenance);
            when(anagPlannedShutdownService.findAllByTypePlannedIntoPeriod(
                anyLong(), anyLong(), eq(TypePlanned.NON_PROGRAMMATO), any(LocalDate.class), any(LocalDate.class)))
                .thenReturn(new ArrayList<>());

            setupTimeoutDataForDay(LocalDate.of(2025, 1, 1), 100L, 10L);

            KpiA1DetailResultDTO savedDetailResult = new KpiA1DetailResultDTO();
            savedDetailResult.setId(1L);
            when(kpiA1DetailResultService.save(any(KpiA1DetailResultDTO.class)))
                .thenReturn(savedDetailResult);

            // When
            kpiA1Job.executeInternal(jobExecutionContext);

            // Then
            verify(anagPlannedShutdownService).findAllByTypePlannedIntoPeriod(
                anyLong(), anyLong(), eq(TypePlanned.PROGRAMMATO), any(LocalDate.class), any(LocalDate.class));
        }
    }

    @Nested
    @DisplayName("Error Handling Tests")
    class ErrorHandlingTests {

        @Test
        @DisplayName("Should handle missing KPI configuration gracefully")
        void shouldHandleMissingKpiConfigurationGracefully() {
            // Given
            List<InstanceDTO> instances = List.of(instanceDTO);
            when(instanceService.findInstanceToCalculate(ModuleCode.A1, 10))
                .thenReturn(instances);
            when(kpiConfigurationService.findKpiConfigurationByCode(ModuleCode.A1.code))
                .thenReturn(Optional.empty());

            // When & Then - Should not throw exception
            kpiA1Job.executeInternal(jobExecutionContext);
            
            verify(instanceService, never()).updateInstanceStatusInProgress(anyLong());
        }

        @Test
        @DisplayName("Should handle missing instance module gracefully")
        void shouldHandleMissingInstanceModuleGracefully() {
            // Given
            List<InstanceDTO> instances = List.of(instanceDTO);
            when(instanceService.findInstanceToCalculate(ModuleCode.A1, 10))
                .thenReturn(instances);
            when(kpiConfigurationService.findKpiConfigurationByCode(ModuleCode.A1.code))
                .thenReturn(Optional.of(kpiConfigurationDTO));
            when(instanceModuleService.findOne(1L, 1L))
                .thenReturn(Optional.empty());

            // When & Then - Should not throw exception
            kpiA1Job.executeInternal(jobExecutionContext);
        }
    }

    private void setupBasicMocks() {
        List<InstanceDTO> instances = List.of(instanceDTO);
        when(instanceService.findInstanceToCalculate(ModuleCode.A1, 10))
            .thenReturn(instances);
        when(kpiConfigurationService.findKpiConfigurationByCode(ModuleCode.A1.code))
            .thenReturn(Optional.of(kpiConfigurationDTO));
        when(instanceModuleService.findOne(1L, 1L))
            .thenReturn(Optional.of(instanceModuleDTO));
        when(kpiA1ResultService.save(any(KpiA1ResultDTO.class)))
            .thenReturn(kpiA1ResultDTO);
        when(kpiA1AnalyticDataService.deleteAllByInstanceModule(anyLong()))
            .thenReturn(0);
        when(kpiA1DetailResultService.deleteAllByInstanceModule(anyLong()))
            .thenReturn(0);
        when(kpiA1ResultService.deleteAllByInstanceModule(anyLong()))
            .thenReturn(0);
    }

    private void setupTimeoutDataForDay(LocalDate date, Long totReq, Long reqTimeout) {
        PagoPaRecordedTimeoutDTO timeoutData = new PagoPaRecordedTimeoutDTO();
        timeoutData.setTotReq(totReq);
        timeoutData.setReqOk(totReq - reqTimeout);
        timeoutData.setReqTimeout(reqTimeout);
        timeoutData.setStartDate(date.atStartOfDay().toInstant(java.time.ZoneOffset.UTC));
        timeoutData.setEndDate(date.atTime(23, 59, 59).toInstant(java.time.ZoneOffset.UTC));

        when(pagoPaRecordedTimeoutService.findAllRecordIntoDayForPartnerStationAndMethod(
            anyString(), anyString(), anyString(), eq(date)))
            .thenReturn(List.of(timeoutData));
    }
}
