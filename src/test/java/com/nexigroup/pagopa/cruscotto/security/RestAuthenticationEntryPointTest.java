package com.nexigroup.pagopa.cruscotto.security;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.mockito.Mockito.*;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;

class RestAuthenticationEntryPointTest {

    private RestAuthenticationEntryPoint entryPoint;
    private HttpServletRequest request;
    private HttpServletResponse response;

    @BeforeEach
    void setUp() {
        entryPoint = new RestAuthenticationEntryPoint();
        request = mock(HttpServletRequest.class);
        response = mock(HttpServletResponse.class);
    }

    @Test
    void testCommence_SendsUnauthorized() throws IOException, ServletException {
        AuthenticationException authException = mock(AuthenticationException.class);

        entryPoint.commence(request, response, authException);

        // Verify that sendError was called with 401 and "Unauthorized"
        verify(response).sendError(HttpServletResponse.SC_UNAUTHORIZED, "Unauthorized");
    }

    @Test
    void testHandle_SendsUnauthorized() throws IOException, ServletException {
        AccessDeniedException accessDeniedException = mock(AccessDeniedException.class);

        entryPoint.handle(request, response, accessDeniedException);

        // Verify that sendError was called with 401 and "Unauthorized"
        verify(response).sendError(HttpServletResponse.SC_UNAUTHORIZED, "Unauthorized");
    }
}
