package com.nexigroup.pagopa.cruscotto.job.config;

import com.nexigroup.pagopa.cruscotto.config.ApplicationProperties;
import com.nexigroup.pagopa.cruscotto.service.QrtzLogTriggerExecutedService;
import java.nio.charset.StandardCharsets;
import java.text.MessageFormat;
import java.time.Instant;
import lombok.Getter;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.JobListener;
import org.quartz.Trigger;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class DbLoggingJobListener implements JobListener {

    private static final Logger LOGGER = LoggerFactory.getLogger(DbLoggingJobListener.class);

    private static final String MESSAGE_JOB_TO_BE_FIRED = "Job {1}.{0} fired (by trigger {4}.{3}) at: {2, date, HH:mm:ss MM/dd/yyyy}";
    private static final String MESSAGE_JOB_SUCCESS = "Job {1}.{0} execution complete at {2, date, HH:mm:ss MM/dd/yyyy} and reports: {8}";
    private static final String MESSAGE_JOB_FAILED = "Job {1}.{0} execution failed at {2, date, HH:mm:ss MM/dd/yyyy} and reports: {8}";
    private static final String MESSAGE_JOB_VETOED =
        "Job {1}.{0} was vetoed. It was to be fired (by trigger {4}.{3}) at: {2, date, HH:mm:ss MM/dd/yyyy}";

    private final QrtzLogTriggerExecutedService qrtzLogTriggerExecutedService;

    private final ApplicationProperties applicationProperties;

    public DbLoggingJobListener(QrtzLogTriggerExecutedService qrtzLogTriggerExecutedService, ApplicationProperties applicationProperties) {
        this.qrtzLogTriggerExecutedService = qrtzLogTriggerExecutedService;
        this.applicationProperties = applicationProperties;
    }

    @Override
    public String getName() {
        return DbLoggingJobListener.class.getSimpleName();
    }

    @Override
    public void jobToBeExecuted(JobExecutionContext context) {
        if (isLoggingDisabled()) {
            return;
        }
        LOGGER.info(MessageFormat.format(MESSAGE_JOB_TO_BE_FIRED, buildBaseLogArgs(context)));

        qrtzLogTriggerExecutedService.jobToBeExecuted(context.getFireInstanceId(), context.getFireTime().toInstant());
    }

    @Override
    public void jobExecutionVetoed(JobExecutionContext context) {
        if (isLoggingDisabled()) {
            return;
        }
        LOGGER.info(MessageFormat.format(MESSAGE_JOB_VETOED, buildBaseLogArgs(context)));
    }

    @Override
    public void jobWasExecuted(JobExecutionContext context, JobExecutionException jobException) {
        if (isLoggingDisabled()) {
            return;
        }

        if (jobException != null) {
            logFailedExecution(context, jobException);
        } else {
            logSuccessfulExecution(context);
        }

        qrtzLogTriggerExecutedService.jobWasExecuted(
            context.getFireInstanceId(),
            Instant.now(),
            jobException != null ? ExceptionUtils.getStackTrace(jobException).getBytes(StandardCharsets.UTF_8) : null
        );
    }

    private boolean isLoggingDisabled() {
        return !applicationProperties.getQuartz().getActiveDbLog();
    }

    private Object[] buildBaseLogArgs(JobExecutionContext context) {
        Trigger trigger = context.getTrigger();
        return new Object[] {
            context.getJobDetail().getKey().getName(),
            context.getJobDetail().getKey().getGroup(),
            new java.util.Date(),
            trigger.getKey().getName(),
            trigger.getKey().getGroup(),
            trigger.getPreviousFireTime(),
            trigger.getNextFireTime(),
            context.getRefireCount(),
        };
        //        context.getFireInstanceId()
    }

    private void logFailedExecution(JobExecutionContext context, JobExecutionException exception) {
        Object[] args = ArrayUtils.add(buildBaseLogArgs(context), exception.getMessage());
        LOGGER.warn(MessageFormat.format(MESSAGE_JOB_FAILED, args), exception);
    }

    private void logSuccessfulExecution(JobExecutionContext context) {
        Object[] args = ArrayUtils.add(buildBaseLogArgs(context), String.valueOf(context.getResult()));
        LOGGER.info(MessageFormat.format(MESSAGE_JOB_SUCCESS, args));
    }
}
