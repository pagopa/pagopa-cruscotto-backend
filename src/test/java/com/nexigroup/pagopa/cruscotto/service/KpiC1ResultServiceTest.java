package com.nexigroup.pagopa.cruscotto.service;

import com.nexigroup.pagopa.cruscotto.domain.KpiC1Result;
import com.nexigroup.pagopa.cruscotto.domain.KpiConfiguration;
import com.nexigroup.pagopa.cruscotto.domain.enumeration.EvaluationType;
import com.nexigroup.pagopa.cruscotto.repository.KpiC1ResultRepository;
import com.nexigroup.pagopa.cruscotto.repository.KpiConfigurationRepository;
import com.nexigroup.pagopa.cruscotto.service.dto.KpiC1ResultDTO;
import com.nexigroup.pagopa.cruscotto.service.mapper.KpiC1ResultMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class KpiC1ResultServiceTest {

    @Mock
    private KpiC1ResultRepository kpiC1ResultRepository;

    @Mock
    private KpiConfigurationRepository kpiConfigurationRepository;

    @Mock
    private KpiC1ResultMapper kpiC1ResultMapper;

    @InjectMocks
    private KpiC1ResultService kpiC1ResultService;

    private KpiC1Result sampleResult;
    private KpiC1ResultDTO sampleDto;
    private KpiConfiguration sampleConfig;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        sampleResult = new KpiC1Result();
        sampleDto = new KpiC1ResultDTO();
        sampleConfig = new KpiConfiguration();
    }

    @Test
    void testSave() {
        when(kpiC1ResultRepository.save(sampleResult)).thenReturn(sampleResult);
        KpiC1Result result = kpiC1ResultService.save(sampleResult);
        assertThat(result).isEqualTo(sampleResult);
        verify(kpiC1ResultRepository).save(sampleResult);
    }

    @Test
    void testFindByInstanceAndInstanceModuleAndReferenceDate() {
        LocalDate date = LocalDate.now();
        when(kpiC1ResultRepository.findByInstanceAndInstanceModuleAndReferenceDate(1L, 2L, date))
            .thenReturn(Optional.of(sampleResult));

        Optional<KpiC1Result> result = kpiC1ResultService.findByInstanceAndInstanceModuleAndReferenceDate(1L, 2L, date);
        assertThat(result).isPresent();
        assertThat(result.orElseThrow()).isEqualTo(sampleResult);
    }

    @Test
    void testFindByInstanceId() {
        when(kpiC1ResultRepository.findByInstanceId(1L)).thenReturn(Arrays.asList(sampleResult));
        List<KpiC1Result> results = kpiC1ResultService.findByInstanceId(1L);
        assertThat(results).containsExactly(sampleResult);
    }

    @Test
    void testFindByReferenceDateBetween() {
        LocalDate start = LocalDate.now().minusDays(5);
        LocalDate end = LocalDate.now();
        when(kpiC1ResultRepository.findByReferenceDateBetween(start, end))
            .thenReturn(Arrays.asList(sampleResult));

        List<KpiC1Result> results = kpiC1ResultService.findByReferenceDateBetween(start, end);
        assertThat(results).containsExactly(sampleResult);
    }

    @Test
    void testDeleteAll() {
        doNothing().when(kpiC1ResultRepository).deleteAll();
        kpiC1ResultService.deleteAll();
        verify(kpiC1ResultRepository).deleteAll();
    }

    @Test
    void testDeleteByReferenceDateBefore() {
        LocalDate cutoff = LocalDate.now();
        doNothing().when(kpiC1ResultRepository).deleteByReferenceDateBefore(cutoff);
        kpiC1ResultService.deleteByReferenceDateBefore(cutoff);
        verify(kpiC1ResultRepository).deleteByReferenceDateBefore(cutoff);
    }

    @Test
    void testFindByInstanceModuleIdDtoWithConfiguration() {
        // Prepare sample module and configuration
        sampleConfig.setNotificationTolerance(new BigDecimal("5"));
        sampleConfig.setEvaluationType(EvaluationType.MESE);

        // Mock instanceModule and module inside sampleResult
        var module = new com.nexigroup.pagopa.cruscotto.domain.Module();
        var instanceModule = new com.nexigroup.pagopa.cruscotto.domain.InstanceModule();
        instanceModule.setModule(module);
        sampleResult.setInstanceModule(instanceModule);

        when(kpiC1ResultRepository.findByInstanceModuleId(1L)).thenReturn(List.of(sampleResult));
        when(kpiC1ResultMapper.toDto(sampleResult)).thenReturn(sampleDto);
        when(kpiConfigurationRepository.findByModule(module)).thenReturn(Optional.of(sampleConfig));

        List<KpiC1ResultDTO> dtos = kpiC1ResultService.findByInstanceModuleId(1L);
        assertThat(dtos).hasSize(1);
        assertThat(dtos.get(0).getTolerance()).isEqualTo(new BigDecimal("5"));
        assertThat(dtos.get(0).getEvaluationType()).isEqualTo(EvaluationType.MESE);
    }

    @Test
    void testFindByInstanceAndInstanceModule() {
        when(kpiC1ResultRepository.findByInstanceAndInstanceModule(1L, 2L))
            .thenReturn(Arrays.asList(sampleResult));

        List<KpiC1Result> results = kpiC1ResultService.findByInstanceAndInstanceModule(1L, 2L);
        assertThat(results).containsExactly(sampleResult);
    }
}
