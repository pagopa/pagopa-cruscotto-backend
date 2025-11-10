package com.nexigroup.pagopa.cruscotto.service.impl;

import com.nexigroup.pagopa.cruscotto.domain.*;
import com.nexigroup.pagopa.cruscotto.domain.enumeration.OutcomeStatus;
import com.nexigroup.pagopa.cruscotto.domain.enumeration.EvaluationType;
import com.nexigroup.pagopa.cruscotto.repository.*;
import com.nexigroup.pagopa.cruscotto.service.*;
import com.nexigroup.pagopa.cruscotto.service.dto.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class KpiB3DataServiceImplTest {

    @InjectMocks
    private KpiB3DataServiceImpl service;

    @Mock
    private KpiB3ResultRepository kpiB3ResultRepository;
    @Mock
    private KpiB3DetailResultRepository kpiB3DetailResultRepository;
    @Mock
    private KpiB3AnalyticDataRepository kpiB3AnalyticDataRepository;
    @Mock
    private InstanceRepository instanceRepository;
    @Mock
    private InstanceModuleRepository instanceModuleRepository;
    @Mock
    private AnagStationRepository anagStationRepository;
    @Mock
    private PagopaNumeroStandinDrilldownService pagopaNumeroStandinDrilldownService;
    @Mock
    private KpiB3ResultService kpiB3ResultService;
    @Mock
    private KpiB3DetailResultService kpiB3DetailResultService;
    @Mock
    private KpiB3AnalyticDataService kpiB3AnalyticDataService;

    private InstanceDTO instanceDTO;
    private InstanceModuleDTO instanceModuleDTO;
    private KpiConfigurationDTO kpiConfig;
    private LocalDate analysisDate;

    @BeforeEach
    void setUp() {
        instanceDTO = new InstanceDTO();
        instanceDTO.setId(1L);
        instanceDTO.setInstanceIdentification("INST1");
        instanceDTO.setPartnerFiscalCode("PF123");
        instanceDTO.setAnalysisPeriodStartDate(LocalDate.of(2025, 1, 1));
        instanceDTO.setAnalysisPeriodEndDate(LocalDate.of(2025, 3, 31));

        instanceModuleDTO = new InstanceModuleDTO();
        instanceModuleDTO.setId(10L);
        instanceModuleDTO.setInstanceId(1L);

        kpiConfig = new KpiConfigurationDTO();
        kpiConfig.setEligibilityThreshold(5.0);
        kpiConfig.setTolerance(1.0);
        kpiConfig.setEvaluationType(EvaluationType.TOTALE);
        kpiConfig.setExcludePlannedShutdown(true);
        kpiConfig.setExcludeUnplannedShutdown(false);

        analysisDate = LocalDate.of(2025, 10, 28);
    }

    @Test
    void testSaveKpiB3Results_withStandInData() {
        // Mock instance and module
        Instance instance = new Instance();
        instance.setId(1L);

        InstanceModule module = new InstanceModule();
        module.setId(10L);
        module.setInstance(instance);

        when(instanceRepository.findById(1L)).thenReturn(Optional.of(instance));
        when(instanceModuleRepository.findById(10L)).thenReturn(Optional.of(module));

        // Mock stations
        AnagStation station = new AnagStation();
        station.setId(100L);
        station.setName("ST01");
        when(anagStationRepository.findByAnagPartnerFiscalCode("PF123")).thenReturn(List.of(station));

        // Mock stand-in data
        PagopaNumeroStandin standIn = new PagopaNumeroStandin();
        standIn.setId(200L);
        standIn.setStationCode("ST01");
        standIn.setEventType("ADD_TO_STANDIN");
        standIn.setStandInCount(3);
        standIn.setIntervalStart(LocalDateTime.of(2025, 1, 15, 10, 0));
        standIn.setIntervalEnd(LocalDateTime.of(2025, 1, 15, 12, 0));

        List<PagopaNumeroStandin> standInData = List.of(standIn);

        // Mock repository saves
        KpiB3Result savedResult = new KpiB3Result();
        savedResult.setId(500L);
        when(kpiB3ResultRepository.save(any())).thenReturn(savedResult);

        KpiB3DetailResult savedDetail = new KpiB3DetailResult();
        savedDetail.setId(600L);
        when(kpiB3DetailResultRepository.save(any())).thenReturn(savedDetail);

        KpiB3AnalyticData savedAnalytic = new KpiB3AnalyticData();
        savedAnalytic.setId(700L);
        when(kpiB3AnalyticDataRepository.save(any())).thenReturn(savedAnalytic);

        // Execute
        service.saveKpiB3Results(instanceDTO, instanceModuleDTO, kpiConfig, analysisDate, OutcomeStatus.OK, standInData);

        // Verify repository interactions
        verify(kpiB3ResultRepository, atLeastOnce()).save(any());
        verify(kpiB3DetailResultRepository, atLeastOnce()).save(any());
        verify(kpiB3AnalyticDataRepository, atLeastOnce()).save(any());
        verify(pagopaNumeroStandinDrilldownService, atLeastOnce()).saveStandInSnapshot(any(), any(), any(), any(), any(), any());
    }

    @Test
    void testCreateKpiB3Result() {
        KpiB3ResultDTO resultDTO = new KpiB3ResultDTO();
        resultDTO.setId(1L);
        when(kpiB3ResultService.save(any())).thenReturn(resultDTO);

        KpiB3ResultDTO created = service.createKpiB3Result(instanceDTO, instanceModuleDTO, kpiConfig, analysisDate);
        assertNotNull(created);
        assertEquals(1L, created.getId());
        verify(kpiB3ResultService, times(1)).save(any());
    }

    @Test
    void testSaveKpiB3AnalyticData_skipsNullStation() {
        PagopaNumeroStandin standIn = new PagopaNumeroStandin();
        standIn.setId(1L);
        standIn.setStationCode("UNKNOWN");
        standIn.setEventType("ADD_TO_STANDIN");
        standIn.setIntervalStart(LocalDateTime.now());
        standIn.setStandInCount(1);

        service.saveKpiB3AnalyticData(instanceDTO, instanceModuleDTO, List.of(standIn));

        // Verify that saveAll is called with an empty list
        ArgumentCaptor<List<KpiB3AnalyticDataDTO>> captor = ArgumentCaptor.forClass(List.class);
        verify(kpiB3AnalyticDataService).saveAll(captor.capture());

        List<KpiB3AnalyticDataDTO> capturedList = captor.getValue();
        assertTrue(capturedList.isEmpty(), "Expected empty list when station is null");
    }

    @Test
    void testUpdateKpiB3ResultOutcome_callsService() {
        service.updateKpiB3ResultOutcome(100L, OutcomeStatus.KO);
        verify(kpiB3ResultService, times(1)).updateKpiB3ResultOutcome(100L, OutcomeStatus.KO);
    }

    @Test
    void testGetMonthsInPeriod_returnsCorrectMonths() throws Exception {
        LocalDate start = LocalDate.of(2025, 1, 15);
        LocalDate end = LocalDate.of(2025, 3, 5);

        var method = KpiB3DataServiceImpl.class.getDeclaredMethod("getMonthsInPeriod", LocalDate.class, LocalDate.class);
        method.setAccessible(true);
        List<YearMonth> months = (List<YearMonth>) method.invoke(service, start, end);

        assertEquals(3, months.size());
        assertEquals(YearMonth.of(2025, 1), months.get(0));
        assertEquals(YearMonth.of(2025, 3), months.get(2));
    }
}
