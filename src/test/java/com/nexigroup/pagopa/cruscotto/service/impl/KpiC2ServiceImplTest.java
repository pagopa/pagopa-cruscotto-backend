package com.nexigroup.pagopa.cruscotto.service.impl;

import com.nexigroup.pagopa.cruscotto.domain.*;
import com.nexigroup.pagopa.cruscotto.domain.Module;
import com.nexigroup.pagopa.cruscotto.domain.enumeration.*;
import com.nexigroup.pagopa.cruscotto.repository.*;
import com.nexigroup.pagopa.cruscotto.service.AnagInstitutionService;
import com.nexigroup.pagopa.cruscotto.service.InstanceModuleService;
import com.nexigroup.pagopa.cruscotto.service.dto.*;
import com.nexigroup.pagopa.cruscotto.service.filter.AnagInstitutionFilter;
import com.nexigroup.pagopa.cruscotto.service.mapper.KpiC2ResultMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class KpiC2ServiceImplTest {

    @InjectMocks
    private KpiC2ServiceImpl kpiC2Service;

    @Mock
    private KpiC2ResultRepository kpiC2ResultRepository;
    @Mock
    private KpiC2DetailResultRepository kpiC2DetailResultRepository;
    @Mock
    private KpiC2AnalyticDataRepository kpiC2AnalyticDataRepository;
    @Mock
    private KpiConfigurationRepository kpiConfigurationRepository;
    @Mock
    private InstanceRepository instanceRepository;
    @Mock
    private ModuleRepository moduleRepository;
    @Mock
    private PagopaSendRepository pagopaSendRepository;
    @Mock
    private AnagStationRepository anagStationRepository;
    @Mock
    private InstanceModuleService instanceModuleService;
    @Mock
    private KpiC2ResultMapper kpiC2ResultMapper;
    @Mock
    private KpiC2AnalyticDrillDownRepository kpiC2AnalyticDrillDownRepository;
    @Mock
    private AnagInstitutionService anagInstitutionService;

    private Instance createDummyInstance() {
        AnagPartner partner = new AnagPartner();
        partner.setFiscalCode("PARTNER_CF");
        partner.setId(1L);

        Instance instance = new Instance();
        instance.setId(1L);
        instance.setPartner(partner);
        instance.setAnalysisPeriodStartDate(LocalDate.of(2025,1,1));
        instance.setAnalysisPeriodEndDate(LocalDate.of(2025,1,31));
        return instance;
    }

    private KpiConfiguration createDummyConfiguration() {
        KpiConfiguration config = new KpiConfiguration();
        config.setEligibilityThreshold(95.0);
        config.setInstitutionTolerance(BigDecimal.valueOf(0.0));
        config.setNotificationTolerance(BigDecimal.valueOf(1.0));
        config.setEvaluationType(EvaluationType.MESE);
        return config;
    }

    private Module createDummyModule() {
        Module module = new Module();
        module.setId(1L);
        module.setCode(ModuleCode.C2.code);
        return module;
    }

    @Test
    void testExecuteKpiC2Calculation_HappyPath() {
        Instance instance = createDummyInstance();
        KpiConfiguration config = createDummyConfiguration();
        Module module = createDummyModule();
        InstanceModuleDTO instanceModuleDTO = new InstanceModuleDTO();
        instanceModuleDTO.setId(1L);

        KpiC2Result savedResult = new KpiC2Result();
        savedResult.setId(1L);

        when(kpiConfigurationRepository.findByModuleCode(ModuleCode.C2.code))
            .thenReturn(Optional.of(config));
        when(moduleRepository.findByCode(ModuleCode.C2.code))
            .thenReturn(Optional.of(module));
        when(instanceModuleService.findOne(instance.getId(), module.getId()))
            .thenReturn(Optional.of(instanceModuleDTO));
        when(instanceModuleService.findById(instanceModuleDTO.getId()))
            .thenReturn(Optional.of(new InstanceModule()));
        when(kpiC2ResultRepository.save(any(KpiC2Result.class))).thenReturn(savedResult);
        when(anagInstitutionService.findAllNoPaging(any(AnagInstitutionFilter.class)))
            .thenReturn(Collections.emptyList());
        when(kpiC2ResultMapper.toDto(savedResult)).thenReturn(new KpiC2ResultDTO());

        KpiC2ResultDTO resultDTO = kpiC2Service.executeKpiC2Calculation(instance);

        assertNotNull(resultDTO);
        verify(kpiC2ResultRepository).save(any(KpiC2Result.class));
    }

    @Test
    void testGetKpiC2Results() {
        Instance instance = createDummyInstance();
        KpiC2Result result = new KpiC2Result();
        result.setId(1L);

        when(instanceRepository.findById(1L)).thenReturn(Optional.of(instance));
        when(kpiC2ResultRepository.findByInstanceOrderByAnalysisDateDesc(instance))
            .thenReturn(Collections.singletonList(result));
        when(kpiC2ResultMapper.toDto(result)).thenReturn(new KpiC2ResultDTO());

        List<KpiC2ResultDTO> results = kpiC2Service.getKpiC2Results("1");

        assertEquals(1, results.size());
        verify(kpiC2ResultRepository).findByInstanceOrderByAnalysisDateDesc(instance);
    }

    @Test
    void testDeleteKpiC2Data() {
        Instance instance = createDummyInstance();
        when(instanceRepository.findById(1L)).thenReturn(Optional.of(instance));

        kpiC2Service.deleteKpiC2Data("1");

        verify(kpiC2ResultRepository).deleteByInstance(instance);
    }

    @Test
    void testRecalculateKpiC2() {
        Instance instance = createDummyInstance();
        KpiC2ResultDTO dto = new KpiC2ResultDTO();

        when(instanceRepository.findById(1L)).thenReturn(Optional.of(instance));
        doNothing().when(kpiC2ResultRepository).deleteByInstance(instance);
        // We need to mock the service call itself if we want to avoid recursion
        KpiC2ServiceImpl spyService = Mockito.spy(kpiC2Service);
        doReturn(dto).when(spyService).executeKpiC2Calculation(instance);

        KpiC2ResultDTO result = spyService.recalculateKpiC2("1");
        assertNotNull(result);
    }

    @Test
    void testExistsKpiC2Calculation_ReturnsTrue() {
        Instance instance = createDummyInstance();
        LocalDateTime dateTime = LocalDateTime.of(2025,1,10,0,0);
        when(instanceRepository.findById(1L)).thenReturn(Optional.of(instance));
        when(kpiC2ResultRepository.existsByInstanceAndAnalysisDate(instance, dateTime.toLocalDate()))
            .thenReturn(true);

        assertTrue(kpiC2Service.existsKpiC2Calculation("1", dateTime));
    }

    @Test
    void testUpdateKpiC2ResultOutcome() {
        KpiC2Result result = new KpiC2Result();
        result.setId(1L);
        when(kpiC2ResultRepository.findById(1L)).thenReturn(Optional.of(result));

        kpiC2Service.updateKpiC2ResultOutcome(1L, OutcomeStatus.OK);

        assertEquals(OutcomeStatus.OK, result.getOutcome());
        verify(kpiC2ResultRepository).save(result);
    }
}
