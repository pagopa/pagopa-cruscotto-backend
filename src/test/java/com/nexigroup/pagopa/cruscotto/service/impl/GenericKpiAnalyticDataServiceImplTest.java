package com.nexigroup.pagopa.cruscotto.service.impl;

import com.nexigroup.pagopa.cruscotto.domain.enumeration.ModuleCode;
import com.nexigroup.pagopa.cruscotto.repository.KpiAnalyticDataRepository;
import com.nexigroup.pagopa.cruscotto.service.dto.KpiAnalyticDataDTO;
import com.nexigroup.pagopa.cruscotto.service.mapper.KpiAnalyticDataMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GenericKpiAnalyticDataServiceImplTest {

    @Mock
    private KpiAnalyticDataRepository repository;

    @Mock
    private KpiAnalyticDataMapper mapper;

    @InjectMocks
    private GenericKpiAnalyticDataServiceImpl service;

    private KpiAnalyticDataDTO dto;
    private com.nexigroup.pagopa.cruscotto.domain.KpiAnalyticData entity;

    @BeforeEach
    void setUp() {
        dto = new KpiAnalyticDataDTO();
        dto.setId(1L);
        dto.setModuleCode(ModuleCode.B4);

        entity = new com.nexigroup.pagopa.cruscotto.domain.KpiAnalyticData();
        entity.setId(1L);
        entity.setModuleCode(ModuleCode.B4);
    }

    @Test
    void testSave() {
        when(mapper.toEntity(dto)).thenReturn(entity);
        when(repository.save(entity)).thenReturn(entity);
        when(mapper.toDto(entity)).thenReturn(dto);

        KpiAnalyticDataDTO result = service.save(dto);

        assertThat(result).isEqualTo(dto);
        verify(repository).save(entity);
    }

    @Test
    void testSaveAll() {
        List<KpiAnalyticDataDTO> dtos = List.of(dto);
        List<com.nexigroup.pagopa.cruscotto.domain.KpiAnalyticData> entities = List.of(entity);

        when(mapper.toEntity(dto)).thenReturn(entity);
        when(repository.saveAll(entities)).thenReturn(entities);
        when(mapper.toDto(entity)).thenReturn(dto);

        List<KpiAnalyticDataDTO> result = service.saveAll(dtos);

        assertThat(result).containsExactly(dto);
        verify(repository).saveAll(entities);
    }

    @Test
    void testPartialUpdate() {
        when(repository.findById(dto.getId())).thenReturn(Optional.of(entity));
        doNothing().when(mapper).partialUpdate(entity, dto);
        when(repository.save(entity)).thenReturn(entity);
        when(mapper.toDto(entity)).thenReturn(dto);

        Optional<KpiAnalyticDataDTO> result = service.partialUpdate(dto);

        assertThat(result).isPresent();
        assertThat(result.orElseThrow()).isEqualTo(dto);
        verify(mapper).partialUpdate(entity, dto);
        verify(repository).save(entity);
    }

    @Test
    void testUpdate() {
        when(mapper.toEntity(dto)).thenReturn(entity);
        when(repository.save(entity)).thenReturn(entity);
        when(mapper.toDto(entity)).thenReturn(dto);

        KpiAnalyticDataDTO result = service.update(dto);

        assertThat(result).isEqualTo(dto);
        verify(repository).save(entity);
    }

    @Test
    void testFindAllWithModuleCode() {
        when(repository.findAllByModuleCode(ModuleCode.B4)).thenReturn(List.of(entity));
        when(mapper.toDto(entity)).thenReturn(dto);

        List<KpiAnalyticDataDTO> result = service.findAll(ModuleCode.B4);

        assertThat(result).containsExactly(dto);
    }

    @Test
    void testFindAll() {
        when(repository.findAll()).thenReturn(List.of(entity));
        when(mapper.toDto(entity)).thenReturn(dto);

        List<KpiAnalyticDataDTO> result = service.findAll();

        assertThat(result).containsExactly(dto);
    }

    @Test
    void testFindOne() {
        when(repository.findById(1L)).thenReturn(Optional.of(entity));
        when(mapper.toDto(entity)).thenReturn(dto);

        Optional<KpiAnalyticDataDTO> result = service.findOne(1L);

        assertThat(result).isPresent();
        assertThat(result.orElseThrow()).isEqualTo(dto);
    }

    @Test
    void testDelete() {
        doNothing().when(repository).deleteById(1L);

        service.delete(1L);

        verify(repository).deleteById(1L);
    }

    @Test
    void testDeleteAllByInstanceModuleId() {
        doNothing().when(repository).deleteAllByModuleCodeAndInstanceModuleId(ModuleCode.B4, 100L);

        service.deleteAllByInstanceModuleId(ModuleCode.B4, 100L);

        verify(repository).deleteAllByModuleCodeAndInstanceModuleId(ModuleCode.B4, 100L);
    }

    @Test
    void testFindByInstanceModuleId() {
        when(repository.findAllByModuleCodeAndInstanceModuleId(ModuleCode.B4, 100L))
            .thenReturn(List.of(entity));
        when(mapper.toDto(entity)).thenReturn(dto);

        List<KpiAnalyticDataDTO> result = service.findByInstanceModuleId(ModuleCode.B4, 100L);

        assertThat(result).containsExactly(dto);
    }

    @Test
    void testFindByDetailResultId() {
        when(repository.findAllByModuleCodeAndInstanceModuleId(ModuleCode.B4, 100L))
            .thenReturn(List.of(entity));
        when(mapper.toDto(entity)).thenReturn(dto);

        List<KpiAnalyticDataDTO> result = service.findByDetailResultId(ModuleCode.B4, 100L);

        assertThat(result).containsExactly(dto);
    }

    @Test
    void testFindByDateRange() {
        when(repository.findAllByModuleCode(ModuleCode.B4)).thenReturn(List.of(entity));
        when(mapper.toDto(entity)).thenReturn(dto);

        List<KpiAnalyticDataDTO> result = service.findByDateRange(ModuleCode.B4, LocalDate.now(), LocalDate.now());

        assertThat(result).containsExactly(dto);
    }
}
