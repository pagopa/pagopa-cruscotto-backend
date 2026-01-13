package com.nexigroup.pagopa.cruscotto.service.impl;

import com.nexigroup.pagopa.cruscotto.domain.QQrtzLogTriggerExecuted;
import com.nexigroup.pagopa.cruscotto.domain.QTaxonomy;
import com.nexigroup.pagopa.cruscotto.domain.QrtzLogTriggerExecuted;
import com.nexigroup.pagopa.cruscotto.domain.Taxonomy;
import com.nexigroup.pagopa.cruscotto.service.JobService;
import com.nexigroup.pagopa.cruscotto.service.dto.JobsDTO;
import com.nexigroup.pagopa.cruscotto.service.dto.QrtzLogTriggerExecutedDTO;
import com.nexigroup.pagopa.cruscotto.service.dto.TaxonomyDTO;
import com.nexigroup.pagopa.cruscotto.service.filter.JobExecutionFilter;
import com.nexigroup.pagopa.cruscotto.service.qdsl.QdslUtility;
import com.nexigroup.pagopa.cruscotto.service.qdsl.QueryBuilder;
import com.nexigroup.pagopa.cruscotto.service.util.JobUtils;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.QBean;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.JPQLQuery;
import com.nexigroup.pagopa.cruscotto.job.standin.InitializeStandInDataJob;
import jakarta.transaction.Transactional;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.commons.collections4.ListUtils;
import org.apache.commons.lang3.StringUtils;
import org.quartz.*;
import org.quartz.Trigger.TriggerState;
import org.quartz.impl.matchers.GroupMatcher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;
import org.springframework.stereotype.Service;

@Service
@Transactional
public class JobServiceImpl implements JobService {

    private static final String ID_FIELD = "id";
    private static final String FIRE_INSTANCE_ID_FIELD = "fireInstanceId";
    private static final String SCHEDULED_TIME_FIELD = "scheduledTime";
    private static final String TRIGGER_GROUP_FIELD = "triggerGroup";
    private static final String TRIGGER_NAME_FIELD = "triggerName";
    private static final String INIT_FIRED_TIME_FIELD = "initFiredTime";
    private static final String END_FIRED_TIME_FIELD = "endFiredTime";
    private static final String STATE_FIELD_FIELD = "state";
    private static final String MESSAGE_EXCEPTION_FIELD = "messageException";

    private static final Logger LOGGER = LoggerFactory.getLogger(JobServiceImpl.class);

    private final SchedulerFactoryBean schedulerFactoryBean;

    private final QueryBuilder queryBuilder;
    
    private final ApplicationContext applicationContext;

    public JobServiceImpl(SchedulerFactoryBean schedulerFactoryBean, QueryBuilder queryBuilder, ApplicationContext applicationContext) {
        this.schedulerFactoryBean = schedulerFactoryBean;
        this.queryBuilder = queryBuilder;
        this.applicationContext = applicationContext;
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
     * Start a job now with parameters
     */
    @Override
    public boolean startJobNow(String jobName, Map<String, Object> jobData) {
        LOGGER.debug("Request received for starting job now with parameters.");

        String groupKey = "DEFAULT";
        JobKey jobKey = new JobKey(jobName, groupKey);
        LOGGER.debug("Parameters received for starting job now : jobKey :{}, data: {}", jobName, jobData);
        try {
            JobDataMap dataMap = new JobDataMap(jobData);
            schedulerFactoryBean.getScheduler().triggerJob(jobKey, dataMap);
            LOGGER.debug("Job with jobKey :{} started now successfully with parameters.", jobName);
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
                    jobs.setSchedulerName(scheduler.getSchedulerName());
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

    /**
     * Retrieves a paginated list of QrtzLogTriggerExecutedDTO based on the given filter criteria.
     *
     * @param filter the filter criteria to apply when retrieving the log trigger executions
     * @param pageable the pagination information, including page number and size
     * @return a paginated list of QrtzLogTriggerExecutedDTO that match the filter criteria
     */
    @Override
    public Page<QrtzLogTriggerExecutedDTO> findAll(JobExecutionFilter filter, Pageable pageable) {
        QQrtzLogTriggerExecuted qQrtzLogTriggerExecuted = QQrtzLogTriggerExecuted.qrtzLogTriggerExecuted;

        BooleanBuilder predicate = new BooleanBuilder();

        if (StringUtils.isNotBlank(filter.getSchedulerName())) {
            predicate.and(qQrtzLogTriggerExecuted.schedulerName.eq(filter.getSchedulerName()));
        }

        if (StringUtils.isNotBlank(filter.getJobGroup())) {
            predicate.and(qQrtzLogTriggerExecuted.jobGroup.eq(filter.getJobGroup()));
        }

        if (StringUtils.isNotBlank(filter.getJobName())) {
            predicate.and(qQrtzLogTriggerExecuted.jobName.eq(filter.getJobName()));
        }

        JPQLQuery<QrtzLogTriggerExecuted> jpql = queryBuilder
            .<QrtzLogTriggerExecuted>createQuery()
            .from(qQrtzLogTriggerExecuted)
            .where(predicate);

        long size = jpql.fetchCount();

        jpql.offset(pageable.getOffset());
        jpql.limit(pageable.getPageSize());

        pageable
            .getSortOr(Sort.by(Sort.Direction.DESC, "scheduledTime"))
            .forEach(order -> {
                jpql.orderBy(
                    new OrderSpecifier<>(
                        order.isAscending() ? Order.ASC : Order.DESC,
                        Expressions.stringPath(order.getProperty()),
                        QdslUtility.toQueryDslNullHandling(order.getNullHandling())
                    )
                );
            });

        List<QrtzLogTriggerExecuted> list = jpql.fetch();

        List<QrtzLogTriggerExecutedDTO> result = new ArrayList<>();
        if (list != null && !list.isEmpty()) {
            list.forEach(item -> {
                result.add(convertQrtzLogTriggerExecutedToDTO(item));
            });
        }

        return new PageImpl<>(result, pageable, size);
    }

    private QrtzLogTriggerExecutedDTO convertQrtzLogTriggerExecutedToDTO(QrtzLogTriggerExecuted qrtzLogTriggerExecuted) {
        QrtzLogTriggerExecutedDTO qrtzLogTriggerExecutedDTO = new QrtzLogTriggerExecutedDTO();
        qrtzLogTriggerExecutedDTO.setId(qrtzLogTriggerExecuted.getId());
        qrtzLogTriggerExecutedDTO.setFireInstanceId(qrtzLogTriggerExecuted.getFireInstanceId());
        qrtzLogTriggerExecutedDTO.setScheduledTime(qrtzLogTriggerExecuted.getScheduledTime());
        qrtzLogTriggerExecutedDTO.setTriggerGroup(qrtzLogTriggerExecuted.getTriggerGroup());
        qrtzLogTriggerExecutedDTO.setTriggerName(qrtzLogTriggerExecuted.getTriggerName());
        qrtzLogTriggerExecutedDTO.setInitFiredTime(qrtzLogTriggerExecuted.getInitFiredTime());
        qrtzLogTriggerExecutedDTO.setEndFiredTime(qrtzLogTriggerExecuted.getEndFiredTime());
        qrtzLogTriggerExecutedDTO.setState(qrtzLogTriggerExecuted.getState());
        qrtzLogTriggerExecutedDTO.setMessageException(
            qrtzLogTriggerExecuted.getMessageException() != null
                ? new String(qrtzLogTriggerExecuted.getMessageException(), StandardCharsets.UTF_8)
                : null
        );
        return qrtzLogTriggerExecutedDTO;
    }

    /**
     * Checks if the most recent execution of a job with the given name is in COMPLETED or ERROR state.
     *
     * @param jobName the name of the job to check
     * @return true if the most recent execution is completed (COMPLETED or ERROR state), false otherwise
     */
    @Override
    public boolean isJobExecutionCompleted(String jobName) {
        LOGGER.debug("Checking if job execution is completed for job: {}", jobName);

        QQrtzLogTriggerExecuted qQrtzLogTriggerExecuted = QQrtzLogTriggerExecuted.qrtzLogTriggerExecuted;

        try {
            // Query to get the most recent execution of the job
            JPQLQuery<QrtzLogTriggerExecuted> jpql = queryBuilder
                .<QrtzLogTriggerExecuted>createQuery()
                .from(qQrtzLogTriggerExecuted)
                .where(qQrtzLogTriggerExecuted.jobName.eq(jobName))
                .orderBy(qQrtzLogTriggerExecuted.scheduledTime.desc())
                .limit(1);

            QrtzLogTriggerExecuted latestExecution = jpql.fetchOne();

            if (latestExecution == null) {
                LOGGER.debug("No execution found for job: {}", jobName);
                return false;
            }

            String state = latestExecution.getState();
            boolean isCompleted = "COMPLETED".equals(state) || "ERROR".equals(state);

            LOGGER.debug("Job {} latest execution state: {}, isCompleted: {}", jobName, state, isCompleted);

            return isCompleted;
        } catch (Exception e) {
            LOGGER.error("Error checking job execution status for {}: {}", jobName, e.getMessage(), e);
            return false;
        }
    }
}
