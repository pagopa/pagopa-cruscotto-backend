package com.nexigroup.pagopa.cruscotto.service.impl;

import com.nexigroup.pagopa.cruscotto.domain.Instance;
import com.nexigroup.pagopa.cruscotto.domain.KpiB4AnalyticData;
import com.nexigroup.pagopa.cruscotto.domain.KpiB4DetailResult;
import com.nexigroup.pagopa.cruscotto.repository.*;
import com.nexigroup.pagopa.cruscotto.service.dto.KpiB4AnalyticDataDTO;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class KpiB4AnalyticDataServiceImplTest {

    @Mock
    private InstanceRepository instanceRepository;
    @Mock
    private InstanceModuleRepository instanceModuleRepository;
    @Mock
    private KpiB4AnalyticDataRepository kpiB4AnalyticDataRepository;
    @Mock
    private KpiB4DetailResultRepository kpiB4DetailResultRepository;
    @Mock
    private AnagStationRepository anagStationRepository;

    @InjectMocks
    private KpiB4AnalyticDataServiceImpl service;

    @Captor
    private ArgumentCaptor<Long> longCaptor;

    @Test
    void testSave_ReturnsSameDTO() {
        KpiB4AnalyticDataDTO dto = new KpiB4AnalyticDataDTO();
        dto.setId(1L);

        KpiB4AnalyticDataDTO result = service.save(dto);

        assertThat(result).isEqualTo(dto);
    }

    @Test
    void testDeleteAllByInstanceModule_ReturnsZero() {
        int result = service.deleteAllByInstanceModule(123L);
        assertThat(result).isZero();
    }

    @Test
    void testFindByDetailResultId_ReturnsMappedDTOs() {
        // Arrange
        KpiB4DetailResult detailResult = new KpiB4DetailResult();
        detailResult.setId(10L);

        Instance instance = new Instance();
        instance.setId(99L);
        instance.setAnalysisPeriodStartDate(LocalDate.of(2024, 1, 1));
        instance.setAnalysisPeriodEndDate(LocalDate.of(2024, 1, 31));

        KpiB4AnalyticData entity = new KpiB4AnalyticData();
        entity.setId(1L);
        entity.setInstanceId(99L);
        entity.setNumRequestGpd(5L);
        entity.setNumRequestCp(2L);
        entity.setAnalysisDate(LocalDate.of(2024, 2, 1));
        entity.setEvaluationDate(LocalDate.of(2024, 2, 2));
        entity.setInstance(instance);
        entity.setKpiB4DetailResult(detailResult);

        when(kpiB4AnalyticDataRepository.findAllByDetailResultIdOrderByEvaluationDateDesc(10L))
            .thenReturn(List.of(entity));

        // Act
        List<KpiB4AnalyticDataDTO> result = service.findByDetailResultId(10L);

        // Assert
        assertThat(result).hasSize(1);
        KpiB4AnalyticDataDTO dto = result.get(0);

        assertThat(dto.getId()).isEqualTo(1L);
        assertThat(dto.getInstanceId()).isEqualTo(99L);
        assertThat(dto.getNumRequestGpd()).isEqualTo(5L);
        assertThat(dto.getNumRequestCp()).isEqualTo(2L);
        assertThat(dto.getKpiB4DetailResultId()).isEqualTo(10L);
        assertThat(dto.getAnalysisDatePeriod()).isEqualTo("01/01/2024 - 31/01/2024");

        verify(kpiB4AnalyticDataRepository, times(1))
            .findAllByDetailResultIdOrderByEvaluationDateDesc(10L);
    }

    @Test
    void testFindByDetailResultId_HandlesNullInstance() {
        KpiB4DetailResult detailResult = new KpiB4DetailResult();
        detailResult.setId(20L);

        KpiB4AnalyticData entity = new KpiB4AnalyticData();
        entity.setId(2L);
        entity.setKpiB4DetailResult(detailResult);
        entity.setInstance(null);

        when(kpiB4AnalyticDataRepository.findAllByDetailResultIdOrderByEvaluationDateDesc(20L))
            .thenReturn(List.of(entity));

        List<KpiB4AnalyticDataDTO> result = service.findByDetailResultId(20L);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getAnalysisDatePeriod()).isNull();
    }
}
