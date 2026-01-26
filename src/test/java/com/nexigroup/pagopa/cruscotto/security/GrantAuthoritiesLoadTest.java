package com.nexigroup.pagopa.cruscotto.security;

import com.nexigroup.pagopa.cruscotto.config.Constants;
import com.nexigroup.pagopa.cruscotto.repository.AuthGroupRepository;
import com.nexigroup.pagopa.cruscotto.repository.AuthPermissionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.MockitoAnnotations;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import javax.cache.Cache;
import javax.cache.CacheManager;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class GrantAuthoritiesLoadTest {

    @Mock
    private CacheManager cacheManager;

    @Mock
    private Cache<Object, Object> cache;

    @Mock
    private AuthGroupRepository authGroupRepository;

    @Mock
    private AuthPermissionRepository authPermissionRepository;

    private GrantAuthoritiesLoad grantAuthoritiesLoad;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        grantAuthoritiesLoad = new GrantAuthoritiesLoad(cacheManager, authGroupRepository, authPermissionRepository);
    }

    @Test
    void testLoad_ReturnsCachedAuthorities() {
        String subject = "user1";
        String iat = "12345";
        String typeLogin = "loginType";
        String key = subject + "_" + iat + "_" + typeLogin;

        // Simula che la cache contenga già le authorities
        Collection<GrantedAuthority> cachedAuthorities = List.of(() -> "MOD1.PERM1");
        when(cacheManager.getCache(anyString())).thenReturn(cache);
        when(cache.get(key)).thenReturn(cachedAuthorities);

        // Chiamata al metodo
        Collection<GrantedAuthority> result = grantAuthoritiesLoad.load(
            Map.of("authorities", List.of("ROLE_ADMIN")), subject, iat, typeLogin
        );

        // Verifica che ritorni i valori della cache (confronto ignorando il tipo di collezione)
        assertEquals(new HashSet<>(cachedAuthorities), new HashSet<>(result));

        // Verifica che cache.get sia stato chiamato almeno una volta
        verify(cache, atLeastOnce()).get(key);

        // Non deve essere chiamato put perché è già presente nella cache
        verify(cache, never()).put(eq(key), any());
    }

    @Test
    void testLoad_PasswordExpiredRole_AddsPasswordModificationAuthority() {
        Map<String, Object> claims = Map.of("authorities", List.of(Constants.ROLE_PASSWORD_EXPIRED));

        try (MockedStatic<SecurityUtils> mocked = mockStatic(SecurityUtils.class)) {
            mocked.when(() -> SecurityUtils.extractAuthorityFromClaims(claims))
                .thenReturn(List.of(new SimpleGrantedAuthority(Constants.ROLE_PASSWORD_EXPIRED)));

            when(cacheManager.getCache(anyString())).thenReturn(cache);
            when(cache.get(anyString())).thenReturn(null);

            Collection<GrantedAuthority> result = grantAuthoritiesLoad.load(claims, "user2", "12345", "loginType");

            assertTrue(result.stream()
                .anyMatch(a -> a.getAuthority().equals(AuthoritiesConstants.PASSWORD_MODIFICATION)));
        }
    }

    @Test
    void testLoad_ThrowsExceptionWhenGroupNotFound() {
        String subject = "user4";
        String iat = "12345";
        String typeLogin = "loginType";
        String key = subject + "_" + iat + "_" + typeLogin;

        when(cacheManager.getCache(anyString())).thenReturn(cache);
        when(cache.get(key)).thenReturn(null);

        Map<String, Object> claims = Map.of("authorities", List.of("ROLE_UNKNOWN"));

        when(authGroupRepository.findOneByNome("ROLE_UNKNOWN")).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class,
            () -> grantAuthoritiesLoad.load(claims, subject, iat, typeLogin));
    }
}
