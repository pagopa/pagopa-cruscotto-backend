package com.nexigroup.pagopa.cruscotto.service.impl;

import com.nexigroup.pagopa.cruscotto.service.JobService;
import com.nexigroup.pagopa.cruscotto.service.dto.JobsDTO;
import com.nexigroup.pagopa.cruscotto.service.util.JobUtils;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import org.quartz.*;
import org.quartz.Trigger.TriggerState;
import org.quartz.impl.matchers.GroupMatcher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;
import org.springframework.stereotype.Service;

@Service
public class JobServiceImpl implements JobService {

    private static final Logger LOGGER = LoggerFactory.getLogger(JobServiceImpl.class);

    private final SchedulerFactoryBean schedulerFactoryBean;

    public JobServiceImpl(SchedulerFactoryBean schedulerFactoryBean) {
        this.schedulerFactoryBean = schedulerFactoryBean;
    }

    /**
     * Update scheduled cron job.
     */
    @Override
    public boolean updateCronJob(String jobName, Date date, String cronExpression) {
        LOGGER.debug("Request received for updating cron job.");

        LOGGER.debug("Parameters received for updating cron job : jobKey :{}, date: {}", jobName, date);
        try {
            Trigger newTrigger = JobUtils.createCronTrigger(jobName, date, cronExpression, SimpleTrigger.MISFIRE_INSTRUCTION_FIRE_NOW);

            Date dt = schedulerFactoryBean.getScheduler().rescheduleJob(TriggerKey.triggerKey(jobName), newTrigger);
            LOGGER.debug("Trigger associated with jobKey :{} rescheduled successfully for date :{}", jobName, dt);
            return true;
        } catch (Exception e) {
            LOGGER.debug("SchedulerException while updating cron job with key :{} message :{}", jobName, e.getMessage());
            return false;
        }
    }

    /**
     * Pause a job
     */
    @Override
    public boolean pauseJob(String jobName) {
        LOGGER.debug("Request received for pausing job.");

        String groupKey = "DEFAULT";
        JobKey jobkey = new JobKey(jobName, groupKey);
        LOGGER.debug("Parameters received for pausing job : jobKey :{}, groupKey :{}", jobName, groupKey);

        try {
            schedulerFactoryBean.getScheduler().pauseJob(jobkey);
            LOGGER.debug("Job with jobKey :{} paused successfully.", jobName);
            return true;
        } catch (SchedulerException e) {
            LOGGER.debug("SchedulerException while pausing job with key :{} message :{}", jobName, e.getMessage());
            return false;
        }
    }

    /**
     * Resume paused job
     */
    @Override
    public boolean resumeJob(String jobName) {
        LOGGER.debug("Request received for resuming job.");

        String groupKey = "DEFAULT";

        JobKey jobKey = new JobKey(jobName, groupKey);
        LOGGER.debug("Parameters received for resuming job : jobKey :{}", jobName);
        try {
            schedulerFactoryBean.getScheduler().resumeJob(jobKey);
            LOGGER.debug("Job with jobKey :{} resumed successfully.", jobName);
            return true;
        } catch (SchedulerException e) {
            LOGGER.debug("SchedulerException while resuming job with key :{} message :{}", jobName, e.getMessage());
            return false;
        }
    }

    /**
     * Start a job now
     */
    @Override
    public boolean startJobNow(String jobName) {
        LOGGER.debug("Request received for starting job now.");

        String groupKey = "DEFAULT";
        JobKey jobKey = new JobKey(jobName, groupKey);
        LOGGER.debug("Parameters received for starting job now : jobKey :{}", jobName);
        try {
            schedulerFactoryBean.getScheduler().triggerJob(jobKey);
            LOGGER.debug("Job with jobKey :{} started now succesfully.", jobName);
            return true;
        } catch (SchedulerException e) {
            LOGGER.debug("SchedulerException while starting job now with key :{} message :{}", jobName, e.getMessage());
            return false;
        }
    }

    /**
     * Check if a job is already running
     */
    @Override
    public boolean checkJobRunning(String jobName) {
        LOGGER.debug("Request received to check if job is running");

        String groupKey = "DEFAULT";

        LOGGER.debug("Parameters received for checking job is running now : jobKey :{}", jobName);
        try {
            List<JobExecutionContext> currentJobs = schedulerFactoryBean.getScheduler().getCurrentlyExecutingJobs();
            if (currentJobs != null) {
                for (JobExecutionContext jobCtx : currentJobs) {
                    String jobNameDB = jobCtx.getJobDetail().getKey().getName();
                    String groupNameDB = jobCtx.getJobDetail().getKey().getGroup();
                    if (jobName.equalsIgnoreCase(jobNameDB) && groupKey.equalsIgnoreCase(groupNameDB)) {
                        return true;
                    }
                }
            }
        } catch (SchedulerException e) {
            LOGGER.debug("SchedulerException while checking job with key :{} is running. error message :{}", jobName, e.getMessage());
            return false;
        }
        return false;
    }

    /**
     * Stop a job
     */
    @Override
    public boolean stopJob(String jobName) {
        LOGGER.debug("JobServiceImpl.stopJob()");
        try {
            String groupKey = "DEFAULT";

            Scheduler scheduler = schedulerFactoryBean.getScheduler();
            JobKey jobKey = new JobKey(jobName, groupKey);

            return scheduler.interrupt(jobKey);
        } catch (SchedulerException e) {
            LOGGER.debug("SchedulerException while stopping job. error message :{}", e.getMessage());
        }
        return false;
    }

    /**
     * Check job exists with given name
     */
    @Override
    public boolean checkJobWithName(String jobName) {
        try {
            String groupKey = "DEFAULT";
            JobKey jobKey = new JobKey(jobName, groupKey);
            Scheduler scheduler = schedulerFactoryBean.getScheduler();
            if (scheduler.checkExists(jobKey)) {
                return true;
            }
        } catch (SchedulerException e) {
            LOGGER.debug("SchedulerException while checking job with name and group exist:{}", e.getMessage());
        }
        return false;
    }

    /**
     * Get all jobs
     */
    @Override
    public List<JobsDTO> getAllJobs() {
        List<JobsDTO> list = new ArrayList<>();
        try {
            Scheduler scheduler = schedulerFactoryBean.getScheduler();

            for (String groupName : scheduler.getJobGroupNames()) {
                for (JobKey jobKey : scheduler.getJobKeys(GroupMatcher.jobGroupEquals(groupName))) {
                    String jobName = jobKey.getName();
                    String jobGroup = jobKey.getGroup();
                    String jobStatus = "UNDEFINED";

                    List<Trigger> triggers = (List<Trigger>) scheduler.getTriggersOfJob(jobKey);
                    Date scheduleTime = null;
                    Date nextFireTime = null;
                    Date lastFiredTime = null;
                    String cron = null;
                    if (!triggers.isEmpty()) {
                        scheduleTime = triggers.get(0).getStartTime();
                        nextFireTime = triggers.get(0).getNextFireTime();
                        lastFiredTime = triggers.get(0).getPreviousFireTime();

                        if (triggers.get(0) instanceof CronTrigger) {
                            cron = ((CronTrigger) triggers.get(0)).getCronExpression();
                        }
                    }

                    JobsDTO jobs = new JobsDTO();
                    jobs.setJobName(jobName);
                    jobs.setGroupName(jobGroup);
                    jobs.setScheduleTime(scheduleTime);
                    jobs.setLastFiredTime(lastFiredTime);
                    jobs.setNextFireTime(nextFireTime);
                    jobs.setCron(cron);

                    if (checkJobRunning(jobName)) {
                        jobStatus = "RUNNING";
                    } else {
                        jobStatus = getJobState(jobName);
                    }

                    jobs.setJobStatus(jobStatus);

                    list.add(jobs);

                    LOGGER.debug("Job details:");
                    LOGGER.debug("Job Name:{}, Group Name:{}, Schedule Time:{}", jobName, groupName, scheduleTime);
                }
            }
        } catch (SchedulerException e) {
            LOGGER.error("SchedulerException while fetching all jobs. error message", e);
        }
        return list;
    }

    /**
     * Get the current state of job
     */
    public String getJobState(String jobName) {
        LOGGER.debug("JobServiceImpl.getJobState()");

        try {
            String groupKey = "DEFAULT";
            JobKey jobKey = new JobKey(jobName, groupKey);

            Scheduler scheduler = schedulerFactoryBean.getScheduler();
            JobDetail jobDetail = scheduler.getJobDetail(jobKey);

            List<? extends Trigger> triggers = scheduler.getTriggersOfJob(jobDetail.getKey());
            if (triggers != null && !triggers.isEmpty()) {
                for (Trigger trigger : triggers) {
                    TriggerState triggerState = scheduler.getTriggerState(trigger.getKey());

                    if (TriggerState.PAUSED.equals(triggerState)) {
                        return "PAUSED";
                    } else if (TriggerState.BLOCKED.equals(triggerState)) {
                        return "BLOCKED";
                    } else if (TriggerState.COMPLETE.equals(triggerState)) {
                        return "COMPLETE";
                    } else if (TriggerState.ERROR.equals(triggerState)) {
                        return "ERROR";
                    } else if (TriggerState.NONE.equals(triggerState)) {
                        return "NONE";
                    } else if (TriggerState.NORMAL.equals(triggerState)) {
                        return "SCHEDULED";
                    }
                }
            }
        } catch (SchedulerException e) {
            LOGGER.debug("SchedulerException while checking job with name and group exist:{}", e.getMessage());
        }
        return null;
    }
}
