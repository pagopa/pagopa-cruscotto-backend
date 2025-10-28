package com.nexigroup.pagopa.cruscotto.service.impl;

import com.nexigroup.pagopa.cruscotto.domain.PagoPaPaymentReceipt;
import com.nexigroup.pagopa.cruscotto.domain.QPagoPaPaymentReceipt;
import com.nexigroup.pagopa.cruscotto.service.dto.PagoPaPaymentReceiptDTO;
import com.nexigroup.pagopa.cruscotto.service.filter.PagoPaPaymentReceiptFilter;
import com.nexigroup.pagopa.cruscotto.service.qdsl.QueryBuilder;
import com.querydsl.core.types.EntityPath;
import com.querydsl.core.types.Expression;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Predicate;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.mock;

@ExtendWith(MockitoExtension.class)
@DisplayName("PagoPaPaymentReceiptServiceImpl Tests")
class PagoPaPaymentReceiptServiceImplTest {

    @Mock
    private QueryBuilder queryBuilder;

    @Mock
    private JPAQueryFactory queryFactory;

    @Mock
    private JPAQuery<PagoPaPaymentReceipt> receiptQuery;

    @InjectMocks
    private PagoPaPaymentReceiptServiceImpl service;

    @BeforeEach
    void setup() {
        // Lenient stubs to prevent unnecessary stubbing exceptions
        lenient().doReturn(queryFactory).when(queryBuilder).createQueryFactory();
        lenient().doReturn(receiptQuery).when(queryBuilder).createQuery();

        lenient().doReturn(receiptQuery).when(receiptQuery).from(any(EntityPath.class));
        lenient().doReturn(receiptQuery).when(receiptQuery).where(any(Predicate.class));
        lenient().doReturn(receiptQuery).when(receiptQuery).select(any(Expression.class));
        lenient().doReturn(receiptQuery).when(receiptQuery).orderBy(any(OrderSpecifier.class));
        lenient().doReturn(receiptQuery).when(receiptQuery).offset(anyLong());
        lenient().doReturn(receiptQuery).when(receiptQuery).limit(anyLong());
    }

    @Test
    @DisplayName("findAllStationIntoPeriodForPartner: returns grouped stations")
    void findAllStationIntoPeriodForPartner_returnsStations() {
        JPAQuery<String> stationQuery = mock(JPAQuery.class);

        lenient().doReturn(stationQuery).when(queryFactory).select(any(Expression.class));

        lenient().doReturn(stationQuery).when(stationQuery).from(any(EntityPath.class));
        lenient().doReturn(stationQuery).when(stationQuery).where(any(Predicate.class));
        lenient().doReturn(stationQuery).when(stationQuery).groupBy(any(Expression.class));
        lenient().doReturn(stationQuery).when(stationQuery).orderBy(any(OrderSpecifier.class));

        lenient().doReturn(Arrays.asList("ST01", "ST02")).when(stationQuery).fetch();

        List<String> results = service.findAllStationIntoPeriodForPartner(
            "PARTNER01",
            LocalDate.of(2023, 1, 1),
            LocalDate.of(2023, 1, 2)
        );

        assertThat(results).containsExactly("ST01", "ST02");
    }

    @Test
    @DisplayName("findAllRecordIntoDayForPartnerAndStation: returns DTOs")
    void findAllRecordIntoDayForPartnerAndStation_returnsDTOs() {
        JPAQuery<PagoPaPaymentReceiptDTO> dtoQuery = mock(JPAQuery.class);
        lenient().doReturn(dtoQuery).when(queryFactory).select(any(Expression.class));

        lenient().doReturn(dtoQuery).when(dtoQuery).from(any(EntityPath.class));
        lenient().doReturn(dtoQuery).when(dtoQuery).where(any(Predicate.class));
        lenient().doReturn(dtoQuery).when(dtoQuery).orderBy(any(OrderSpecifier.class));

        PagoPaPaymentReceiptDTO dto = new PagoPaPaymentReceiptDTO();
        dto.setId(1L);
        dto.setCfPartner("PART");
        dto.setStation("ST1");

        lenient().doReturn(Collections.singletonList(dto)).when(dtoQuery).fetch();

        List<PagoPaPaymentReceiptDTO> results = service.findAllRecordIntoDayForPartnerAndStation(
            "PART",
            "ST1",
            LocalDate.of(2023, 2, 1)
        );

        assertThat(results).hasSize(1);
        assertThat(results.get(0).getId()).isEqualTo(1L);
        assertThat(results.get(0).getStation()).isEqualTo("ST1");
    }

    @Test
    @DisplayName("findAll: with descending sort also returns Page correctly")
    void findAll_withDescendingSort_returnsPage() {
        QPagoPaPaymentReceipt q = QPagoPaPaymentReceipt.pagoPaPaymentReceipt;

        // Mock count query
        JPAQuery<Long> countQuery = mock(JPAQuery.class);
        lenient().doReturn(countQuery).when(receiptQuery).select(q.id);
        lenient().doReturn(Arrays.asList(1L)).when(countQuery).fetch();

        // Mock DTO query
        JPAQuery<PagoPaPaymentReceiptDTO> dtoQuery = mock(JPAQuery.class);
        lenient().doReturn(dtoQuery).when(receiptQuery).select(any(Expression.class));
        lenient().doReturn(dtoQuery).when(dtoQuery).offset(anyLong());
        lenient().doReturn(dtoQuery).when(dtoQuery).limit(anyLong());
        lenient().doReturn(dtoQuery).when(dtoQuery).orderBy(any(OrderSpecifier.class));

        PagoPaPaymentReceiptDTO dto = new PagoPaPaymentReceiptDTO();
        dto.setId(200L);
        dto.setCfPartner("PX");
        dto.setStation("SX");
        lenient().doReturn(Collections.singletonList(dto)).when(dtoQuery).fetch();

        PagoPaPaymentReceiptFilter filter = new PagoPaPaymentReceiptFilter();
        filter.setCfPartner("PX");
        filter.setStation("SX");

        // ⬅️ now use descending sort to hit the other branch
        Pageable pageable = PageRequest.of(0, 10, Sort.by(Sort.Order.desc("startDate")));

        Page<PagoPaPaymentReceiptDTO> page = service.findAll(filter, pageable);

        assertThat(page.getTotalElements()).isEqualTo(1L);
        assertThat(page.getContent()).hasSize(1);
        assertThat(page.getContent().get(0).getId()).isEqualTo(200L);
    }


    @Test
    @DisplayName("findAll: no results returns empty Page")
    void findAll_withEmptyFilter_returnsEmptyPage() {
        // Mock the query returned by select
        JPAQuery<PagoPaPaymentReceiptDTO> dtoQuery = mock(JPAQuery.class);

        lenient().doReturn(receiptQuery).when(receiptQuery).from(any(EntityPath.class));
        lenient().doReturn(dtoQuery).when(receiptQuery).select(any(Expression.class));
        lenient().doReturn(Collections.emptyList()).when(dtoQuery).fetch();

        Page<PagoPaPaymentReceiptDTO> page =
            service.findAll(new PagoPaPaymentReceiptFilter(), PageRequest.of(0, 5));

        assertThat(page).isEmpty();
    }

}
