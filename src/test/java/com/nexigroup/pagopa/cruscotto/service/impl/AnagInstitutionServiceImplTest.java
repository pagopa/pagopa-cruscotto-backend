package com.nexigroup.pagopa.cruscotto.service.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.util.Collections;
import java.util.List;

import com.nexigroup.pagopa.cruscotto.job.cache.CreditorInstitution;
import com.nexigroup.pagopa.cruscotto.repository.AnagInstitutionRepository;
import com.nexigroup.pagopa.cruscotto.service.dto.AnagInstitutionDTO;
import com.nexigroup.pagopa.cruscotto.service.dto.InstitutionIdentificationDTO;
import com.nexigroup.pagopa.cruscotto.service.filter.AnagInstitutionFilter;
import com.nexigroup.pagopa.cruscotto.service.filter.InstitutionFilter;
import com.nexigroup.pagopa.cruscotto.domain.AnagInstitution;
import com.nexigroup.pagopa.cruscotto.service.qdsl.QueryBuilder;

import com.querydsl.core.types.EntityPath;
import com.querydsl.core.types.Expression;
import com.querydsl.core.types.Predicate;
import com.querydsl.jpa.impl.JPAQuery;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;

import com.querydsl.core.BooleanBuilder;

@ExtendWith(MockitoExtension.class)
@DisplayName("AnagInstitutionServiceImpl Tests")
class AnagInstitutionServiceImplTest {

    @InjectMocks
    private AnagInstitutionServiceImpl service;

    @Mock
    private AnagInstitutionRepository repository;

    @Mock
    private QueryBuilder queryBuilder;

    @Test
    void testFindByInstitutionCode() {
        AnagInstitution institution = new AnagInstitution();
        institution.setFiscalCode("123");
        when(repository.findByFiscalCode("123")).thenReturn(institution);

        AnagInstitution result = service.findByInstitutionCode("123");

        assertThat(result).isNotNull();
        assertThat(result.getFiscalCode()).isEqualTo("123");
    }

    @Test
    void testSaveAllNewInstitutions() {
        CreditorInstitution ci = new CreditorInstitution();
        ci.setCreditorInstitutionCode("123");
        ci.setBusinessName("Test Business");
        ci.setEnabled(true);

        // removed unnecessary stubbing of repository.findOne(...)
        when(repository.saveAll(any())).thenReturn(Collections.emptyList());

        service.saveAll(Collections.singletonList(ci));

        verify(repository, times(1)).saveAll(any());
    }

    @Test
    void testFindAllInstitutionFilter() {
        InstitutionFilter filter = new InstitutionFilter();
        filter.setFiscalCode("123");
        filter.setName("Name");

        Pageable pageable = PageRequest.of(0, 10);

        // Main query mock (must match return type of queryBuilder.createQuery())
        @SuppressWarnings({"rawtypes", "unchecked"})
        JPAQuery mainQuery = mock(JPAQuery.class);

        // Selected query mock
        @SuppressWarnings({"rawtypes", "unchecked"})
        JPAQuery selectedQuery = mock(JPAQuery.class);

        // queryBuilder returns main query
        when(queryBuilder.createQuery()).thenReturn(mainQuery);

        // from / where chain
        when(mainQuery.from((EntityPath<?>) any())).thenReturn(mainQuery);
        when(mainQuery.where(any(BooleanBuilder.class))).thenReturn(mainQuery);

        // fetchCount
        lenient().when(mainQuery.fetch()).thenReturn(List.of(new Object()));

        // select returns selected query
        when(mainQuery.select(any(Expression.class))).thenReturn(selectedQuery);

        // pagination
        when(selectedQuery.offset(anyLong())).thenReturn(selectedQuery);
        when(selectedQuery.limit(anyLong())).thenReturn(selectedQuery);

        // fetch returns sample DTO
        when(selectedQuery.fetch()).thenReturn(Collections.singletonList(new InstitutionIdentificationDTO()));

        // call service
        Page<InstitutionIdentificationDTO> result = service.findAll(filter, pageable);

        // assertions
        assertThat(result.getTotalElements()).isEqualTo(1L);
        assertThat(result.getContent()).hasSize(1);
    }


    @Test
    void testFindAllAnagInstitutionFilter() {
        AnagInstitutionFilter filter = new AnagInstitutionFilter();
        filter.setInstitutionId(1L);
        Pageable pageable = PageRequest.of(0, 10);

        @SuppressWarnings({"rawtypes","unchecked"})
        JPAQuery mainQuery = mock(JPAQuery.class);
        @SuppressWarnings({"rawtypes","unchecked"})
        JPAQuery selectedQuery = mock(JPAQuery.class);
        @SuppressWarnings({"rawtypes","unchecked"})
        JPAQuery joinMock = mock(JPAQuery.class);

        // queryBuilder returns main query
        when(queryBuilder.createQuery()).thenReturn(mainQuery);

        // from / where chain
        when(mainQuery.from(any(EntityPath.class))).thenReturn(mainQuery);
        when(mainQuery.where(any(BooleanBuilder.class))).thenReturn(mainQuery);

        // leftJoin().on() chain
        when(mainQuery.leftJoin(any(EntityPath.class))).thenReturn(joinMock);
        when(joinMock.on(any(Predicate.class))).thenReturn(mainQuery);

        // fetchCount
        lenient().when(mainQuery.fetch()).thenReturn(List.of(new Object()));

        // select returns selected query
        when(mainQuery.select(any(Expression.class))).thenReturn(selectedQuery);

        // pagination
        when(selectedQuery.offset(anyLong())).thenReturn(selectedQuery);
        when(selectedQuery.limit(anyLong())).thenReturn(selectedQuery);

        // fetch sample DTO
        when(selectedQuery.fetch()).thenReturn(Collections.singletonList(new AnagInstitutionDTO()));

        // call service
        Page<AnagInstitutionDTO> result = service.findAll(filter, pageable);

        assertThat(result.getTotalElements()).isEqualTo(1L);
        assertThat(result.getContent()).hasSize(1);
    }
}
