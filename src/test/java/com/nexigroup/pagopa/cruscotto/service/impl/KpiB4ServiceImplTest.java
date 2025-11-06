package com.nexigroup.pagopa.cruscotto.service.impl;

import com.nexigroup.pagopa.cruscotto.domain.*;
import com.nexigroup.pagopa.cruscotto.domain.Module;
import com.nexigroup.pagopa.cruscotto.domain.enumeration.*;
import com.nexigroup.pagopa.cruscotto.repository.*;
import com.nexigroup.pagopa.cruscotto.service.InstanceModuleService;
import com.nexigroup.pagopa.cruscotto.service.dto.*;
import com.nexigroup.pagopa.cruscotto.service.mapper.KpiB4ResultMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class KpiB4ServiceImplTest {

    @Mock
    private KpiB4ResultRepository kpiB4ResultRepository;
    @Mock
    private KpiB4DetailResultRepository kpiB4DetailResultRepository;
    @Mock
    private KpiB4AnalyticDataRepository kpiB4AnalyticDataRepository;
    @Mock
    private KpiConfigurationRepository kpiConfigurationRepository;
    @Mock
    private InstanceRepository instanceRepository;
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
    @Mock
    private KpiB4ResultMapper kpiB4ResultMapper;

    @InjectMocks
    private KpiB4ServiceImpl kpiB4Service;

    private Instance instance;
    private KpiConfiguration kpiConfiguration;
    private Module moduleB4;
    private InstanceModuleDTO instanceModuleDTO;

    @BeforeEach
    void setUp() {
        instance = new Instance();
        instance.setId(1L);
        instance.setAnalysisPeriodStartDate(LocalDate.of(2025, 1, 1));
        instance.setAnalysisPeriodEndDate(LocalDate.of(2025, 1, 31));
        AnagPartner partner = new AnagPartner();
        partner.setFiscalCode("ABC123");
        instance.setPartner(partner);

        kpiConfiguration = new KpiConfiguration();
        kpiConfiguration.setEligibilityThreshold(95.0);
        kpiConfiguration.setTolerance(5.0);
        kpiConfiguration.setEvaluationType(EvaluationType.MESE);
        kpiConfiguration.setExcludePlannedShutdown(true);
        kpiConfiguration.setExcludeUnplannedShutdown(false);

        moduleB4 = new Module();
        moduleB4.setId(10L);
        moduleB4.setCode(ModuleCode.B4.code);

        instanceModuleDTO = new InstanceModuleDTO();
        instanceModuleDTO.setId(100L);
    }

    @Test
    void testRecalculateKpiB4() {
        when(instanceRepository.findById(instance.getId())).thenReturn(Optional.of(instance));
        when(kpiB4ResultRepository.save(any())).thenAnswer(i -> i.getArgument(0));
        doNothing().when(kpiB4ResultRepository).deleteByInstance(instance);
        when(kpiConfigurationRepository.findByModuleCode(ModuleCode.B4.code)).thenReturn(Optional.of(kpiConfiguration));
        when(moduleRepository.findByCode(ModuleCode.B4.code)).thenReturn(Optional.of(moduleB4));
        when(instanceModuleService.findOne(instance.getId(), moduleB4.getId())).thenReturn(Optional.of(instanceModuleDTO));
        when(instanceModuleService.findById(instanceModuleDTO.getId())).thenReturn(Optional.of(new InstanceModule()));
        when(kpiB4ResultMapper.toDto((KpiB4Result) any())).thenReturn(new KpiB4ResultDTO());

        KpiB4ResultDTO result = kpiB4Service.recalculateKpiB4(String.valueOf(instance.getId()));

        assertNotNull(result);
        verify(kpiB4ResultRepository, times(1)).deleteByInstance(instance);
        verify(kpiB4ResultRepository, times(1)).save(any(KpiB4Result.class));
    }

    @Test
    void testExistsKpiB4Calculation() {
        LocalDateTime now = LocalDateTime.now();
        when(instanceRepository.findById(instance.getId())).thenReturn(Optional.of(instance));
        when(kpiB4ResultRepository.existsByInstanceAndAnalysisDate(instance, now.toLocalDate())).thenReturn(true);

        boolean exists = kpiB4Service.existsKpiB4Calculation(String.valueOf(instance.getId()), now);

        assertTrue(exists);
    }

    @Test
    void testUpdateKpiB4ResultOutcome() {
        KpiB4Result result = new KpiB4Result();
        when(kpiB4ResultRepository.findById(1L)).thenReturn(Optional.of(result));
        when(kpiB4ResultRepository.save(result)).thenReturn(result);

        kpiB4Service.updateKpiB4ResultOutcome(1L, OutcomeStatus.KO);

        assertEquals(OutcomeStatus.KO, result.getOutcome());
        verify(kpiB4ResultRepository, times(1)).save(result);
    }

    @Test
    void testGetKpiB4Results_withPagination() {
        when(instanceRepository.findById(instance.getId())).thenReturn(Optional.of(instance));
        List<KpiB4Result> results = List.of(new KpiB4Result());
        Page<KpiB4Result> page = new PageImpl<>(results);
        when(kpiB4ResultRepository.findByInstanceOrderByAnalysisDateDesc(eq(instance), any(Pageable.class))).thenReturn(page);
        when(kpiB4ResultMapper.toDto((KpiB4Result) any())).thenReturn(new KpiB4ResultDTO());

        Page<KpiB4ResultDTO> resultPage = kpiB4Service.getKpiB4Results(String.valueOf(instance.getId()), Pageable.unpaged());

        assertNotNull(resultPage);
        assertEquals(1, resultPage.getContent().size());
    }
}
