package com.nexigroup.pagopa.cruscotto.service.impl;

import com.nexigroup.pagopa.cruscotto.domain.*;
import com.nexigroup.pagopa.cruscotto.domain.Module;
import com.nexigroup.pagopa.cruscotto.domain.enumeration.*;
import com.nexigroup.pagopa.cruscotto.repository.*;
import com.nexigroup.pagopa.cruscotto.service.InstanceModuleService;
import com.nexigroup.pagopa.cruscotto.service.dto.*;
import com.nexigroup.pagopa.cruscotto.service.mapper.KpiB8ResultMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.lang.reflect.Method;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class KpiB8ServiceImplTest {

    @Mock
    private KpiB8ResultRepository kpiB8ResultRepository;
    @Mock
    private InstanceRepository instanceRepository;
    @Mock
    private KpiB8ResultMapper kpiB8ResultMapper;
    @Mock
    private KpiB8DetailResultRepository kpiB8DetailResultRepository;
    @Mock
    private KpiB8AnalyticDataRepository kpiB8AnalyticDataRepository;
    @Mock
    private KpiConfigurationRepository kpiConfigurationRepository;
    @Mock
    private ModuleRepository moduleRepository;
    @Mock
    private PagopaApiLogRepository pagopaApiLogRepository;
    @Mock
    private AnagStationRepository anagStationRepository;
    @Mock
    private PagopaApiLogDrilldownRepository pagopaApiLogDrilldownRepository;
    @Mock
    private InstanceModuleService instanceModuleService;


    @InjectMocks
    private KpiB8ServiceImpl kpiB8Service;

    private Instance instance;
    private KpiB8Result kpiB8Result;

    @BeforeEach
    void setUp() {
        instance = new Instance();
        instance.setId(1L);
        instance.setAnalysisPeriodStartDate(LocalDate.now().minusDays(10));
        instance.setAnalysisPeriodEndDate(LocalDate.now());
        AnagPartner partner = new AnagPartner();
        partner.setFiscalCode("12345678901");
        instance.setPartner(partner);

        Module module = new Module();
        module.setId(1L);
        module.setCode(ModuleCode.B8.code);

        InstanceModuleDTO instanceModuleDTO = new InstanceModuleDTO();
        instanceModuleDTO.setId(1L);

        KpiConfiguration kpiConfiguration = new KpiConfiguration();
        kpiConfiguration.setEligibilityThreshold(95.0);
        kpiConfiguration.setTolerance(5.0);
        kpiConfiguration.setEvaluationType(EvaluationType.MESE);

        kpiB8Result = new KpiB8Result();
        kpiB8Result.setId(1L);
        kpiB8Result.setInstance(instance);
        kpiB8Result.setAnalysisDate(LocalDate.now());
    }

    @Test
    void testGetKpiB8ResultsWithoutPagination() {
        when(instanceRepository.findById(instance.getId())).thenReturn(Optional.of(instance));
        when(kpiB8ResultRepository.findByInstanceOrderByAnalysisDateDesc(instance))
            .thenReturn(Collections.singletonList(kpiB8Result));
        when(kpiB8ResultMapper.toDto((KpiB8Result) any())).thenReturn(new KpiB8ResultDTO());

        List<KpiB8ResultDTO> results = kpiB8Service.getKpiB8Results(String.valueOf(instance.getId()));

        assertEquals(1, results.size());
    }

    @Test
    void testGetKpiB8ResultsWithPagination() {
        when(instanceRepository.findById(instance.getId())).thenReturn(Optional.of(instance));
        Page<KpiB8Result> page = new PageImpl<>(Collections.singletonList(kpiB8Result));
        when(kpiB8ResultRepository.findByInstanceOrderByAnalysisDateDesc(eq(instance), any(PageRequest.class)))
            .thenReturn(page);
        when(kpiB8ResultMapper.toDto((KpiB8Result) any())).thenReturn(new KpiB8ResultDTO());

        Page<KpiB8ResultDTO> results = kpiB8Service.getKpiB8Results(String.valueOf(instance.getId()), PageRequest.of(0, 10));

        assertEquals(1, results.getTotalElements());
    }

    @Test
    void testExistsKpiB8Calculation() {
        when(instanceRepository.findById(instance.getId())).thenReturn(Optional.of(instance));
        when(kpiB8ResultRepository.existsByInstanceAndAnalysisDate(eq(instance), any(LocalDate.class)))
            .thenReturn(true);

        boolean exists = kpiB8Service.existsKpiB8Calculation(String.valueOf(instance.getId()), LocalDateTime.now());

        assertTrue(exists);
    }

    @Test
    void testUpdateKpiB8ResultOutcome() {
        when(kpiB8ResultRepository.findById(kpiB8Result.getId())).thenReturn(Optional.of(kpiB8Result));
        when(kpiB8ResultRepository.save(any())).thenReturn(kpiB8Result);

        kpiB8Service.updateKpiB8ResultOutcome(kpiB8Result.getId(), OutcomeStatus.KO);

        assertEquals(OutcomeStatus.KO, kpiB8Result.getOutcome());
        verify(kpiB8ResultRepository).save(kpiB8Result);
    }

    @Test
    void testExecuteKpiB8CalculationSuccess() {
        // Mock KpiConfiguration
        KpiConfiguration kpiConfig = new KpiConfiguration();
        kpiConfig.setEligibilityThreshold(95.0);
        kpiConfig.setTolerance(5.0);
        kpiConfig.setEvaluationType(EvaluationType.MESE);

        when(kpiConfigurationRepository.findByModuleCode(ModuleCode.B8.code))
            .thenReturn(Optional.of(kpiConfig));

        // Mock Module e InstanceModule
        Module module = new Module();
        module.setId(1L);
        module.setCode(ModuleCode.B8.code);

        InstanceModuleDTO instanceModuleDTO = new InstanceModuleDTO();
        instanceModuleDTO.setId(1L);

        when(moduleRepository.findByCode(ModuleCode.B8.code)).thenReturn(Optional.of(module));
        when(instanceModuleService.findOne(instance.getId(), module.getId())).thenReturn(Optional.of(instanceModuleDTO));
        when(instanceModuleService.findById(instanceModuleDTO.getId())).thenReturn(Optional.of(new InstanceModule()));

        // Mock repository save e mapper
        when(kpiB8ResultRepository.save(any(KpiB8Result.class))).thenAnswer(i -> i.getArgument(0));
        when(kpiB8ResultMapper.toDto(any(KpiB8Result.class))).thenReturn(new KpiB8ResultDTO());

        KpiB8ResultDTO resultDTO = kpiB8Service.executeKpiB8Calculation(instance);

        assertNotNull(resultDTO);
    }

    @Test
    void testRecalculateKpiB8() {
        // Stub principali
        when(instanceRepository.findById(instance.getId()))
            .thenReturn(Optional.of(instance));

        doNothing().when(kpiB8ResultRepository).deleteByInstance(instance);

        // KpiConfiguration e Module
        doReturn(Optional.of(new KpiConfiguration()))
            .when(kpiConfigurationRepository).findByModuleCode(ModuleCode.B8.code);
        doReturn(Optional.of(new Module()))
            .when(moduleRepository).findByCode(ModuleCode.B8.code);

        // InstanceModuleService: lenient per evitare problemi di strict stubbing
        lenient().doReturn(Optional.of(new InstanceModuleDTO()))
            .when(instanceModuleService).findOne(anyLong(), any());
        lenient().doReturn(Optional.of(new InstanceModule()))
            .when(instanceModuleService).findById(any());

        // KpiB8Result repository e mapper
        when(kpiB8ResultRepository.save(any())).thenAnswer(i -> i.getArgument(0));
        when(kpiB8ResultMapper.toDto((KpiB8Result) any())).thenReturn(new KpiB8ResultDTO());

        // Metodo sotto test
        KpiB8ResultDTO result = kpiB8Service.recalculateKpiB8(String.valueOf(instance.getId()));

        assertNotNull(result);

        // Verifiche
        verify(kpiB8ResultRepository).deleteByInstance(instance);
        verify(kpiB8ResultRepository).save(any());
        verify(kpiB8ResultMapper).toDto((KpiB8Result) any());
    }

    @Test
    void testCreateDetailResultsNoStations() throws Exception {
        when(anagStationRepository.findByAnagPartnerFiscalCode(instance.getPartner().getFiscalCode()))
            .thenReturn(Collections.emptyList());

        KpiB8Result result = new KpiB8Result();
        result.setInstance(instance);
        result.setInstanceModule(new InstanceModule());
        result.setAnalysisDate(LocalDate.now());

        // Usa reflection per invocare il metodo privato
        Method method = KpiB8ServiceImpl.class.getDeclaredMethod(
            "createAndSaveDetailResults",
            KpiB8Result.class,
            Instance.class
        );
        method.setAccessible(true);
        method.invoke(kpiB8Service, result, instance);

        // Nessuna eccezione = test ok
        verify(anagStationRepository).findByAnagPartnerFiscalCode(anyString());
    }

    @Test
    void testCreateAnalyticDataWithFallbackDetailResult() throws Exception {
        KpiB8Result result = new KpiB8Result();
        result.setInstance(instance);
        result.setInstanceModule(new InstanceModule());
        result.setAnalysisDate(LocalDate.now());

        KpiB8DetailResult totalDetail = new KpiB8DetailResult();
        totalDetail.setEvaluationType(EvaluationType.TOTALE);

        when(kpiB8DetailResultRepository.findByKpiB8Result(result))
            .thenReturn(Collections.singletonList(totalDetail));
        when(pagopaApiLogRepository.calculateDailyAggregatedDataAndApiGPDOrAca(anyString(), any(), any()))
            .thenReturn(Collections.singletonList(new Object[]{LocalDate.now(), 10L, 1L}));
        when(kpiB8AnalyticDataRepository.save(any())).thenAnswer(i -> i.getArgument(0));

        // Reflection
        Method method = KpiB8ServiceImpl.class.getDeclaredMethod(
            "createAndSaveAnalyticData",
            KpiB8Result.class,
            Instance.class
        );
        method.setAccessible(true);
        method.invoke(kpiB8Service, result, instance);

        verify(kpiB8AnalyticDataRepository, atLeastOnce()).save(any());
    }


}
