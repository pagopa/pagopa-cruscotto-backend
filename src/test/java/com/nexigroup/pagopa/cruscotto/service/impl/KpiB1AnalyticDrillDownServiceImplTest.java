package com.nexigroup.pagopa.cruscotto.service.impl;

import com.nexigroup.pagopa.cruscotto.domain.KpiB1AnalyticData;
import com.nexigroup.pagopa.cruscotto.domain.KpiB1AnalyticDrillDown;
import com.nexigroup.pagopa.cruscotto.domain.QKpiB1AnalyticDrillDown;
import com.nexigroup.pagopa.cruscotto.repository.KpiB1AnalyticDataRepository;
import com.nexigroup.pagopa.cruscotto.repository.KpiB1AnalyticDrillDownRepository;
import com.nexigroup.pagopa.cruscotto.service.dto.KpiB1AnalyticDrillDownDTO;
import com.nexigroup.pagopa.cruscotto.service.qdsl.QueryBuilder;
import com.querydsl.core.types.Expression;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Predicate;
import com.querydsl.jpa.JPQLQuery;
import com.querydsl.jpa.impl.JPAQuery;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class KpiB1AnalyticDrillDownServiceImplTest {

    @Mock
    private KpiB1AnalyticDrillDownRepository drillDownRepository;

    @Mock
    private KpiB1AnalyticDataRepository analyticDataRepository;

    @Mock
    private QueryBuilder queryBuilder;

    @Mock
    private JPQLQuery<KpiB1AnalyticDrillDownDTO> query;

    @InjectMocks
    private KpiB1AnalyticDrillDownServiceImpl service;

    private KpiB1AnalyticDrillDownDTO buildDTO(Long analyticId) {
        KpiB1AnalyticDrillDownDTO dto = new KpiB1AnalyticDrillDownDTO();
        dto.setId(1L);
        dto.setKpiB1AnalyticDataId(analyticId);
        dto.setDataDate(LocalDate.of(2024, 1, 1));
        dto.setInstitutionFiscalCode("INST01");
        dto.setStationCode("ST01");
        dto.setPartnerFiscalCode("PARTNER01");
        dto.setTransactionCount(123);
        return dto;
    }

    @Test
    void saveAll_shouldSaveEachValidDrillDown() {
        // given
        KpiB1AnalyticDrillDownDTO dto = buildDTO(10L);
        KpiB1AnalyticData data = new KpiB1AnalyticData();
        data.setId(10L);

        when(analyticDataRepository.findById(10L)).thenReturn(Optional.of(data));

        // when
        service.saveAll(List.of(dto));

        // then
        verify(analyticDataRepository, times(1)).findById(10L);
        ArgumentCaptor<KpiB1AnalyticDrillDown> captor = ArgumentCaptor.forClass(KpiB1AnalyticDrillDown.class);
        verify(drillDownRepository, times(1)).save(captor.capture());

        KpiB1AnalyticDrillDown saved = captor.getValue();
        assertEquals(data, saved.getKpiB1AnalyticData());
        assertEquals(dto.getStationCode(), saved.getStationCode());
        assertEquals(dto.getTransactionCount(), saved.getTransactionCount());
    }

    @Test
    void saveAll_shouldThrowWhenAnalyticDataNotFound() {
        // given
        KpiB1AnalyticDrillDownDTO dto = buildDTO(99L);
        when(analyticDataRepository.findById(99L)).thenReturn(Optional.empty());

        // when + then
        assertThrows(IllegalArgumentException.class, () -> service.saveAll(List.of(dto)));
        verify(drillDownRepository, never()).save(any());
    }

    @Test
    void deleteByKpiB1AnalyticDataIds_shouldReturnZeroWhenListIsEmpty() {
        int result = service.deleteByKpiB1AnalyticDataIds(Collections.emptyList());
        assertEquals(0, result);
        verify(drillDownRepository, never()).deleteByKpiB1AnalyticDataIds(any());
    }

    @Test
    void deleteByKpiB1AnalyticDataIds_shouldDelegateToRepository() {
        when(drillDownRepository.deleteByKpiB1AnalyticDataIds(List.of(1L, 2L))).thenReturn(2);

        int result = service.deleteByKpiB1AnalyticDataIds(List.of(1L, 2L));

        assertEquals(2, result);
        verify(drillDownRepository, times(1)).deleteByKpiB1AnalyticDataIds(List.of(1L, 2L));
    }

    @Test
    void findByAnalyticDataId_shouldReturnFetchedResults() {
        // given
        Long analyticId = 5L;
        List<KpiB1AnalyticDrillDownDTO> expected = List.of(buildDTO(analyticId));

        // Creo un mock di JPAQuery senza costruttore reale
        @SuppressWarnings({"rawtypes", "unchecked"})
        JPAQuery queryMock = mock(JPAQuery.class);

        when(queryBuilder.createQuery()).thenReturn(queryMock);

        // Tutti i metodi chain restituiscono lo stesso mock
        when(queryMock.from(any(QKpiB1AnalyticDrillDown.class))).thenReturn(queryMock);
        when(queryMock.where(any(Predicate.class))).thenReturn(queryMock);
        when(queryMock.orderBy(any(OrderSpecifier[].class))).thenReturn(queryMock); // <-- qui il fix
        when(queryMock.select(any(Expression.class))).thenReturn(queryMock);

        when(queryMock.fetch()).thenReturn(expected);

        // when
        List<KpiB1AnalyticDrillDownDTO> result = service.findByAnalyticDataId(analyticId);

        // then
        assertEquals(1, result.size());
        assertEquals(expected.get(0).getStationCode(), result.get(0).getStationCode());

        verify(queryBuilder).createQuery();
        verify(queryMock).fetch();
    }

}
