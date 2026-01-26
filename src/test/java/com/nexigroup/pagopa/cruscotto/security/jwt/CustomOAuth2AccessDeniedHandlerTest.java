package com.nexigroup.pagopa.cruscotto.security.jwt;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.oauth2.server.resource.authentication.AbstractOAuth2TokenAuthenticationToken;


import static org.assertj.core.api.Assertions.assertThat;

class CustomOAuth2AccessDeniedHandlerTest {

    private final ObjectMapper mapper = new ObjectMapper();
    private CustomOAuth2AccessDeniedHandler handler;

    @BeforeEach
    void init() {
        handler = new CustomOAuth2AccessDeniedHandler();
    }

    @Test
    void handle_withRealm_withoutOAuth2Principal() throws Exception {
        handler.setRealmName("testRealm");

        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setRequestURI("/api/test");

        MockHttpServletResponse response = new MockHttpServletResponse();
        AccessDeniedException ex = new AccessDeniedException("Access Denied");

        handler.handle(request, response, ex);

        assertThat(response.getStatus()).isEqualTo(HttpStatus.FORBIDDEN.value());
        assertThat(response.getContentType()).isEqualTo(MediaType.APPLICATION_JSON_VALUE);
        assertThat(response.getHeader("WWW-Authenticate"))
            .isEqualTo("Bearer realm=\"testRealm\"");

        JsonNode json = mapper.readTree(response.getContentAsString());

        assertThat(json.get("status").asInt()).isEqualTo(403);
        assertThat(json.get("message").asText()).isEqualTo("Access Denied");
        assertThat(json.get("url").asText()).isEqualTo("/api/test");
    }

    @Test
    void handle_withOAuth2Principal_insufficientScope() throws Exception {
        handler.setRealmName("secureRealm");

        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setRequestURI("/api/secure");

        MockHttpServletResponse response = new MockHttpServletResponse();

        AbstractOAuth2TokenAuthenticationToken auth =
            Mockito.mock(AbstractOAuth2TokenAuthenticationToken.class);
        request.setUserPrincipal(auth);

        AccessDeniedException ex = new AccessDeniedException("Some message");

        handler.handle(request, response, ex);

        assertThat(response.getStatus()).isEqualTo(HttpStatus.FORBIDDEN.value());

        String header = response.getHeader("WWW-Authenticate");
        assertThat(header).contains("error=\"insufficient_scope\"");
        assertThat(header).contains("realm=\"secureRealm\"");
        assertThat(header).contains("error_description=\"The request requires higher privileges than provided by the access token.\"");

        JsonNode json = mapper.readTree(response.getContentAsString());

        assertThat(json.get("status").asInt()).isEqualTo(403);
        assertThat(json.get("message").asText())
            .isEqualTo("The request requires higher privileges than provided by the access token.");
        assertThat(json.get("url").asText()).isEqualTo("/api/secure");
    }

    @Test
    void handle_withoutRealm() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setRequestURI("/api/norealm");

        MockHttpServletResponse response = new MockHttpServletResponse();
        AccessDeniedException ex = new AccessDeniedException("Denied");

        handler.handle(request, response, ex);

        assertThat(response.getHeader("WWW-Authenticate"))
            .isEqualTo("Bearer");
    }
}
