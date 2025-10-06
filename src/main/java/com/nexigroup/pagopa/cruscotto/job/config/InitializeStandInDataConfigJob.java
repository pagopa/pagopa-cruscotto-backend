package com.nexigroup.pagopa.cruscotto.job.config;

import com.nexigroup.pagopa.cruscotto.job.standin.InitializeStandInDataJob;
import org.quartz.JobBuilder;
import org.quartz.JobDataMap;
import org.quartz.JobDetail;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class InitializeStandInDataConfigJob {

    @Bean
    public JobDetail initializeStandInDataJobDetail() {
        return JobBuilder.newJob(InitializeStandInDataJob.class)
            .withIdentity(JobConstant.INITIALIZE_STAND_IN_DATA_JOB, "DEFAULT")
            .setJobData(new JobDataMap())
            .storeDurably()
            .build();
    }
}