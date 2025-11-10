package com.nexigroup.pagopa.cruscotto.service.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

import com.nexigroup.pagopa.cruscotto.domain.*;
import com.nexigroup.pagopa.cruscotto.repository.*;
import com.nexigroup.pagopa.cruscotto.service.dto.KpiA1AnalyticDataDTO;
import com.nexigroup.pagopa.cruscotto.service.qdsl.QueryBuilder;
import com.querydsl.core.types.*;
import com.querydsl.jpa.JPQLQuery;

import com.querydsl.jpa.impl.JPAQuery;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
@DisplayName("KpiA1AnalyticDataServiceImpl Tests")
class KpiA1AnalyticDataServiceImplTest {

    @Mock
    private AnagStationRepository anagStationRepository;

    @Mock
    private InstanceRepository instanceRepository;

    @Mock
    private InstanceModuleRepository instanceModuleRepository;

    @Mock
    private KpiA1AnalyticDataRepository kpiA1AnalyticDataRepository;

    @Mock
    private KpiA1DetailResultRepository kpiA1DetailResultRepository;

    @Mock
    private QueryBuilder queryBuilder;

    @Mock
    private JPQLQuery<KpiA1AnalyticDataDTO> query;

    private KpiA1AnalyticDataServiceImpl service;

    @BeforeEach
    void setUp() {
        service = new KpiA1AnalyticDataServiceImpl(
            anagStationRepository,
            instanceRepository,
            instanceModuleRepository,
            kpiA1AnalyticDataRepository,
            kpiA1DetailResultRepository,
            queryBuilder
        );
    }

    @Test
    void saveAll_shouldSaveAllEntities() {
        // Arrange
        KpiA1AnalyticDataDTO dto = new KpiA1AnalyticDataDTO();
        dto.setInstanceId(1L);
        dto.setInstanceModuleId(2L);
        dto.setStationId(3L);
        dto.setKpiA1DetailResultId(4L);
        dto.setAnalysisDate(LocalDate.now());
        dto.setEvaluationDate(LocalDate.now());
        dto.setMethod("METHOD");
        dto.setTotReq(10L);
        dto.setReqOk(8L);
        dto.setReqTimeoutReal(1L);
        dto.setReqTimeoutValid(1L);

        Instance instance = new Instance();
        instance.setId(1L);

        InstanceModule module = new InstanceModule();
        module.setId(2L);

        AnagStation station = new AnagStation();
        station.setId(3L);

        KpiA1DetailResult detailResult = new KpiA1DetailResult();
        detailResult.setId(4L);

        when(instanceRepository.findById(1L)).thenReturn(Optional.of(instance));
        when(instanceModuleRepository.findById(2L)).thenReturn(Optional.of(module));
        when(anagStationRepository.findById(3L)).thenReturn(Optional.of(station));
        when(kpiA1DetailResultRepository.findById(4L)).thenReturn(Optional.of(detailResult));

        // Act
        service.saveAll(List.of(dto));

        // Assert
        ArgumentCaptor<KpiA1AnalyticData> captor = ArgumentCaptor.forClass(KpiA1AnalyticData.class);
        verify(kpiA1AnalyticDataRepository).save(captor.capture());

        KpiA1AnalyticData saved = captor.getValue();
        assertThat(saved.getInstance()).isEqualTo(instance);
        assertThat(saved.getInstanceModule()).isEqualTo(module);
        assertThat(saved.getStation()).isEqualTo(station);
        assertThat(saved.getKpiA1DetailResult()).isEqualTo(detailResult);
        assertThat(saved.getTotReq()).isEqualTo(10);
    }

    @Test
    void saveAll_shouldThrowExceptionIfInstanceNotFound() {
        KpiA1AnalyticDataDTO dto = new KpiA1AnalyticDataDTO();
        dto.setInstanceId(1L);

        when(instanceRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () -> service.saveAll(List.of(dto)));
    }

    @Test
    void deleteAllByInstanceModule_shouldCallRepository() {
        when(kpiA1AnalyticDataRepository.deleteAllByInstanceModuleId(2L)).thenReturn(5);

        int result = service.deleteAllByInstanceModule(2L);

        assertThat(result).isEqualTo(5);
        verify(kpiA1AnalyticDataRepository).deleteAllByInstanceModuleId(2L);
    }

    @Test
    void findByDetailResultId_shouldReturnResults() {
        long detailResultId = 10L;

        @SuppressWarnings({ "rawtypes", "unchecked" })
        JPAQuery queryMock = mock(JPAQuery.class);

        // Proper argument matchers
        lenient().when(queryMock.from(any(EntityPath.class))).thenReturn(queryMock);
        lenient().when(queryMock.leftJoin(any(EntityPath.class), any(Path.class))).thenReturn(queryMock);
        lenient().when(queryMock.where(any(Predicate.class))).thenReturn(queryMock);
        lenient().when(queryMock.orderBy(any(OrderSpecifier[].class))).thenReturn(queryMock);
        lenient().when(queryMock.select(any(Expression.class))).thenReturn(queryMock);

        // Mock fetch()
        KpiA1AnalyticDataDTO dto = new KpiA1AnalyticDataDTO();
        when(queryMock.fetch()).thenReturn(List.of(dto));

        when(queryBuilder.createQuery()).thenReturn(queryMock);

        // Execute service method
        List<KpiA1AnalyticDataDTO> results = service.findByDetailResultId(detailResultId);

        // Assertions
        assertThat(results).hasSize(1);
        assertThat(results.get(0)).isEqualTo(dto);

        // Verify
        verify(queryBuilder).createQuery();
        verify(queryMock).fetch();
    }

}
