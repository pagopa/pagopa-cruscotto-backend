package com.nexigroup.pagopa.cruscotto.config;

import com.nexigroup.pagopa.cruscotto.job.cache.LoadRegistryJob;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Properties specific to PagoPa Cruscotto Backend.
 * <p>
 * Properties are configured in the {@code application.yml} file.
 */
@Getter
@ConfigurationProperties(prefix = "application", ignoreUnknownFields = true)
public class ApplicationProperties {

    private final Liquibase liquibase = new Liquibase();

    @Setter
    private boolean enableCsrf;

    private final Password password = new Password();

    private final Job job = new Job();

    private final Quartz quartz = new Quartz();

    private final PagoPaClient pagoPaClient = new PagoPaClient();

    @Setter
    @Getter
    public static class Liquibase {

        private Boolean asyncStart = true;
    }

    @Setter
    @Getter
    public static class Password {

        private Integer dayPasswordExpired;

        private Integer failedLoginAttempts;

        private Integer hoursKeyResetPasswordExpired;
    }

    @Getter
    @Setter
    public static class Job {

        private LoadTaxonomyJob loadTaxonomyJob = new LoadTaxonomyJob();

        private LoadMaintenanceJob loadMaintenanceJob = new LoadMaintenanceJob();

        private LoadRegistryJob loadRegistryJob = new LoadRegistryJob();
    }

    @Getter
    @Setter
    public static class LoadTaxonomyJob {

        private boolean enabled;

        private String cron;
    }

    @Getter
    @Setter
    public static class LoadMaintenanceJob {

        private boolean enabled;

        private String cron;
    }

    @Getter
    @Setter
    public static class LoadRegistryJob {

        private boolean enabled;

        private String cron;
    }

    @Getter
    @Setter
    public static class Quartz {

        private Boolean exposeSchedulerInRepository;
    }

    @Setter
    @Getter
    public static class PagoPaClient {

        private String proxyHost;

        private Integer proxyPort;

        private Api taxonomy;

        private Api backOffice;

        private Api cache;

        @Setter
        @Getter
        public static class Api {

            private String url;

            private String apiKeyName;

            private String apiKeyValue;
        }
    }
}
