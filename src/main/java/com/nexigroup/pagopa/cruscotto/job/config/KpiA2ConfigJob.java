package com.nexigroup.pagopa.cruscotto.job.config;

import org.quartz.CronScheduleBuilder;
import org.quartz.JobBuilder;
import org.quartz.JobDataMap;
import org.quartz.JobDetail;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.nexigroup.pagopa.cruscotto.config.ApplicationProperties;
import com.nexigroup.pagopa.cruscotto.job.kpi.a2.KpiA2Job;

@Configuration
public class KpiA2ConfigJob {

    @Bean
    public Trigger kpiA2JobTrigger(ApplicationProperties applicationProperties) {
        CronScheduleBuilder scheduleBuilder = CronScheduleBuilder.cronSchedule(applicationProperties.getJob().getKpiA2Job().getCron())
        														 .withMisfireHandlingInstructionFireAndProceed();

        return TriggerBuilder.newTrigger()
        					 .forJob(kpiA2JobDetail())
        					 .withIdentity(JobConstant.KPI_A2, "DEFAULT")
        					 .withSchedule(scheduleBuilder)
        					 .build();
    }

    @Bean
    public JobDetail kpiA2JobDetail() {
        return JobBuilder.newJob(KpiA2Job.class)
        				 .withIdentity(JobConstant.KPI_A2, "DEFAULT")
        				 .setJobData(new JobDataMap())
        				 .storeDurably()
        				 .build();
    }
}
