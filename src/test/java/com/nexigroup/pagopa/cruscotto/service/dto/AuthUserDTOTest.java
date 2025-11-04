package com.nexigroup.pagopa.cruscotto.service.dto;

import com.nexigroup.pagopa.cruscotto.domain.AuthUser;
import com.nexigroup.pagopa.cruscotto.domain.AuthGroup;
import com.nexigroup.pagopa.cruscotto.domain.enumeration.AuthenticationType;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

class AuthUserDTOTest {

    @Test
    void testNoArgsConstructor() {
        AuthUserDTO dto = new AuthUserDTO();
        assertThat(dto).isNotNull();
    }

    @Test
    void testConstructorWithAuthUser() {
        AuthUser user = createAuthUser();
        AuthUserDTO dto = new AuthUserDTO(user);

        assertThat(dto.getId()).isEqualTo(user.getId());
        assertThat(dto.getLogin()).isEqualTo(user.getLogin());
        assertThat(dto.getFirstName()).isEqualTo(user.getFirstName());
        assertThat(dto.getLastName()).isEqualTo(user.getLastName());
        assertThat(dto.getEmail()).isEqualTo(user.getEmail());
        assertThat(dto.isActivated()).isEqualTo(user.getActivated());
        assertThat(dto.getImageUrl()).isEqualTo(user.getImageUrl());
        assertThat(dto.getLangKey()).isEqualTo(user.getLangKey());
        assertThat(dto.getCreatedBy()).isEqualTo(user.getCreatedBy());
        assertThat(dto.getCreatedDate()).isEqualTo(user.getCreatedDate());
        assertThat(dto.getLastModifiedBy()).isEqualTo(user.getLastModifiedBy());
        assertThat(dto.getLastModifiedDate()).isEqualTo(user.getLastModifiedDate());
        assertThat(dto.isBlocked()).isEqualTo(user.isBlocked());
        assertThat(dto.isDeleted()).isEqualTo(user.isDeleted());
        assertThat(dto.getDeletedDate()).isEqualTo(user.getDeletedDate());
        assertThat(dto.getAuthenticationType()).isEqualTo(user.getAuthenticationType());
        assertThat(dto.getGroupId()).isEqualTo(user.getGroup().getId());
        assertThat(dto.getGroupName()).isEqualTo(user.getGroup().getNome());
    }

    @Test
    void testConstructorWithAuthUserAndAuthorities() {
        AuthUser user = createAuthUser();
        Set<String> authorities = Set.of("ROLE_USER", "ROLE_ADMIN");
        AuthUserDTO dto = new AuthUserDTO(user, authorities);

        assertThat(dto.getAuthorities()).isEqualTo(authorities);
        assertThat(dto.getId()).isEqualTo(user.getId());
    }

    @Test
    void testSettersAndGetters() {
        AuthUserDTO dto = new AuthUserDTO();

        dto.setId(1L);
        dto.setLogin("testUser");
        dto.setFirstName("First");
        dto.setLastName("Last");
        dto.setEmail("test@example.com");
        dto.setImageUrl("http://image.url");
        dto.setActivated(true);
        dto.setLangKey("en");
        dto.setCreatedBy("creator");
        dto.setCreatedDate(Instant.now());
        dto.setLastModifiedBy("modifier");
        dto.setLastModifiedDate(Instant.now());
        dto.setGroupId(2L);
        dto.setGroupName("GroupName");
        dto.setAuthorities(Set.of("ROLE_USER"));
        dto.setPasswordExpiredDate(LocalDate.now());
        dto.setBlocked(true);
        dto.setDeleted(true);
        dto.setAuthenticationType(AuthenticationType.FORM_LOGIN);
        dto.setDeletedDate(ZonedDateTime.now());

        assertThat(dto.getId()).isEqualTo(1L);
        assertThat(dto.getLogin()).isEqualTo("testUser");
        assertThat(dto.getFirstName()).isEqualTo("First");
        assertThat(dto.getLastName()).isEqualTo("Last");
        assertThat(dto.getEmail()).isEqualTo("test@example.com");
        assertThat(dto.getImageUrl()).isEqualTo("http://image.url");
        assertThat(dto.isActivated()).isTrue();
        assertThat(dto.getLangKey()).isEqualTo("en");
        assertThat(dto.getCreatedBy()).isEqualTo("creator");
        assertThat(dto.getLastModifiedBy()).isEqualTo("modifier");
        assertThat(dto.getGroupId()).isEqualTo(2L);
        assertThat(dto.getGroupName()).isEqualTo("GroupName");
        assertThat(dto.getAuthorities()).contains("ROLE_USER");
        assertThat(dto.getPasswordExpiredDate()).isEqualTo(LocalDate.now());
        assertThat(dto.isBlocked()).isTrue();
        assertThat(dto.isDeleted()).isTrue();
        assertThat(dto.getAuthenticationType()).isEqualTo(AuthenticationType.FORM_LOGIN);
        assertThat(dto.getDeletedDate()).isNotNull();
    }

    private AuthUser createAuthUser() {
        AuthUser user = new AuthUser();
        user.setId(1L);
        user.setLogin("testUser");
        user.setFirstName("First");
        user.setLastName("Last");
        user.setEmail("test@example.com");
        user.setActivated(true);
        user.setImageUrl("http://image.url");
        user.setLangKey("en");
        user.setCreatedBy("creator");
        user.setCreatedDate(Instant.now());
        user.setLastModifiedBy("modifier");
        user.setLastModifiedDate(Instant.now());
        user.setBlocked(false);
        user.setDeleted(false);
        user.setDeletedDate(ZonedDateTime.now());
        user.setAuthenticationType(AuthenticationType.FORM_LOGIN);

        AuthGroup group = new AuthGroup();
        group.setId(2L);
        group.setNome("GroupName");
        user.setGroup(group);

        return user;
    }
}
