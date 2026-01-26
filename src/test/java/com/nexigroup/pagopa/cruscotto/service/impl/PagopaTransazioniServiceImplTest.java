package com.nexigroup.pagopa.cruscotto.service.impl;

import com.nexigroup.pagopa.cruscotto.domain.QPagopaTransaction;
import com.nexigroup.pagopa.cruscotto.service.dto.PagopaTransactionDTO;
import com.nexigroup.pagopa.cruscotto.service.qdsl.QueryBuilder;
import com.querydsl.core.types.Expression;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.dsl.NumberExpression;
import com.querydsl.jpa.impl.JPAQuery;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PagopaTransazioniServiceImplTest {

    @Mock
    private QueryBuilder queryBuilder;

    @Mock
    private JPAQuery dtoQuery; // raw type

    @Mock
    private JPAQuery<Long> longQuery;

    @Mock
    private JPAQuery<Integer> intQuery;

    private PagopaTransazioniServiceImpl service;

    @BeforeEach
    void setUp() {
        service = new PagopaTransazioniServiceImpl(null, queryBuilder);
    }

    @Test
    void testConvertToDTOAndEntity() {
        var entity = new com.nexigroup.pagopa.cruscotto.domain.PagopaTransaction();
        entity.setId(1L);
        entity.setCfPartner("P1");
        entity.setCfInstitution("I1");
        entity.setDate(LocalDate.now());
        entity.setStation("S1");
        entity.setTransactionTotal(50);

        PagopaTransactionDTO dto = PagopaTransazioniServiceImpl.convertToDTO(entity);
        assertEquals(entity.getId(), dto.getId());
        assertEquals(entity.getCfPartner(), dto.getCfPartner());

        var convertedEntity = PagopaTransazioniServiceImpl.convertToEntity(dto);
        assertEquals(dto.getId(), convertedEntity.getId());
        assertEquals(dto.getCfInstitution(), convertedEntity.getCfInstitution());
    }

    @Test
    void testFindAllRecordIntoPeriodForPartner() {
        when(queryBuilder.createQuery()).thenReturn(dtoQuery);
        when(dtoQuery.from(any(QPagopaTransaction.class))).thenReturn(dtoQuery);
        when(dtoQuery.where((Predicate) any())).thenReturn(dtoQuery);
        doReturn(dtoQuery).when(dtoQuery).orderBy(any(OrderSpecifier[].class)); // fix varargs
        when(dtoQuery.select(any(Expression.class))).thenReturn(dtoQuery);
        when(dtoQuery.fetch()).thenReturn(List.of(new PagopaTransactionDTO()));

        List<PagopaTransactionDTO> result = service.findAllRecordIntoPeriodForPartner(
            "P1", LocalDate.now().minusDays(1), LocalDate.now());

        assertEquals(1, result.size());
    }

    @Test
    void testFindAllRecordIntoPeriodForEntity() {
        when(queryBuilder.createQuery()).thenReturn(dtoQuery);
        when(dtoQuery.from(any(QPagopaTransaction.class))).thenReturn(dtoQuery);
        when(dtoQuery.where((Predicate) any())).thenReturn(dtoQuery);
        doReturn(dtoQuery).when(dtoQuery).orderBy(any(OrderSpecifier[].class));
        when(dtoQuery.select(any(Expression.class))).thenReturn(dtoQuery);
        when(dtoQuery.fetch()).thenReturn(List.of(new PagopaTransactionDTO()));

        List<PagopaTransactionDTO> result = service.findAllRecordIntoPeriodForEntity(
            "I1", LocalDate.now().minusDays(1), LocalDate.now());

        assertEquals(1, result.size());
    }

    @Test
    void testCountUniqueEntitiesForPartnerInPeriod() {
        when(queryBuilder.createQuery()).thenReturn((JPAQuery) longQuery);
        when(longQuery.from(any(QPagopaTransaction.class))).thenReturn(longQuery);
        when(longQuery.where((Predicate) any())).thenReturn(longQuery);
        when(longQuery.select(any(NumberExpression.class))).thenReturn(longQuery);
        when(longQuery.fetchOne()).thenReturn(5L);

        long result = service.countUniqueEntitiesForPartnerInPeriod(
            "P1", LocalDate.now().minusDays(1), LocalDate.now());

        assertEquals(5L, result);
    }

    @Test
    void testSumTotalTransactionsForPartnerInPeriod() {
        when(queryBuilder.createQuery()).thenReturn((JPAQuery) intQuery);
        when(intQuery.from(any(QPagopaTransaction.class))).thenReturn(intQuery);
        when(intQuery.where((Predicate) any())).thenReturn(intQuery);
        when(intQuery.select(any(NumberExpression.class))).thenReturn(intQuery);
        when(intQuery.fetchOne()).thenReturn(100);

        long result = service.sumTotalTransactionsForPartnerInPeriod(
            "P1", LocalDate.now().minusDays(1), LocalDate.now());

        assertEquals(100L, result);
    }
}
