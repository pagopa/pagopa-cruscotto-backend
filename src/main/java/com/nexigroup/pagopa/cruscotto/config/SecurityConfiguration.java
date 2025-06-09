package com.nexigroup.pagopa.cruscotto.config;

import static org.springframework.security.config.Customizer.withDefaults;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.servlet.util.matcher.MvcRequestMatcher;
import org.springframework.web.servlet.handler.HandlerMappingIntrospector;

import com.nexigroup.pagopa.cruscotto.security.AuthoritiesConstants;
import com.nexigroup.pagopa.cruscotto.security.jwt.CustomBearerTokenAuthenticationEntryPoint;
import com.nexigroup.pagopa.cruscotto.security.jwt.CustomOAuth2AccessDeniedHandler;
import com.nexigroup.pagopa.cruscotto.security.oauth2.JwtGrantedAuthorityConverter;

@Configuration
@EnableMethodSecurity(securedEnabled = true)
public class SecurityConfiguration {

    private static final Logger LOGGER = LoggerFactory.getLogger(SecurityConfiguration.class);

    //    private final DomainUserDetailsService userDetailsService;
    //
    //    private final CorsFilter corsFilter;
    //
    //    private final TokenProvider tokenProvider;
    //
    //    private final JHipsterProperties jHipsterProperties;
    //
    //    private final AuthenticationSuccessHandler authenticationSuccessHandler;
    //
    //    private final AuthenticationFailureHandler authenticationFailureHandler;
    //
    //    private final GrantAuthoritiesLoad grantAuthoritiesLoad;
    //
    //    private final OAuth2AuthorizedClientRepository authorizedClientRepository;
    //
    //    private final PasswordEncoder passwordEncoder;
    //
    //    private final Environment env;
    //
    //    private final HandlerExceptionResolver handlerExceptionResolver;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(
        HttpSecurity http,
        MvcRequestMatcher.Builder mvc,
        JwtDecoder jwtDecoder,
        JwtGrantedAuthorityConverter jwtGrantedAuthorityConverter
    ) throws Exception {
        http
            .cors(withDefaults())
            .csrf(csrf -> csrf.disable())
            .authorizeHttpRequests(authz ->
                // prettier-ignore
                authz
                    .requestMatchers(mvc.pattern(HttpMethod.POST, "/api/authenticate")).permitAll()
                    .requestMatchers(mvc.pattern(HttpMethod.GET, "/api/authenticate")).permitAll()
                    .requestMatchers(mvc.pattern("/api/account/reset-password/init")).permitAll()
                    .requestMatchers(mvc.pattern("/api/account/reset-password/finish")).permitAll()
                    .requestMatchers(mvc.pattern("/api/**")).authenticated()
                    .requestMatchers(mvc.pattern("/v3/api-docs/**")).hasAuthority(AuthoritiesConstants.CONTROL_TOOLS)
                    .requestMatchers(mvc.pattern("/management/health")).permitAll()
                    .requestMatchers(mvc.pattern("/management/health/**")).permitAll()
                    .requestMatchers(mvc.pattern("/management/info")).permitAll()
                    .requestMatchers(mvc.pattern("/management/prometheus")).permitAll()
                    .requestMatchers(mvc.pattern("/management/**")).hasAuthority(AuthoritiesConstants.CONTROL_TOOLS)
            )
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .exceptionHandling(exceptions ->
                exceptions
                    .authenticationEntryPoint(new CustomBearerTokenAuthenticationEntryPoint())
                    .accessDeniedHandler(new CustomOAuth2AccessDeniedHandler())
            )
            .oauth2ResourceServer(
                oauth2 ->
                    oauth2.jwt(jwtConfigurer -> jwtConfigurer.decoder(jwtDecoder).jwtAuthenticationConverter(jwtGrantedAuthorityConverter))
                //                    .bearerTokenResolver(bearerTokenResolver())
            );

        return http.build();
    }

    @Bean
    MvcRequestMatcher.Builder mvc(HandlerMappingIntrospector introspector) {
        return new MvcRequestMatcher.Builder(introspector);
    }
    //    public BearerTokenResolver bearerTokenResolver() {
    //        return new BearerTokenResolver() {
    //            @Override
    //            public String resolve(HttpServletRequest request) {
    //                LOGGER.debug("Resolve token for request {}", request.getRequestURI());
    //                String token = null;
    //                Cookie[] cookies = request.getCookies();
    //                if (cookies != null) {
    //                    Cookie cookie = Arrays.stream(cookies)
    //                        .filter(c -> Constants.OIDC_ACCESS_TOKEN.equals(c.getName()))
    //                        .findAny()
    //                        .orElse(null);
    //                    if (cookie != null) {
    //                        token = cookie.getValue();
    //                    }
    //                }
    //                return token;
    //            }
    //        };
    //    }
}
