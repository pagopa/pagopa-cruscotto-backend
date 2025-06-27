package com.nexigroup.pagopa.cruscotto.service;

import com.nexigroup.pagopa.cruscotto.domain.QrtzLogTriggerExecuted;
import com.nexigroup.pagopa.cruscotto.service.dto.QrtzLogTriggerExecutedDTO;
import java.time.Instant;
import java.util.List;

/**
 * Service Interface for managing {@link QrtzLogTriggerExecuted}.
 */
public interface QrtzLogTriggerExecutedService {
    /**
     * Saves a QrtzLogTriggerExecutedDTO and returns the persisted entity.
     *
     * @param qrtzLogTriggerExecutedDTO the DTO containing the details of the log trigger entity to be saved
     * @return the saved QrtzLogTriggerExecutedDTO entity
     */
    QrtzLogTriggerExecutedDTO save(QrtzLogTriggerExecutedDTO qrtzLogTriggerExecutedDTO);

    /**
     * Logs information when a job is about to be executed.
     *
     * @param fireInstanceId the unique identifier of the job's firing instance
     * @param initFiredTime  the initial time when the job execution was triggered
     */
    void jobToBeExecuted(String fireInstanceId, Instant initFiredTime);

    /**
     * Logs the information after a job has been executed, including its identifier, end time, and any exception details.
     *
     * @param fireInstanceId the unique identifier of the job's firing instance
     * @param endFiredTime the time when the job execution was completed
     * @param exception serialized byte array representing exception details, if any occurred during execution
     */
    void jobWasExecuted(String fireInstanceId, Instant endFiredTime, byte[] exception);

    List<Long> findByScheduledTimeBefore(Instant scheduledTime);

    void deleteById(Long id);
}
