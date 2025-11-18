package com.nexigroup.pagopa.cruscotto.service.impl;

import com.nexigroup.pagopa.cruscotto.domain.*;
import com.nexigroup.pagopa.cruscotto.domain.Module;
import com.nexigroup.pagopa.cruscotto.domain.enumeration.EvaluationType;
import com.nexigroup.pagopa.cruscotto.domain.enumeration.ModuleCode;
import com.nexigroup.pagopa.cruscotto.domain.enumeration.OutcomeStatus;
import com.nexigroup.pagopa.cruscotto.repository.*;
import com.nexigroup.pagopa.cruscotto.service.InstanceModuleService;
import com.nexigroup.pagopa.cruscotto.service.dto.InstanceModuleDTO;
import com.nexigroup.pagopa.cruscotto.service.dto.KpiB4ResultDTO;
import com.nexigroup.pagopa.cruscotto.service.mapper.KpiB4ResultMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
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

    @InjectMocks
    private KpiB4ServiceImpl kpiB4Service;

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

    private Instance instance;
    private KpiConfiguration configuration;
    private Module moduleB4;
    private InstanceModuleDTO instanceModuleDTO;
    private KpiB4Result result;

    @BeforeEach
    void setUp() {
        instance = new Instance();
        instance.setId(1L);
        instance.setAnalysisPeriodStartDate(LocalDate.of(2025, 1, 1));
        instance.setAnalysisPeriodEndDate(LocalDate.of(2025, 1, 31));

        AnagPartner partner = new AnagPartner();
        partner.setFiscalCode("CF123");
        instance.setPartner(partner);

        configuration = new KpiConfiguration();
        configuration.setEligibilityThreshold(95.0);
        configuration.setTolerance(5.0);
        configuration.setEvaluationType(EvaluationType.MESE);

        moduleB4 = new Module();
        moduleB4.setId(1L);
        moduleB4.setCode(ModuleCode.B4.code);

        instanceModuleDTO = new InstanceModuleDTO();
        instanceModuleDTO.setId(1L);

        result = new KpiB4Result();
        result.setId(1L);
        result.setInstance(instance);
    }

    @Test
    void testExecuteKpiB4Calculation_Compliant() {
        when(kpiConfigurationRepository.findByModuleCode(ModuleCode.B4.code)).thenReturn(Optional.of(configuration));
        when(moduleRepository.findByCode(ModuleCode.B4.code)).thenReturn(Optional.of(moduleB4));
        when(instanceModuleService.findOne(instance.getId(), moduleB4.getId())).thenReturn(Optional.of(instanceModuleDTO));
        when(instanceModuleService.findById(instanceModuleDTO.getId())).thenReturn(Optional.of(new InstanceModule()));

        when(kpiB4ResultRepository.save(any(KpiB4Result.class))).thenAnswer(inv -> inv.getArgument(0));
        when(kpiB4ResultMapper.toDto(any(KpiB4Result.class))).thenAnswer(inv -> {
            KpiB4Result r = inv.getArgument(0);
            KpiB4ResultDTO dto = new KpiB4ResultDTO();
            dto.setOutcome(r.getOutcome());
            return dto;
        });

        KpiB4ResultDTO dto = kpiB4Service.executeKpiB4Calculation(instance);

        assertNotNull(dto);
        assertEquals(OutcomeStatus.OK, dto.getOutcome());
    }

    @Test
    void testGetKpiB4Results_ReturnsList() {
        when(instanceRepository.findById(1L)).thenReturn(Optional.of(instance));
        when(kpiB4ResultRepository.findByInstanceOrderByAnalysisDateDesc(instance)).thenReturn(List.of(result));
        when(kpiB4ResultMapper.toDto(result)).thenReturn(new KpiB4ResultDTO());

        List<KpiB4ResultDTO> results = kpiB4Service.getKpiB4Results("1");
        assertEquals(1, results.size());
    }

    @Test
    void testGetKpiB4Results_WithPagination() {
        when(instanceRepository.findById(1L)).thenReturn(Optional.of(instance));
        Page<KpiB4Result> page = new PageImpl<>(List.of(result));
        when(kpiB4ResultRepository.findByInstanceOrderByAnalysisDateDesc(eq(instance), any(Pageable.class)))
            .thenReturn(page);
        when(kpiB4ResultMapper.toDto(result)).thenReturn(new KpiB4ResultDTO());

        Page<KpiB4ResultDTO> results = kpiB4Service.getKpiB4Results("1", Pageable.unpaged());
        assertEquals(1, results.getContent().size());
    }

    @Test
    void testGetKpiB4Result_ByDate() {
        LocalDateTime analysisDate = LocalDateTime.of(2025, 1, 15, 0, 0);
        when(instanceRepository.findById(1L)).thenReturn(Optional.of(instance));
        when(kpiB4ResultRepository.findByInstanceAndAnalysisDate(instance, analysisDate.toLocalDate()))
            .thenReturn(result);
        when(kpiB4ResultMapper.toDto(result)).thenReturn(new KpiB4ResultDTO());

        KpiB4ResultDTO dto = kpiB4Service.getKpiB4Result("1", analysisDate);
        assertNotNull(dto);
    }

    @Test
    void testDeleteKpiB4Data() {
        when(instanceRepository.findById(1L)).thenReturn(Optional.of(instance));
        doNothing().when(kpiB4ResultRepository).deleteByInstance(instance);

        assertDoesNotThrow(() -> kpiB4Service.deleteKpiB4Data("1"));
        verify(kpiB4ResultRepository).deleteByInstance(instance);
    }

    @Test
    void testExistsKpiB4Calculation_True() {
        LocalDateTime analysisDate = LocalDateTime.of(2025, 1, 15, 0, 0);
        when(instanceRepository.findById(1L)).thenReturn(Optional.of(instance));
        when(kpiB4ResultRepository.existsByInstanceAndAnalysisDate(instance, analysisDate.toLocalDate()))
            .thenReturn(true);

        assertTrue(kpiB4Service.existsKpiB4Calculation("1", analysisDate));
    }

    @Test
    void testRecalculateKpiB4() {
        when(instanceRepository.findById(1L)).thenReturn(Optional.of(instance));
        doNothing().when(kpiB4ResultRepository).deleteByInstance(instance);
        when(kpiB4ResultRepository.save(any(KpiB4Result.class))).thenAnswer(inv -> inv.getArgument(0));
        when(kpiConfigurationRepository.findByModuleCode(ModuleCode.B4.code)).thenReturn(Optional.of(configuration));
        when(moduleRepository.findByCode(ModuleCode.B4.code)).thenReturn(Optional.of(moduleB4));
        when(instanceModuleService.findOne(instance.getId(), moduleB4.getId())).thenReturn(Optional.of(instanceModuleDTO));
        when(instanceModuleService.findById(instanceModuleDTO.getId())).thenReturn(Optional.of(new InstanceModule()));
        when(kpiB4ResultMapper.toDto(any(KpiB4Result.class))).thenAnswer(inv -> new KpiB4ResultDTO());

        KpiB4ResultDTO dto = kpiB4Service.recalculateKpiB4("1");
        assertNotNull(dto);
    }

    @Test
    void testUpdateKpiB4ResultOutcome() {
        when(kpiB4ResultRepository.findById(1L)).thenReturn(Optional.of(result));
        when(kpiB4ResultRepository.save(result)).thenReturn(result);

        assertDoesNotThrow(() -> kpiB4Service.updateKpiB4ResultOutcome(1L, OutcomeStatus.KO));
        assertEquals(OutcomeStatus.KO, result.getOutcome());
    }

    // Puoi aggiungere altri test per metodi privati indirettamente tramite executeKpiB4Calculation
    // Come createAndSaveDetailResults e createAndSaveAnalyticData verificando che repository.save sia chiamato

}
