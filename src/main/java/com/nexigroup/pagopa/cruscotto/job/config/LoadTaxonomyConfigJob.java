package com.nexigroup.pagopa.cruscotto.job.config;

import com.nexigroup.pagopa.cruscotto.config.ApplicationProperties;

import com.nexigroup.pagopa.cruscotto.job.taxonomy.LoadTaxonomyJob;
import org.quartz.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class LoadTaxonomyConfigJob {

	@Bean
	public Trigger loadTaxonomyJobTrigger(ApplicationProperties applicationProperties) {
		CronScheduleBuilder scheduleBuilder = CronScheduleBuilder
				.cronSchedule(applicationProperties.getJob().getLoadTaxonomyJob().getCron())
				.withMisfireHandlingInstructionFireAndProceed();

		return TriggerBuilder.newTrigger().forJob(loadTaxonomyJobDetail())
				.withIdentity(JobConstant.LOAD_TAXONOMY_JOB, "DEFAULT").withSchedule(scheduleBuilder)
				.build();
	}

	@Bean
	public JobDetail loadTaxonomyJobDetail() {
		return JobBuilder.newJob(LoadTaxonomyJob.class)
				.withIdentity(JobConstant.LOAD_TAXONOMY_JOB, "DEFAULT").setJobData(new JobDataMap())
				.storeDurably().build();
	}
}
