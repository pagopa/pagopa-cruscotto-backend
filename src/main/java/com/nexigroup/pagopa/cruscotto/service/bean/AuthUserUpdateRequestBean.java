package com.nexigroup.pagopa.cruscotto.service.bean;

import com.nexigroup.pagopa.cruscotto.service.validation.ValidAuthGroup;
import jakarta.validation.constraints.*;

public class AuthUserUpdateRequestBean {

    @NotNull
    @Digits(integer = 19, fraction = 0)
    private Long id;

    @NotBlank
    @Size(max = 50)
    private String firstName;

    @NotBlank
    @Size(max = 50)
    private String lastName;

    @Email
    @Size(min = 5, max = 254)
    private String email;

    private String password;

    @Size(min = 2, max = 10)
    private String langKey;

    @NotNull
    @Digits(integer = 19, fraction = 0)
    @ValidAuthGroup
    private Long groupId;

    public AuthUserUpdateRequestBean() {
        // Empty constructor needed for Jackson.
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getLangKey() {
        return langKey;
    }

    public void setLangKey(String langKey) {
        this.langKey = langKey;
    }

    public Long getGroupId() {
        return groupId;
    }

    public void setGroupId(Long groupId) {
        this.groupId = groupId;
    }

    @Override
    public String toString() {
        return (
            "UserUpdateRequestBean{" +
            ", id='" +
            id +
            '\'' +
            ", firstName='" +
            firstName +
            '\'' +
            ", lastName='" +
            lastName +
            '\'' +
            ", email='" +
            email +
            '\'' +
            ", langKey='" +
            langKey +
            '\'' +
            ", groupId=" +
            groupId +
            "}"
        );
    }
}
