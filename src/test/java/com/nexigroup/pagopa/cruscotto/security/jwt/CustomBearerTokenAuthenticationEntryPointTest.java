package com.nexigroup.pagopa.cruscotto.security.jwt;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.oauth2.core.*;
import org.springframework.security.oauth2.server.resource.BearerTokenError;

public class CustomBearerTokenAuthenticationEntryPointTest {

    private CustomBearerTokenAuthenticationEntryPoint entryPoint;
    private HttpServletRequest request;
    private HttpServletResponse response;
    private StringWriter responseWriter;

    @BeforeEach
    void setup() throws Exception {
        entryPoint = new CustomBearerTokenAuthenticationEntryPoint();
        entryPoint.setRealmName("test-realm");

        request = mock(HttpServletRequest.class);
        response = mock(HttpServletResponse.class);

        responseWriter = new StringWriter();
        when(response.getWriter()).thenReturn(new PrintWriter(responseWriter));
        when(request.getRequestURI()).thenReturn("/api/test");
    }

    @Test
    void testCommence_withGenericAuthenticationException() throws Exception {
        AuthenticationException ex = new AuthenticationException("Test generic exception") {};

        entryPoint.commence(request, response, ex);

        // Verifica che lo status HTTP sia 401
        verify(response).setStatus(HttpStatus.UNAUTHORIZED.value());

        // Verifica header
        verify(response).addHeader(eq("WWW-Authenticate"), contains("Bearer realm=\"test-realm\""));
        verify(response).setContentType("application/json");

        String json = responseWriter.toString();

        // Verifica JSON robusta (non dipendente dai nomi dei campi)
        assertTrue(json.contains("Test generic exception"));
        assertTrue(json.contains("Unauthenticated"));
        assertTrue(json.contains("/api/test"));

        // Qualsiasi campo contenente 403 è valido
        assertTrue(json.contains("403"), "Il JSON deve contenere il valore 403 (FORBIDDEN)");
    }

    @Test
    void testCommence_withOAuth2AuthenticationException() throws Exception {
        OAuth2Error error = new OAuth2Error("invalid_token", "Token expired", "https://docs/errors/1");
        OAuth2AuthenticationException ex = new OAuth2AuthenticationException(error);

        entryPoint.commence(request, response, ex);

        verify(response).setStatus(HttpStatus.UNAUTHORIZED.value());

        ArgumentCaptor<String> headerCaptor = ArgumentCaptor.forClass(String.class);
        verify(response).addHeader(eq("WWW-Authenticate"), headerCaptor.capture());

        String header = headerCaptor.getValue();
        assertTrue(header.contains("Bearer"));
        assertTrue(header.contains("error=\"invalid_token\""));
        assertTrue(header.contains("error_description=\"Token expired\""));
        assertTrue(header.contains("error_uri=\"https://docs/errors/1\""));
        assertTrue(header.contains("realm=\"test-realm\""));
    }

    @Test
    void testCommence_withBearerTokenError() throws Exception {
        BearerTokenError tokenError = new BearerTokenError(
            "invalid_token",
            HttpStatus.FORBIDDEN,
            "The token is invalid",
            "https://docs/errors/token",
            "read write"
        );

        OAuth2AuthenticationException ex = new OAuth2AuthenticationException(tokenError);

        entryPoint.commence(request, response, ex);

        verify(response).setStatus(HttpStatus.FORBIDDEN.value());

        ArgumentCaptor<String> headerCaptor = ArgumentCaptor.forClass(String.class);
        verify(response).addHeader(eq("WWW-Authenticate"), headerCaptor.capture());

        String header = headerCaptor.getValue();
        assertTrue(header.contains("scope=\"read write\""));
        assertTrue(header.contains("error_description=\"The token is invalid\""));
        assertTrue(header.contains("error_uri=\"https://docs/errors/token\""));
        assertTrue(header.contains("realm=\"test-realm\""));
    }

    @Test
    void testWWWAuthenticateHeaderBuilder() {
        Map<String, String> params = Map.of(
            "realm", "test",
            "error", "invalid"
        );

        String value = invokeComputeHeader(params);

        assertTrue(value.startsWith("Bearer"));

        // Verifica indipendente dall’ordine
        assertTrue(value.contains("realm=\"test\""));
        assertTrue(value.contains("error=\"invalid\""));

        // Verifica che siano separati da virgola
        assertTrue(value.contains(","));
    }

    /** Utility to call private computeWWWAuthenticateHeaderValue */
    private String invokeComputeHeader(Map<String, String> params) {
        try {
            var method = CustomBearerTokenAuthenticationEntryPoint.class
                .getDeclaredMethod("computeWWWAuthenticateHeaderValue", Map.class);
            method.setAccessible(true);
            return (String) method.invoke(null, params);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
