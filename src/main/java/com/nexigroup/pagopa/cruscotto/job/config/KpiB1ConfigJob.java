package com.nexigroup.pagopa.cruscotto.job.config;

import com.nexigroup.pagopa.cruscotto.config.ApplicationProperties;
import com.nexigroup.pagopa.cruscotto.job.kpi.b1.KpiB1Job;
import org.quartz.CronScheduleBuilder;
import org.quartz.JobBuilder;
import org.quartz.JobDataMap;
import org.quartz.JobDetail;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class KpiB1ConfigJob {

    @Bean
    public Trigger kpiB1JobTrigger(ApplicationProperties applicationProperties) {
        CronScheduleBuilder scheduleBuilder = CronScheduleBuilder.cronSchedule(
            applicationProperties.getJob().getKpiB1Job().getCron()
        ).withMisfireHandlingInstructionFireAndProceed();

        return TriggerBuilder.newTrigger()
            .forJob(kpiB1JobDetail())
            .withIdentity(JobConstant.KPI_B1_JOB, "DEFAULT")
            .withSchedule(scheduleBuilder)
            .build();
    }

    @Bean
    public JobDetail kpiB1JobDetail() {
        return JobBuilder.newJob(KpiB1Job.class)
            .withIdentity(JobConstant.KPI_B1_JOB, "DEFAULT")
            .setJobData(new JobDataMap())
            .storeDurably()
            .build();
    }
}