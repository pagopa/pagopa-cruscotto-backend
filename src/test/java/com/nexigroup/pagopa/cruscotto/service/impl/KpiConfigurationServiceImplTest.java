package com.nexigroup.pagopa.cruscotto.service.impl;

import com.nexigroup.pagopa.cruscotto.domain.KpiConfiguration;
import com.nexigroup.pagopa.cruscotto.domain.Module;
import com.nexigroup.pagopa.cruscotto.repository.KpiConfigurationRepository;
import com.nexigroup.pagopa.cruscotto.repository.ModuleRepository;
import com.nexigroup.pagopa.cruscotto.security.SecurityUtils;
import com.nexigroup.pagopa.cruscotto.service.GenericServiceException;
import com.nexigroup.pagopa.cruscotto.service.bean.KpiConfigurationRequestBean;
import com.nexigroup.pagopa.cruscotto.service.dto.KpiConfigurationDTO;
import com.nexigroup.pagopa.cruscotto.service.mapper.KpiConfigurationMapper;
import com.nexigroup.pagopa.cruscotto.service.qdsl.QueryBuilder;
import com.nexigroup.pagopa.cruscotto.service.util.UserUtils;
import com.querydsl.core.types.EntityPath;
import com.querydsl.core.types.Predicate;
import com.querydsl.jpa.impl.JPAQuery;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.Optional;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("KpiConfigurationServiceImpl Tests")
class KpiConfigurationServiceImplTest {

    @Mock private QueryBuilder queryBuilder;
    @Mock private KpiConfigurationRepository kpiConfigurationRepository;
    @Mock private KpiConfigurationMapper kpiConfigurationMapper;
    @Mock private ModuleRepository moduleRepository;
    @Mock private UserUtils userUtils;

    @InjectMocks private KpiConfigurationServiceImpl service;

    private Module module;
    private KpiConfiguration kpi;
    private KpiConfigurationDTO dto;

    private static MockedStatic<SecurityUtils> securityUtilsStatic;

    @SuppressWarnings("rawtypes")
    private JPAQuery mockQuery; // mock globale condiviso

    @BeforeAll
    static void initStaticMock() {
        securityUtilsStatic = mockStatic(SecurityUtils.class);
        securityUtilsStatic.when(SecurityUtils::getCurrentUserLogin)
            .thenReturn(Optional.of("testuser"));
    }

    @AfterAll
    static void closeStaticMock() {
        securityUtilsStatic.close();
    }

    @BeforeEach
    void setup() {
        module = new Module();
        module.setId(1L);
        module.setCode("TEST_MODULE");
        module.setName("Test Module");
        module.setConfigAverageTimeLimit(true);
        module.setConfigEligibilityThreshold(true);
        module.setConfigEvaluationType(true);
        module.setConfigExcludePlannedShutdown(true);
        module.setConfigExcludeUnplannedShutdown(true);
        module.setConfigTolerance(true);

        kpi = new KpiConfiguration();
        kpi.setId(10L);
        kpi.setModule(module);

        dto = new KpiConfigurationDTO();
        dto.setId(10L);
        dto.setModuleId(1L);
        dto.setModuleCode("TEST_MODULE");

        // mock globale JPAQuery
        mockQuery = mock(JPAQuery.class);
        lenient().when(queryBuilder.createQuery()).thenReturn(mockQuery);
        lenient().when(mockQuery.from(any(EntityPath.class))).thenReturn(mockQuery);
        lenient().when(mockQuery.leftJoin(any(EntityPath.class), any())).thenReturn(mockQuery);
        lenient().when(mockQuery.where(any(Predicate.class))).thenReturn(mockQuery);
        lenient().when(mockQuery.offset(anyLong())).thenReturn(mockQuery);
        lenient().when(mockQuery.limit(anyLong())).thenReturn(mockQuery);
        lenient().when(mockQuery.fetch()).thenReturn(Collections.emptyList());
        lenient().when(mockQuery.fetchOne()).thenReturn(null);

        // mapper stub
        lenient().when(kpiConfigurationMapper.toDto(kpi)).thenReturn(dto);
    }

    @Test
    void saveNew_moduleNotFound() {
        KpiConfigurationRequestBean request = new KpiConfigurationRequestBean();
        request.setModuleCode("INVALID");
        when(moduleRepository.findByCode("INVALID")).thenReturn(Optional.empty());

        assertThrows(GenericServiceException.class, () -> service.saveNew(request));
    }

    @Test
    void update_moduleNotFound() {
        KpiConfigurationRequestBean request = new KpiConfigurationRequestBean();
        request.setModuleCode("INVALID");
        request.setId(10L);
        when(moduleRepository.findByCode("INVALID")).thenReturn(Optional.empty());

        assertThrows(GenericServiceException.class, () -> service.update(request));
    }

    @Test
    void delete_notFound() {
        when(kpiConfigurationRepository.findById(999L)).thenReturn(Optional.empty());
        assertThrows(GenericServiceException.class, () -> service.delete(999L));
    }

}
