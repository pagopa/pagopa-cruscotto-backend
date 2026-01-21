package com.nexigroup.pagopa.cruscotto.job.standin;

import com.nexigroup.pagopa.cruscotto.config.ApplicationProperties;
import com.nexigroup.pagopa.cruscotto.domain.PagopaNumeroStandin;
import com.nexigroup.pagopa.cruscotto.repository.PagopaNumeroStandinRepository;
import com.nexigroup.pagopa.cruscotto.service.JobService;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import org.springframework.lang.NonNull;
import org.quartz.DisallowConcurrentExecution;
import org.quartz.JobExecutionContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.scheduling.quartz.QuartzJobBean;
import org.springframework.stereotype.Component;

/**
 * Job to initialize Stand-In data by loading the last N months of events.
 * Runs only once on application startup if no Stand-In data exists.
 * Loads data in configurable chunks to respect API limitations.
 */
@Component
@DisallowConcurrentExecution
public class InitializeStandInDataJob extends QuartzJobBean {

    private static final Logger LOGGER = LoggerFactory.getLogger(InitializeStandInDataJob.class);

    private static final String LOAD_STANDIN_DATA_JOB = "loadStandInDataJob";

    private static final DateTimeFormatter API_DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    private final ApplicationProperties applicationProperties;
    private final PagopaNumeroStandinRepository pagopaNumeroStandinRepository;
    private final JobService jobService;

    private volatile boolean initializationCompleted = false;

    public InitializeStandInDataJob(
        ApplicationProperties applicationProperties,
        PagopaNumeroStandinRepository pagopaNumeroStandinRepository,
        JobService jobService) {
        this.applicationProperties = applicationProperties;
        this.pagopaNumeroStandinRepository = pagopaNumeroStandinRepository;
        this.jobService = jobService;
    }



    @Override
    protected void executeInternal(@NonNull JobExecutionContext context) {
        int initializationMonths = applicationProperties.getJob().getLoadStandInDataJob().getInitializationMonths();
        LOGGER.info("Starting Stand-In data initialization for the last {} months", initializationMonths);

        try {
            if (!applicationProperties.getJob().getLoadStandInDataJob().isEnabled()) {
                LOGGER.info("LoadStandInDataJob is disabled, skipping Stand-In data initialization");
                return;
            }

            // Always run initialization but avoid duplicates in the save process
            logExistingData();
            initializeStandInDataInBackground();
            initializationCompleted = true;
            LOGGER.info("Stand-In data initialization completed successfully");
        } catch (Exception e) {
            LOGGER.error("Failed to initialize Stand-In data: {}", e.getMessage(), e);
            throw new RuntimeException("InitializeStandInDataJob failed", e);
        }
    }

    /**
     * Logs information about existing Stand-In data
     */
    private void logExistingData() {
        try {
            int initializationMonths = applicationProperties.getJob().getLoadStandInDataJob().getInitializationMonths();
            // Check if any Stand-In data exists in the last N months
            LocalDate nMonthsAgo = LocalDate.now().minusMonths(initializationMonths);
            LocalDate yesterday = LocalDate.now().minusDays(1);

            List<PagopaNumeroStandin> existingData = pagopaNumeroStandinRepository.findByDateRange(
                nMonthsAgo.atStartOfDay(),
                yesterday.atTime(23, 59, 59)
            );

            LOGGER.debug("Found {} existing Stand-In records in the last {} months",
                existingData.size(), initializationMonths);

        } catch (Exception e) {
            LOGGER.warn("Error checking existing Stand-In data: {}", e.getMessage());
        }
    }

    /**
     * Initializes Stand-In data by loading chunks in parallel for maximum performance
     */
    private void initializeStandInDataInBackground() throws InterruptedException {
        int initializationMonths = applicationProperties.getJob().getLoadStandInDataJob().getInitializationMonths();
        int chunkSizeDays = applicationProperties.getJob().getLoadStandInDataJob().getChunkSizeDays();
        int parallelChunks = applicationProperties.getJob().getLoadStandInDataJob().getParallelChunks();
        int delayBetweenChunkStartsMs = applicationProperties.getJob().getLoadStandInDataJob().getDelayBetweenChunkStartsMs();

        LocalDate endDate = LocalDate.now().minusDays(1); // Yesterday
        LocalDate startDate = endDate.minusMonths(initializationMonths);

        LOGGER.info("Initializing Stand-In data from {} to {} ({}-day chunks, {} parallel executions, {}ms delay between starts)",
            startDate, endDate, chunkSizeDays, parallelChunks, delayBetweenChunkStartsMs);

        // Prepare all chunks
        List<ChunkInfo> chunks = new ArrayList<>();
        LocalDate currentChunkStart = startDate;

        while (!currentChunkStart.isAfter(endDate)) {
            LocalDate currentChunkEnd = currentChunkStart.plusDays(chunkSizeDays - 1);
            if (currentChunkEnd.isAfter(endDate)) {
                currentChunkEnd = endDate;
            }
            chunks.add(new ChunkInfo(currentChunkStart, currentChunkEnd));
            currentChunkStart = currentChunkEnd.plusDays(1);
        }

        int totalChunks = chunks.size();
        LOGGER.info("Total chunks to process: {}", totalChunks);

        // Execute chunks in parallel using thread pool
        ExecutorService executor = Executors.newFixedThreadPool(parallelChunks);
        List<Future<Boolean>> futures = new ArrayList<>();
        AtomicInteger successCount = new AtomicInteger(0);
        AtomicInteger failureCount = new AtomicInteger(0);

        try {
            long startTime = System.currentTimeMillis();

            // Submit all chunks with delay between submissions
            for (int i = 0; i < chunks.size(); i++) {
                final ChunkInfo chunk = chunks.get(i);
                final int chunkNumber = i + 1;

                Future<Boolean> future = executor.submit(() -> {
                    try {
                        LOGGER.debug("Starting chunk {}/{}: {} to {}", chunkNumber, totalChunks, chunk.start, chunk.end);
                        boolean success = loadStandInDataChunk(chunk.start, chunk.end);

                        if (success) {
                            successCount.incrementAndGet();
                            LOGGER.info("✓ Chunk {}/{} completed successfully: {} to {}", chunkNumber, totalChunks, chunk.start, chunk.end);
                        } else {
                            failureCount.incrementAndGet();
                            LOGGER.warn("✗ Chunk {}/{} failed: {} to {}", chunkNumber, totalChunks, chunk.start, chunk.end);
                        }

                        return success;
                    } catch (Exception e) {
                        failureCount.incrementAndGet();
                        LOGGER.error("✗ Chunk {}/{} error: {} to {} - {}", chunkNumber, totalChunks, chunk.start, chunk.end, e.getMessage());
                        return false;
                    }
                });

                futures.add(future);

                // Delay between chunk starts to avoid overwhelming the system
                if (i < chunks.size() - 1) {
                    Thread.sleep(delayBetweenChunkStartsMs);
                }
            }

            // Wait for all chunks to complete
            LOGGER.info("All {} chunks submitted, waiting for completion...", totalChunks);

            for (Future<Boolean> future : futures) {
                try {
                    future.get(); // Wait for completion
                } catch (Exception e) {
                    LOGGER.error("Error waiting for chunk completion: {}", e.getMessage());
                }
            }

            long elapsedTime = System.currentTimeMillis() - startTime;

            LOGGER.info("Stand-In data initialization completed in {}ms ({}s) - Total: {}, Success: {}, Failed: {}",
                elapsedTime, elapsedTime / 1000, totalChunks, successCount.get(), failureCount.get());

            if (failureCount.get() > 0) {
                LOGGER.warn("{} chunks failed during initialization. Consider reviewing logs and retrying if necessary.", failureCount.get());
            }

        } finally {
            executor.shutdown();
            try {
                if (!executor.awaitTermination(60, TimeUnit.SECONDS)) {
                    executor.shutdownNow();
                }
            } catch (InterruptedException e) {
                executor.shutdownNow();
                Thread.currentThread().interrupt();
            }
        }
    }

    /**
     * Helper class to store chunk information
     */
    private static class ChunkInfo {
        final LocalDate start;
        final LocalDate end;

        ChunkInfo(LocalDate start, LocalDate end) {
            this.start = start;
            this.end = end;
        }
    }

    /**
     * Loads Stand-In data for a specific date range chunk
     */
    private boolean loadStandInDataChunk(LocalDate fromDate, LocalDate toDate) {
        try {
            // Check if loadStandInDataJob exists and is available
            if (!jobService.checkJobWithName(LOAD_STANDIN_DATA_JOB)) {
                LOGGER.error("loadStandInDataJob not found in scheduler");
                return false;
            }

            if (jobService.checkJobRunning(LOAD_STANDIN_DATA_JOB)) {
                LOGGER.warn("loadStandInDataJob is currently running, skipping chunk {} to {}", fromDate, toDate);
                return false;
            }

            // Prepare job parameters
            Map<String, Object> jobData = new HashMap<>();
            jobData.put("fromDate", fromDate.format(API_DATE_FORMATTER));
            jobData.put("toDate", toDate.format(API_DATE_FORMATTER));

            // Execute loadStandInDataJob with date range parameters
            boolean jobStarted = jobService.startJobNow(LOAD_STANDIN_DATA_JOB, jobData);

            if (!jobStarted) {
                LOGGER.error("Failed to start loadStandInDataJob for chunk {} to {}", fromDate, toDate);
                return false;
            }

            // Wait for the job to complete by polling its execution status
            // This replaces the fixed Thread.sleep approach with a more robust solution
            return waitForJobCompletion(fromDate, toDate);

        } catch (Exception e) {
            LOGGER.error("Error executing loadStandInDataJob for chunk {} to {}: {}",
                fromDate, toDate, e.getMessage(), e);
            return false;
        }
    }

    /**
     * Waits for the loadStandInDataJob to complete by polling its execution status
     * in the QRTZ_LOG_TRIGGER_EXECUTED table.
     *
     * This method continuously checks if the job has reached a terminal state (COMPLETED or ERROR)
     * by querying the job execution log. There is NO timeout - the method waits until the job
     * reaches a terminal state or detects an anomaly (job not running but not completed).
     *
     * The continuous polling approach is safer than a fixed timeout because:
     * - We don't know in advance how long a chunk will take (depends on data volume and API speed)
     * - A timeout that's too short causes false failures
     * - A timeout that's too long delays error detection
     * - The job will naturally complete when it's done
     *
     * @param fromDate the start date of the chunk being processed (for logging)
     * @param toDate the end date of the chunk being processed (for logging)
     * @return true if the job completed successfully (COMPLETED state), false otherwise
     * @throws InterruptedException if the thread is interrupted while waiting
     */
    private boolean waitForJobCompletion(LocalDate fromDate, LocalDate toDate) throws InterruptedException {
        int pollingIntervalMs = applicationProperties.getJob().getLoadStandInDataJob().getPollingIntervalMs();

        long startTime = System.currentTimeMillis();
        int pollCount = 0;
        int consecutiveNotRunningCount = 0;
        final int MAX_CONSECUTIVE_NOT_RUNNING = 3; // Failsafe: if job is not running for 3 consecutive checks, stop waiting

        LOGGER.info("Waiting for loadStandInDataJob completion for chunk {} to {} (polling every {}ms, no timeout)",
            fromDate, toDate, pollingIntervalMs);

        while (true) {
            pollCount++;
            Thread.sleep(pollingIntervalMs);

            // Check if the job execution is completed by querying the log table
            boolean isCompleted = jobService.isJobExecutionCompleted(LOAD_STANDIN_DATA_JOB);

            if (isCompleted) {
                long elapsedTime = System.currentTimeMillis() - startTime;
                LOGGER.info("loadStandInDataJob completed for chunk {} to {} after {} polls ({}ms / {}s)",
                    fromDate, toDate, pollCount, elapsedTime, elapsedTime / 1000);
                return true;
            }

            // Check if job is still running
            boolean isRunning = jobService.checkJobRunning(LOAD_STANDIN_DATA_JOB);

            if (!isRunning) {
                consecutiveNotRunningCount++;
                LOGGER.debug("loadStandInDataJob for chunk {} to {} is not running (consecutive: {}) but not marked as completed either (poll #{})",
                    fromDate, toDate, consecutiveNotRunningCount, pollCount);

                // Failsafe: if job is not running for multiple consecutive checks, it's likely an error
                if (consecutiveNotRunningCount >= MAX_CONSECUTIVE_NOT_RUNNING) {
                    LOGGER.error("loadStandInDataJob for chunk {} to {} has not been running for {} consecutive checks but is not marked as completed. " +
                            "This indicates an error. Total polls: {}, elapsed time: {}ms",
                        fromDate, toDate, consecutiveNotRunningCount, pollCount, System.currentTimeMillis() - startTime);
                    return false;
                }
            } else {
                // Job is running, reset counter
                consecutiveNotRunningCount = 0;

                // Log progress every 10 polls
                if (pollCount % 10 == 0) {
                    long elapsedTime = System.currentTimeMillis() - startTime;
                    LOGGER.debug("Still waiting for loadStandInDataJob completion for chunk {} to {} - poll #{}, elapsed: {}s",
                        fromDate, toDate, pollCount, elapsedTime / 1000);
                }
            }
        }
    }

    /**
     * Checks if initialization has been completed
     */
    public boolean isInitializationCompleted() {
        return initializationCompleted;
    }


}
