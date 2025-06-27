package com.nexigroup.pagopa.cruscotto.security;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

import com.nexigroup.pagopa.cruscotto.IntegrationTest;
import com.nexigroup.pagopa.cruscotto.domain.AuthGroup;
import com.nexigroup.pagopa.cruscotto.domain.AuthUser;
import com.nexigroup.pagopa.cruscotto.domain.enumeration.AuthenticationType;
import com.nexigroup.pagopa.cruscotto.repository.AuthGroupRepository;
import com.nexigroup.pagopa.cruscotto.repository.AuthUserRepository;
import com.nexigroup.pagopa.cruscotto.service.AuthUserService;
import java.util.Locale;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.transaction.annotation.Transactional;

/**
 * Integrations tests for {@link DomainUserDetailsService}.
 */
@Transactional
@IntegrationTest
class DomainUserDetailsServiceIT {

    private static final String USER_LOGIN_NOT_FOUND = "test-user-one-not-found";
    private static final String USER_ONE_LOGIN = "test-user-one";
    private static final String USER_ONE_EMAIL = "test-user-one@localhost";
    private static final String USER_TWO_LOGIN = "test-user-two";
    private static final String USER_TWO_EMAIL = "test-user-two@localhost";
    private static final String USER_THREE_LOGIN = "test-user-three";
    private static final String USER_THREE_EMAIL = "test-user-three@localhost";

    private static final String ADMIN_TEST = "ADMIN_TEST";

    @Autowired
    private AuthUserRepository userRepository;

    @Autowired
    private AuthGroupRepository authGroupRepository;

    @Autowired
    private AuthUserService userService;

    @Autowired
    @Qualifier("userDetailsService")
    private UserDetailsService domainUserDetailsService;

    private AuthGroup authGroup;

    public AuthUser getUserOne() {
        AuthUser userOne = new AuthUser();
        userOne.setAuthenticationType(AuthenticationType.FORM_LOGIN);
        userOne.setLogin(USER_ONE_LOGIN);
        userOne.setPassword(RandomStringUtils.insecure().nextAlphanumeric(60));
        userOne.setActivated(true);
        userOne.setEmail(USER_ONE_EMAIL);
        userOne.setFirstName("userOne");
        userOne.setLastName("doe");
        userOne.setLangKey("en");
        userOne.setGroup(authGroup);
        return userOne;
    }

    public AuthUser getUserTwo() {
        AuthUser userTwo = new AuthUser();
        userTwo.setAuthenticationType(AuthenticationType.FORM_LOGIN);
        userTwo.setLogin(USER_TWO_LOGIN);
        userTwo.setPassword(RandomStringUtils.insecure().nextAlphanumeric(60));
        userTwo.setActivated(true);
        userTwo.setEmail(USER_TWO_EMAIL);
        userTwo.setFirstName("userTwo");
        userTwo.setLastName("doe");
        userTwo.setLangKey("en");
        userTwo.setGroup(authGroup);
        return userTwo;
    }

    public AuthUser getUserThree() {
        AuthUser userThree = new AuthUser();
        userThree.setAuthenticationType(AuthenticationType.FORM_LOGIN);
        userThree.setLogin(USER_THREE_LOGIN);
        userThree.setPassword(RandomStringUtils.insecure().nextAlphanumeric(60));
        userThree.setActivated(false);
        userThree.setEmail(USER_THREE_EMAIL);
        userThree.setFirstName("userThree");
        userThree.setLastName("doe");
        userThree.setLangKey("en");
        userThree.setGroup(authGroup);
        return userThree;
    }

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
        userRepository.save(getUserOne());
        userRepository.save(getUserTwo());
        userRepository.save(getUserThree());
    }

    @AfterEach
    public void cleanup() {
        userService.deleteUser(USER_ONE_LOGIN);
        userService.deleteUser(USER_TWO_LOGIN);
        userService.deleteUser(USER_THREE_LOGIN);
        authGroupRepository.delete(authGroup);
    }

    @Test
    void assertThatUserCanBeFoundByLogin() {
        UserDetails userDetails = domainUserDetailsService.loadUserByUsername(USER_ONE_LOGIN);
        assertThat(userDetails).isNotNull();
        assertThat(userDetails.getUsername()).isEqualTo(USER_ONE_LOGIN);
    }

    @Test
    void assertThatUserCanBeFoundByLoginIgnoreCase() {
        UserDetails userDetails = domainUserDetailsService.loadUserByUsername(USER_ONE_LOGIN.toUpperCase(Locale.ENGLISH));
        assertThat(userDetails).isNotNull();
        assertThat(userDetails.getUsername()).isEqualTo(USER_ONE_LOGIN);
    }

    @Test
    void assertThatUserCanNotBeFoundByLogin() {
        assertThatExceptionOfType(UsernameNotFoundException.class).isThrownBy(() ->
                domainUserDetailsService.loadUserByUsername(USER_LOGIN_NOT_FOUND)
        );
    }

    @Test
    void assertThatUserNotActivatedExceptionIsThrownForNotActivatedUsers() {
        assertThatExceptionOfType(UserNotActivatedException.class).isThrownBy(() ->
            domainUserDetailsService.loadUserByUsername(USER_THREE_LOGIN)
        );
    }
}
