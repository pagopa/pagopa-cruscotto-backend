package com.nexigroup.pagopa.cruscotto.job.config;

import com.nexigroup.pagopa.cruscotto.config.ApplicationProperties;
import com.nexigroup.pagopa.cruscotto.job.kpi.a1.KpiA1Job;
import org.quartz.CronScheduleBuilder;
import org.quartz.JobBuilder;
import org.quartz.JobDataMap;
import org.quartz.JobDetail;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class KpiA1ConfigJob {

    @Bean
    public Trigger kpiA1JobTrigger(ApplicationProperties applicationProperties) {
        CronScheduleBuilder scheduleBuilder = CronScheduleBuilder.cronSchedule(
            applicationProperties.getJob().getKpiA1Job().getCron()
        ).withMisfireHandlingInstructionFireAndProceed();

        return TriggerBuilder.newTrigger()
            .forJob(kpiA1JobDetail())
            .withIdentity(JobConstant.KPI_A1_JOB, "DEFAULT")
            .withSchedule(scheduleBuilder)
            .build();
    }

    @Bean
    public JobDetail kpiA1JobDetail() {
        return JobBuilder.newJob(KpiA1Job.class)
            .withIdentity(JobConstant.KPI_A1_JOB, "DEFAULT")
            .setJobData(new JobDataMap())
            .storeDurably()
            .build();
    }
}
