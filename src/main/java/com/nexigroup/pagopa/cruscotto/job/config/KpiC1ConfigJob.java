package com.nexigroup.pagopa.cruscotto.job.config;

import com.nexigroup.pagopa.cruscotto.config.ApplicationProperties;
import com.nexigroup.pagopa.cruscotto.job.kpi.c1.KpiC1Job;
import org.quartz.CronScheduleBuilder;
import org.quartz.JobBuilder;
import org.quartz.JobDataMap;
import org.quartz.JobDetail;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class KpiC1ConfigJob {

    @Bean
    public Trigger kpiC1JobTrigger(ApplicationProperties applicationProperties) {
        CronScheduleBuilder scheduleBuilder = CronScheduleBuilder.cronSchedule(
            applicationProperties.getJob().getKpiC1Job().getCron()
        ).withMisfireHandlingInstructionFireAndProceed();

        return TriggerBuilder.newTrigger()
            .forJob(kpiC1JobDetail())
            .withIdentity(JobConstant.KPI_C1_JOB, "DEFAULT")
            .withSchedule(scheduleBuilder)
            .build();
    }

    @Bean
    public JobDetail kpiC1JobDetail() {
        return JobBuilder.newJob(KpiC1Job.class)
            .withIdentity(JobConstant.KPI_C1_JOB, "DEFAULT")
            .setJobData(new JobDataMap())
            .storeDurably()
            .build();
    }
}