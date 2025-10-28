package com.nexigroup.pagopa.cruscotto.service.impl;

import com.nexigroup.pagopa.cruscotto.domain.QPagoPaRecordedTimeout;
import com.nexigroup.pagopa.cruscotto.service.dto.PagoPaRecordedTimeoutDTO;
import com.nexigroup.pagopa.cruscotto.service.filter.PagoPaRecordedTimeoutFilter;
import com.nexigroup.pagopa.cruscotto.service.qdsl.QueryBuilder;
import com.querydsl.core.Tuple;
import com.querydsl.core.types.Expression;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Predicate;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.data.domain.*;

import java.time.LocalDate;
import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@SuppressWarnings({"unchecked", "rawtypes"})
@ExtendWith(MockitoExtension.class)
@DisplayName("PagoPaRecordedTimeoutServiceImpl Tests")
@MockitoSettings(strictness = Strictness.LENIENT)
class PagoPaRecordedTimeoutServiceImplTest {

    @Mock
    private QueryBuilder queryBuilder;

    @Mock
    private JPAQueryFactory queryFactory;

    @Mock
    private JPAQuery tupleQuery;

    @Mock
    private JPAQuery longQuery;

    @Mock
    private JPAQuery objectQuery;

    @InjectMocks
    private PagoPaRecordedTimeoutServiceImpl service;

    @BeforeEach
    void setUp() {
        // No setup required since all dependencies are mocked using @Mock and @InjectMocks annotations.
        // This method is intentionally left empty.
    }

    @Test
    void testFindAllWithFilters() {
        QPagoPaRecordedTimeout q = QPagoPaRecordedTimeout.pagoPaRecordedTimeout;
        Pageable pageable = PageRequest.of(0, 10, Sort.by("station"));

        when(queryBuilder.createQuery()).thenReturn(objectQuery);
        when(objectQuery.from(q)).thenReturn(objectQuery);
        doReturn(objectQuery).when(objectQuery).where((Predicate) any());

        when(objectQuery.select((Expression) any())).thenReturn(objectQuery);
        when(objectQuery.offset(anyLong())).thenReturn(objectQuery);
        when(objectQuery.limit(anyLong())).thenReturn(objectQuery);
        when(objectQuery.orderBy((OrderSpecifier<?>[]) any())).thenReturn(objectQuery);

        PagoPaRecordedTimeoutDTO dto = new PagoPaRecordedTimeoutDTO();
        dto.setStation("stationY");
        when(objectQuery.fetch()).thenReturn(List.of(dto));

        PagoPaRecordedTimeoutFilter filter = new PagoPaRecordedTimeoutFilter();
        filter.setCfPartner("partnerY");

        Page<PagoPaRecordedTimeoutDTO> result = service.findAll(filter, pageable);

        assertThat(result.getTotalElements()).isEqualTo(1);
        assertThat(result.getContent()).extracting("station").contains("stationY");
    }

    @Test
    void testFindAllWithEmptyResults() {
        QPagoPaRecordedTimeout q = QPagoPaRecordedTimeout.pagoPaRecordedTimeout;
        Pageable pageable = PageRequest.of(0, 5);

        when(queryBuilder.createQuery()).thenReturn(objectQuery);
        when(objectQuery.from(q)).thenReturn(objectQuery);

        when(objectQuery.select((Expression) any())).thenReturn(objectQuery);
        when(objectQuery.offset(anyLong())).thenReturn(objectQuery);
        when(objectQuery.limit(anyLong())).thenReturn(objectQuery);
        when(objectQuery.fetch()).thenReturn(Collections.emptyList());

        Page<PagoPaRecordedTimeoutDTO> result = service.findAll(new PagoPaRecordedTimeoutFilter(), pageable);

        assertThat(result.getTotalElements()).isZero();
        assertThat(result.getContent()).isEmpty();
    }

    @Test
    void testSumRecordIntoPeriodForPartnerStationAndMethod() {
        QPagoPaRecordedTimeout q = QPagoPaRecordedTimeout.pagoPaRecordedTimeout;

        when(queryBuilder.createQueryFactory()).thenReturn(queryFactory);
        when(queryFactory.select(q.totReq.sum())).thenReturn(longQuery);
        when(longQuery.from(q)).thenReturn(longQuery);
        doReturn(longQuery).when(longQuery).where((Predicate) any());
        when(longQuery.fetchOne()).thenReturn(42L);

        Long result = service.sumRecordIntoPeriodForPartnerStationAndMethod(
            "partner1", "station1", "methodA", LocalDate.now().minusDays(1), LocalDate.now()
        );

        assertThat(result).isEqualTo(42L);
    }

    @Test
    void testSumRecordReturnsNull() {
        QPagoPaRecordedTimeout q = QPagoPaRecordedTimeout.pagoPaRecordedTimeout;

        when(queryBuilder.createQueryFactory()).thenReturn(queryFactory);
        when(queryFactory.select(q.totReq.sum())).thenReturn(longQuery);
        when(longQuery.from(q)).thenReturn(longQuery);
        doReturn(longQuery).when(longQuery).where((Predicate) any());
        when(longQuery.fetchOne()).thenReturn(null);

        Long result = service.sumRecordIntoPeriodForPartnerStationAndMethod(
            "partner2", "stationX", "methodB", LocalDate.now().minusDays(2), LocalDate.now()
        );

        assertThat(result).isNull();
    }

    @Test
    void testFindAllStationAndMethodIntoPeriodForPartner() {
        QPagoPaRecordedTimeout q = QPagoPaRecordedTimeout.pagoPaRecordedTimeout;

        when(queryBuilder.createQueryFactory()).thenReturn(queryFactory);
        when(queryFactory.select(q.station, q.method)).thenReturn(tupleQuery);
        when(tupleQuery.from(q)).thenReturn(tupleQuery);
        doReturn(tupleQuery).when(tupleQuery).where((Predicate) any());
        when(tupleQuery.groupBy(q.station, q.method)).thenReturn(tupleQuery);

        // fix: support both overloads of orderBy
        when(tupleQuery.orderBy(any(OrderSpecifier.class), any(OrderSpecifier.class)))
            .thenReturn(tupleQuery);
        when(tupleQuery.orderBy((OrderSpecifier<?>[]) any()))
            .thenReturn(tupleQuery);

        Tuple tuple1 = mock(Tuple.class);
        when(tuple1.get(q.station)).thenReturn("station1");
        when(tuple1.get(q.method)).thenReturn("methodA");
        Tuple tuple2 = mock(Tuple.class);
        when(tuple2.get(q.station)).thenReturn("station1");
        when(tuple2.get(q.method)).thenReturn("methodB");

        when(tupleQuery.fetch()).thenReturn(List.of(tuple1, tuple2));

        Map<String, List<String>> result = service.findAllStationAndMethodIntoPeriodForPartner(
            "partnerZ", LocalDate.now().minusDays(3), LocalDate.now()
        );

        assertThat(result).containsKey("station1");
        assertThat(result.get("station1")).containsExactlyInAnyOrder("methodA", "methodB");
    }

    @Test
    void testFindAllRecordIntoDayForPartnerStationAndMethod() {
        QPagoPaRecordedTimeout q = QPagoPaRecordedTimeout.pagoPaRecordedTimeout;

        when(queryBuilder.createQueryFactory()).thenReturn(queryFactory);
        when(queryFactory.select(any(Expression.class))).thenReturn(objectQuery);
        when(objectQuery.from(q)).thenReturn(objectQuery);
        doReturn(objectQuery).when(objectQuery).where((Predicate) any());
        when(objectQuery.orderBy((OrderSpecifier<?>) any())).thenReturn(objectQuery);

        PagoPaRecordedTimeoutDTO dto = new PagoPaRecordedTimeoutDTO();
        dto.setStation("station5");
        dto.setMethod("methodX");
        when(objectQuery.fetch()).thenReturn(List.of(dto));

        List<PagoPaRecordedTimeoutDTO> result = service.findAllRecordIntoDayForPartnerStationAndMethod(
            "partnerX", "station5", "methodX", LocalDate.now()
        );

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getMethod()).isEqualTo("methodX");
    }
}
