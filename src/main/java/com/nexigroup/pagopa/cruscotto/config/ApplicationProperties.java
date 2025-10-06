package com.nexigroup.pagopa.cruscotto.config;

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
    
    private final AuthGroup authGroup = new AuthGroup();
    

    @Setter
    @Getter
    public static class Liquibase {

        private Boolean asyncStart = true;

        private String dbVersion;
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

        private KpiA1Job kpiA1Job = new KpiA1Job();

        private KpiA2Job kpiA2Job = new KpiA2Job();

        private KpiB2Job kpiB2Job = new KpiB2Job();

        private KpiB9Job kpiB9Job = new KpiB9Job();

        private KpiB3Job kpiB3Job = new KpiB3Job();

        private KpiB4Job kpiB4Job = new KpiB4Job();

        private LoadStandInDataJob loadStandInDataJob = new LoadStandInDataJob();

        private ClearLogJob clearLogJob = new ClearLogJob();
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
    public static class KpiA1Job {

        private boolean enabled = true;

        private String cron;

        private int limit;
    }

    @Getter
    @Setter
    public static class KpiA2Job {

        private boolean enabled = true;

        private String cron;

        private int limit;
    }

    @Getter
    @Setter
    public static class KpiB2Job {

        private boolean enabled = true;

        private String cron;

        private int limit;
    }

    @Getter
    @Setter
    public static class KpiB9Job {

        private boolean enabled = true;

        private String cron;

        private int limit;
    }

    @Getter
    @Setter
    public static class KpiB3Job {

        private boolean enabled = true;

        private String cron;

        private int limit;
    }

    @Getter
    @Setter
    public static class KpiB4Job {

        private boolean enabled = true;

        private String cron;

        private int limit;
    }

    @Getter
    @Setter
    public static class ClearLogJob {

        private boolean enabled;

        private String cron;

        private Integer days;
    }

    @Getter
    @Setter
    public static class Quartz {

        private Boolean exposeSchedulerInRepository;

        private Boolean activeDbLog;
    }

    @Setter
    @Getter
    public static class PagoPaClient {

        private String proxyHost;

        private Integer proxyPort;

        private Api taxonomy;

        private Api backOffice;

        private Api cache;

        private Api standIn;

        @Setter
        @Getter
        public static class Api {

            private String url;

            private String apiKeyName;

            private String apiKeyValue;
        }
    }
    
    @Setter
    @Getter
    public static class LoadStandInDataJob {

        private boolean enabled;

        private String cronExpression = "0 0 0 * * ?"; // Every day at midnight
    }

    @Setter
    @Getter
    public static class AuthGroup {

        private String superAdmin;
    }    
}
