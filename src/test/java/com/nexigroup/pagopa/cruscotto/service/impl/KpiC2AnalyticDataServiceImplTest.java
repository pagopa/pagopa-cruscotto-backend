package com.nexigroup.pagopa.cruscotto.service.impl;

import com.nexigroup.pagopa.cruscotto.domain.Instance;
import com.nexigroup.pagopa.cruscotto.domain.KpiC2AnalyticData;
import com.nexigroup.pagopa.cruscotto.domain.KpiC2DetailResult;
import com.nexigroup.pagopa.cruscotto.repository.*;
import com.nexigroup.pagopa.cruscotto.service.dto.KpiC2AnalyticDataDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class KpiC2AnalyticDataServiceImplTest {

    @Mock
    private InstanceRepository instanceRepository;

    @Mock
    private InstanceModuleRepository instanceModuleRepository;

    @Mock
    private KpiC2AnalyticDataRepository kpiC2AnalyticDataRepository;

    @Mock
    private KpiC2DetailResultRepository kpiC2DetailResultRepository;

    @Mock
    private AnagStationRepository anagStationRepository;

    @InjectMocks
    private KpiC2AnalyticDataServiceImpl service;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testSave_ReturnsSameDTO() {
        KpiC2AnalyticDataDTO dto = new KpiC2AnalyticDataDTO();
        dto.setId(1L);

        KpiC2AnalyticDataDTO result = service.save(dto);

        assertEquals(dto, result);
    }

    @Test
    void testDeleteAllByInstanceModule_ReturnsZero() {
        int deleted = service.deleteAllByInstanceModule(1L);
        assertEquals(0, deleted);
    }

    @Test
    void testFindByDetailResultId_ReturnsEmptyList_WhenNoData() {
        when(kpiC2AnalyticDataRepository.findAllByDetailResultIdOrderByEvaluationDateDesc(1L))
            .thenReturn(Collections.emptyList());

        List<KpiC2AnalyticDataDTO> result = service.findByDetailResultId(1L);

        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(kpiC2AnalyticDataRepository).findAllByDetailResultIdOrderByEvaluationDateDesc(1L);
    }

    @Test
    void testFindByDetailResultId_ReturnsDTOList() {
        KpiC2DetailResult detailResult = new KpiC2DetailResult();
        detailResult.setId(10L);

        Instance instance = new Instance();
        instance.setAnalysisPeriodStartDate(LocalDate.of(2025, 1, 1));
        instance.setAnalysisPeriodEndDate(LocalDate.of(2025, 1, 31));

        KpiC2AnalyticData data = new KpiC2AnalyticData();
        data.setId(1L);
        data.setAnalysisDate(LocalDate.of(2025, 2, 1));
        data.setEvaluationDate(LocalDate.of(2025, 2, 2));
        data.setNumInstitution(5L);
        data.setNumInstitutionSend(3L);
        data.setPerInstitutionSend(BigDecimal.valueOf(60));
        data.setNumPayment(100L);
        data.setNumNotification(50);
        data.setPerNotification(BigDecimal.valueOf(50));
        data.setKpiC2DetailResult(detailResult);
        data.setInstance(instance);

        when(kpiC2AnalyticDataRepository.findAllByDetailResultIdOrderByEvaluationDateDesc(10L))
            .thenReturn(Arrays.asList(data));

        List<KpiC2AnalyticDataDTO> dtos = service.findByDetailResultId(10L);

        assertEquals(1, dtos.size());
        KpiC2AnalyticDataDTO dto = dtos.get(0);
        assertEquals(1L, dto.getId());
        assertEquals(5, dto.getNumInstitution());
        assertEquals("01/01/2025 - 31/01/2025", dto.getAnalysisDatePeriod());
        assertEquals(10L, dto.getKpiC2DetailResultId());
    }
}
