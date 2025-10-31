package com.nexigroup.pagopa.cruscotto.service.impl;

import static org.mockito.Mockito.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.nexigroup.pagopa.cruscotto.domain.KpiResult;
import com.nexigroup.pagopa.cruscotto.domain.enumeration.ModuleCode;
import com.nexigroup.pagopa.cruscotto.domain.enumeration.OutcomeStatus;
import com.nexigroup.pagopa.cruscotto.repository.KpiResultRepository;
import com.nexigroup.pagopa.cruscotto.service.dto.KpiResultDTO;
import com.nexigroup.pagopa.cruscotto.service.mapper.KpiResultMapper;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class GenericKpiResultServiceImplTest {

    @Mock
    private KpiResultRepository kpiResultRepository;

    @Mock
    private KpiResultMapper kpiResultMapper;

    @InjectMocks
    private GenericKpiResultServiceImpl service;

    private KpiResult kpiResult;
    private KpiResultDTO kpiResultDTO;

    @BeforeEach
    void setUp() {
        kpiResult = new KpiResult();
        kpiResult.setId(1L);
        kpiResultDTO = new KpiResultDTO();
        kpiResultDTO.setId(1L);
    }

    @Test
    void testSave() {
        when(kpiResultMapper.toEntity(kpiResultDTO)).thenReturn(kpiResult);
        when(kpiResultRepository.save(kpiResult)).thenReturn(kpiResult);
        when(kpiResultMapper.toDto(kpiResult)).thenReturn(kpiResultDTO);

        KpiResultDTO result = service.save(kpiResultDTO);

        assertThat(result).isEqualTo(kpiResultDTO);
        verify(kpiResultRepository).save(kpiResult);
    }

    @Test
    void testUpdate() {
        when(kpiResultMapper.toEntity(kpiResultDTO)).thenReturn(kpiResult);
        when(kpiResultRepository.save(kpiResult)).thenReturn(kpiResult);
        when(kpiResultMapper.toDto(kpiResult)).thenReturn(kpiResultDTO);

        KpiResultDTO result = service.update(kpiResultDTO);

        assertThat(result).isEqualTo(kpiResultDTO);
        verify(kpiResultRepository).save(kpiResult);
    }

    @Test
    void testPartialUpdate() {
        when(kpiResultRepository.findById(1L)).thenReturn(Optional.of(kpiResult));
        doNothing().when(kpiResultMapper).partialUpdate(kpiResult, kpiResultDTO);
        when(kpiResultRepository.save(kpiResult)).thenReturn(kpiResult);
        when(kpiResultMapper.toDto(kpiResult)).thenReturn(kpiResultDTO);

        Optional<KpiResultDTO> result = service.partialUpdate(kpiResultDTO);

        assertThat(result).isPresent().contains(kpiResultDTO);
        verify(kpiResultMapper).partialUpdate(kpiResult, kpiResultDTO);
        verify(kpiResultRepository).save(kpiResult);
    }

    @Test
    void testFindAll() {
        when(kpiResultRepository.findByModuleCode(ModuleCode.B3)).thenReturn(List.of(kpiResult));
        when(kpiResultMapper.toDto(kpiResult)).thenReturn(kpiResultDTO);

        List<KpiResultDTO> result = service.findAll(ModuleCode.B3);

        assertThat(result).containsExactly(kpiResultDTO);
    }

    @Test
    void testFindOne() {
        when(kpiResultRepository.findById(1L)).thenReturn(Optional.of(kpiResult));
        when(kpiResultMapper.toDto(kpiResult)).thenReturn(kpiResultDTO);

        Optional<KpiResultDTO> result = service.findOne(1L);

        assertThat(result).isPresent().contains(kpiResultDTO);
    }

    @Test
    void testDelete() {
        doNothing().when(kpiResultRepository).deleteById(1L);

        service.delete(1L);

        verify(kpiResultRepository).deleteById(1L);
    }

    @Test
    void testFindByInstanceModuleId() {
        when(kpiResultRepository.findByModuleCodeAndInstanceModuleId(ModuleCode.B3, 1L))
            .thenReturn(List.of(kpiResult));
        when(kpiResultMapper.toDto(kpiResult)).thenReturn(kpiResultDTO);

        List<KpiResultDTO> result = service.findByInstanceModuleId(ModuleCode.B3, 1L);

        assertThat(result).containsExactly(kpiResultDTO);
    }

    @Test
    void testDeleteAllByInstanceModuleId() {
        doNothing().when(kpiResultRepository).deleteByModuleCodeAndInstanceModuleId(ModuleCode.B3, 1L);

        service.deleteAllByInstanceModuleId(ModuleCode.B3, 1L);

        verify(kpiResultRepository).deleteByModuleCodeAndInstanceModuleId(ModuleCode.B3, 1L);
    }

    @Test
    void testUpdateOutcome() {
        when(kpiResultRepository.findById(1L)).thenReturn(Optional.of(kpiResult));
        when(kpiResultRepository.save(kpiResult)).thenReturn(kpiResult);

        service.updateOutcome(1L, OutcomeStatus.OK);

        assertThat(kpiResult.getOutcome()).isEqualTo(OutcomeStatus.OK);
        verify(kpiResultRepository).save(kpiResult);
    }

    @Test
    void testFindByInstanceModuleIdAndOutcome() {
        when(kpiResultRepository.findByModuleCodeAndInstanceModuleIdAndOutcome(ModuleCode.B3, 1L, OutcomeStatus.OK))
            .thenReturn(List.of(kpiResult));
        when(kpiResultMapper.toDto(kpiResult)).thenReturn(kpiResultDTO);

        List<KpiResultDTO> result = service.findByInstanceModuleIdAndOutcome(ModuleCode.B3, 1L, OutcomeStatus.OK);

        assertThat(result).containsExactly(kpiResultDTO);
    }
}
