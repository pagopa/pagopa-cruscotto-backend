package com.nexigroup.pagopa.cruscotto.config;

import com.nexigroup.pagopa.cruscotto.job.config.DbLoggingJobListener;
import com.nexigroup.pagopa.cruscotto.job.config.DbLoggingTriggerListener;
import java.util.Map;
import java.util.Properties;
import org.quartz.*;
import org.springframework.boot.autoconfigure.quartz.QuartzProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;

@Configuration
public class QuartzConfiguration {

    private final ApplicationProperties applicationProperties;

    private final QuartzProperties quartzProperties;

    private final ApplicationContext applicationContext;

    public QuartzConfiguration(
        ApplicationProperties applicationProperties,
        QuartzProperties quartzProperties,
        ApplicationContext applicationContext
    ) {
        this.applicationProperties = applicationProperties;
        this.quartzProperties = quartzProperties;
        this.applicationContext = applicationContext;
    }

    @Bean
    public SchedulerFactoryBean schedulerFactoryBean(
        Map<String, JobDetail> jobMap,
        Map<String, Trigger> triggers,
        DbLoggingJobListener dbLoggingJobListener,
        DbLoggingTriggerListener dbLoggingTriggerListener
    ) throws SchedulerException {
        AutoWiringSpringBeanJobFactory jobFactory = new AutoWiringSpringBeanJobFactory();
        jobFactory.setApplicationContext(applicationContext);

        Properties properties = new Properties();
        properties.putAll(quartzProperties.getProperties());

        SchedulerFactoryBean scheduler = new SchedulerFactoryBean();
        scheduler.setGlobalJobListeners(dbLoggingJobListener);
        scheduler.setGlobalTriggerListeners(dbLoggingTriggerListener);
        scheduler.setQuartzProperties(properties);
        scheduler.setApplicationContextSchedulerContextKey("applicationContext");
        scheduler.setOverwriteExistingJobs(true);
        scheduler.setWaitForJobsToCompleteOnShutdown(false);
        scheduler.setStartupDelay(10);
        scheduler.setJobFactory(jobFactory);
        scheduler.setExposeSchedulerInRepository(applicationProperties.getQuartz().getExposeSchedulerInRepository());
        scheduler.setJobDetails(jobMap.values().toArray(new JobDetail[0]));
        scheduler.setTriggers(triggers.values().toArray(new Trigger[0]));

        return scheduler;
    }
}
