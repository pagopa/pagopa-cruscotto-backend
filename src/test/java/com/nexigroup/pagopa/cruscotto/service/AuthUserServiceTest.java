package com.nexigroup.pagopa.cruscotto.service;

import com.nexigroup.pagopa.cruscotto.domain.*;
import com.nexigroup.pagopa.cruscotto.domain.enumeration.AuthenticationType;
import com.nexigroup.pagopa.cruscotto.repository.*;
import com.nexigroup.pagopa.cruscotto.service.bean.AuthUserCreateRequestBean;
import com.nexigroup.pagopa.cruscotto.config.ApplicationProperties;
import com.nexigroup.pagopa.cruscotto.security.SecurityUtils;

import com.nexigroup.pagopa.cruscotto.service.qdsl.QueryBuilder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.cache.CacheManager;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.Instant;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AuthUserServiceTest {

    @Mock
    private AuthUserRepository authUserRepository;

    @Mock
    private AuthUserHistoryRepository authUserHistoryRepository;

    @Mock
    private AuthGroupRepository authGroupRepository;

    @Mock
    private AuthPermissionRepository authPermissionRepository;

    @Mock
    private ApplicationProperties properties;

    @Mock
    private QueryBuilder queryBuilder;

    @Mock
    private CacheManager cacheManager;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private AuthUserService authUserService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testActivateRegistration_Success() {
        AuthUser user = new AuthUser();
        user.setActivated(false);
        user.setActivationKey("key123");

        when(authUserRepository.findOneByActivationKey("key123")).thenReturn(Optional.of(user));

        Optional<AuthUser> result = authUserService.activateRegistration("key123");

        assertTrue(result.isPresent());
        assertTrue(result.orElseThrow().getActivated());
        assertNull(result.orElseThrow().getActivationKey());
        verify(authUserRepository, never()).save(any());
    }

    @Test
    void testRequestPasswordResetByMail_Success() {
        AuthUser user = new AuthUser();
        user.setActivated(true);

        when(authUserRepository.findOneByEmailIgnoreCaseAndNotDeleted("test@mail.com", AuthenticationType.FORM_LOGIN))
            .thenReturn(Optional.of(user));

        Optional<AuthUser> result = authUserService.requestPasswordResetByMail("test@mail.com", AuthenticationType.FORM_LOGIN);

        assertTrue(result.isPresent());
        assertNotNull(result.orElseThrow().getResetKey());
        assertNotNull(result.orElseThrow().getResetDate());
    }

    @Test
    void testCreateUser_Success() {
        AuthUserCreateRequestBean bean = new AuthUserCreateRequestBean();
        bean.setLogin("login1");
        bean.setFirstName("John");
        bean.setLastName("Doe");
        bean.setEmail("john@doe.com");
        bean.setPassword("pass123");
        bean.setGroupId(1L);

        AuthGroup group = new AuthGroup();
        group.setId(1L);

        when(authGroupRepository.findById(1L)).thenReturn(Optional.of(group));
        when(passwordEncoder.encode("pass123")).thenReturn("encodedPass");
        when(authUserRepository.save(any(AuthUser.class))).thenAnswer(i -> i.getArguments()[0]);

        AuthUser loggedUser = new AuthUser();
        loggedUser.setLogin("admin");
        loggedUser.setGroup(group);

        try (MockedStatic<SecurityUtils> securityUtilsMock = mockStatic(SecurityUtils.class)) {
            securityUtilsMock.when(SecurityUtils::getCurrentUserLogin).thenReturn(Optional.of("admin"));
            securityUtilsMock.when(SecurityUtils::getAuthenticationTypeUserLogin)
                .thenReturn(Optional.of(AuthenticationType.FORM_LOGIN));

            when(authUserRepository.findOneByLoginAndNotDeleted("admin", AuthenticationType.FORM_LOGIN))
                .thenReturn(Optional.of(loggedUser));

            AuthUser createdUser = authUserService.createUser(bean);

            assertNotNull(createdUser);
            assertEquals("login1", createdUser.getLogin());
            assertEquals("encodedPass", createdUser.getPassword());
            verify(authUserHistoryRepository, times(1)).save(any(AuthUserHistory.class));
        }
    }

    @Test
    void testChangePassword_Success() {
        AuthUser user = new AuthUser();
        user.setPassword("encodedPass");
        user.setFailedLoginAttempts(1);

        // Mock ApplicationProperties.Password
        ApplicationProperties.Password passwordProps = mock(ApplicationProperties.Password.class);
        when(passwordProps.getDayPasswordExpired()).thenReturn(90); // example value
        when(properties.getPassword()).thenReturn(passwordProps);

        // Mock static method
        try (MockedStatic<SecurityUtils> securityUtilsMock = mockStatic(SecurityUtils.class)) {
            securityUtilsMock.when(SecurityUtils::getCurrentUserLogin)
                .thenReturn(Optional.of("login1"));
            securityUtilsMock.when(SecurityUtils::getAuthenticationTypeUserLogin)
                .thenReturn(Optional.of(AuthenticationType.FORM_LOGIN));

            when(authUserRepository.findOneByLoginAndNotDeleted("login1", AuthenticationType.FORM_LOGIN))
                .thenReturn(Optional.of(user));
            when(passwordEncoder.matches("current", "encodedPass")).thenReturn(true);
            when(passwordEncoder.encode("newPass")).thenReturn("newEncoded");

            authUserService.changePassword("current", "newPass");

            assertEquals("newEncoded", user.getPassword());
            assertEquals(0, user.getFailedLoginAttempts());
            verify(authUserHistoryRepository, times(1)).save(any(AuthUserHistory.class));
        }
    }

    @Test
    void testCompletePasswordReset_Success() {
        AuthUser user = new AuthUser();
        user.setResetDate(Instant.now());

        // Mock ApplicationProperties.Password
        ApplicationProperties.Password passwordProps = mock(ApplicationProperties.Password.class);
        when(passwordProps.getDayPasswordExpired()).thenReturn(90); // example expiration days
        when(properties.getPassword()).thenReturn(passwordProps);

        when(authUserRepository.findOneByResetKey("reset123")).thenReturn(Optional.of(user));
        when(passwordEncoder.encode("newPass")).thenReturn("newEncoded");

        Optional<AuthUser> result = authUserService.completePasswordReset("newPass", "reset123");

        assertTrue(result.isPresent());
        assertEquals("newEncoded", result.orElseThrow().getPassword());
        assertEquals(90, result.orElseThrow().getPasswordExpiredDay());
    }

    @Test
    void testDeleteUser_Success() {
        AuthUser user = new AuthUser();
        when(authUserRepository.findOneByLoginAndNotDeleted("login1", AuthenticationType.FORM_LOGIN))
            .thenReturn(Optional.of(user));
        when(authUserRepository.save(user)).thenReturn(user);

        boolean deleted = authUserService.deleteUser("login1");

        assertTrue(deleted);
        assertTrue(user.isDeleted());
        assertFalse(user.getActivated());
    }

}
