package com.nexigroup.pagopa.cruscotto.service.impl;

import com.nexigroup.pagopa.cruscotto.domain.enumeration.ModuleCode;
import com.nexigroup.pagopa.cruscotto.repository.KpiDetailResultRepository;
import com.nexigroup.pagopa.cruscotto.service.dto.KpiDetailResultDTO;
import com.nexigroup.pagopa.cruscotto.service.mapper.KpiDetailResultMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GenericKpiDetailResultServiceImplTest {

    @Mock
    private KpiDetailResultRepository kpiDetailResultRepository;

    @Mock
    private KpiDetailResultMapper kpiDetailResultMapper;

    @InjectMocks
    private GenericKpiDetailResultServiceImpl service;

    private KpiDetailResultDTO dto;
    private com.nexigroup.pagopa.cruscotto.domain.KpiDetailResult entity;

    @BeforeEach
    void setUp() {
        dto = new KpiDetailResultDTO();
        dto.setId(1L);

        entity = new com.nexigroup.pagopa.cruscotto.domain.KpiDetailResult();
        entity.setId(1L);
    }

    @Test
    void testSave() {
        when(kpiDetailResultMapper.toEntity(dto)).thenReturn(entity);
        when(kpiDetailResultRepository.save(entity)).thenReturn(entity);
        when(kpiDetailResultMapper.toDto(entity)).thenReturn(dto);

        KpiDetailResultDTO result = service.save(dto);

        assertThat(result).isEqualTo(dto);
        verify(kpiDetailResultRepository).save(entity);
    }

    @Test
    void testSaveAll() {
        List<KpiDetailResultDTO> dtos = List.of(dto);
        List<com.nexigroup.pagopa.cruscotto.domain.KpiDetailResult> entities = List.of(entity);

        when(kpiDetailResultMapper.toEntity(dto)).thenReturn(entity);
        when(kpiDetailResultRepository.saveAll(entities)).thenReturn(entities);
        when(kpiDetailResultMapper.toDto(entity)).thenReturn(dto);

        List<KpiDetailResultDTO> result = service.saveAll(dtos);

        assertThat(result).containsExactly(dto);
        verify(kpiDetailResultRepository).saveAll(entities);
    }

    @Test
    void testPartialUpdate() {
        when(kpiDetailResultRepository.findById(dto.getId())).thenReturn(Optional.of(entity));
        // No need to stub partialUpdate if it’s void
        when(kpiDetailResultRepository.save(entity)).thenReturn(entity);
        when(kpiDetailResultMapper.toDto(entity)).thenReturn(dto);

        Optional<KpiDetailResultDTO> result = service.partialUpdate(dto);

        assertThat(result).isPresent().contains(dto);
        verify(kpiDetailResultMapper).partialUpdate(dto, entity); // just verify it was called
    }

    @Test
    void testUpdate() {
        when(kpiDetailResultMapper.toEntity(dto)).thenReturn(entity);
        when(kpiDetailResultRepository.save(entity)).thenReturn(entity);
        when(kpiDetailResultMapper.toDto(entity)).thenReturn(dto);

        KpiDetailResultDTO result = service.update(dto);

        assertThat(result).isEqualTo(dto);
        verify(kpiDetailResultRepository).save(entity);
    }

    @Test
    void testFindAllByModuleCode() {
        ModuleCode moduleCode = ModuleCode.B4; // replace with actual enum
        when(kpiDetailResultRepository.findAllByModuleCode(moduleCode)).thenReturn(List.of(entity));
        when(kpiDetailResultMapper.toDto(entity)).thenReturn(dto);

        List<KpiDetailResultDTO> result = service.findAll(moduleCode);

        assertThat(result).containsExactly(dto);
        verify(kpiDetailResultRepository).findAllByModuleCode(moduleCode);
    }

    @Test
    void testFindAll() {
        when(kpiDetailResultRepository.findAll()).thenReturn(List.of(entity));
        when(kpiDetailResultMapper.toDto(entity)).thenReturn(dto);

        List<KpiDetailResultDTO> result = service.findAll();

        assertThat(result).containsExactly(dto);
        verify(kpiDetailResultRepository).findAll();
    }

    @Test
    void testFindOne() {
        when(kpiDetailResultRepository.findById(1L)).thenReturn(Optional.of(entity));
        when(kpiDetailResultMapper.toDto(entity)).thenReturn(dto);

        Optional<KpiDetailResultDTO> result = service.findOne(1L);

        assertThat(result).isPresent().contains(dto);
        verify(kpiDetailResultRepository).findById(1L);
    }

    @Test
    void testDelete() {
        doNothing().when(kpiDetailResultRepository).deleteById(1L);

        service.delete(1L);

        verify(kpiDetailResultRepository).deleteById(1L);
    }

    @Test
    void testDeleteAllByInstanceModuleId() {
        ModuleCode moduleCode = ModuleCode.B4;
        Long instanceModuleId = 2L;

        doNothing().when(kpiDetailResultRepository).deleteAllByModuleCodeAndInstanceModuleId(moduleCode, instanceModuleId);

        service.deleteAllByInstanceModuleId(moduleCode, instanceModuleId);

        verify(kpiDetailResultRepository).deleteAllByModuleCodeAndInstanceModuleId(moduleCode, instanceModuleId);
    }

    @Test
    void testFindByInstanceModuleId() {
        ModuleCode moduleCode = ModuleCode.B4;
        Long instanceModuleId = 2L;

        when(kpiDetailResultRepository.findAllByModuleCodeAndInstanceModuleId(moduleCode, instanceModuleId))
            .thenReturn(List.of(entity));
        when(kpiDetailResultMapper.toDto(entity)).thenReturn(dto);

        List<KpiDetailResultDTO> result = service.findByInstanceModuleId(moduleCode, instanceModuleId);

        assertThat(result).containsExactly(dto);
    }

    @Test
    void testFindByKpiResultId() {
        ModuleCode moduleCode = ModuleCode.B4;
        Long kpiResultId = 3L;

        // delegates to findByInstanceModuleId
        when(kpiDetailResultRepository.findAllByModuleCodeAndInstanceModuleId(moduleCode, kpiResultId))
            .thenReturn(List.of(entity));
        when(kpiDetailResultMapper.toDto(entity)).thenReturn(dto);

        List<KpiDetailResultDTO> result = service.findByKpiResultId(moduleCode, kpiResultId);

        assertThat(result).containsExactly(dto);
    }
}
