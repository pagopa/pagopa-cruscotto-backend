package com.nexigroup.pagopa.cruscotto.service.impl;

import com.nexigroup.pagopa.cruscotto.domain.PagoPaTaxonomyAggregatePosition;
import com.nexigroup.pagopa.cruscotto.service.TaxonomyService;
import com.nexigroup.pagopa.cruscotto.service.dto.PagoPaTaxonomyAggregatePositionDTO;
import com.nexigroup.pagopa.cruscotto.service.dto.PagoPaTaxonomyIncorrectDTO;
import com.nexigroup.pagopa.cruscotto.service.filter.PagoPaTaxonomyAggregatePositionFilter;
import com.nexigroup.pagopa.cruscotto.service.qdsl.QueryBuilder;
import com.querydsl.core.types.Expression;
import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.dsl.EntityPathBase;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
@DisplayName("PagoPaTaxonomyAggregatePositionServiceImpl Tests")
class PagoPaTaxonomyAggregatePositionServiceImplTest {

    private QueryBuilder queryBuilder;
    private TaxonomyService taxonomyService;
    private PagoPaTaxonomyAggregatePositionServiceImpl service;

    @BeforeEach
    void setUp() {
        queryBuilder = mock(QueryBuilder.class);
        taxonomyService = mock(TaxonomyService.class);
        service = new PagoPaTaxonomyAggregatePositionServiceImpl(queryBuilder, taxonomyService);
    }

    @Test
    void testFindAllRecordIntoDayForPartner() {
        String fiscalCode = "ABC123";
        LocalDate day = LocalDate.of(2025, 9, 22);

        // Mock JPAQueryFactory
        JPAQueryFactory mockFactory = mock(JPAQueryFactory.class);
        JPAQuery<PagoPaTaxonomyAggregatePositionDTO> mockQuery = mock(JPAQuery.class);

        when(queryBuilder.createQueryFactory()).thenReturn(mockFactory);
        when(mockFactory.<PagoPaTaxonomyAggregatePositionDTO>select(
            (Expression<PagoPaTaxonomyAggregatePositionDTO>) any()))
            .thenReturn(mockQuery);
        when(mockQuery.from((EntityPathBase<?>) any())).thenReturn(mockQuery);
        when(mockQuery.where((Predicate) any())).thenReturn(mockQuery);
        when(mockQuery.orderBy((com.querydsl.core.types.OrderSpecifier<?>) any())).thenReturn(mockQuery);

        PagoPaTaxonomyAggregatePositionDTO dto = new PagoPaTaxonomyAggregatePositionDTO();
        when(mockQuery.fetch()).thenReturn(List.of(dto));

        List<PagoPaTaxonomyAggregatePositionDTO> result =
            service.findAllRecordIntoDayForPartner(fiscalCode, day);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(dto, result.get(0));
    }

    @Test
    void testFindAllWithFilter() {
        PagoPaTaxonomyAggregatePositionFilter filter = new PagoPaTaxonomyAggregatePositionFilter();
        filter.setCfPartner("ABC123");
        Pageable pageable = PageRequest.of(0, 10);

        JPAQuery<PagoPaTaxonomyAggregatePosition> mockQuery = mock(JPAQuery.class);
        JPAQuery<PagoPaTaxonomyAggregatePositionDTO> mockDtoQuery = mock(JPAQuery.class);

        when(queryBuilder.<PagoPaTaxonomyAggregatePosition>createQuery()).thenReturn(mockQuery);
        when(mockQuery.from((EntityPathBase<PagoPaTaxonomyAggregatePosition>) ArgumentMatchers.any()))
            .thenReturn(mockQuery);
        when(mockQuery.where((Predicate) ArgumentMatchers.any())).thenReturn(mockQuery);
        lenient().when(mockQuery.fetch()).thenReturn(List.of(new PagoPaTaxonomyAggregatePosition()));
        when(mockQuery.<PagoPaTaxonomyAggregatePositionDTO>select(
            (Expression<PagoPaTaxonomyAggregatePositionDTO>) ArgumentMatchers.any()))
            .thenReturn(mockDtoQuery);

        when(mockDtoQuery.offset(anyLong())).thenReturn(mockDtoQuery);
        when(mockDtoQuery.limit(anyLong())).thenReturn(mockDtoQuery);
        when(mockDtoQuery.fetch()).thenReturn(List.of(new PagoPaTaxonomyAggregatePositionDTO()));

        Page<PagoPaTaxonomyAggregatePositionDTO> result = service.findAll(filter, pageable);

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
    }

    @Test
    void testFindIncorrectTaxonomyRecordsForPartnerAndDay_NoValidIdentifiers() {
        String fiscalCode = "ABC123";
        LocalDate day = LocalDate.now();

        when(taxonomyService.getAllUpdatedTakingsIdentifiers()).thenReturn(List.of());

        List<PagoPaTaxonomyIncorrectDTO> result = service.findIncorrectTaxonomyRecordsForPartnerAndDay(fiscalCode, day);

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void testFindIncorrectTaxonomyRecordsForPartnerAndDay_WithInvalidRecords() {
        String fiscalCode = "ABC123";
        LocalDate day = LocalDate.now();

        when(taxonomyService.getAllUpdatedTakingsIdentifiers()).thenReturn(List.of("VALID1", "VALID2"));

        JPAQueryFactory mockFactory = mock(JPAQueryFactory.class);
        JPAQuery<PagoPaTaxonomyIncorrectDTO> mockQuery = mock(JPAQuery.class);

        when(queryBuilder.createQueryFactory()).thenReturn(mockFactory);
        when(mockFactory.<PagoPaTaxonomyIncorrectDTO>select(
            (Expression<PagoPaTaxonomyIncorrectDTO>) ArgumentMatchers.any()))
            .thenReturn(mockQuery);
        when(mockQuery.from((EntityPathBase<?>) ArgumentMatchers.any())).thenReturn(mockQuery);
        when(mockQuery.where((Predicate) ArgumentMatchers.any())).thenReturn(mockQuery);
        when(mockQuery.groupBy((Expression<?>) ArgumentMatchers.any())).thenReturn(mockQuery);
        when(mockQuery.orderBy((com.querydsl.core.types.OrderSpecifier<?>) ArgumentMatchers.any())).thenReturn(mockQuery);

        // Make transferCategory at least 10 characters to avoid StringIndexOutOfBounds
        PagoPaTaxonomyIncorrectDTO invalidRecord = new PagoPaTaxonomyIncorrectDTO();
        invalidRecord.setTransferCategory("INVALID1234");
        when(mockQuery.fetch()).thenReturn(List.of(invalidRecord));

        List<PagoPaTaxonomyIncorrectDTO> result = service.findIncorrectTaxonomyRecordsForPartnerAndDay(fiscalCode, day);

        assertNotNull(result);
        assertFalse(result.isEmpty());
        assertEquals("INVALID1234", result.get(0).getTransferCategory());
    }
}
