package com.nexigroup.pagopa.cruscotto.config;

import com.nexigroup.pagopa.cruscotto.job.client.PagoPaClient;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.Map;

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
    public static class Job
    {
        private LoadTaxonomyJob loadTaxonomyJob = new LoadTaxonomyJob();
    }

    @Getter
    @Setter
    public static class LoadTaxonomyJob {

        private boolean enabled;

        private String cron;
    }

    @Getter
    @Setter
    public static class Quartz
    {
        private Boolean exposeSchedulerInRepository;
    }

    @Setter
    @Getter
    public static class PagoPaClient {

        private String proxyHost;

        private Integer proxyPort;

        private String host;

        private Map<String, String> api;
    }
}
