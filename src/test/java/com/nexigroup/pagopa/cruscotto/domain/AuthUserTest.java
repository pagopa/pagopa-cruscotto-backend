package com.nexigroup.pagopa.cruscotto.domain;

import static org.assertj.core.api.Assertions.assertThat;

import com.nexigroup.pagopa.cruscotto.domain.enumeration.AuthenticationType;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.time.ZonedDateTime;
import java.util.HashSet;

class AuthUserTest {

    @Test
    void testGettersAndSetters() {
        AuthUser user = new AuthUser();

        user.setId(1L);
        user.setLogin("testLogin");
        user.setPassword("hashedPassword123");
        user.setFirstName("John");
        user.setLastName("Doe");
        user.setEmail("john.doe@example.com");
        user.setActivated(true);
        user.setLangKey("en");
        user.setImageUrl("http://image.url");
        user.setActivationKey("activationKey");
        user.setResetKey("resetKey");
        user.setResetDate(Instant.now());
        user.setFailedLoginAttempts(3);
        user.setLastPasswordChangeDate(ZonedDateTime.now());
        user.setPasswordExpiredDay(90);
        user.setBlocked(true);
        user.setDeleted(true);
        user.setDeletedDate(ZonedDateTime.now());
        user.setAuthenticationType(AuthenticationType.FORM_LOGIN);
        user.setAuthUserHistory(new HashSet<>());

        assertThat(user.getId()).isEqualTo(1L);
        assertThat(user.getLogin()).isEqualTo("testLogin");
        assertThat(user.getPassword()).isEqualTo("hashedPassword123");
        assertThat(user.getFirstName()).isEqualTo("John");
        assertThat(user.getLastName()).isEqualTo("Doe");
        assertThat(user.getEmail()).isEqualTo("john.doe@example.com");
        assertThat(user.getActivated()).isTrue();
        assertThat(user.getLangKey()).isEqualTo("en");
        assertThat(user.getImageUrl()).isEqualTo("http://image.url");
        assertThat(user.getActivationKey()).isEqualTo("activationKey");
        assertThat(user.getResetKey()).isEqualTo("resetKey");
        assertThat(user.getFailedLoginAttempts()).isEqualTo(3);
        assertThat(user.getPasswordExpiredDay()).isEqualTo(90);
        assertThat(user.isBlocked()).isTrue();
        assertThat(user.isDeleted()).isTrue();
        assertThat(user.getAuthenticationType()).isEqualTo(AuthenticationType.FORM_LOGIN);
        assertThat(user.getAuthUserHistory()).isNotNull();
    }

    @Test
    void testEqualsAndHashCode() {
        AuthUser user1 = new AuthUser();
        user1.setId(1L);

        AuthUser user2 = new AuthUser();
        user2.setId(1L);

        AuthUser user3 = new AuthUser();
        user3.setId(2L);

        assertThat(user1).isEqualTo(user2);
        assertThat(user1).isNotEqualTo(user3);
        assertThat(user1.hashCode()).isEqualTo(user2.hashCode());
    }

    @Test
    void testToStringContainsFields() {
        AuthUser user = new AuthUser();
        user.setLogin("testLogin");
        user.setFirstName("John");
        user.setLastName("Doe");
        user.setEmail("john.doe@example.com");
        user.setImageUrl("http://image.url");
        user.setActivated(true);
        user.setLangKey("en");
        user.setActivationKey("activationKey");

        String result = user.toString();

        assertThat(result).contains("testLogin");
        assertThat(result).contains("John");
        assertThat(result).contains("Doe");
        assertThat(result).contains("john.doe@example.com");
        assertThat(result).contains("http://image.url");
        assertThat(result).contains("true");
        assertThat(result).contains("activationKey");
    }

    @Test
    void testDefaultValues() {
        AuthUser user = new AuthUser();

        assertThat(user.getPasswordExpiredDay()).isEqualTo(AuthUser.DEFAULT_DAY_PASSWORD_EXPIRED);
        assertThat(user.getActivated()).isFalse();
        assertThat(user.isBlocked()).isFalse();
        assertThat(user.isDeleted()).isFalse();
    }
}
