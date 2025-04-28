package com.nexigroup.pagopa.cruscotto.security.jwt;

import static com.nexigroup.pagopa.cruscotto.security.jwt.JwtAuthenticationTestUtils.*;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.nexigroup.pagopa.cruscotto.IntegrationTest;
import com.nexigroup.pagopa.cruscotto.config.Constants;
import com.nexigroup.pagopa.cruscotto.domain.AuthGroup;
import com.nexigroup.pagopa.cruscotto.repository.AuthGroupRepository;
import com.nexigroup.pagopa.cruscotto.security.helper.CookieHelper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.time.Duration;

@AutoConfigureMockMvc
@IntegrationTest
class TokenAuthenticationIT {

    private static final String ADMIN_TEST = "ADMIN_TEST";

    @Autowired
    private MockMvc mvc;

    @Value("${jhipster.security.authentication.jwt.base64-secret}")
    private String jwtKey;

    @Autowired
    private AuthGroupRepository authGroupRepository;

    private AuthGroup authGroup;

    public AuthGroup getAuthGroup() {
        AuthGroup authGroup = new AuthGroup();
        authGroup.setNome(ADMIN_TEST);
        authGroup.setDescrizione(ADMIN_TEST);
        authGroup.setLivelloVisibilita(0);
        return authGroup;
    }

    @BeforeEach
    public void init() {
        authGroup = authGroupRepository.save(getAuthGroup());
    }

    @AfterEach
    public void cleanup() {
        authGroupRepository.delete(authGroup);
    }

//    @Test
//    void testLoginWithValidToken() throws Exception {
//        expectOk(createValidToken(jwtKey, ADMIN_TEST));
//    }

//    @Test
//    void testReturnFalseWhenJWThasInvalidSignature() throws Exception {
//        expectUnauthorized(createTokenWithDifferentSignature());
//    }
//
//    @Test
//    void testReturnFalseWhenJWTisMalformed() throws Exception {
//        expectUnauthorized(createSignedInvalidJwt(jwtKey));
//    }
//
//    @Test
//    void testReturnFalseWhenJWTisExpired() throws Exception {
//        expectUnauthorized(createExpiredToken(jwtKey));
//    }

    private void expectOk(String token) throws Exception {
        mvc.perform(MockMvcRequestBuilders.get("/api/authenticate").cookie(CookieHelper.generateCookie(Constants.OIDC_ACCESS_TOKEN, token, Duration.ofHours(1)))).andExpect(status().isOk());
    }

    private void expectUnauthorized(String token) throws Exception {
        mvc
            .perform(MockMvcRequestBuilders.get("/api/authenticate").header(AUTHORIZATION, BEARER + token))
            .andExpect(status().isUnauthorized());
    }
}
