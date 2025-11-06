package com.nexigroup.pagopa.cruscotto.service.impl;

import com.querydsl.core.types.*;
import com.querydsl.jpa.impl.JPAQuery;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

import com.nexigroup.pagopa.cruscotto.domain.*;
import com.nexigroup.pagopa.cruscotto.repository.*;
import com.nexigroup.pagopa.cruscotto.service.dto.KpiB2AnalyticDataDTO;
import com.nexigroup.pagopa.cruscotto.service.qdsl.QueryBuilder;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
@DisplayName("TaxonomyServiceImpl Tests")
class KpiB2AnalyticDataServiceImplTest {

    @Mock private AnagStationRepository anagStationRepository;
    @Mock private InstanceRepository instanceRepository;
    @Mock private InstanceModuleRepository instanceModuleRepository;
    @Mock private KpiB2AnalyticDataRepository kpiB2AnalyticDataRepository;
    @Mock private KpiB2DetailResultRepository kpiB2DetailResultRepository;
    @Mock private QueryBuilder queryBuilder;

    @InjectMocks private KpiB2AnalyticDataServiceImpl service;
    @Test
    void testSaveAll() {
        // Prepare DTO and mocked entities
        KpiB2AnalyticDataDTO dto = new KpiB2AnalyticDataDTO();
        dto.setInstanceId(1L);
        dto.setInstanceModuleId(2L);
        dto.setStationId(3L);
        dto.setKpiB2DetailResultId(4L);
        dto.setAnalysisDate(LocalDate.now());

        Instance instance = new Instance();
        instance.setId(1L);
        InstanceModule module = new InstanceModule();
        module.setId(2L);
        AnagStation station = new AnagStation();
        station.setId(3L);
        KpiB2DetailResult detailResult = new KpiB2DetailResult();
        detailResult.setId(4L);

        when(instanceRepository.findById(1L)).thenReturn(Optional.of(instance));
        when(instanceModuleRepository.findById(2L)).thenReturn(Optional.of(module));
        when(anagStationRepository.findById(3L)).thenReturn(Optional.of(station));
        when(kpiB2DetailResultRepository.findById(4L)).thenReturn(Optional.of(detailResult));

        service.saveAll(List.of(dto));

        verify(kpiB2AnalyticDataRepository, times(1)).save(any());
    }

    @Test
    void testDeleteAllByInstanceModule() {
        when(kpiB2AnalyticDataRepository.deleteAllByInstanceModuleId(2L)).thenReturn(5);

        int deleted = service.deleteAllByInstanceModule(2L);

        assertThat(deleted).isEqualTo(5);
        verify(kpiB2AnalyticDataRepository).deleteAllByInstanceModuleId(2L);
    }

    @SuppressWarnings({"unchecked","rawtypes"})
    @Test
    void testFindByDetailResultId() {
        JPAQuery mockQuery = mock(JPAQuery.class);

        // Force generic type for createQuery()
        Mockito.<JPAQuery<KpiB2AnalyticDataDTO>>when(
            queryBuilder.<KpiB2AnalyticDataDTO>createQuery()
        ).thenReturn(mockQuery);

        // Cast arguments to resolve overloaded method ambiguity
        when(mockQuery.from((EntityPath<?>) any(EntityPath.class))).thenReturn(mockQuery);
        when(mockQuery.leftJoin((EntityPath<?>) any(EntityPath.class), (Path<?>) any(Path.class))).thenReturn(mockQuery);
        when(mockQuery.where((Predicate) any(Predicate.class))).thenReturn(mockQuery);

        // Risolvi varargs problem
        lenient().when(mockQuery.orderBy((OrderSpecifier<?>[]) any(OrderSpecifier[].class))).thenReturn(mockQuery);

        JPAQuery mockQueryDto = mock(JPAQuery.class);
        Mockito.<JPAQuery<KpiB2AnalyticDataDTO>>when(
            mockQuery.<KpiB2AnalyticDataDTO>select((Expression<KpiB2AnalyticDataDTO>) any())
        ).thenReturn(mockQueryDto);

        KpiB2AnalyticDataDTO dto = new KpiB2AnalyticDataDTO();
        when(mockQueryDto.fetch()).thenReturn(List.of(dto));

        List<KpiB2AnalyticDataDTO> result = service.findByDetailResultId(10L);

        assertThat(result).hasSize(1).contains(dto);
    }

}
