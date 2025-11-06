package com.nexigroup.pagopa.cruscotto.service.impl;

import com.querydsl.core.types.EntityPath;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.Predicate;
import com.querydsl.jpa.impl.JPAQuery;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import com.nexigroup.pagopa.cruscotto.domain.*;
import com.nexigroup.pagopa.cruscotto.repository.AuthPermissionRepository;
import com.nexigroup.pagopa.cruscotto.service.dto.AuthPermissionDTO;
import com.nexigroup.pagopa.cruscotto.service.filter.AuthPermissionFilter;
import com.nexigroup.pagopa.cruscotto.service.mapper.AuthPermissionMapper;
import com.nexigroup.pagopa.cruscotto.service.qdsl.QueryBuilder;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.jpa.JPQLQuery;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.data.domain.*;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
@DisplayName("AuthPermissionServiceImpl Tests")
class AuthPermissionServiceImplTest {

    @Mock
    private QueryBuilder queryBuilder;

    @Mock
    private AuthPermissionRepository authPermissionRepository;

    @Mock
    private AuthPermissionMapper authPermissionMapper;

    @Mock
    private JPQLQuery<AuthPermission> jpqlQuery;

    @Mock
    private JPQLQuery<AuthPermissionDTO> jpqlQueryDTO;

    @InjectMocks
    private AuthPermissionServiceImpl authPermissionService;

    @Test
    void testSave() {
        AuthPermissionDTO dto = new AuthPermissionDTO();
        dto.setId(1L);

        AuthPermission entity = new AuthPermission();
        entity.setId(1L);

        when(authPermissionMapper.toEntity(dto)).thenReturn(entity);
        when(authPermissionRepository.save(entity)).thenReturn(entity);
        when(authPermissionMapper.toDto(entity)).thenReturn(dto);

        AuthPermissionDTO result = authPermissionService.save(dto);

        assertThat(result).isEqualTo(dto);
        verify(authPermissionRepository).save(entity);
    }

    @Test
    void testFindOne() {
        AuthPermission entity = new AuthPermission();
        entity.setId(1L);
        AuthPermissionDTO dto = new AuthPermissionDTO();
        dto.setId(1L);

        when(authPermissionRepository.findById(1L)).thenReturn(Optional.of(entity));
        when(authPermissionMapper.toDto(entity)).thenReturn(dto);

        Optional<AuthPermissionDTO> result = authPermissionService.findOne(1L);

        assertThat(result).isPresent().contains(dto);
    }

    @Test
    void testDelete() {
        authPermissionService.delete(1L);
        verify(authPermissionRepository).deleteById(1L);
    }

    @Test
    void testFindAll() {
        AuthPermissionFilter filter = new AuthPermissionFilter();
        Pageable pageable = PageRequest.of(0, 10, Sort.by("id"));

        // Use JPAQuery instead of JPQLQuery
        JPAQuery<AuthPermission> mockQuery = mock(JPAQuery.class);
        JPAQuery<AuthPermissionDTO> mockQueryDTO = mock(JPAQuery.class);

        when(queryBuilder.<AuthPermission>createQuery()).thenReturn(mockQuery);
        when(mockQuery.from(QAuthPermission.authPermission)).thenReturn(mockQuery);
        when(mockQuery.where(any(BooleanBuilder.class))).thenReturn(mockQuery);
        lenient().when(mockQuery.fetch()).thenReturn(List.of(new AuthPermission()));

        // Cast argument to Expression to avoid ambiguous select()
        when(mockQuery.select(any(com.querydsl.core.types.Expression.class))).thenReturn(mockQueryDTO);
        when(mockQueryDTO.offset(anyLong())).thenReturn(mockQueryDTO);
        when(mockQueryDTO.limit(anyLong())).thenReturn(mockQueryDTO);
        when(mockQueryDTO.fetch()).thenReturn(List.of(new AuthPermissionDTO()));

        Page<AuthPermissionDTO> page = authPermissionService.findAll(filter, pageable);

        assertThat(page.getTotalElements()).isEqualTo(1);
        assertThat(page.getContent()).hasSize(1);
    }

    @Test
    void testListAllPermissionSelected() {
        long functionId = 1L;

        JPAQuery<AuthFunctionAuthPermission> mockQuery = mock(JPAQuery.class);
        JPAQuery<Long> mockQueryDTO = mock(JPAQuery.class);

        // Return the mock query from the query builder
        when(queryBuilder.<AuthFunctionAuthPermission>createQuery()).thenReturn(mockQuery);

        // Stub 'from' to return the same mock for fluent chaining
        when(mockQuery.from(QAuthFunctionAuthPermission.authFunctionAuthPermission)).thenReturn(mockQuery);

        // Stub 'join' to return the same mock to continue the chain
        when(mockQuery.join(any(EntityPath.class), any(Path.class))).thenReturn(mockQuery);

        // Stub 'where' to return the same mock
        when(mockQuery.where(any(Predicate.class))).thenReturn(mockQuery);

        // Stub 'select' to return the DTO query
        when(mockQuery.select(any(com.querydsl.core.types.Expression.class))).thenReturn(mockQueryDTO);

        // Finally, stub 'fetch' to return the result list
        when(mockQueryDTO.fetch()).thenReturn(List.of(1L, 2L));

        // Call the service method
        List<Long> ids = authPermissionService.listAllPermissionSelected(functionId);

        // Assert
        assertThat(ids).containsExactly(1L, 2L);
    }

}
