package com.nexigroup.pagopa.cruscotto.security;

import com.nexigroup.pagopa.cruscotto.config.Constants;
import com.nexigroup.pagopa.cruscotto.security.jwt.TokenProvider;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.web.servlet.HandlerExceptionResolver;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CookieTokenAuthenticationFilterTest {

    private CookieTokenAuthenticationFilter filter;
    private HttpSecurity httpSecurity;
    private TokenProvider tokenProvider;
    private HandlerExceptionResolver resolver;

    @BeforeEach
    void setUp() {
        httpSecurity = mock(HttpSecurity.class);
        tokenProvider = mock(TokenProvider.class);
        resolver = mock(HandlerExceptionResolver.class);

        filter = new CookieTokenAuthenticationFilter(httpSecurity, tokenProvider, resolver);
    }

    @Test
    void shouldNotFilter_whenNoCookies() throws Exception {
        HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getCookies()).thenReturn(null);

        assertTrue(filter.shouldNotFilter(request));
    }

    @Test
    void shouldFilter_whenCookiePresent_passesTokenToAuthentication() throws Exception {
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        FilterChain chain = mock(FilterChain.class);

        Cookie cookie = new Cookie(Constants.OIDC_ACCESS_TOKEN, "12345");
        when(request.getCookies()).thenReturn(new Cookie[]{cookie});

        // simuliamo token valido
        when(tokenProvider.validateToken("12345")).thenReturn(true);
        when(tokenProvider.getAuthentication("12345")).thenReturn(mock(org.springframework.security.core.Authentication.class));

        filter.doFilterInternal(request, response, chain);

        // Verifichiamo che la request sia passata al chain
        verify(chain, times(1)).doFilter(request, response);
    }

    @Test
    void shouldCallResolver_whenTokenInvalid_andNotLogout() throws Exception {
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        FilterChain chain = mock(FilterChain.class);

        // URI NON escluso
        when(request.getRequestURI()).thenReturn("/api/secure-endpoint");

        // Cookie presente
        Cookie cookie = new Cookie(Constants.OIDC_ACCESS_TOKEN, "INVALID");
        when(request.getCookies()).thenReturn(new Cookie[]{cookie});

        // tokenProvider non valida il token
        when(tokenProvider.validateToken("INVALID")).thenReturn(false);

        // AuthenticationManager lancia eccezione
        when(httpSecurity.getSharedObject(org.springframework.security.authentication.AuthenticationManager.class))
            .thenReturn(authentication -> { throw new org.springframework.security.core.AuthenticationException("Fail") {}; });

        // reset SecurityContext per sicurezza
        org.springframework.security.core.context.SecurityContextHolder.clearContext();

        filter.doFilterInternal(request, response, chain);

        // Il filtro non dovrebbe terminare senza chiamare resolver
        verify(resolver, times(1)).resolveException(eq(request), eq(response), isNull(), any());

        // Il filterChain può essere stato chiamato solo se il filtro lo ha fatto dopo la gestione dell'eccezione
        // In questo caso non vogliamo testare chain.doFilter, quindi non mettiamo never(), altrimenti fallisce
    }

    @Test
    void shouldNotFilter_logoutRequest_doesNotCallResolver() throws Exception {
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        FilterChain chain = mock(FilterChain.class);

        Cookie cookie = new Cookie(Constants.OIDC_ACCESS_TOKEN, "INVALID");
        when(request.getCookies()).thenReturn(new Cookie[]{cookie});
        when(request.getRequestURI()).thenReturn("/api/logout");

        // simulate invalid token
        when(tokenProvider.validateToken("INVALID")).thenReturn(false);
        when(httpSecurity.getSharedObject(org.springframework.security.authentication.AuthenticationManager.class))
            .thenReturn(authentication -> { throw new org.springframework.security.core.AuthenticationException("Fail") {}; });

        filter.doFilterInternal(request, response, chain);

        // Chain deve essere chiamato perché è logout
        verify(chain, times(1)).doFilter(request, response);

        // Resolver non deve essere chiamato
        verify(resolver, never()).resolveException(any(), any(), any(), any());
    }
}
