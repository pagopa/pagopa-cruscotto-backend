package com.nexigroup.pagopa.cruscotto.security.jwt;

import static com.nexigroup.pagopa.cruscotto.security.SecurityUtils.JWT_ALGORITHM;

import com.nexigroup.pagopa.cruscotto.config.Constants;
import com.nexigroup.pagopa.cruscotto.security.GrantAuthoritiesLoad;
import com.nexigroup.pagopa.cruscotto.security.oauth2.JwtInvalid;
import java.security.Key;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.oauth2.jwt.*;
import org.springframework.stereotype.Component;

@Component
public class TokenProvider {

    private final Logger log = LoggerFactory.getLogger(TokenProvider.class);

    private final JwtEncoder jwtEncoder;

    private final JwtDecoder jwtDecoder;

    private static final String AUTHORITIES_KEY = "roles";

    private final GrantAuthoritiesLoad grantAuthoritiesLoad;

    private Key key;

    @Value("${jhipster.security.authentication.jwt.token-validity-in-seconds:0}")
    private long tokenValidityInSeconds;

    @Value("${jhipster.security.authentication.jwt.token-validity-in-seconds-for-remember-me:0}")
    private long tokenValidityInSecondsForRememberMe;

    public TokenProvider(JwtEncoder jwtEncoder, JwtDecoder jwtDecoder, GrantAuthoritiesLoad grantAuthoritiesLoad) {
        this.jwtEncoder = jwtEncoder;
        this.jwtDecoder = jwtDecoder;
        this.grantAuthoritiesLoad = grantAuthoritiesLoad;
    }

    public String createToken(Authentication authentication, boolean rememberMe) {
        List<String> authorities = authentication
            .getAuthorities()
            .stream()
            .map(GrantedAuthority::getAuthority)
            .collect(Collectors.toList());

        Instant now = Instant.now();
        Instant validity;
        if (rememberMe) {
            validity = now.plus(this.tokenValidityInSecondsForRememberMe, ChronoUnit.SECONDS);
        } else {
            validity = now.plus(this.tokenValidityInSeconds, ChronoUnit.SECONDS);
        }

        JwtClaimsSet jwtClaimsSet = JwtClaimsSet.builder()
            .issuedAt(now)
            .expiresAt(validity)
            .subject(authentication.getName())
            .claim(AUTHORITIES_KEY, authorities)
            .build();

        JwsHeader jwsHeader = JwsHeader.with(JWT_ALGORITHM).build();

        Map<String, Object> claims = new HashMap<>();
        claims.put(AUTHORITIES_KEY, authorities);

        grantAuthoritiesLoad.load(claims, authentication.getName(), String.valueOf(now.getEpochSecond()), Constants.FORM_LOGIN);

        return this.jwtEncoder.encode(JwtEncoderParameters.from(jwsHeader, jwtClaimsSet)).getTokenValue();
    }

    public Authentication getAuthentication(String token) {
        Map<String, Object> claims = jwtDecoder.decode(token).getClaims();

        Instant iat = Instant.ofEpochSecond(Long.parseLong(claims.get("iat").toString()));

        Collection<? extends GrantedAuthority> authorities = grantAuthoritiesLoad.load(
            claims,
            claims.get("sub").toString(),
            String.valueOf(iat.getEpochSecond()),
            Constants.FORM_LOGIN
        );

        User principal = new User(claims.get("sub").toString(), "", authorities);

        return new UsernamePasswordAuthenticationToken(principal, token, authorities);
    }

    public boolean validateToken(String authToken) throws JwtInvalid {
        try {
            jwtDecoder.decode(authToken);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            log.info("Invalid JWT token.");
            log.trace("Invalid JWT token trace.", e);
        }
        return false;
    }
}
