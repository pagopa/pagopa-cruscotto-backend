package com.nexigroup.pagopa.cruscotto.job.config;

import com.nexigroup.pagopa.cruscotto.config.ApplicationProperties;
import com.nexigroup.pagopa.cruscotto.service.QrtzLogTriggerExecutedService;
import com.nexigroup.pagopa.cruscotto.service.dto.QrtzLogTriggerExecutedDTO;
import java.text.MessageFormat;
import org.quartz.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class DbLoggingTriggerListener implements TriggerListener {

    private static final Logger LOGGER = LoggerFactory.getLogger(DbLoggingTriggerListener.class);

    private static final String MESSAGE_TRIGGER_FIRED = "Trigger {1}.{0} fired job {6}.{5} at: {4, date, HH:mm:ss MM/dd/yyyy}";
    private static final String MESSAGE_TRIGGER_MISFIRED =
        "Trigger {1}.{0} misfired job {6}.{5} at: {4, date, HH:mm:ss MM/dd/yyyy}. Should have fired at: {3, date, HH:mm:ss MM/dd/yyyy}";
    private static final String MESSAGE_TRIGGER_COMPLETE =
        "Trigger {1}.{0} completed firing job {6}.{5} at {4, date, HH:mm:ss MM/dd/yyyy} with resulting trigger instruction code: {9}";

    private final QrtzLogTriggerExecutedService qrtzLogTriggerExecutedService;

    private final ApplicationProperties applicationProperties;

    public DbLoggingTriggerListener(
        QrtzLogTriggerExecutedService qrtzLogTriggerExecutedService,
        ApplicationProperties applicationProperties
    ) {
        this.qrtzLogTriggerExecutedService = qrtzLogTriggerExecutedService;
        this.applicationProperties = applicationProperties;
    }

    @Override
    public String getName() {
        return DbLoggingTriggerListener.class.getSimpleName();
    }

    @Override
    public void triggerFired(Trigger trigger, JobExecutionContext context) {
        if (isLoggingDisabled()) {
            return;
        }

        Object[] args = {
            trigger.getKey().getName(),
            trigger.getKey().getGroup(),
            trigger.getPreviousFireTime(),
            trigger.getNextFireTime(),
            new java.util.Date(),
            context.getJobDetail().getKey().getName(),
            context.getJobDetail().getKey().getGroup(),
            context.getRefireCount(),
        };

        String schedulerName = "UNKNOWN";
        try {
            schedulerName = context.getScheduler().getSchedulerName();
        } catch (SchedulerException e) {
            LOGGER.error("Error getting scheduler name", e);
        }

        logInfo(MessageFormat.format(MESSAGE_TRIGGER_FIRED, args));

        QrtzLogTriggerExecutedDTO qrtzLogTriggerExecutedDTO = new QrtzLogTriggerExecutedDTO();
        qrtzLogTriggerExecutedDTO.setFireInstanceId(context.getFireInstanceId());
        qrtzLogTriggerExecutedDTO.setTriggerName(trigger.getKey().getName());
        qrtzLogTriggerExecutedDTO.setTriggerGroup(trigger.getKey().getGroup());
        qrtzLogTriggerExecutedDTO.setJobName(context.getJobDetail().getKey().getName());
        qrtzLogTriggerExecutedDTO.setJobGroup(context.getJobDetail().getKey().getGroup());
        qrtzLogTriggerExecutedDTO.setSchedulerName(schedulerName);
        qrtzLogTriggerExecutedDTO.setScheduledTime(context.getScheduledFireTime().toInstant());
        qrtzLogTriggerExecutedDTO.setState("SCHEDULED");
        qrtzLogTriggerExecutedService.save(qrtzLogTriggerExecutedDTO);
    }

    @Override
    public boolean vetoJobExecution(Trigger trigger, JobExecutionContext context) {
        return false;
    }

    @Override
    public void triggerMisfired(Trigger trigger) {
        if (isLoggingDisabled()) {
            return;
        }

        Object[] args = {
            trigger.getKey().getName(),
            trigger.getKey().getGroup(),
            trigger.getPreviousFireTime(),
            trigger.getNextFireTime(),
            new java.util.Date(),
            trigger.getJobKey().getName(),
            trigger.getJobKey().getGroup(),
        };

        logInfo(MessageFormat.format(MESSAGE_TRIGGER_MISFIRED, args));
    }

    @Override
    public void triggerComplete(
        Trigger trigger,
        JobExecutionContext context,
        Trigger.CompletedExecutionInstruction triggerInstructionCode
    ) {
        if (isLoggingDisabled()) {
            return;
        }

        Object[] args = {
            trigger.getKey().getName(),
            trigger.getKey().getGroup(),
            trigger.getPreviousFireTime(),
            trigger.getNextFireTime(),
            new java.util.Date(),
            context.getJobDetail().getKey().getName(),
            context.getJobDetail().getKey().getGroup(),
            context.getRefireCount(),
            triggerInstructionCode.toString(),
            getInstructionDescription(triggerInstructionCode),
        };

        logInfo(MessageFormat.format(MESSAGE_TRIGGER_COMPLETE, args));
    }

    private boolean isLoggingDisabled() {
        return !applicationProperties.getQuartz().getActiveDbLog();
    }

    private String getInstructionDescription(Trigger.CompletedExecutionInstruction instruction) {
        return switch (instruction) {
            case DELETE_TRIGGER -> "DELETE TRIGGER";
            case NOOP -> "DO NOTHING";
            case RE_EXECUTE_JOB -> "RE-EXECUTE JOB";
            case SET_ALL_JOB_TRIGGERS_COMPLETE -> "SET ALL OF JOB'S TRIGGERS COMPLETE";
            case SET_TRIGGER_COMPLETE -> "SET THIS TRIGGER COMPLETE";
            default -> "UNKNOWN";
        };
    }

    private void logInfo(String message) {
        LOGGER.info(message);
    }
}
