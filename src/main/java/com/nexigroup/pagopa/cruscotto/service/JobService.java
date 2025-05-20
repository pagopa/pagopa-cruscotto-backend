package com.nexigroup.pagopa.cruscotto.service;

import com.nexigroup.pagopa.cruscotto.service.dto.JobsDTO;
import java.util.Date;
import java.util.List;

public interface JobService {
    boolean updateCronJob(String jobName, Date date, String cronExpression);

    boolean pauseJob(String jobName);

    boolean resumeJob(String jobName);

    boolean startJobNow(String jobName);

    boolean checkJobRunning(String jobName);

    boolean stopJob(String jobName);

    boolean checkJobWithName(String jobName);

    List<JobsDTO> getAllJobs();

    String getJobState(String jobName);
}
