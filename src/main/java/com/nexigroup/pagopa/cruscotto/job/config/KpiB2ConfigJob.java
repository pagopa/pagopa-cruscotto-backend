package com.nexigroup.pagopa.cruscotto.job.config;

import com.nexigroup.pagopa.cruscotto.config.ApplicationProperties;
import com.nexigroup.pagopa.cruscotto.job.kpi.b2.KpiB2Job;
import org.quartz.CronScheduleBuilder;
import org.quartz.JobBuilder;
import org.quartz.JobDataMap;
import org.quartz.JobDetail;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class KpiB2ConfigJob {

    @Bean
    public Trigger kpiB2JobTrigger(ApplicationProperties applicationProperties) {
        CronScheduleBuilder scheduleBuilder = CronScheduleBuilder.cronSchedule(
            applicationProperties.getJob().getKpiB2Job().getCron()
        ).withMisfireHandlingInstructionFireAndProceed();

        return TriggerBuilder.newTrigger()
            .forJob(kpiB2JobDetail())
            .withIdentity(JobConstant.KPI_B2_JOB, "DEFAULT")
            .withSchedule(scheduleBuilder)
            .build();
    }

    @Bean
    public JobDetail kpiB2JobDetail() {
        return JobBuilder.newJob(KpiB2Job.class)
            .withIdentity(JobConstant.KPI_B2_JOB, "DEFAULT")
            .setJobData(new JobDataMap())
            .storeDurably()
            .build();
    }
}
