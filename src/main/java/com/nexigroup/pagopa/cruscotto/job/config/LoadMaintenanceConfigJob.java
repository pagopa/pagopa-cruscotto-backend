package com.nexigroup.pagopa.cruscotto.job.config;

import com.nexigroup.pagopa.cruscotto.config.ApplicationProperties;
import com.nexigroup.pagopa.cruscotto.job.maintenance.LoadMaintenanceJob;
import org.quartz.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class LoadMaintenanceConfigJob {

	@Bean
	public Trigger loadMaintenanceJobTrigger(ApplicationProperties applicationProperties) {
		CronScheduleBuilder scheduleBuilder = CronScheduleBuilder
				.cronSchedule(applicationProperties.getJob().getLoadMaintenanceJob().getCron())
				.withMisfireHandlingInstructionFireAndProceed();

		return TriggerBuilder.newTrigger().forJob(loadMaintenanceJobDetail())
				.withIdentity(JobConstant.LOAD_MAINTENANCE_JOB, "DEFAULT").withSchedule(scheduleBuilder)
				.build();
	}

	@Bean
	public JobDetail loadMaintenanceJobDetail() {
		return JobBuilder.newJob(LoadMaintenanceJob.class)
				.withIdentity(JobConstant.LOAD_MAINTENANCE_JOB, "DEFAULT").setJobData(new JobDataMap())
				.storeDurably().build();
	}
}
