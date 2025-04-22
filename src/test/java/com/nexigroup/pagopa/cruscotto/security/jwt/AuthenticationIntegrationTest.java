package com.nexigroup.pagopa.cruscotto.security.jwt;

import com.nexigroup.pagopa.cruscotto.config.CacheConfiguration;
import com.nexigroup.pagopa.cruscotto.config.SecurityConfiguration;
import com.nexigroup.pagopa.cruscotto.config.SecurityJwtConfiguration;
import com.nexigroup.pagopa.cruscotto.config.WebConfigurer;
import com.nexigroup.pagopa.cruscotto.management.SecurityMetersService;
import com.nexigroup.pagopa.cruscotto.repository.AuthGroupRepository;
import com.nexigroup.pagopa.cruscotto.repository.AuthPermissionRepository;
import com.nexigroup.pagopa.cruscotto.security.GrantAuthoritiesLoad;
import com.nexigroup.pagopa.cruscotto.web.rest.LoginController;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.springframework.boot.test.context.SpringBootTest;
import tech.jhipster.config.JHipsterProperties;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@SpringBootTest(
    properties = {
        "jhipster.security.authentication.jwt.base64-secret=fd54a45s65fds737b9aafcb3412e07ed99b267f33413274720ddbb7f6c5e64e9f14075f2d7ed041592f0b7657baf8",
        "jhipster.security.authentication.jwt.token-validity-in-seconds=60000",
    },
    classes = {
        JHipsterProperties.class,
        WebConfigurer.class,
        SecurityConfiguration.class,
        CacheConfiguration.class,
        SecurityJwtConfiguration.class,
        SecurityMetersService.class,
        LoginController.class,
        JwtAuthenticationTestUtils.class,
        TokenProvider.class,
        GrantAuthoritiesLoad.class,
        AuthGroupRepository.class,
        AuthPermissionRepository.class
    }
)
public @interface AuthenticationIntegrationTest {
}
