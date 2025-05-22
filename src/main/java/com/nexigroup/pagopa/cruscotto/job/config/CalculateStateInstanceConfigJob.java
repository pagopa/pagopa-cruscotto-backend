package com.nexigroup.pagopa.cruscotto.job.config;

import com.nexigroup.pagopa.cruscotto.job.kpi.CalculateStateInstanceJob;
import org.quartz.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CalculateStateInstanceConfigJob {

    @Bean
    public JobDetail calculateStateInstanceJobDetail() {
        return JobBuilder.newJob(CalculateStateInstanceJob.class)
            .withIdentity(JobConstant.CALCULATE_STATE_INSTANCE_JOB, "DEFAULT")
            .setJobData(new JobDataMap())
            .storeDurably()
            .build();
    }
}
