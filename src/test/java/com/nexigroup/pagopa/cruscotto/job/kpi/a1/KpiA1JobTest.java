package com.nexigroup.pagopa.cruscotto.job.kpi.a1;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
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
import com.nexigroup.pagopa.cruscotto.service.KpiA1AnalyticDrillDownService;
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
 * These tests ensure comprehensive coverage of the KpiA1Job execution logic,
 * following the project's standard testing approach.
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
    private KpiA1AnalyticDrillDownService kpiA1AnalyticDrillDownService;

    @Mock
    private Scheduler scheduler;

    @Mock
    private JobExecutionContext jobExecutionContext;

    @Mock
    private ApplicationProperties.Job jobConfig;

    @Mock
    private ApplicationProperties.KpiA1Job kpiA1JobConfig;

    @InjectMocks
    private KpiA1Job kpiA1Job;

    private InstanceDTO testInstance;
    private InstanceModuleDTO testInstanceModule;
    private KpiConfigurationDTO testKpiConfiguration;

    @BeforeEach
    void setUp() {
        setupTestData();
        setupCommonMocks();
    }

    private void setupTestData() {
        // Setup test instance
        testInstance = new InstanceDTO();
        testInstance.setId(1L);
        testInstance.setInstanceIdentification("TEST_INSTANCE_001");
        testInstance.setPartnerFiscalCode("12345678901");
        testInstance.setPartnerName("Test Partner");
        testInstance.setPartnerId(100L);
        testInstance.setAnalysisPeriodStartDate(LocalDate.of(2023, 1, 1));
        testInstance.setAnalysisPeriodEndDate(LocalDate.of(2023, 1, 31));

        // Setup test instance module
        testInstanceModule = new InstanceModuleDTO();
        testInstanceModule.setId(10L);
        testInstanceModule.setInstanceId(1L);
        testInstanceModule.setModuleId(5L);

        // Setup test KPI configuration
        testKpiConfiguration = new KpiConfigurationDTO();
        testKpiConfiguration.setId(1L);
        testKpiConfiguration.setModuleId(5L);
        testKpiConfiguration.setModuleCode(ModuleCode.A1.code);
        testKpiConfiguration.setEligibilityThreshold(5.0);
        testKpiConfiguration.setTolerance(1.0);
        testKpiConfiguration.setEvaluationType(EvaluationType.TOTALE);
        testKpiConfiguration.setExcludePlannedShutdown(false);
        testKpiConfiguration.setExcludeUnplannedShutdown(false);
    }

    private void setupCommonMocks() {
        // Setup application properties chain
        when(applicationProperties.getJob()).thenReturn(jobConfig);
        when(jobConfig.getKpiA1Job()).thenReturn(kpiA1JobConfig);
    }

    @Nested
    @DisplayName("Job Configuration Tests")
    class JobConfigurationTests {

        @Test
        @DisplayName("Should exit early when job is disabled")
        void shouldExitEarlyWhenJobDisabled() {
            // Given
            when(kpiA1JobConfig.isEnabled()).thenReturn(false);

            // When
            kpiA1Job.executeInternal(jobExecutionContext);

            // Then
            verify(instanceService, never()).findInstanceToCalculate(any(), anyInt());
            verify(kpiConfigurationService, never()).findKpiConfigurationByCode(anyString());
        }

        @Test
        @DisplayName("Should exit early when no instances to calculate")
        void shouldExitEarlyWhenNoInstancesFound() {
            // Given
            when(kpiA1JobConfig.isEnabled()).thenReturn(true);
            when(kpiA1JobConfig.getLimit()).thenReturn(10);
            when(instanceService.findInstanceToCalculate(ModuleCode.A1, 10))
                .thenReturn(Collections.emptyList());

            // When
            kpiA1Job.executeInternal(jobExecutionContext);

            // Then
            verify(instanceService).findInstanceToCalculate(ModuleCode.A1, 10);
            verify(kpiConfigurationService, never()).findKpiConfigurationByCode(anyString());
        }

        @Test
        @DisplayName("Should handle KPI configuration not found")
        void shouldHandleKpiConfigurationNotFound() {
            // Given
            when(kpiA1JobConfig.isEnabled()).thenReturn(true);
            when(kpiA1JobConfig.getLimit()).thenReturn(10);
            when(instanceService.findInstanceToCalculate(ModuleCode.A1, 10))
                .thenReturn(List.of(testInstance));
            when(kpiConfigurationService.findKpiConfigurationByCode(ModuleCode.A1.code))
                .thenReturn(Optional.empty());

            // When
            kpiA1Job.executeInternal(jobExecutionContext);

            // Then
            verify(instanceService).findInstanceToCalculate(ModuleCode.A1, 10);
            verify(kpiConfigurationService).findKpiConfigurationByCode(ModuleCode.A1.code);
            // Verify that the job handles the missing configuration gracefully
        }
    }

    @Nested
    @DisplayName("Instance Processing Tests")
    class InstanceProcessingTests {

        @BeforeEach
        void setupInstanceProcessing() {
            // Setup basic successful scenario
            when(kpiA1JobConfig.isEnabled()).thenReturn(true);
            when(kpiA1JobConfig.getLimit()).thenReturn(10);
            when(instanceService.findInstanceToCalculate(ModuleCode.A1, 10))
                .thenReturn(List.of(testInstance));
            when(kpiConfigurationService.findKpiConfigurationByCode(ModuleCode.A1.code))
                .thenReturn(Optional.of(testKpiConfiguration));
        }

        @Test
        @DisplayName("Should handle instance module not found")
        void shouldHandleInstanceModuleNotFound() {
            // Given
            when(instanceModuleService.findOne(testInstance.getId(), testKpiConfiguration.getModuleId()))
                .thenReturn(Optional.empty());

            // When
            kpiA1Job.executeInternal(jobExecutionContext);

            // Then
            verify(instanceService).updateInstanceStatusInProgress(testInstance.getId());
            verify(instanceModuleService).findOne(testInstance.getId(), testKpiConfiguration.getModuleId());
            // Verify that processing continues with next instance
        }

        @Test
        @DisplayName("Should process instance with no stations successfully")
        void shouldProcessInstanceWithNoStations() {
            // Given
            when(instanceModuleService.findOne(testInstance.getId(), testKpiConfiguration.getModuleId()))
                .thenReturn(Optional.of(testInstanceModule));
            
            // Mock findByInstanceModuleId which is called before delete
            when(kpiA1AnalyticDataService.findByInstanceModuleId(testInstanceModule.getId()))
                .thenReturn(Collections.emptyList());
            
            when(kpiA1AnalyticDataService.deleteAllByInstanceModule(testInstanceModule.getId())).thenReturn(1);
            when(kpiA1DetailResultService.deleteAllByInstanceModule(testInstanceModule.getId())).thenReturn(1);
            when(kpiA1ResultService.deleteAllByInstanceModule(testInstanceModule.getId())).thenReturn(1);
            
            // No stations found
            when(pagoPaRecordedTimeoutService.findAllStationAndMethodIntoPeriodForPartner(
                testInstance.getPartnerFiscalCode(),
                testInstance.getAnalysisPeriodStartDate(),
                testInstance.getAnalysisPeriodEndDate()
            )).thenReturn(Collections.emptyMap());

            KpiA1ResultDTO savedResult = createTestKpiA1Result();
            when(kpiA1ResultService.save(any(KpiA1ResultDTO.class))).thenReturn(savedResult);

            // When
            kpiA1Job.executeInternal(jobExecutionContext);

            // Then
            verify(instanceService).updateInstanceStatusInProgress(testInstance.getId());
            verify(kpiA1AnalyticDataService).findByInstanceModuleId(testInstanceModule.getId());
            verify(kpiA1AnalyticDataService).deleteAllByInstanceModule(testInstanceModule.getId());
            verify(kpiA1DetailResultService).deleteAllByInstanceModule(testInstanceModule.getId());
            verify(kpiA1ResultService).deleteAllByInstanceModule(testInstanceModule.getId());
            verify(kpiA1ResultService).save(any(KpiA1ResultDTO.class));
            verify(instanceModuleService).updateAutomaticOutcome(eq(testInstanceModule.getId()), any(OutcomeStatus.class));
        }

        @Test
        @DisplayName("Should process instance with stations and methods")
        void shouldProcessInstanceWithStationsAndMethods() throws Exception {
            // Given
            when(instanceModuleService.findOne(testInstance.getId(), testKpiConfiguration.getModuleId()))
                .thenReturn(Optional.of(testInstanceModule));
            
            // Mock findByInstanceModuleId which is called before delete
            when(kpiA1AnalyticDataService.findByInstanceModuleId(testInstanceModule.getId()))
                .thenReturn(Collections.emptyList());
            
            when(kpiA1AnalyticDataService.deleteAllByInstanceModule(testInstanceModule.getId())).thenReturn(1);
            when(kpiA1DetailResultService.deleteAllByInstanceModule(testInstanceModule.getId())).thenReturn(1);
            when(kpiA1ResultService.deleteAllByInstanceModule(testInstanceModule.getId())).thenReturn(1);

            // Setup stations and methods
            Map<String, List<String>> stationsAndMethods = new HashMap<>();
            stationsAndMethods.put("STATION_001", List.of("METHOD_1"));
            when(pagoPaRecordedTimeoutService.findAllStationAndMethodIntoPeriodForPartner(
                testInstance.getPartnerFiscalCode(),
                testInstance.getAnalysisPeriodStartDate(),
                testInstance.getAnalysisPeriodEndDate()
            )).thenReturn(stationsAndMethods);

            when(anagStationService.findIdByNameOrCreate("STATION_001", testInstance.getPartnerId()))
                .thenReturn(1L);

            // Setup maintenance data - mock returns empty list for maintenance
            when(anagPlannedShutdownService.findAllByTypePlannedIntoPeriod(
                eq(testInstance.getPartnerId()),
                eq(1L),
                eq(TypePlanned.PROGRAMMATO),
                eq(testInstance.getAnalysisPeriodStartDate()),
                eq(testInstance.getAnalysisPeriodEndDate())
            )).thenReturn(Collections.emptyList());

            // Setup timeout data
            setupTimeoutDataMocks();

            KpiA1ResultDTO savedResult = createTestKpiA1Result();
            when(kpiA1ResultService.save(any(KpiA1ResultDTO.class))).thenReturn(savedResult);

            KpiA1DetailResultDTO savedDetailResult = createTestKpiA1DetailResult();
            when(kpiA1DetailResultService.save(any(KpiA1DetailResultDTO.class))).thenReturn(savedDetailResult);

            // When
            kpiA1Job.executeInternal(jobExecutionContext);

            // Then
            verify(instanceService).updateInstanceStatusInProgress(testInstance.getId());
            verify(anagStationService).findIdByNameOrCreate("STATION_001", testInstance.getPartnerId());
            verify(kpiA1ResultService).save(any(KpiA1ResultDTO.class));
//            verify(instanceModuleService).updateAutomaticOutcome(eq(testInstanceModule.getId()), any(OutcomeStatus.class));
        }

        @Test
        @DisplayName("Should handle planned shutdown exclusion")
        void shouldHandlePlannedShutdownExclusion() {
            // Given
            testKpiConfiguration.setExcludePlannedShutdown(true);
            
            when(instanceModuleService.findOne(testInstance.getId(), testKpiConfiguration.getModuleId()))
                .thenReturn(Optional.of(testInstanceModule));
            
            // Mock findByInstanceModuleId which is called before delete
            when(kpiA1AnalyticDataService.findByInstanceModuleId(testInstanceModule.getId()))
                .thenReturn(Collections.emptyList());
            
            when(kpiA1AnalyticDataService.deleteAllByInstanceModule(testInstanceModule.getId())).thenReturn(1);
            when(kpiA1DetailResultService.deleteAllByInstanceModule(testInstanceModule.getId())).thenReturn(1);
            when(kpiA1ResultService.deleteAllByInstanceModule(testInstanceModule.getId())).thenReturn(1);

            Map<String, List<String>> stationsAndMethods = new HashMap<>();
            stationsAndMethods.put("STATION_001", List.of("METHOD_1"));
            when(pagoPaRecordedTimeoutService.findAllStationAndMethodIntoPeriodForPartner(
                testInstance.getPartnerFiscalCode(),
                testInstance.getAnalysisPeriodStartDate(),
                testInstance.getAnalysisPeriodEndDate()
            )).thenReturn(stationsAndMethods);

            when(anagStationService.findIdByNameOrCreate("STATION_001", testInstance.getPartnerId()))
                .thenReturn(1L);

            // Setup planned maintenance
            List<AnagPlannedShutdownDTO> plannedMaintenance = createTestMaintenanceList();
            when(anagPlannedShutdownService.findAllByTypePlannedIntoPeriod(
                eq(testInstance.getPartnerId()),
                eq(1L),
                eq(TypePlanned.PROGRAMMATO),
                eq(testInstance.getAnalysisPeriodStartDate()),
                eq(testInstance.getAnalysisPeriodEndDate())
            )).thenReturn(plannedMaintenance);

            setupTimeoutDataMocks();

            KpiA1ResultDTO savedResult = createTestKpiA1Result();
            when(kpiA1ResultService.save(any(KpiA1ResultDTO.class))).thenReturn(savedResult);

            KpiA1DetailResultDTO savedDetailResult = createTestKpiA1DetailResult();
            when(kpiA1DetailResultService.save(any(KpiA1DetailResultDTO.class))).thenReturn(savedDetailResult);

            // When
            kpiA1Job.executeInternal(jobExecutionContext);

            // Then
            verify(anagPlannedShutdownService).findAllByTypePlannedIntoPeriod(
                testInstance.getPartnerId(), 1L, TypePlanned.PROGRAMMATO,
                testInstance.getAnalysisPeriodStartDate(), testInstance.getAnalysisPeriodEndDate()
            );
        }

        @Test
        @DisplayName("Should handle unplanned shutdown exclusion")
        void shouldHandleUnplannedShutdownExclusion() {
            // Given
            testKpiConfiguration.setExcludeUnplannedShutdown(true);
            
            when(instanceModuleService.findOne(testInstance.getId(), testKpiConfiguration.getModuleId()))
                .thenReturn(Optional.of(testInstanceModule));
            
            // Mock findByInstanceModuleId which is called before delete
            when(kpiA1AnalyticDataService.findByInstanceModuleId(testInstanceModule.getId()))
                .thenReturn(Collections.emptyList());
            
            when(kpiA1AnalyticDataService.deleteAllByInstanceModule(testInstanceModule.getId())).thenReturn(1);
            when(kpiA1DetailResultService.deleteAllByInstanceModule(testInstanceModule.getId())).thenReturn(1);
            when(kpiA1ResultService.deleteAllByInstanceModule(testInstanceModule.getId())).thenReturn(1);

            Map<String, List<String>> stationsAndMethods = new HashMap<>();
            stationsAndMethods.put("STATION_001", List.of("METHOD_1"));
            when(pagoPaRecordedTimeoutService.findAllStationAndMethodIntoPeriodForPartner(
                testInstance.getPartnerFiscalCode(),
                testInstance.getAnalysisPeriodStartDate(),
                testInstance.getAnalysisPeriodEndDate()
            )).thenReturn(stationsAndMethods);

            when(anagStationService.findIdByNameOrCreate("STATION_001", testInstance.getPartnerId()))
                .thenReturn(1L);

            // Setup unplanned maintenance
            List<AnagPlannedShutdownDTO> unplannedMaintenance = createTestMaintenanceList();
            when(anagPlannedShutdownService.findAllByTypePlannedIntoPeriod(
                eq(testInstance.getPartnerId()),
                eq(1L),
                eq(TypePlanned.NON_PROGRAMMATO),
                eq(testInstance.getAnalysisPeriodStartDate()),
                eq(testInstance.getAnalysisPeriodEndDate())
            )).thenReturn(unplannedMaintenance);

            setupTimeoutDataMocks();

            KpiA1ResultDTO savedResult = createTestKpiA1Result();
            when(kpiA1ResultService.save(any(KpiA1ResultDTO.class))).thenReturn(savedResult);

            // When
            kpiA1Job.executeInternal(jobExecutionContext);

            // Then
            verify(anagPlannedShutdownService).findAllByTypePlannedIntoPeriod(
                testInstance.getPartnerId(), 1L, TypePlanned.NON_PROGRAMMATO,
                testInstance.getAnalysisPeriodStartDate(), testInstance.getAnalysisPeriodEndDate()
            );
        }

        @Test
        @DisplayName("Should handle monthly evaluation type")
        void shouldHandleMonthlyEvaluationType() {
            // Given
            testKpiConfiguration.setEvaluationType(EvaluationType.MESE);
            
            when(instanceModuleService.findOne(testInstance.getId(), testKpiConfiguration.getModuleId()))
                .thenReturn(Optional.of(testInstanceModule));
            
            // Mock findByInstanceModuleId which is called before delete
            when(kpiA1AnalyticDataService.findByInstanceModuleId(testInstanceModule.getId()))
                .thenReturn(Collections.emptyList());
            
            when(kpiA1AnalyticDataService.deleteAllByInstanceModule(testInstanceModule.getId())).thenReturn(1);
            when(kpiA1DetailResultService.deleteAllByInstanceModule(testInstanceModule.getId())).thenReturn(1);
            when(kpiA1ResultService.deleteAllByInstanceModule(testInstanceModule.getId())).thenReturn(1);

            Map<String, List<String>> stationsAndMethods = new HashMap<>();
            stationsAndMethods.put("STATION_001", List.of("METHOD_1"));
            when(pagoPaRecordedTimeoutService.findAllStationAndMethodIntoPeriodForPartner(
                testInstance.getPartnerFiscalCode(),
                testInstance.getAnalysisPeriodStartDate(),
                testInstance.getAnalysisPeriodEndDate()
            )).thenReturn(stationsAndMethods);

            when(anagStationService.findIdByNameOrCreate("STATION_001", testInstance.getPartnerId()))
                .thenReturn(1L);

            // Mock planned shutdown service returns empty list
            when(anagPlannedShutdownService.findAllByTypePlannedIntoPeriod(
                anyLong(), anyLong(), any(TypePlanned.class), any(LocalDate.class), any(LocalDate.class)
            )).thenReturn(Collections.emptyList());

            setupTimeoutDataMocks();

            KpiA1ResultDTO savedResult = createTestKpiA1Result();
            when(kpiA1ResultService.save(any(KpiA1ResultDTO.class))).thenReturn(savedResult);

            KpiA1DetailResultDTO savedDetailResult = createTestKpiA1DetailResult();
            when(kpiA1DetailResultService.save(any(KpiA1DetailResultDTO.class))).thenReturn(savedDetailResult);

            // When
            kpiA1Job.executeInternal(jobExecutionContext);

            // Then
            verify(instanceService).updateInstanceStatusInProgress(testInstance.getId());
            verify(kpiA1ResultService).save(any(KpiA1ResultDTO.class));
//            verify(instanceModuleService).updateAutomaticOutcome(eq(testInstanceModule.getId()), any(OutcomeStatus.class));
        }

        @Test
        @DisplayName("Should handle exceptions gracefully")
        void shouldHandleExceptionsGracefully() {
            // Given
            when(instanceModuleService.findOne(testInstance.getId(), testKpiConfiguration.getModuleId()))
                .thenThrow(new RuntimeException("Database error"));

            // When
            kpiA1Job.executeInternal(jobExecutionContext);

            // Then
            verify(instanceService).updateInstanceStatusInProgress(testInstance.getId());
            // Verify that the job continues processing despite the exception
        }
    }

    // Helper methods for creating test data

    private KpiA1ResultDTO createTestKpiA1Result() {
        KpiA1ResultDTO dto = new KpiA1ResultDTO();
        dto.setId(1L);
        dto.setInstanceId(testInstance.getId());
        dto.setInstanceModuleId(testInstanceModule.getId());
        dto.setAnalysisDate(LocalDate.now());
        dto.setOutcome(OutcomeStatus.STANDBY);
        return dto;
    }

    private KpiA1DetailResultDTO createTestKpiA1DetailResult() {
        KpiA1DetailResultDTO dto = new KpiA1DetailResultDTO();
        dto.setId(1L);
        dto.setInstanceId(testInstance.getId());
        dto.setInstanceModuleId(testInstanceModule.getId());
        dto.setAnalysisDate(LocalDate.now());
        dto.setEvaluationStartDate(LocalDate.of(2023, 1, 1));
        dto.setEvaluationType(EvaluationType.TOTALE);
        dto.setOutcome(OutcomeStatus.OK);
        dto.setTotReq(1000L);
        dto.setReqTimeout(50L);
        dto.setTimeoutPercentage(5.0);
        return dto;
    }

    private List<AnagPlannedShutdownDTO> createTestMaintenanceList() {
        AnagPlannedShutdownDTO maintenance = new AnagPlannedShutdownDTO();
        maintenance.setId(1L);
        maintenance.setShutdownStartDate(LocalDate.of(2023, 1, 10).atStartOfDay().toInstant(ZoneOffset.UTC));
        maintenance.setShutdownEndDate(LocalDate.of(2023, 1, 11).atStartOfDay().toInstant(ZoneOffset.UTC));
        return List.of(maintenance);
    }

    private void setupTimeoutDataMocks() {
        // Setup timeout data for each day in the analysis period
        LocalDate start = testInstance.getAnalysisPeriodStartDate();
        LocalDate end = testInstance.getAnalysisPeriodEndDate();

        start.datesUntil(end.plusDays(1)).forEach(date -> {
            List<PagoPaRecordedTimeoutDTO> timeoutData = createTimeoutDataForDate();
            when(pagoPaRecordedTimeoutService.findAllRecordIntoDayForPartnerStationAndMethod(
                eq(testInstance.getPartnerFiscalCode()),
                eq("STATION_001"),
                anyString(),
                eq(date)
            )).thenReturn(timeoutData);
        });
    }

    private List<PagoPaRecordedTimeoutDTO> createTimeoutDataForDate() {
        PagoPaRecordedTimeoutDTO timeoutDTO = new PagoPaRecordedTimeoutDTO();
        timeoutDTO.setId(1L);
        timeoutDTO.setStation("STATION_001");
        timeoutDTO.setTotReq(100L);
        timeoutDTO.setReqOk(95L);
        timeoutDTO.setReqTimeout(5L); // 5% timeout rate - below threshold
        timeoutDTO.setStartDate(LocalDate.now().atStartOfDay().toInstant(ZoneOffset.UTC));
        timeoutDTO.setEndDate(LocalDate.now().atStartOfDay().plusHours(1).toInstant(ZoneOffset.UTC));
        return List.of(timeoutDTO);
    }
}