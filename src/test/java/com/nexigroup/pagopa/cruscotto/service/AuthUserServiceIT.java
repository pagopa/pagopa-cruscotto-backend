package com.nexigroup.pagopa.cruscotto.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import com.nexigroup.pagopa.cruscotto.IntegrationTest;
import com.nexigroup.pagopa.cruscotto.domain.AuthUser;
import com.nexigroup.pagopa.cruscotto.domain.enumeration.AuthenticationType;
import com.nexigroup.pagopa.cruscotto.repository.AuthUserRepository;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.data.auditing.DateTimeProvider;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.transaction.annotation.Transactional;
import tech.jhipster.security.RandomUtil;

/**
 * Integration tests for {@link AuthUserService}.
 */
@IntegrationTest
@Transactional
class AuthUserServiceIT {

    private static final String DEFAULT_LOGIN = "johndoe_service";

    private static final String DEFAULT_EMAIL = "johndoe_service@localhost";

    private static final String DEFAULT_FIRSTNAME = "john";

    private static final String DEFAULT_LASTNAME = "doe";

    @Autowired
    private CacheManager cacheManager;

    private static final String DEFAULT_IMAGEURL = "https://placehold.it/50x50";

    private static final String DEFAULT_LANGKEY = "dummy";

    @Autowired
    private AuthUserRepository userRepository;

    @Autowired
    private AuthUserService userService;

    @MockitoBean
    private DateTimeProvider dateTimeProvider;

    private AuthUser user;

    @BeforeEach
    public void init() {
        user = new AuthUser();
        user.setLogin(DEFAULT_LOGIN);
        user.setAuthenticationType(AuthenticationType.FORM_LOGIN);
        user.setPassword(RandomStringUtils.insecure().nextAlphanumeric(60));
        user.setActivated(true);
        user.setEmail(DEFAULT_EMAIL);
        user.setFirstName(DEFAULT_FIRSTNAME);
        user.setLastName(DEFAULT_LASTNAME);
        user.setImageUrl(DEFAULT_IMAGEURL);
        user.setLangKey(DEFAULT_LANGKEY);

        when(dateTimeProvider.getNow()).thenReturn(Optional.of(LocalDateTime.now()));
    }

    @AfterEach
    public void cleanupAndCheck() {
        cacheManager
            .getCacheNames()
            .stream()
            .map(cacheName -> this.cacheManager.getCache(cacheName))
            .filter(Objects::nonNull)
            .forEach(Cache::clear);
    }

    @Test
    @Transactional
    void assertThatUserMustExistToResetPassword() {
        AuthUser authUser = userRepository.saveAndFlush(user);
        Optional<AuthUser> maybeUser = userService.requestPasswordResetByMail("invalid.login@localhost", AuthenticationType.FORM_LOGIN);
        assertThat(maybeUser).isNotPresent();

        maybeUser = userService.requestPasswordResetByMail(authUser.getEmail(), AuthenticationType.FORM_LOGIN);
        assertThat(maybeUser).isPresent();
        assertThat(maybeUser.orElse(null).getEmail()).isEqualTo(authUser.getEmail());
        assertThat(maybeUser.orElse(null).getResetDate()).isNotNull();
        assertThat(maybeUser.orElse(null).getResetKey()).isNotNull();
        userRepository.delete(authUser);
    }

    @Test
    @Transactional
    void assertThatResetKeyMustNotBeOlderThan24Hours() {
        Instant daysAgo = Instant.now().minus(25, ChronoUnit.HOURS);
        String resetKey = RandomUtil.generateResetKey();
        user.setActivated(true);
        user.setResetDate(daysAgo);
        user.setResetKey(resetKey);
        AuthUser authUser = userRepository.saveAndFlush(user);

        Optional<AuthUser> maybeUser = userService.completePasswordReset("johndoe2", authUser.getResetKey());
        assertThat(maybeUser).isNotPresent();
        userRepository.delete(authUser);
    }

    @Test
    @Transactional
    void assertThatResetKeyMustBeValid() {
        Instant daysAgo = Instant.now().minus(25, ChronoUnit.HOURS);
        user.setActivated(true);
        user.setResetDate(daysAgo);
        user.setResetKey("1234");
        AuthUser authUser = userRepository.saveAndFlush(user);

        Optional<AuthUser> maybeUser = userService.completePasswordReset("johndoe2", authUser.getResetKey());
        assertThat(maybeUser).isNotPresent();
        userRepository.delete(authUser);
    }

    @Test
    @Transactional
    void assertThatUserCanResetPassword() {
        String oldPassword = user.getPassword();
        Instant daysAgo = Instant.now().minus(2, ChronoUnit.HOURS);
        String resetKey = RandomUtil.generateResetKey();
        user.setActivated(true);
        user.setResetDate(daysAgo);
        user.setResetKey(resetKey);
        AuthUser authUser = userRepository.saveAndFlush(user);

        Optional<AuthUser> maybeUser = userService.completePasswordReset("johndoe2", authUser.getResetKey());
        assertThat(maybeUser).isPresent();
        assertThat(maybeUser.orElse(null).getResetDate()).isNull();
        assertThat(maybeUser.orElse(null).getResetKey()).isNull();
        assertThat(maybeUser.orElse(null).getPassword()).isNotEqualTo(oldPassword);

        userRepository.delete(authUser);
    }

    @Test
    @Transactional
    void assertThatNotActivatedUsersWithNullActivationKeyCreatedBefore3DaysAreNotDeleted() {
        Instant now = Instant.now();
        when(dateTimeProvider.getNow()).thenReturn(Optional.of(now.minus(4, ChronoUnit.DAYS)));
        user.setActivated(false);
        AuthUser dbUser = userRepository.saveAndFlush(user);
        dbUser.setCreatedDate(now.minus(4, ChronoUnit.DAYS));
        userRepository.saveAndFlush(dbUser);
        Instant threeDaysAgo = now.minus(3, ChronoUnit.DAYS);
        List<AuthUser> users = userRepository.findAllByActivatedIsFalseAndActivationKeyIsNotNullAndCreatedDateBefore(threeDaysAgo);
        assertThat(users).isEmpty();

        userRepository.delete(dbUser);
    }
}
