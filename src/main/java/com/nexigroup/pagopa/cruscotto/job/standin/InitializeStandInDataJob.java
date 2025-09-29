package com.nexigroup.pagopa.cruscotto.job.standin;

import com.nexigroup.pagopa.cruscotto.config.ApplicationProperties;
import com.nexigroup.pagopa.cruscotto.domain.PagopaNumeroStandin;
import com.nexigroup.pagopa.cruscotto.repository.PagopaNumeroStandinRepository;
import com.nexigroup.pagopa.cruscotto.service.JobService;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.lang.NonNull;
import org.quartz.DisallowConcurrentExecution;
import org.quartz.JobExecutionContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.scheduling.quartz.QuartzJobBean;
import org.springframework.stereotype.Component;

/**
 * Job to initialize Stand-In data by loading the last 6 months of events.
 * Runs only once on application startup if no Stand-In data exists.
 * Loads data in 10-day chunks to respect API limitations.
 */
@Component
@DisallowConcurrentExecution
public class InitializeStandInDataJob extends QuartzJobBean {

    private static final Logger LOGGER = LoggerFactory.getLogger(InitializeStandInDataJob.class);
    
    private static final DateTimeFormatter API_DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private static final int CHUNK_SIZE_DAYS = 10;
    private static final int INITIALIZATION_MONTHS = 6;
    private static final int DELAY_BETWEEN_CHUNKS_MS = 3000; // 3 seconds

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
        LOGGER.info("Starting Stand-In data initialization for the last {} months", INITIALIZATION_MONTHS);
        
        try {
            if (!applicationProperties.getJob().getLoadStandInDataJob().isEnabled()) {
                LOGGER.info("LoadStandInDataJob is disabled, skipping Stand-In data initialization");
                return;
            }

            if (shouldInitializeStandInData()) {
                initializeStandInDataInBackground();
                initializationCompleted = true;
                LOGGER.info("Stand-In data initialization completed successfully");
            } else {
                LOGGER.info("Stand-In data already exists, no initialization needed");
                initializationCompleted = true;
            }
        } catch (Exception e) {
            LOGGER.error("Failed to initialize Stand-In data: {}", e.getMessage(), e);
            throw new RuntimeException("InitializeStandInDataJob failed", e);
        }
    }

    /**
     * Checks if Stand-In data initialization is needed
     */
    private boolean shouldInitializeStandInData() {
        try {
            // Check if any Stand-In data exists in the last 6 months
            LocalDate sixMonthsAgo = LocalDate.now().minusMonths(INITIALIZATION_MONTHS);
            LocalDate yesterday = LocalDate.now().minusDays(1);
            
            List<PagopaNumeroStandin> existingData = pagopaNumeroStandinRepository.findByDateRange(
                sixMonthsAgo.atStartOfDay(), 
                yesterday.atTime(23, 59, 59)
            );
            
            LOGGER.debug("Found {} existing Stand-In records in the last {} months", 
                        existingData.size(), INITIALIZATION_MONTHS);
            
            return existingData.isEmpty();
            
        } catch (Exception e) {
            LOGGER.warn("Error checking existing Stand-In data, proceeding with initialization: {}", e.getMessage());
            return true;
        }
    }

    /**
     * Initializes Stand-In data by loading chunks in the background
     */
    private void initializeStandInDataInBackground() throws InterruptedException {
        LocalDate endDate = LocalDate.now().minusDays(1); // Yesterday
        LocalDate startDate = endDate.minusMonths(INITIALIZATION_MONTHS);
        
        LOGGER.info("Initializing Stand-In data from {} to {} ({}-day chunks)", 
                   startDate, endDate, CHUNK_SIZE_DAYS);

        int totalChunks = 0;
        int successChunks = 0;
        int failedChunks = 0;

        LocalDate currentChunkStart = startDate;
        
        while (!currentChunkStart.isAfter(endDate)) {
            // Calculate chunk end date (max CHUNK_SIZE_DAYS or until endDate)
            LocalDate currentChunkEnd = currentChunkStart.plusDays(CHUNK_SIZE_DAYS - 1);
            if (currentChunkEnd.isAfter(endDate)) {
                currentChunkEnd = endDate;
            }

            totalChunks++;
            
            LOGGER.info("Loading chunk {}: {} to {}", totalChunks, currentChunkStart, currentChunkEnd);
            
            try {
                boolean success = loadStandInDataChunk(currentChunkStart, currentChunkEnd);
                
                if (success) {
                    successChunks++;
                    LOGGER.debug("Successfully loaded chunk {} to {}", currentChunkStart, currentChunkEnd);
                } else {
                    failedChunks++;
                    LOGGER.warn("Failed to load chunk {} to {}", currentChunkStart, currentChunkEnd);
                }
                
            } catch (Exception e) {
                failedChunks++;
                LOGGER.error("Error loading chunk {} to {}: {}", currentChunkStart, currentChunkEnd, e.getMessage());
            }
            
            // Move to next chunk
            currentChunkStart = currentChunkEnd.plusDays(1);
            
            // Delay between chunks to avoid overwhelming the system
            if (!currentChunkStart.isAfter(endDate)) {
                LOGGER.debug("Waiting {} ms before next chunk", DELAY_BETWEEN_CHUNKS_MS);
                Thread.sleep(DELAY_BETWEEN_CHUNKS_MS);
            }
        }
        
        LOGGER.info("Stand-In data initialization summary - Total chunks: {}, Success: {}, Failed: {}", 
                   totalChunks, successChunks, failedChunks);
                   
        if (failedChunks > 0) {
            LOGGER.warn("Some chunks failed during initialization. Consider running the job again or checking manually.");
        }
    }

    /**
     * Loads Stand-In data for a specific date range chunk
     */
    private boolean loadStandInDataChunk(LocalDate fromDate, LocalDate toDate) {
        try {
            // Check if loadStandInDataJob exists and is available
            if (!jobService.checkJobWithName("loadStandInDataJob")) {
                LOGGER.error("loadStandInDataJob not found in scheduler");
                return false;
            }
            
            if (jobService.checkJobRunning("loadStandInDataJob")) {
                LOGGER.warn("loadStandInDataJob is currently running, skipping chunk {} to {}", fromDate, toDate);
                return false;
            }
            
            // Prepare job parameters
            Map<String, Object> jobData = new HashMap<>();
            jobData.put("fromDate", fromDate.format(API_DATE_FORMATTER));
            jobData.put("toDate", toDate.format(API_DATE_FORMATTER));
            
            // Execute loadStandInDataJob with date range parameters
            boolean jobStarted = jobService.startJobNow("loadStandInDataJob", jobData);
            
            if (jobStarted) {
                // Wait a bit for the job to complete (simplified approach)
                // In a production environment, you might want to implement proper job completion monitoring
                Thread.sleep(5000);
                return true;
            } else {
                LOGGER.error("Failed to start loadStandInDataJob for chunk {} to {}", fromDate, toDate);
                return false;
            }
            
        } catch (Exception e) {
            LOGGER.error("Error executing loadStandInDataJob for chunk {} to {}: {}", 
                        fromDate, toDate, e.getMessage(), e);
            return false;
        }
    }

    /**
     * Checks if initialization has been completed
     */
    public boolean isInitializationCompleted() {
        return initializationCompleted;
    }


}