package com.nexigroup.pagopa.cruscotto.security.jwt;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import com.nexigroup.pagopa.cruscotto.config.Constants;
import com.nexigroup.pagopa.cruscotto.security.GrantAuthoritiesLoad;
import com.nexigroup.pagopa.cruscotto.security.oauth2.JwtInvalid;

import java.time.Instant;
import java.util.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.Authentication;

import org.springframework.security.oauth2.jwt.*;

import static org.assertj.core.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class TokenProviderTest {

    @Mock
    private JwtEncoder jwtEncoder;

    @Mock
    private JwtDecoder jwtDecoder;

    @Mock
    private GrantAuthoritiesLoad grantAuthoritiesLoad;

    @InjectMocks
    private TokenProvider tokenProvider;

    @BeforeEach
    void setUp() throws Exception {
        var tokenValidityField = TokenProvider.class.getDeclaredField("tokenValidityInSeconds");
        tokenValidityField.setAccessible(true);
        tokenValidityField.set(tokenProvider, 3600L);

        var tokenValidityRememberField = TokenProvider.class.getDeclaredField("tokenValidityInSecondsForRememberMe");
        tokenValidityRememberField.setAccessible(true);
        tokenValidityRememberField.set(tokenProvider, 7200L);
    }

    // -----------------------------------------------------
    // createToken
    // -----------------------------------------------------
    @Test
    void createToken_shouldGenerateToken() {
        Authentication authentication = new UsernamePasswordAuthenticationToken(
            "user",
            "password",
            List.of(new SimpleGrantedAuthority("ROLE_USER"))
        );

        Jwt jwt = mock(Jwt.class);
        when(jwt.getTokenValue()).thenReturn("mock-token");

        when(jwtEncoder.encode(any())).thenReturn(jwt);

        doAnswer(invocation -> {
            Map<String, Object> claims = invocation.getArgument(0);
            claims.put("extra", "value");
            return null;
        }).when(grantAuthoritiesLoad)
            .load(anyMap(), eq("user"), anyString(), eq(Constants.FORM_LOGIN));

        String token = tokenProvider.createToken(authentication, false);

        assertThat(token).isEqualTo("mock-token");
    }

    @Test
    void createToken_rememberMe_shouldUseLongerValidity() {
        Authentication authentication = new UsernamePasswordAuthenticationToken(
            "admin",
            "password",
            List.of(new SimpleGrantedAuthority("ROLE_ADMIN"))
        );

        Jwt jwt = mock(Jwt.class);
        when(jwt.getTokenValue()).thenReturn("remember-token");
        when(jwtEncoder.encode(any())).thenReturn(jwt);

        String token = tokenProvider.createToken(authentication, true);

        assertThat(token).isEqualTo("remember-token");
    }

    // -----------------------------------------------------
    // getAuthentication
    // -----------------------------------------------------
    @Test
    void getAuthentication_shouldReturnAuthentication() {
        Map<String, Object> claims = new HashMap<>();
        claims.put("sub", "user");
        claims.put("iat", Instant.now().getEpochSecond());
        claims.put("authorities", List.of("ROLE_USER"));

        Jwt jwt = mock(Jwt.class);
        when(jwt.getClaims()).thenReturn(claims);

        when(jwtDecoder.decode("token123")).thenReturn(jwt);

        when(grantAuthoritiesLoad.load(anyMap(), eq("user"), anyString(), eq(Constants.FORM_LOGIN)))
            .thenReturn(List.of(new SimpleGrantedAuthority("ROLE_USER")));

        var auth = tokenProvider.getAuthentication("token123");

        assertThat(auth).isNotNull();
        assertThat(auth.getName()).isEqualTo("user");
        assertThat(auth.getAuthorities()).extracting("authority")
            .containsExactly("ROLE_USER");
    }

    // -----------------------------------------------------
    // validateToken
    // -----------------------------------------------------
    @Test
    void validateToken_shouldReturnTrueForValidToken() throws JwtInvalid {
        when(jwtDecoder.decode("valid-token")).thenReturn(mock(Jwt.class));

        boolean valid = tokenProvider.validateToken("valid-token");

        assertThat(valid).isTrue();
    }

    @Test
    void validateToken_shouldReturnFalseForInvalidToken() throws JwtInvalid {
        when(jwtDecoder.decode("bad-token"))
            .thenThrow(new JwtException("invalid"));

        boolean valid = tokenProvider.validateToken("bad-token");

        assertThat(valid).isFalse();
    }
}
