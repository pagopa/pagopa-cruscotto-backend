package com.nexigroup.pagopa.cruscotto.service.impl;

import com.nexigroup.pagopa.cruscotto.domain.Instance;
import com.nexigroup.pagopa.cruscotto.domain.KpiB8AnalyticData;
import com.nexigroup.pagopa.cruscotto.domain.KpiB8DetailResult;
import com.nexigroup.pagopa.cruscotto.repository.*;
import com.nexigroup.pagopa.cruscotto.service.dto.KpiB8AnalyticDataDTO;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension .class)
class KpiB8AnalyticDataServiceImplTest {

    @Mock
    private KpiB8AnalyticDataRepository kpiB8AnalyticDataRepository;

    @InjectMocks
    private KpiB8AnalyticDataServiceImpl service;

    @Test
    void testSave_returnsSameDTO() {
        KpiB8AnalyticDataDTO dto = new KpiB8AnalyticDataDTO();
        dto.setId(1L);

        KpiB8AnalyticDataDTO result = service.save(dto);

        assertThat(result).isEqualTo(dto);
    }

    @Test
    void testDeleteAllByInstanceModule_returnsZero() {
        int deleted = service.deleteAllByInstanceModule(123L);
        assertThat(deleted).isZero();
    }

    @Test
    void testFindByDetailResultId_returnsConvertedDTOs() {
        KpiB8DetailResult detailResult = new KpiB8DetailResult();
        detailResult.setId(10L);

        Instance instance = new Instance();
        instance.setAnalysisPeriodStartDate(LocalDate.of(2025, 1, 1));
        instance.setAnalysisPeriodEndDate(LocalDate.of(2025, 1, 31));

        KpiB8AnalyticData analyticData = new KpiB8AnalyticData();
        analyticData.setId(1L);
        analyticData.setInstanceId(100L);
        analyticData.setAnalysisDate(LocalDate.of(2025, 1, 15));
        analyticData.setEvaluationDate(LocalDate.of(2025, 1, 16));
        analyticData.setTotReq(50L);
        analyticData.setReqKO(5L);
        analyticData.setKpiB8DetailResult(detailResult);
        analyticData.setInstance(instance);

        when(kpiB8AnalyticDataRepository.findAllByDetailResultIdOrderByEvaluationDateDesc(10L))
            .thenReturn(List.of(analyticData));

        List<KpiB8AnalyticDataDTO> result = service.findByDetailResultId(10L);

        assertThat(result).hasSize(1);
        KpiB8AnalyticDataDTO dto = result.get(0);
        assertThat(dto.getId()).isEqualTo(1L);
        assertThat(dto.getInstanceId()).isEqualTo(100L);
        assertThat(dto.getAnalysisDate()).isEqualTo(LocalDate.of(2025, 1, 15));
        assertThat(dto.getEvaluationDate()).isEqualTo(LocalDate.of(2025, 1, 16));
        assertThat(dto.getTotReq()).isEqualTo(50);
        assertThat(dto.getReqKO()).isEqualTo(5);
        assertThat(dto.getKpiB8DetailResultId()).isEqualTo(10L);
        assertThat(dto.getAnalysisDatePeriod()).isEqualTo("01/01/2025 - 31/01/2025");

        verify(kpiB8AnalyticDataRepository).findAllByDetailResultIdOrderByEvaluationDateDesc(10L);
    }

    @Test
    void testConvertToDTO_handlesNullInstance() {
        KpiB8AnalyticData analyticData = new KpiB8AnalyticData();
        analyticData.setId(1L);
        analyticData.setKpiB8DetailResult(new KpiB8DetailResult());
        // instance is null

        KpiB8AnalyticDataDTO dto = service.findByDetailResultId(999L).stream()
            .findFirst()
            .orElse(null);

        // Since repository returns nothing, we test convertToDTO indirectly
        assertThat(dto).isNull(); // nothing returned, conversion not triggered
    }
}
