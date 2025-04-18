package com.nexigroup.pagopa.cruscotto.service.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import com.nexigroup.pagopa.cruscotto.domain.AuthGroup;
import com.nexigroup.pagopa.cruscotto.domain.AuthUser;
import com.nexigroup.pagopa.cruscotto.security.AuthoritiesConstants;
import com.nexigroup.pagopa.cruscotto.service.dto.AuthUserDTO;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Unit tests for {@link AuthUserMapper}.
 */
class UserMapperTest {

    private static final String DEFAULT_LOGIN = "johndoe";
    private static final Long DEFAULT_ID = 1L;

    private AuthUserMapper userMapper;
    private AuthUser user;
    private AuthUserDTO userDto;

    @BeforeEach
    public void init() {
        userMapper = new AuthUserMapper();
        user = new AuthUser();
        user.setLogin(DEFAULT_LOGIN);
        user.setPassword(RandomStringUtils.insecure().nextAlphanumeric(60));
        user.setActivated(true);
        user.setEmail("johndoe@localhost");
        user.setFirstName("john");
        user.setLastName("doe");
        user.setImageUrl("image_url");
        user.setLangKey("en");

        AuthGroup group = new AuthGroup();
        group.setNome(AuthoritiesConstants.USER);
        user.setGroup(group);

        userDto = new AuthUserDTO(user);
    }

    @Test
    void testUserToUserDTO() {
        AuthUserDTO convertedUserDto = userMapper.authUserToAuthUserDTO(user);

        assertThat(convertedUserDto.getId()).isEqualTo(user.getId());
        assertThat(convertedUserDto.getLogin()).isEqualTo(user.getLogin());
        assertThat(convertedUserDto.getFirstName()).isEqualTo(user.getFirstName());
        assertThat(convertedUserDto.getLastName()).isEqualTo(user.getLastName());
        assertThat(convertedUserDto.getEmail()).isEqualTo(user.getEmail());
        assertThat(convertedUserDto.isActivated()).isEqualTo(user.getActivated());
        assertThat(convertedUserDto.getImageUrl()).isEqualTo(user.getImageUrl());
        assertThat(convertedUserDto.getLangKey()).isEqualTo(user.getLangKey());
        assertThat(convertedUserDto.getGroupName()).isEqualTo(AuthoritiesConstants.USER);
    }

    @Test
    void testUserDTOtoUser() {
        AuthUser convertedUser = userMapper.authUserDTOToAuthUser(userDto);

        assertThat(convertedUser.getId()).isEqualTo(userDto.getId());
        assertThat(convertedUser.getLogin()).isEqualTo(userDto.getLogin());
        assertThat(convertedUser.getFirstName()).isEqualTo(userDto.getFirstName());
        assertThat(convertedUser.getLastName()).isEqualTo(userDto.getLastName());
        assertThat(convertedUser.getEmail()).isEqualTo(userDto.getEmail());
        assertThat(convertedUser.getActivated()).isEqualTo(userDto.isActivated());
        assertThat(convertedUser.getImageUrl()).isEqualTo(userDto.getImageUrl());
        assertThat(convertedUser.getLangKey()).isEqualTo(userDto.getLangKey());
    }

    @Test
    void usersToUserDTOsShouldMapOnlyNonNullUsers() {
        List<AuthUser> users = new ArrayList<>();
        users.add(user);
        users.add(null);

        List<AuthUserDTO> userDTOS = userMapper.authUsersToAuthUserDTOs(users);

        assertThat(userDTOS).isNotEmpty().size().isEqualTo(1);
    }

    @Test
    void userDTOsToUsersShouldMapOnlyNonNullUsers() {
        List<AuthUserDTO> usersDto = new ArrayList<>();
        usersDto.add(userDto);
        usersDto.add(null);

        List<AuthUser> users = userMapper.authUserDTOsToAuthUsers(usersDto);

        assertThat(users).isNotEmpty().size().isEqualTo(1);
    }
}
