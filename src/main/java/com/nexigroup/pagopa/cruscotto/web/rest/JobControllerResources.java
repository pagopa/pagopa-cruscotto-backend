package com.nexigroup.pagopa.cruscotto.web.rest;

import com.cronutils.model.CronType;
import com.cronutils.model.definition.CronDefinition;
import com.cronutils.model.definition.CronDefinitionBuilder;
import com.cronutils.parser.CronParser;
import com.nexigroup.pagopa.cruscotto.security.AuthoritiesConstants;
import com.nexigroup.pagopa.cruscotto.service.JobService;
import com.nexigroup.pagopa.cruscotto.service.dto.JobsDTO;
import com.nexigroup.pagopa.cruscotto.web.rest.errors.BadRequestAlertException;
import com.nexigroup.pagopa.cruscotto.web.rest.errors.JobErrorCode;
import com.nexigroup.pagopa.cruscotto.web.rest.util.HeaderJobUtil;
import java.util.Date;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/jobs/scheduler/")
public class JobControllerResources {

    private final Logger log = LoggerFactory.getLogger(JobControllerResources.class);

    private static final String ENTITY_NAME = "job";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final JobService jobService;

    public JobControllerResources(JobService jobService) {
        this.jobService = jobService;
    }

    @GetMapping("pause")
    @PreAuthorize("hasAuthority(\"" + AuthoritiesConstants.GTW_STRUMENTI_CONTROLLO + "\")")
    public ResponseEntity<Integer> pause(@RequestParam("jobName") String jobName) {
        log.debug("REST request to pause job : {}", jobName);

        if (!jobService.checkJobWithName(jobName)) throw new BadRequestAlertException("Invalid jobName", ENTITY_NAME, "jobNameNotFound");

        boolean checkJobRunning = jobService.checkJobRunning(jobName);

        Integer returnCode = null;
        if (!checkJobRunning) {
            boolean status = jobService.pauseJob(jobName);
            if (status) {
                returnCode = JobErrorCode.SUCCESS; //true
            } else {
                returnCode = JobErrorCode.ERROR; //false
            }
        } else {
            returnCode = JobErrorCode.JOB_ALREADY_IN_RUNNING_STATE; //false
        }

        return ResponseEntity.ok().headers(HeaderJobUtil.createJobPauseAlert(applicationName, true, ENTITY_NAME, jobName)).body(returnCode);
    }

    @GetMapping("resume")
    @PreAuthorize("hasAuthority(\"" + AuthoritiesConstants.GTW_STRUMENTI_CONTROLLO + "\")")
    public ResponseEntity<Integer> resume(@RequestParam("jobName") String jobName) {
        log.debug("REST request to resume job : {}", jobName);

        if (!jobService.checkJobWithName(jobName)) throw new BadRequestAlertException("Invalid jobName", ENTITY_NAME, "jobNameNotFound");

        String jobState = jobService.getJobState(jobName);

        Integer returnCode = null;
        if (jobState.equals("PAUSED")) {
            log.debug("Job current state is PAUSED, Resuming job...");
            boolean status = jobService.resumeJob(jobName);

            if (status) {
                returnCode = JobErrorCode.SUCCESS; //true
            } else {
                returnCode = JobErrorCode.ERROR; //false
            }
        } else {
            returnCode = JobErrorCode.JOB_NOT_IN_PAUSED_STATE; //false
        }

        return ResponseEntity.ok()
            .headers(HeaderJobUtil.createJobResumeAlert(applicationName, true, ENTITY_NAME, jobName))
            .body(returnCode);
    }

    @GetMapping("stop")
    @PreAuthorize("hasAuthority(\"" + AuthoritiesConstants.GTW_STRUMENTI_CONTROLLO + "\")")
    public ResponseEntity<Integer> stopJob(@RequestParam("jobName") String jobName) {
        log.debug("REST request to stop job : {}", jobName);

        if (!jobService.checkJobWithName(jobName)) throw new BadRequestAlertException("Invalid jobName", ENTITY_NAME, "jobNameNotFound");

        boolean checkJobRunning = jobService.checkJobRunning(jobName);

        Integer returnCode = null;
        if (checkJobRunning) {
            boolean status = jobService.stopJob(jobName);
            if (status) {
                returnCode = JobErrorCode.SUCCESS; //true
            } else {
                returnCode = JobErrorCode.ERROR; //false
            }
        } else {
            returnCode = JobErrorCode.JOB_NOT_IN_RUNNING_STATE; //false
        }

        return ResponseEntity.ok().headers(HeaderJobUtil.createJobStopAlert(applicationName, true, ENTITY_NAME, jobName)).body(returnCode);
    }

    @GetMapping("start")
    @PreAuthorize("hasAuthority(\"" + AuthoritiesConstants.GTW_STRUMENTI_CONTROLLO + "\")")
    public ResponseEntity<Integer> startJobNow(@RequestParam("jobName") String jobName) {
        log.debug("REST request to start job now: {}", jobName);

        if (!jobService.checkJobWithName(jobName)) throw new BadRequestAlertException("Invalid jobName", ENTITY_NAME, "jobNameNotFound");

        Integer returnCode = null;
        if (!jobService.checkJobRunning(jobName)) {
            boolean status = jobService.startJobNow(jobName);

            if (status) {
                returnCode = JobErrorCode.SUCCESS; //true
            } else {
                returnCode = JobErrorCode.ERROR; //false
            }
        } else {
            returnCode = JobErrorCode.JOB_ALREADY_IN_RUNNING_STATE; //false
        }

        return ResponseEntity.ok().headers(HeaderJobUtil.createJobStartAlert(applicationName, true, ENTITY_NAME, jobName)).body(returnCode);
    }

    @GetMapping("update")
    @PreAuthorize("hasAuthority(\"" + AuthoritiesConstants.GTW_STRUMENTI_CONTROLLO + "\")")
    public ResponseEntity<Integer> updateJob(
        @RequestParam("jobName") String jobName,
        @RequestParam("cronExpression") String cronExpression
    ) {
        log.debug("REST request to update job: {}", jobName);

        if (!jobService.checkJobWithName(jobName)) throw new BadRequestAlertException("Invalid jobName", ENTITY_NAME, "jobNameNotFound");

        CronDefinition cronDefinition = CronDefinitionBuilder.instanceDefinitionFor(CronType.QUARTZ);

        CronParser cronParser = new CronParser(cronDefinition);

        try {
            cronParser.parse(cronExpression).validate();
        } catch (IllegalArgumentException e) {
            throw new BadRequestAlertException("Cron is not valid", ENTITY_NAME, "cronNotValid");
        }

        Date jobScheduleTime = new Date();
        Integer returnCode = null;
        boolean status = jobService.updateCronJob(jobName, jobScheduleTime, cronExpression);
        if (status) {
            returnCode = JobErrorCode.SUCCESS; //true
        } else {
            returnCode = JobErrorCode.ERROR;
        }

        return ResponseEntity.ok()
            .headers(HeaderJobUtil.createJobUpdateAlert(applicationName, true, ENTITY_NAME, jobName))
            .body(returnCode);
    }

    @GetMapping("jobs")
    @PreAuthorize("hasAuthority(\"" + AuthoritiesConstants.GTW_STRUMENTI_CONTROLLO + "\")")
    public ResponseEntity<List<JobsDTO>> getAllJobs() {
        log.debug("REST request to get all Jobs");

        List<JobsDTO> list = jobService.getAllJobs();
        return ResponseEntity.ok().body(list);
    }
}
