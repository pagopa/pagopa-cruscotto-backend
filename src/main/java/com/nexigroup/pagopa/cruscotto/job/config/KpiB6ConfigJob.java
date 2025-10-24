package com.nexigroup.pagopa.cruscotto.job.config;

import com.nexigroup.pagopa.cruscotto.config.ApplicationProperties;
import com.nexigroup.pagopa.cruscotto.job.kpi.GenericKpiJob;
import org.quartz.CronScheduleBuilder;
import org.quartz.JobBuilder;
import org.quartz.JobDataMap;
import org.quartz.JobDetail;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class KpiB6ConfigJob {

    @Bean
    public Trigger kpiB6JobTrigger(ApplicationProperties applicationProperties) {
        CronScheduleBuilder scheduleBuilder = CronScheduleBuilder.cronSchedule(
            applicationProperties.getJob().getKpiB6Job().getCron()
        ).withMisfireHandlingInstructionFireAndProceed();

        return TriggerBuilder.newTrigger()
            .forJob(kpiB6JobDetail(applicationProperties))
            .withIdentity(JobConstant.KPI_B6_JOB, "DEFAULT")
            .withSchedule(scheduleBuilder)
            .build();
    }

    @Bean
    public JobDetail kpiB6JobDetail(ApplicationProperties applicationProperties) {
        JobDataMap jobDataMap = new JobDataMap();
        jobDataMap.put("moduleCode", "B6");
        jobDataMap.put("enabled", applicationProperties.getJob().getKpiB6Job().isEnabled());
        
        return JobBuilder.newJob(GenericKpiJob.class)
            .withIdentity(JobConstant.KPI_B6_JOB, "DEFAULT")
            .setJobData(jobDataMap)
            .storeDurably()
            .build();
    }
}