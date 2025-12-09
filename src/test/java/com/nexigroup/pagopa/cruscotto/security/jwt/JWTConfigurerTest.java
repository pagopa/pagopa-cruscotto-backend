package com.nexigroup.pagopa.cruscotto.security.jwt;

import com.nexigroup.pagopa.cruscotto.security.CookieTokenAuthenticationFilter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.oauth2.server.resource.web.BearerTokenAuthenticationFilter;
import org.springframework.web.servlet.HandlerExceptionResolver;

import jakarta.servlet.Filter;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class JWTConfigurerTest {

    private TokenProvider tokenProvider;
    private HttpSecurity httpSecurity;
    private HandlerExceptionResolver handlerExceptionResolver;

    @BeforeEach
    void setup() {
        tokenProvider = mock(TokenProvider.class);
        handlerExceptionResolver = mock(HandlerExceptionResolver.class);

        // Completely mock HttpSecurity to avoid deprecated constructors
        httpSecurity = mock(HttpSecurity.class);

        // When addFilterBefore() is called return the same mock (builder pattern)
        when(httpSecurity.addFilterBefore(any(Filter.class), any(Class.class))).thenReturn(httpSecurity);
    }

    @Test
    void shouldAddCustomFilterBeforeBearerTokenFilter() throws Exception {
        JWTConfigurer configurer = new JWTConfigurer(tokenProvider, httpSecurity, handlerExceptionResolver);

        configurer.configure(httpSecurity);

        ArgumentCaptor<Filter> filterCaptor = ArgumentCaptor.forClass(Filter.class);
        ArgumentCaptor<Class<?>> classCaptor = ArgumentCaptor.forClass(Class.class);

        verify(httpSecurity).addFilterBefore(filterCaptor.capture(), (Class<? extends Filter>) classCaptor.capture());

        // Assertions
        assertThat(filterCaptor.getValue())
            .isInstanceOf(CookieTokenAuthenticationFilter.class);

        assertThat(classCaptor.getValue())
            .isEqualTo(BearerTokenAuthenticationFilter.class);
    }

    @Test
    void constructorShouldStoreDependencies() {
        JWTConfigurer configurer = new JWTConfigurer(tokenProvider, httpSecurity, handlerExceptionResolver);

        assertThat(configurer)
            .hasFieldOrPropertyWithValue("tokenProvider", tokenProvider)
            .hasFieldOrPropertyWithValue("httpSecurity", httpSecurity)
            .hasFieldOrPropertyWithValue("handlerExceptionResolver", handlerExceptionResolver);
    }
}
