package com.nexigroup.pagopa.cruscotto.security;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

import com.nexigroup.pagopa.cruscotto.config.Constants;
import com.nexigroup.pagopa.cruscotto.domain.AuthUser;
import com.nexigroup.pagopa.cruscotto.domain.AuthPermission;
import com.nexigroup.pagopa.cruscotto.domain.enumeration.AuthenticationType;
import com.nexigroup.pagopa.cruscotto.repository.AuthPermissionRepository;
import com.nexigroup.pagopa.cruscotto.repository.AuthUserRepository;
import com.nexigroup.pagopa.cruscotto.security.util.PasswordExpiredUtils;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Collections;
import java.util.Optional;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.MockitoAnnotations;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

class DomainUserDetailsServiceTest {

    @Mock
    private AuthUserRepository authUserRepository;

    @Mock
    private AuthPermissionRepository authPermissionRepository;

    @InjectMocks
    private DomainUserDetailsService domainUserDetailsService;

    private AuthUser authUser;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        authUser = new AuthUser();
        authUser.setLogin("testuser");
        authUser.setPassword("password");
        authUser.setActivated(true);
        authUser.setBlocked(false);
        authUser.setLastPasswordChangeDate(ZonedDateTime.now(ZoneId.systemDefault())); // fixed
        authUser.setPasswordExpiredDay(90);

        authUser.setGroup(new com.nexigroup.pagopa.cruscotto.domain.AuthGroup());
        authUser.getGroup().setNome("ROLE_USER");
        authUser.getGroup().setId(1L);
    }

    @Test
    void testLoadUserByUsername_UserFoundAndPasswordNonExpired() {
        when(authUserRepository.findOneByLoginIgnoreCaseAndNotDeleted("testuser", AuthenticationType.FORM_LOGIN))
            .thenReturn(Optional.of(authUser));

        try (MockedStatic<PasswordExpiredUtils> utilities = mockStatic(PasswordExpiredUtils.class)) {
            utilities.when(() -> PasswordExpiredUtils.isPasswordNonExpired(authUser.getLastPasswordChangeDate(), authUser.getPasswordExpiredDay()))
                .thenReturn(true);

            UserDetails userDetails = domainUserDetailsService.loadUserByUsername("testuser");

            assertThat(userDetails.getUsername()).isEqualTo(authUser.getLogin());
            assertThat(userDetails.getAuthorities())
                .extracting(GrantedAuthority::getAuthority)
                .containsExactlyInAnyOrder("ROLE_USER");
        }
    }

    @Test
    void testLoadUserByUsername_UserNotFound() {
        when(authUserRepository.findOneByLoginIgnoreCaseAndNotDeleted("unknown", AuthenticationType.FORM_LOGIN))
            .thenReturn(Optional.empty());

        assertThrows(UsernameNotFoundException.class, () -> domainUserDetailsService.loadUserByUsername("unknown"));
    }

    @Test
    void testLoadUserByUsername_UserNotActivated() {
        authUser.setActivated(false);
        when(authUserRepository.findOneByLoginIgnoreCaseAndNotDeleted("testuser", AuthenticationType.FORM_LOGIN))
            .thenReturn(Optional.of(authUser));

        assertThrows(UserNotActivatedException.class, () -> domainUserDetailsService.loadUserByUsername("testuser"));
    }

    @Test
    void testLoadUserByUsername_PasswordExpired() {
        when(authUserRepository.findOneByLoginIgnoreCaseAndNotDeleted("testuser", AuthenticationType.FORM_LOGIN))
            .thenReturn(Optional.of(authUser));

        try (MockedStatic<PasswordExpiredUtils> utilities = mockStatic(PasswordExpiredUtils.class)) {
            utilities.when(() -> PasswordExpiredUtils.isPasswordNonExpired(authUser.getLastPasswordChangeDate(), authUser.getPasswordExpiredDay()))
                .thenReturn(false);

            UserDetails userDetails = domainUserDetailsService.loadUserByUsername("testuser");

            assertThat(userDetails.getAuthorities())
                .extracting(GrantedAuthority::getAuthority)
                .containsExactly(Constants.ROLE_PASSWORD_EXPIRED);
        } // mock is automatically closed here
    }

    @Test
    void testGetAuthorities() {
        AuthPermission permission = new AuthPermission();
        permission.setNome("READ");
        permission.setModulo("MODULE");

        when(authPermissionRepository.findAllPermissionsByGroupId(1L))
            .thenReturn(Collections.singletonList(permission));

        Set<GrantedAuthority> authorities = domainUserDetailsService.getAuthorities(authUser);

        assertThat(authorities)
            .extracting(GrantedAuthority::getAuthority)
            .containsExactly("MODULE.READ");
    }
}
