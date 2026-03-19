package com.nexigroup.pagopa.cruscotto.config;



import com.fasterxml.jackson.databind.ObjectMapper;
import com.nexigroup.pagopa.cruscotto.management.SecurityMetersService;
import com.nexigroup.pagopa.cruscotto.security.oauth2.JwtInvalid;

import com.nimbusds.jose.jwk.source.ImmutableSecret;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import com.nimbusds.jose.util.Base64;
import org.objectweb.asm.TypeReference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.core.OAuth2Error;
import org.springframework.security.oauth2.core.OAuth2TokenValidator;
import org.springframework.security.oauth2.core.OAuth2TokenValidatorResult;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.*;
import org.springframework.util.StringUtils;

import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Collection;
import java.util.Map;

//@Configuration
public class SecurityJwtConfiguration {

    private static final Logger LOG = LoggerFactory.getLogger(SecurityJwtConfiguration.class);
    public static final MacAlgorithm JWT_ALGORITHM = MacAlgorithm.HS256;
    Boolean skipAuthenticationVerify =false;

    @Value("${jhipster.security.authentication.jwt.base64-secret}")
    private String jwtKey;

    private OAuth2TokenValidator<Jwt> jwtValidator = JwtValidators.createDefault();
    @Bean
    public JwtDecoder jwtDecoder(SecurityMetersService metersService) {
        NimbusJwtDecoder jwtDecoder = NimbusJwtDecoder.withSecretKey(getSecretKey()).macAlgorithm(JWT_ALGORITHM).build();
        return token -> {
            try {
                if (skipAuthenticationVerify){
                    Jwt createdJwt = Jwt.withTokenValue(token).headers((h) -> h.putAll(Map.of("alg", "HS512")))
                        .claims((c) -> c.putAll(extractClaims(token))).build();
                    validateJwt(createdJwt);
                    return createdJwt;
                }else{
                    return jwtDecoder.decode(token);
                }
            } catch (Exception e) {
                if (e.getMessage().contains("Invalid signature")) {
                    metersService.trackTokenInvalidSignature();
                } else if (e.getMessage().contains("Jwt expired at")) {
                    metersService.trackTokenExpired();
                    throw new JwtInvalid(e);
                } else if (
                    e.getMessage().contains("Invalid JWT serialization") ||
                    e.getMessage().contains("Malformed token") ||
                    e.getMessage().contains("Invalid unsecured/JWS/JWE")
                ) {
                    metersService.trackTokenMalformed();
                } else {
                    LOG.error("Unknown JWT error {}", e.getMessage());
                }
                throw e;
            }
        };
    }

    private Jwt validateJwt(Jwt jwt) {
        OAuth2TokenValidatorResult result = this.jwtValidator.validate(jwt);
        if (result.hasErrors()) {
            Collection<OAuth2Error> errors = result.getErrors();
            String validationErrorString = this.getJwtValidationExceptionMessage(errors);
            throw new JwtValidationException("", errors);
        } else {
            return jwt;
        }
    }
    private String getJwtValidationExceptionMessage(Collection<OAuth2Error> errors) {
        for(OAuth2Error oAuth2Error : errors) {
            if (StringUtils.hasLength(oAuth2Error.getDescription())) {
                return String.format("An error occurred while attempting to decode the Jwt: %s", oAuth2Error.getDescription());
            }
        }

        return "Unable to validate Jwt";
    }
    private static final ObjectMapper objectMapper = new ObjectMapper();


    public static Map<String, Object> extractClaims(String token) {
        try {
            String[] parts = token.split("\\.");
            if (parts.length < 2) {
                throw new IllegalArgumentException("Token JWT non valido");
            }

            String payload = parts[1];

            byte[] decodedBytes = Base64.from(payload).decode();
            String json = new String(decodedBytes, StandardCharsets.UTF_8);

            Map<String, Object> claims = objectMapper.readValue(json, Map.class);

            if (claims.get("exp") != null) {
                long exp = ((Number) claims.get("exp")).longValue();
                claims.put("exp", Instant.ofEpochSecond(exp));
            }

            if (claims.get("iat") != null) {
                long iat = ((Number) claims.get("iat")).longValue();
                claims.put("iat", Instant.ofEpochSecond(iat));
            }

            return claims;

        } catch (Exception e) {
            throw new RuntimeException("Errore durante l'estrazione delle claims dal JWT", e);
        }
    }

    @Bean
    public JwtEncoder jwtEncoder() {
        return new NimbusJwtEncoder(new ImmutableSecret<>(getSecretKey()));
    }

    private SecretKey getSecretKey() {
        byte[] keyBytes = Base64.from(jwtKey).decode();
        return new SecretKeySpec(keyBytes, 0, keyBytes.length, JWT_ALGORITHM.getName());
    }
}
