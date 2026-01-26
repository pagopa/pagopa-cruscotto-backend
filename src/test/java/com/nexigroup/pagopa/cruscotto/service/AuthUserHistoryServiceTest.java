package com.nexigroup.pagopa.cruscotto.service;

import com.nexigroup.pagopa.cruscotto.domain.AuthUserHistory;
import com.nexigroup.pagopa.cruscotto.service.qdsl.QueryBuilder;
import com.querydsl.core.types.EntityPath;
import com.querydsl.core.types.Expression;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Predicate;
import com.querydsl.jpa.impl.JPAQuery;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AuthUserHistoryServiceTest {

    private QueryBuilder queryBuilder;
    private JPAQuery<AuthUserHistory> jpqlQuery;
    private AuthUserHistoryService service;

    @BeforeEach
    void setUp() {
        queryBuilder = mock(QueryBuilder.class);

        // IMPORTANT: raw JPAQuery mock
        jpqlQuery = mock(JPAQuery.class);

        service = new AuthUserHistoryService(queryBuilder);

        // Must return raw JPAQuery since createQuery() is raw
        when(queryBuilder.createQuery()).thenReturn((JPAQuery) jpqlQuery);

        when(jpqlQuery.from(any(EntityPath.class))).thenReturn(jpqlQuery);
        when(jpqlQuery.select(any(Expression.class))).thenReturn(jpqlQuery);
        when(jpqlQuery.where((Predicate) any())).thenReturn(jpqlQuery);
        when(jpqlQuery.orderBy(any(OrderSpecifier.class))).thenReturn(jpqlQuery);
        when(jpqlQuery.limit(anyLong())).thenReturn(jpqlQuery);
    }

    @Test
    void testGetOldPassword_returnsPasswords() {
        AuthUserHistory h1 = new AuthUserHistory();
        h1.setPassword("pass1");

        AuthUserHistory h2 = new AuthUserHistory();
        h2.setPassword("pass2");

        when(jpqlQuery.fetch()).thenReturn(List.of(h1, h2));

        String[] result = service.getOldPassword(1L, 2);

        assertArrayEquals(new String[]{"pass1", "pass2"}, result);
    }
}
