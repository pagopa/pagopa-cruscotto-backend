package com.nexigroup.pagopa.cruscotto.service.impl;

import com.nexigroup.pagopa.cruscotto.domain.Module;
import com.nexigroup.pagopa.cruscotto.domain.enumeration.AnalysisType;
import com.nexigroup.pagopa.cruscotto.domain.enumeration.ModuleStatus;
import com.nexigroup.pagopa.cruscotto.repository.ModuleRepository;
import com.nexigroup.pagopa.cruscotto.security.SecurityUtils;
import com.nexigroup.pagopa.cruscotto.service.GenericServiceException;
import com.nexigroup.pagopa.cruscotto.service.bean.ModuleRequestBean;
import com.nexigroup.pagopa.cruscotto.service.dto.ModuleDTO;
import com.nexigroup.pagopa.cruscotto.service.mapper.ModuleMapper;
import com.nexigroup.pagopa.cruscotto.service.qdsl.QueryBuilder;
import com.querydsl.core.types.Expression;
import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.dsl.EntityPathBase;
import com.querydsl.jpa.impl.JPAQuery;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;

import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("ModuleServiceImpl Tests")
class ModuleServiceImplTest {

    @Mock
    private ModuleRepository moduleRepository;

    @Mock
    private ModuleMapper moduleMapper;

    @Mock
    private QueryBuilder queryBuilder;

    @InjectMocks
    private ModuleServiceImpl moduleService;

    // Use raw types to avoid generic conflicts
    @Mock
    private JPAQuery jpqlQuery;

    @Mock
    private JPAQuery jpqlQueryDTO;

    @Test
    void testFindAll() {
        Pageable pageable = PageRequest.of(0, 10, Sort.by("code"));

        when(queryBuilder.createQuery()).thenReturn(jpqlQuery);
        when(jpqlQuery.from(any(EntityPathBase.class))).thenReturn(jpqlQuery);
        when(jpqlQuery.where(any(Predicate.class))).thenReturn(jpqlQuery);
        lenient().when(jpqlQuery.fetch()).thenReturn(Collections.singletonList(1L));
        when(jpqlQuery.select(any(Expression.class))).thenReturn(jpqlQueryDTO);
        when(jpqlQueryDTO.offset(anyLong())).thenReturn(jpqlQueryDTO);
        when(jpqlQueryDTO.limit(anyLong())).thenReturn(jpqlQueryDTO);
        when(jpqlQueryDTO.fetch()).thenReturn(Collections.singletonList(new ModuleDTO()));

        Page<ModuleDTO> result = moduleService.findAll(pageable);

        assertEquals(1, result.getTotalElements());
        verify(queryBuilder).createQuery();
    }

    @Test
    void testFindOne() {
        Module module = new Module();
        module.setId(1L);

        ModuleDTO dto = new ModuleDTO();
        dto.setId(1L);

        when(moduleRepository.findById(1L)).thenReturn(Optional.of(module));
        when(moduleMapper.toDto(module)).thenReturn(dto);

        Optional<ModuleDTO> result = moduleService.findOne(1L);

        assertTrue(result.isPresent());
        assertEquals(1L, result.orElseThrow().getId());
    }

    @Test
    void testDeleteModule_Manual() {
        Module module = new Module();
        module.setId(1L);
        module.setAnalysisType(AnalysisType.MANUALE);
        module.setDeleted(false);
        module.setStatus(ModuleStatus.ATTIVO);

        when(moduleRepository.findOneByIdAndNotDeleted(1L)).thenReturn(Optional.of(module));
        when(moduleRepository.save(any(Module.class))).thenReturn(module);

        boolean deleted = moduleService.deleteModule(1L);

        assertTrue(deleted);
        assertEquals(ModuleStatus.NON_ATTIVO, module.getStatus());
        assertTrue(module.isDeleted());
        assertNotNull(module.getDeletedDate());
    }

    @Test
    void testDeleteModule_NonManual() {
        Module module = new Module();
        module.setId(1L);
        module.setAnalysisType(AnalysisType.AUTOMATICA);
        module.setDeleted(false);

        when(moduleRepository.findOneByIdAndNotDeleted(1L)).thenReturn(Optional.of(module));

        boolean deleted = moduleService.deleteModule(1L);

        assertFalse(deleted);
        assertFalse(module.isDeleted());
    }

    @Test
    void testFindAllWithoutConfiguration() {
        Pageable pageable = PageRequest.of(0, 10);

        when(queryBuilder.createQuery()).thenReturn(jpqlQuery);
        when(jpqlQuery.from(any(EntityPathBase.class))).thenReturn(jpqlQuery);
        when(jpqlQuery.leftJoin(any(EntityPathBase.class))).thenReturn(jpqlQuery);
        when(jpqlQuery.on(any(Predicate.class))).thenReturn(jpqlQuery);
        when(jpqlQuery.where(any(Predicate.class))).thenReturn(jpqlQuery);
        lenient().when(jpqlQuery.fetch()).thenReturn(Collections.singletonList(1L));
        when(jpqlQuery.select(any(Expression.class))).thenReturn(jpqlQueryDTO);
        when(jpqlQueryDTO.offset(anyLong())).thenReturn(jpqlQueryDTO);
        when(jpqlQueryDTO.limit(anyLong())).thenReturn(jpqlQueryDTO);
        when(jpqlQueryDTO.fetch()).thenReturn(Collections.singletonList(new ModuleDTO()));

        Page<ModuleDTO> result = moduleService.findAllWithoutConfiguration(pageable);

        assertEquals(1, result.getTotalElements());
    }

    @Test
    void testSaveNew_Success() {
        ModuleRequestBean request = new ModuleRequestBean();
        request.setCode("TEST");
        request.setName("Test Module");
        request.setAnalysisType(AnalysisType.MANUALE);
        request.setStatus(ModuleStatus.ATTIVO);
        request.setAllowManualOutcome(Boolean.TRUE); // fixed NPE

        Module module = new Module();
        module.setId(1L);

        ModuleDTO dto = new ModuleDTO();
        dto.setId(1L);

        try (MockedStatic<SecurityUtils> mockedSecurity = mockStatic(SecurityUtils.class)) {
            mockedSecurity.when(SecurityUtils::getCurrentUserLogin).thenReturn(Optional.of("user"));

            when(moduleRepository.findByCode("TEST")).thenReturn(Optional.empty());
            when(moduleRepository.save(any(Module.class))).thenReturn(module);
            when(moduleMapper.toDto(module)).thenReturn(dto);

            ModuleDTO result = moduleService.saveNew(request);

            assertNotNull(result);
            assertEquals(1L, result.getId());
        }
    }

    @Test
    void testUpdate_Success() {
        ModuleRequestBean request = new ModuleRequestBean();
        request.setId(1L);
        request.setCode("CODE");
        request.setName("Updated Module");
        request.setAnalysisType(AnalysisType.MANUALE);
        request.setStatus(ModuleStatus.ATTIVO);
        request.setAllowManualOutcome(Boolean.TRUE); // fixed NPE

        Module module = new Module();
        module.setId(1L);
        module.setCode("CODE");

        ModuleDTO dto = new ModuleDTO();
        dto.setId(1L);

        try (MockedStatic<SecurityUtils> mockedSecurity = mockStatic(SecurityUtils.class)) {
            mockedSecurity.when(SecurityUtils::getCurrentUserLogin).thenReturn(Optional.of("user"));

            when(moduleRepository.findById(1L)).thenReturn(Optional.of(module));
            when(moduleRepository.save(any(Module.class))).thenReturn(module);
            when(moduleMapper.toDto(module)).thenReturn(dto);

            ModuleDTO result = moduleService.update(request);

            assertNotNull(result);
            assertEquals(1L, result.getId());
        }
    }

    @Test
    void testUpdate_NotFound() {
        ModuleRequestBean request = new ModuleRequestBean();
        request.setId(1L);

        when(moduleRepository.findById(1L)).thenReturn(Optional.empty());

        GenericServiceException ex = assertThrows(GenericServiceException.class, () -> moduleService.update(request));
        assertTrue(ex.getMessage().contains("Module with id 1 not exist"));
    }
}
