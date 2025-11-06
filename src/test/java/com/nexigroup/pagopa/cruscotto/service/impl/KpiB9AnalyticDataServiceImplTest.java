package com.nexigroup.pagopa.cruscotto.service.impl;

import com.querydsl.core.types.Expression;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Predicate;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import com.nexigroup.pagopa.cruscotto.domain.*;
import com.nexigroup.pagopa.cruscotto.repository.*;
import com.nexigroup.pagopa.cruscotto.service.dto.KpiB9AnalyticDataDTO;
import com.nexigroup.pagopa.cruscotto.service.qdsl.QueryBuilder;
import com.querydsl.jpa.impl.JPAQuery;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.lang.reflect.Method;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("KpiB9AnalyticDataServiceImpl Tests")
class KpiB9AnalyticDataServiceImplTest {
    private AnagStationRepository anagStationRepository;
    private InstanceRepository instanceRepository;
    private InstanceModuleRepository instanceModuleRepository;
    private KpiB9AnalyticDataRepository kpiB9AnalyticDataRepository;
    private KpiB9DetailResultRepository kpiB9DetailResultRepository;
    private QueryBuilder queryBuilder;

    private KpiB9AnalyticDataServiceImpl service;

    @BeforeEach
    void setUp() {
        anagStationRepository = mock(AnagStationRepository.class);
        instanceRepository = mock(InstanceRepository.class);
        instanceModuleRepository = mock(InstanceModuleRepository.class);
        kpiB9AnalyticDataRepository = mock(KpiB9AnalyticDataRepository.class);
        kpiB9DetailResultRepository = mock(KpiB9DetailResultRepository.class);
        queryBuilder = mock(QueryBuilder.class);

        service = new KpiB9AnalyticDataServiceImpl(
            anagStationRepository,
            instanceRepository,
            instanceModuleRepository,
            kpiB9AnalyticDataRepository,
            kpiB9DetailResultRepository,
            queryBuilder
        );
    }

    @Test
    void saveAll_shouldSaveEntities() {
        KpiB9AnalyticDataDTO dto = new KpiB9AnalyticDataDTO();
        dto.setInstanceId(1L);
        dto.setInstanceModuleId(2L);
        dto.setStationId(3L);
        dto.setKpiB9DetailResultId(4L);
        dto.setAnalysisDate(LocalDate.now());
        dto.setEvaluationDate(LocalDate.now());
        dto.setTotRes(10L);
        dto.setResOk(8L);
        dto.setResKoReal(1L);
        dto.setResKoValid(1L);

        Instance instance = new Instance();
        instance.setId(1L);
        InstanceModule module = new InstanceModule();
        module.setId(2L);
        AnagStation station = new AnagStation();
        station.setId(3L);
        KpiB9DetailResult detailResult = new KpiB9DetailResult();
        detailResult.setId(4L);

        when(instanceRepository.findById(1L)).thenReturn(Optional.of(instance));
        when(instanceModuleRepository.findById(2L)).thenReturn(Optional.of(module));
        when(anagStationRepository.findById(3L)).thenReturn(Optional.of(station));
        when(kpiB9DetailResultRepository.findById(4L)).thenReturn(Optional.of(detailResult));

        service.saveAll(List.of(dto));

        ArgumentCaptor<KpiB9AnalyticData> captor = ArgumentCaptor.forClass(KpiB9AnalyticData.class);
        verify(kpiB9AnalyticDataRepository).save(captor.capture());
        KpiB9AnalyticData saved = captor.getValue();
        assertThat(saved.getInstance()).isEqualTo(instance);
        assertThat(saved.getInstanceModule()).isEqualTo(module);
        assertThat(saved.getStation()).isEqualTo(station);
        assertThat(saved.getKpiB9DetailResult()).isEqualTo(detailResult);
        assertThat(saved.getTotRes()).isEqualTo(10);
    }

    @Test
    void saveAll_shouldThrowException_whenInstanceMissing() {
        KpiB9AnalyticDataDTO dto = new KpiB9AnalyticDataDTO();
        dto.setInstanceId(1L);
        when(instanceRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () -> service.saveAll(List.of(dto)));
    }

    @Test
    void deleteAllByInstanceModule_shouldCallRepository() {
        when(kpiB9AnalyticDataRepository.deleteAllByInstanceModuleId(2L)).thenReturn(5);

        int deleted = service.deleteAllByInstanceModule(2L);

        assertThat(deleted).isEqualTo(5);
        verify(kpiB9AnalyticDataRepository).deleteAllByInstanceModuleId(2L);
    }

    @Test
    void findByDetailResultId_shouldReturnDtoList() {
        final QKpiB9AnalyticData qData = QKpiB9AnalyticData.kpiB9AnalyticData;
        final QAnagStation qStation = QAnagStation.anagStation;

        @SuppressWarnings("unchecked")
        JPAQuery<Object> query = mock(JPAQuery.class);

        when(queryBuilder.createQuery()).thenReturn(query);

        // Stub chain QueryDSL
        lenient().when(query.from(qData)).thenReturn(query);
        lenient().when(query.leftJoin(qData.station, qStation)).thenReturn(query);
        lenient().when(query.where((Predicate) any())).thenReturn(query);
        lenient().when(query.orderBy((OrderSpecifier<?>[]) any(OrderSpecifier[].class))).thenReturn(query);
        lenient().when(query.select((Expression<Object>) any())).thenReturn(query);

        // Mock fetch()
        KpiB9AnalyticDataDTO dto = new KpiB9AnalyticDataDTO();
        List<KpiB9AnalyticDataDTO> expectedList = List.of(dto);
        lenient().when(query.fetch()).thenReturn((List) expectedList);

        // Chiamata al service
        List<KpiB9AnalyticDataDTO> result = service.findByDetailResultId(1L);

        // Assert
        assertThat(result).isEqualTo(expectedList);
        verify(query).fetch();
    }

    @Test
    void testGetkpiB9AnalyticDataDTO() throws Exception {
        // Arrange
        KpiB9AnalyticData entity = new KpiB9AnalyticData();
        entity.setId(100L);
        Instance instance = new Instance();
        instance.setId(1L);
        entity.setInstance(instance);
        InstanceModule module = new InstanceModule();
        module.setId(2L);
        entity.setInstanceModule(module);
        AnagStation station = new AnagStation();
        station.setId(3L);
        entity.setStation(station);
        KpiB9DetailResult detailResult = new KpiB9DetailResult();
        detailResult.setId(4L);
        entity.setKpiB9DetailResult(detailResult);
        entity.setAnalysisDate(LocalDate.of(2025, 9, 18));
        entity.setEvaluationDate(LocalDate.of(2025, 9, 19));
        entity.setTotRes(10L);
        entity.setResOk(8L);
        entity.setResKoReal(1L);
        entity.setResKoValid(1L);

        // Use reflection to access private static method
        Method method = KpiB9AnalyticDataServiceImpl.class
            .getDeclaredMethod("getkpiB9AnalyticDataDTO", KpiB9AnalyticData.class);
        method.setAccessible(true);

        // Act
        KpiB9AnalyticDataDTO dto = (KpiB9AnalyticDataDTO) method.invoke(null, entity);

        // Assert
        assertThat(dto.getId()).isEqualTo(100L);
        assertThat(dto.getInstanceId()).isEqualTo(1L);
        assertThat(dto.getInstanceModuleId()).isEqualTo(2L);
        assertThat(dto.getStationId()).isEqualTo(3L);
        assertThat(dto.getKpiB9DetailResultId()).isEqualTo(4L);
        assertThat(dto.getAnalysisDate()).isEqualTo(LocalDate.of(2025, 9, 18));
        assertThat(dto.getEvaluationDate()).isEqualTo(LocalDate.of(2025, 9, 19));
        assertThat(dto.getTotRes()).isEqualTo(10);
        assertThat(dto.getResOk()).isEqualTo(8);
        assertThat(dto.getResKoReal()).isEqualTo(1);
        assertThat(dto.getResKoValid()).isEqualTo(1);
    }
}
