package com.nexigroup.pagopa.cruscotto.service.impl;

import com.nexigroup.pagopa.cruscotto.domain.QrtzLogTriggerExecuted;
import com.nexigroup.pagopa.cruscotto.repository.QrtzLogTriggerExecutedRepository;
import com.nexigroup.pagopa.cruscotto.service.QrtzLogTriggerExecutedService;
import com.nexigroup.pagopa.cruscotto.service.dto.QrtzLogTriggerExecutedDTO;
import com.nexigroup.pagopa.cruscotto.service.mapper.QrtzLogTriggerExecutedMapper;
import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link QrtzLogTriggerExecuted}.
 */

@Service
@Transactional
public class QrtzLogTriggerExecutedServiceImpl implements QrtzLogTriggerExecutedService {

    private final Logger log = LoggerFactory.getLogger(QrtzLogTriggerExecutedServiceImpl.class);

    private final QrtzLogTriggerExecutedRepository qrtzLogTriggerExecutedRepository;

    private final QrtzLogTriggerExecutedMapper qrtzLogTriggerExecutedMapper;

    public QrtzLogTriggerExecutedServiceImpl(
        QrtzLogTriggerExecutedRepository qrtzLogTriggerExecutedRepository,
        QrtzLogTriggerExecutedMapper qrtzLogTriggerExecutedMapper
    ) {
        this.qrtzLogTriggerExecutedRepository = qrtzLogTriggerExecutedRepository;
        this.qrtzLogTriggerExecutedMapper = qrtzLogTriggerExecutedMapper;
    }

    /**
     * Saves a QrtzLogTriggerExecuted entity.
     *
     * @param qrtzLogTriggerExecutedDTO the DTO representing the QrtzLogTriggerExecuted entity to be saved
     * @return the DTO of the saved QrtzLogTriggerExecuted entity
     */
    @Override
    public QrtzLogTriggerExecutedDTO save(QrtzLogTriggerExecutedDTO qrtzLogTriggerExecutedDTO) {
        log.debug("Request to save QrtzLogTriggerExecuted : {}", qrtzLogTriggerExecutedDTO);

        QrtzLogTriggerExecuted qrtzLogTriggerExecuted = qrtzLogTriggerExecutedMapper.toEntity(qrtzLogTriggerExecutedDTO);
        qrtzLogTriggerExecuted = qrtzLogTriggerExecutedRepository.save(qrtzLogTriggerExecuted);

        return qrtzLogTriggerExecutedMapper.toDto(qrtzLogTriggerExecuted);
    }

    /**
     * Executes the specified job functionality when triggered.
     *
     * @param fireInstanceId the unique identifier of the job firing instance
     * @param initFiredTime the timestamp indicating when the job was initialized
     */
    @Override
    public void jobToBeExecuted(String fireInstanceId, Instant initFiredTime) {
        QrtzLogTriggerExecuted qrtzLogTriggerExecuted = qrtzLogTriggerExecutedRepository
            .findFirstByFireInstanceId(fireInstanceId)
            .orElseThrow(() -> new IllegalArgumentException("Quartz log trigger executed not found for fire instance id " + fireInstanceId)
            );

        qrtzLogTriggerExecuted.setInitFiredTime(initFiredTime);
        qrtzLogTriggerExecuted.setState("EXECUTING");

        qrtzLogTriggerExecutedRepository.save(qrtzLogTriggerExecuted);
    }

    /**
     * Updates the state, exception message, and end fired time for a job
     * that was executed, and persists the updated state in the database.
     * Throws an exception if the job instance cannot be found.
     *
     * @param fireInstanceId the unique identifier for the job firing instance
     * @param endFiredTime the timestamp indicating when the job execution finished
     * @param exception the exception message as a byte array, or null if no exception occurred
     */
    @Override
    public void jobWasExecuted(String fireInstanceId, Instant endFiredTime, byte[] exception) {
        QrtzLogTriggerExecuted qrtzLogTriggerExecuted = qrtzLogTriggerExecutedRepository
            .findFirstByFireInstanceId(fireInstanceId)
            .orElseThrow(() -> new IllegalArgumentException("Quartz log trigger executed not found for fire instance id " + fireInstanceId)
            );

        qrtzLogTriggerExecuted.setEndFiredTime(endFiredTime);
        qrtzLogTriggerExecuted.setMessageException(exception);
        qrtzLogTriggerExecuted.setState(exception != null ? "ERROR" : "COMPLETED");

        qrtzLogTriggerExecutedRepository.save(qrtzLogTriggerExecuted);
    }

    @Override
    public List<Long> findByScheduledTimeBefore(Instant scheduledTime) {
        return this.qrtzLogTriggerExecutedRepository.findByScheduledTimeBefore(scheduledTime)
            .stream()
            .map(QrtzLogTriggerExecuted::getId)
            .collect(Collectors.toList());
    }

    public void deleteById(Long id) {
        qrtzLogTriggerExecutedRepository.deleteById(id);
    }
}
