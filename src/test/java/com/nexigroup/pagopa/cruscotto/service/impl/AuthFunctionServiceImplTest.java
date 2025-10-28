package com.nexigroup.pagopa.cruscotto.service.impl;

import com.nexigroup.pagopa.cruscotto.domain.AuthFunction;
import com.nexigroup.pagopa.cruscotto.domain.AuthPermission;
import com.nexigroup.pagopa.cruscotto.repository.AuthFunctionRepository;
import com.nexigroup.pagopa.cruscotto.repository.AuthPermissionRepository;
import com.nexigroup.pagopa.cruscotto.service.GenericServiceException;
import com.nexigroup.pagopa.cruscotto.service.dto.AuthFunctionDTO;
import com.nexigroup.pagopa.cruscotto.service.dto.AuthPermissionDTO;
import com.nexigroup.pagopa.cruscotto.service.mapper.AuthFunctionMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.*;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("AuthFunctionServiceImplTest Tests")
class AuthFunctionServiceImplTest {

    @Mock
    private AuthPermissionRepository authPermissionRepository;
    @Mock
    private AuthFunctionRepository authFunctionRepository;
    @Mock
    private AuthFunctionMapper authFunctionMapper;

    @InjectMocks
    private AuthFunctionServiceImpl service;

    @Test
    void save_ShouldPersistAndReturnDTO() {
        AuthFunctionDTO dto = new AuthFunctionDTO();
        dto.setNome("funzione1");
        dto.setModulo("modulo1");
        dto.setDescrizione("descrizione1");

        AuthFunction savedEntity = new AuthFunction();
        savedEntity.setId(1L);
        savedEntity.setNome(dto.getNome());
        savedEntity.setModulo(dto.getModulo());
        savedEntity.setDescrizione(dto.getDescrizione());

        when(authFunctionRepository.save(any(AuthFunction.class))).thenReturn(savedEntity);
        when(authFunctionMapper.toDto(savedEntity)).thenReturn(dto);

        AuthFunctionDTO result = service.save(dto);

        assertThat(result).isNotNull();
        assertThat(result.getNome()).isEqualTo("funzione1");
        verify(authFunctionRepository).save(any(AuthFunction.class));
    }

    @Test
    void update_ShouldUpdateExistingEntity() {
        AuthFunctionDTO dto = new AuthFunctionDTO();
        dto.setId(1L);
        dto.setNome("updated");
        dto.setModulo("m1");
        dto.setDescrizione("d1");

        AuthFunction entity = new AuthFunction();
        entity.setId(1L);

        when(authFunctionRepository.findById(1L)).thenReturn(Optional.of(entity));
        when(authFunctionMapper.toDto(any(AuthFunction.class))).thenReturn(dto);

        Optional<AuthFunctionDTO> result = service.update(dto);

        assertThat(result).isPresent();
        assertThat(result.orElseThrow().getNome()).isEqualTo("updated");
        verify(authFunctionRepository).findById(1L);
    }

    @Test
    void update_ShouldReturnEmpty_WhenEntityNotFound() {
        AuthFunctionDTO dto = new AuthFunctionDTO();
        dto.setId(99L);

        when(authFunctionRepository.findById(99L)).thenReturn(Optional.empty());

        Optional<AuthFunctionDTO> result = service.update(dto);

        assertThat(result).isEmpty();
    }

    @Test
    void findOneWithEagerRelationships_ShouldReturnDTO_WhenEntityExists() {
        AuthFunction entity = new AuthFunction();
        entity.setId(1L);
        entity.setNome("f1");

        AuthFunctionDTO dto = new AuthFunctionDTO();
        dto.setId(1L);
        dto.setNome("f1");

        // Fix: mock findOneWithEagerRelationships, not findById
        when(authFunctionRepository.findOneWithEagerRelationships(1L)).thenReturn(Optional.of(entity));
        when(authFunctionMapper.toDto(entity)).thenReturn(dto);

        Optional<AuthFunctionDTO> result = service.findOneWithEagerRelationships(1L);

        assertThat(result).isPresent();
        assertThat(result.orElseThrow().getNome()).isEqualTo("f1");
    }


    @Test
    void delete_ShouldCallRepository() {
        service.delete(1L);
        verify(authFunctionRepository).deleteById(1L);
    }

    @Test
    void associaPermesso_ShouldAddPermission() {
        AuthFunction function = new AuthFunction();
        function.setId(1L);
        function.setAuthPermissions(new HashSet<>());

        AuthPermission permission = new AuthPermission();
        permission.setId(10L);

        AuthPermissionDTO dto = new AuthPermissionDTO();
        dto.setId(10L);

        when(authFunctionRepository.findById(1L)).thenReturn(Optional.of(function));
        when(authPermissionRepository.findById(10L)).thenReturn(Optional.of(permission));

        service.associaPermesso(1L, new AuthPermissionDTO[]{dto});

        assertThat(function.getAuthPermissions()).contains(permission);
        verify(authFunctionRepository, atLeastOnce()).save(function);
    }

    @Test
    void associaPermesso_ShouldThrow_WhenFunctionNotFound() {
        when(authFunctionRepository.findById(99L)).thenReturn(Optional.empty());

        AuthPermissionDTO dto = new AuthPermissionDTO();
        dto.setId(10L);

        assertThatThrownBy(() -> service.associaPermesso(99L, new AuthPermissionDTO[]{dto}))
            .isInstanceOf(GenericServiceException.class)
            .hasMessageContaining("Function not exist");
    }

    @Test
    void rimuoviAssociazionePermesso_ShouldRemovePermission() {
        AuthFunction function = new AuthFunction();
        function.setId(1L);
        AuthPermission permission = new AuthPermission();
        permission.setId(10L);
        function.setAuthPermissions(new HashSet<>(List.of(permission)));

        when(authFunctionRepository.findById(1L)).thenReturn(Optional.of(function));
        when(authPermissionRepository.findById(10L)).thenReturn(Optional.of(permission));

        service.rimuoviAssociazionePermesso(1L, 10L);

        assertThat(function.getAuthPermissions()).doesNotContain(permission);
        verify(authFunctionRepository).save(function);
    }

    @Test
    void rimuoviAssociazionePermesso_ShouldThrow_WhenFunctionNotFound() {
        when(authFunctionRepository.findById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.rimuoviAssociazionePermesso(1L, 10L))
            .isInstanceOf(GenericServiceException.class)
            .hasMessageContaining("Function not exist");
    }

    @Test
    void rimuoviAssociazionePermesso_ShouldThrow_WhenPermissionNotFound() {
        AuthFunction function = new AuthFunction();
        function.setId(1L);
        when(authFunctionRepository.findById(1L)).thenReturn(Optional.of(function));
        when(authPermissionRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.rimuoviAssociazionePermesso(1L, 99L))
            .isInstanceOf(GenericServiceException.class)
            .hasMessageContaining("Permesso not exist");
    }
}
