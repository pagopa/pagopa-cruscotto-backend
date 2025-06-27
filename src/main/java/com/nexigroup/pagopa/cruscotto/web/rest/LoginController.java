package com.nexigroup.pagopa.cruscotto.web.rest;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.nexigroup.pagopa.cruscotto.security.jwt.TokenProvider;
import com.nexigroup.pagopa.cruscotto.service.AuthUserService;
import com.nexigroup.pagopa.cruscotto.web.rest.vm.LoginVM;
import jakarta.validation.Valid;
import java.util.Collections;
import org.apache.commons.lang3.BooleanUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.CredentialsExpiredException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controller to authenticate users.
 */
@RestController
@RequestMapping("/api")
public class LoginController {

    private final Logger LOGGER = LoggerFactory.getLogger(LoginController.class);
    private final TokenProvider tokenProvider;

    private final AuthUserService authUserService;
    private final AuthenticationManagerBuilder authenticationManagerBuilder;

    public LoginController(
        TokenProvider tokenProvider,
        AuthUserService authUserService,
        AuthenticationManagerBuilder authenticationManagerBuilder
    ) {
        this.tokenProvider = tokenProvider;
        this.authUserService = authUserService;
        this.authenticationManagerBuilder = authenticationManagerBuilder;
    }

    @PostMapping("/authenticate")
    public ResponseEntity<?> authorize(@Valid @RequestBody LoginVM loginVM) {
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
            loginVM.getUsername(),
            loginVM.getPassword()
        );

        try {
            Authentication authentication = authenticationManagerBuilder.getObject().authenticate(authenticationToken);

            SecurityContextHolder.getContext().setAuthentication(authentication);

            authUserService.resetFailedLoginAttempts(loginVM.getUsername());

            String jwt = tokenProvider.createToken(authentication, BooleanUtils.toBooleanDefaultIfNull(loginVM.isRememberMe(), false));

            HttpHeaders httpHeaders = new HttpHeaders();
            httpHeaders.setBearerAuth(jwt);

            return new ResponseEntity<>(new JWTToken(jwt), httpHeaders, HttpStatus.OK);
        } catch (BadCredentialsException badCredentialsException) {
            LOGGER.trace("Bad Credential exception trace", badCredentialsException);

            authUserService.increaseFailedLoginAttempts(loginVM.getUsername());

            return new ResponseEntity<>(
                Collections.singletonMap("AuthenticationException", badCredentialsException.getLocalizedMessage()),
                HttpStatus.UNAUTHORIZED
            );
        } catch (LockedException lockedException) {
            return new ResponseEntity<>(
                Collections.singletonMap("LockedException", lockedException.getLocalizedMessage()),
                HttpStatus.UNAUTHORIZED
            );
        } catch (CredentialsExpiredException credentialsExpiredException) {
            return new ResponseEntity<>(
                Collections.singletonMap("CredentialsExpiredException", credentialsExpiredException.getLocalizedMessage()),
                HttpStatus.UNAUTHORIZED
            );
        } catch (AuthenticationException authenticationException) {
            LOGGER.trace("Authentication exception trace", authenticationException);
            return new ResponseEntity<>(
                Collections.singletonMap("AuthenticationException", authenticationException.getLocalizedMessage()),
                HttpStatus.UNAUTHORIZED
            );
        }
    }

    /**
     * Object to return as body in JWT Authentication.
     */
    static class JWTToken {

        private String idToken;

        JWTToken(String idToken) {
            this.idToken = idToken;
        }

        @JsonProperty("id_token")
        String getIdToken() {
            return idToken;
        }

        void setIdToken(String idToken) {
            this.idToken = idToken;
        }
    }
}
