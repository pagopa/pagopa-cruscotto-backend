package com.nexigroup.pagopa.cruscotto.job.config;

import com.nexigroup.pagopa.cruscotto.config.ApplicationProperties;
import com.nexigroup.pagopa.cruscotto.job.log.ClearLogJob;
import org.quartz.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ClearLogConfigJob {

    @Bean
    public Trigger clearLogJobTrigger(ApplicationProperties applicationProperties) {
        CronScheduleBuilder scheduleBuilder = CronScheduleBuilder.cronSchedule(
            applicationProperties.getJob().getClearLogJob().getCron()
        ).withMisfireHandlingInstructionFireAndProceed();

        return TriggerBuilder.newTrigger()
            .forJob(clearLogJobDetail())
            .withIdentity(JobConstant.CLEAR_LOG_JOB, "DEFAULT")
            .withSchedule(scheduleBuilder)
            .build();
    }

    @Bean
    public JobDetail clearLogJobDetail() {
        return JobBuilder.newJob(ClearLogJob.class)
            .withIdentity(JobConstant.CLEAR_LOG_JOB, "DEFAULT")
            .setJobData(new JobDataMap())
            .storeDurably()
            .build();
    }
}
