package com.nexigroup.pagopa.cruscotto.service.impl;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.nexigroup.pagopa.cruscotto.domain.AuthFunction;
import com.nexigroup.pagopa.cruscotto.domain.AuthGroup;
import com.nexigroup.pagopa.cruscotto.repository.AuthFunctionRepository;
import com.nexigroup.pagopa.cruscotto.repository.AuthGroupRepository;
import com.nexigroup.pagopa.cruscotto.service.bean.AuthGroupUpdateRequestBean;
import com.nexigroup.pagopa.cruscotto.service.dto.AuthFunctionDTO;
import com.nexigroup.pagopa.cruscotto.service.dto.AuthGroupDTO;
import com.nexigroup.pagopa.cruscotto.service.mapper.AuthGroupMapper;
import com.nexigroup.pagopa.cruscotto.service.qdsl.QueryBuilder;
import java.util.Optional;

import com.querydsl.core.types.Path;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
@DisplayName("AuthGroupServiceImpl Tests")
class AuthGroupServiceImplTest {

    @Mock private AuthFunctionRepository authFunctionRepository;
    @Mock private AuthGroupRepository authGroupRepository;
    @Mock private AuthGroupMapper authGroupMapper;
    @Mock private QueryBuilder queryBuilder;

    @InjectMocks
    private AuthGroupServiceImpl authGroupService;

    @Test
    void testSaveAuthGroup() {
        AuthGroupDTO dto = new AuthGroupDTO();
        dto.setNome("Test");
        dto.setDescrizione("Desc");

        AuthGroup savedEntity = new AuthGroup();
        savedEntity.setNome("Test");
        savedEntity.setDescrizione("Desc");

        when(authGroupRepository.getMaxLivelloVisibilita()).thenReturn(0);
        when(authGroupRepository.save(any(AuthGroup.class))).thenReturn(savedEntity);
        when(authGroupMapper.toDto(any(AuthGroup.class))).thenReturn(dto);

        AuthGroupDTO result = authGroupService.save(dto);

        assertNotNull(result);
        assertEquals("Test", result.getNome());
        verify(authGroupRepository).save(any(AuthGroup.class));
    }

    @Test
    void testUpdateAuthGroup() {
        AuthGroupDTO dto = new AuthGroupDTO();
        dto.setId(1L);
        dto.setNome("Updated");
        dto.setDescrizione("Updated Desc");

        AuthGroup existing = new AuthGroup();
        existing.setId(1L);

        when(authGroupRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(authGroupMapper.toDto(any(AuthGroup.class))).thenReturn(dto);

        Optional<AuthGroupDTO> result = authGroupService.update(dto);

        assertTrue(result.isPresent());
        assertEquals("Updated", result.orElseThrow().getNome());
    }

    @Test
    void testDeleteAuthGroup() {
        authGroupService.delete(1L);
        verify(authGroupRepository).deleteById(1L);
    }

    @Test
    void testAssociateFunctions() {
        AuthGroup group = new AuthGroup();
        group.setId(1L);

        AuthFunction function = new AuthFunction();
        function.setId(2L);

        when(authGroupRepository.findById(1L)).thenReturn(Optional.of(group));
        when(authFunctionRepository.findById(2L)).thenReturn(Optional.of(function));

        AuthFunctionDTO dto = new AuthFunctionDTO();
        dto.setId(2L);

        authGroupService.associaFunzioni(1L, new AuthFunctionDTO[]{dto});

        assertTrue(group.getAuthFunctions().contains(function));
        verify(authGroupRepository, atLeastOnce()).save(group);
    }

    @Test
    void testRemoveFunctionAssociation() {
        AuthGroup group = new AuthGroup();
        group.setId(1L);

        AuthFunction function = new AuthFunction();
        function.setId(2L);
        group.getAuthFunctions().add(function);

        when(authGroupRepository.findById(1L)).thenReturn(Optional.of(group));
        when(authFunctionRepository.findById(2L)).thenReturn(Optional.of(function));

        authGroupService.rimuoviAssociazioneFunzione(1L, 2L);

        assertFalse(group.getAuthFunctions().contains(function));
        verify(authGroupRepository).save(group);
    }

    @Test
    void testUpdateGroupVisibilityLevel() {
        AuthGroupUpdateRequestBean request = new AuthGroupUpdateRequestBean();
        request.setId("1");
        request.setLivelloVisibilita("5");

        var queryFactory = mock(com.querydsl.jpa.impl.JPAQueryFactory.class);
        var updateClause = mock(com.querydsl.jpa.impl.JPAUpdateClause.class);

        when(queryBuilder.createQueryFactory()).thenReturn(queryFactory);
        when(queryFactory.update(any())).thenReturn(updateClause);
        when(updateClause.set((Path<Object>) any(), (Object) any())).thenReturn(updateClause);
        when(updateClause.where(any())).thenReturn(updateClause);

        authGroupService.aggiornaLivelloVisibilitaGruppi(new AuthGroupUpdateRequestBean[]{request});

        verify(queryFactory).update(any());
    }
}
