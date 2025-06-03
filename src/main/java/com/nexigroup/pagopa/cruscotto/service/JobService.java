package com.nexigroup.pagopa.cruscotto.service;

import com.nexigroup.pagopa.cruscotto.service.dto.JobsDTO;
import com.nexigroup.pagopa.cruscotto.service.dto.QrtzLogTriggerExecutedDTO;
import com.nexigroup.pagopa.cruscotto.service.dto.TaxonomyDTO;
import com.nexigroup.pagopa.cruscotto.service.filter.JobExecutionFilter;
import com.nexigroup.pagopa.cruscotto.service.filter.TaxonomyFilter;
import java.util.Date;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

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

    /**
     * Retrieves a paginated list of QrtzLogTriggerExecutedDTO based on the given filter criteria.
     *
     * @param filter the filter criteria to apply when retrieving the log trigger executions
     * @param pageable the pagination information, including page number and size
     * @return a paginated list of QrtzLogTriggerExecutedDTO that match the filter criteria
     */
    Page<QrtzLogTriggerExecutedDTO> findAll(JobExecutionFilter filter, Pageable pageable);
}
