package com.nexigroup.pagopa.cruscotto.service.bean;

import jakarta.validation.constraints.NotNull;

/**
 * A DTO representing a password change required data - current and new password.
 */
public class PasswordChangeRequestBean {

    private String currentPassword;

    @NotNull
    private String newPassword;

    public PasswordChangeRequestBean() {
        // Empty constructor needed for Jackson.
    }

    public PasswordChangeRequestBean(String currentPassword, String newPassword) {
        this.currentPassword = currentPassword;
        this.newPassword = newPassword;
    }

    public String getCurrentPassword() {
        return currentPassword;
    }

    public void setCurrentPassword(String currentPassword) {
        this.currentPassword = currentPassword;
    }

    public String getNewPassword() {
        return newPassword;
    }

    public void setNewPassword(String newPassword) {
        this.newPassword = newPassword;
    }
}
