package com.nexigroup.pagopa.cruscotto.web.rest;

import com.nexigroup.pagopa.cruscotto.config.Constants;
import com.nexigroup.pagopa.cruscotto.security.helper.CookieHelper;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import java.security.Principal;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import org.springframework.core.env.Environment;
import org.springframework.core.env.Profiles;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST controller for managing global OIDC logout.
 */
@RestController
public class LogoutResource {

    private ClientRegistration registration;

    private final Environment env;

    public LogoutResource(Optional<ClientRegistrationRepository> clientRegistrationRepository, Environment env) {
        this.env = env;
        if (env.acceptsProfiles(Profiles.of(Constants.SPRING_PROFILE_OAUTH))) {
            clientRegistrationRepository.ifPresent(repository -> {
                this.registration = repository.findByRegistrationId("keycloak");
            });
        }
    }

    /**
     * {@code POST  /api/logout} : logout the current user.
     *
     * @param response the {@link HttpServletResponse}.
     * @param principal the principal.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and a body with a global logout URL and ID token.
     */
    @PostMapping("/api/logout")
    public ResponseEntity<?> logout(HttpServletResponse response, Principal principal) {
        String idToken = null;
        String logoutUrl = null;

        //        if (principal instanceof JwtAuthenticationToken) {
        //            Jwt jwt = ((JwtAuthenticationToken) principal).getToken();
        //            idToken = jwt.getTokenValue();
        //            logoutUrl = this.registration.getProviderDetails()
        //                .getConfigurationMetadata().get("end_session_endpoint").toString();
        //        }

        Cookie token = CookieHelper.generateExpiredCookie(Constants.OIDC_ACCESS_TOKEN);

        response.addCookie(token);

        Map<String, String> logoutDetails = new HashMap<>();
        logoutDetails.put("logoutUrl", logoutUrl);
        logoutDetails.put("idToken", idToken);

        return ResponseEntity.ok().body(logoutDetails);
    }
}
