package com.nexigroup.pagopa.cruscotto.security;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.nexigroup.pagopa.cruscotto.config.Constants;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.core.OAuth2AccessToken;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

class OAuth2AuthorizedClientCustomRepositoryTest {

    private OAuth2AuthorizedClientCustomRepository repository;
    private HttpServletRequest request;
    private HttpServletResponse response;
    private Authentication authentication;
    private OAuth2AuthorizedClient authorizedClient;

    @BeforeEach
    void setUp() {
        repository = new OAuth2AuthorizedClientCustomRepository();
        request = mock(HttpServletRequest.class);
        response = mock(HttpServletResponse.class);
        authentication = mock(Authentication.class);

        OAuth2AccessToken accessToken = new OAuth2AccessToken(
            OAuth2AccessToken.TokenType.BEARER,
            "mock-token",
            Instant.now(),
            Instant.now().plus(1, ChronoUnit.HOURS)
        );

        authorizedClient = mock(OAuth2AuthorizedClient.class);
        when(authorizedClient.getAccessToken()).thenReturn(accessToken);
    }

    @Test
    void testSaveAuthorizedClientStoresCookie() {
        repository.saveAuthorizedClient(authorizedClient, authentication, request, response);

        ArgumentCaptor<Cookie> cookieCaptor = ArgumentCaptor.forClass(Cookie.class);
        verify(response).addCookie(cookieCaptor.capture());

        Cookie cookie = cookieCaptor.getValue();
        assertEquals(Constants.OIDC_ACCESS_TOKEN, cookie.getName());
        assertEquals("mock-token", cookie.getValue());
    }

    @Test
    void testRemoveAuthorizedClientThrowsException() {
        UnsupportedOperationException exception = assertThrows(
            UnsupportedOperationException.class,
            () -> repository.removeAuthorizedClient("client-id", authentication, request, response)
        );

        assertEquals("removeAuthorizedClient is not supported by OAuth2AuthorizedClientCustomRepository", exception.getMessage());
    }

    @Test
    void testLoadAuthorizedClientReturnsNull() {
        assertNull(repository.loadAuthorizedClient("client-id", authentication, request));
    }
}
