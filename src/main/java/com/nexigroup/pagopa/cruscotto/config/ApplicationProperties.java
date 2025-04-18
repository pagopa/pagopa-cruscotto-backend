package com.nexigroup.pagopa.cruscotto.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Properties specific to PagoPa Cruscotto Backend.
 * <p>
 * Properties are configured in the {@code application.yml} file.
 */
@ConfigurationProperties(prefix = "application", ignoreUnknownFields = true)
public class ApplicationProperties {

    private final Liquibase liquibase = new Liquibase();

    private boolean enableCsrf;

    private final Password password = new Password();

    public Liquibase getLiquibase() {
        return liquibase;
    }

    public Password getPassword() {
        return password;
    }

    public boolean isEnableCsrf() {
        return enableCsrf;
    }

    public void setEnableCsrf(boolean enableCsrf) {
        this.enableCsrf = enableCsrf;
    }

    public static class Liquibase {

        private Boolean asyncStart = true;

        public Boolean getAsyncStart() {
            return asyncStart;
        }

        public void setAsyncStart(Boolean asyncStart) {
            this.asyncStart = asyncStart;
        }
    }

    public static class Password {

        private Integer dayPasswordExpired;

        private Integer failedLoginAttempts;

        private Integer hoursKeyResetPasswordExpired;

        public Integer getDayPasswordExpired() {
            return dayPasswordExpired;
        }

        public void setDayPasswordExpired(Integer dayPasswordExpired) {
            this.dayPasswordExpired = dayPasswordExpired;
        }

        public Integer getFailedLoginAttempts() {
            return failedLoginAttempts;
        }

        public void setFailedLoginAttempts(Integer failedLoginAttempts) {
            this.failedLoginAttempts = failedLoginAttempts;
        }

        public Integer getHoursKeyResetPasswordExpired() {
            return hoursKeyResetPasswordExpired;
        }

        public void setHoursKeyResetPasswordExpired(Integer hoursKeyResetPasswordExpired) {
            this.hoursKeyResetPasswordExpired = hoursKeyResetPasswordExpired;
        }
    }
}
