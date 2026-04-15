package com.nexigroup.pagopa.cruscotto.job.report;

import com.nexigroup.pagopa.cruscotto.config.ApplicationProperties;
import com.nexigroup.pagopa.cruscotto.domain.ReportGeneration;
import com.nexigroup.pagopa.cruscotto.domain.enumeration.ReportStatus;
import com.nexigroup.pagopa.cruscotto.repository.ReportGenerationRepository;
import com.nexigroup.pagopa.cruscotto.service.ReportGenerationService;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicInteger;
import lombok.AllArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.quartz.DisallowConcurrentExecution;
import org.quartz.JobExecutionContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.quartz.QuartzJobBean;
import org.springframework.stereotype.Component;

/**
 * Quartz Job for asynchronous report generation.
 * Scheduled to run every 10 minutes to process PENDING reports.
 * Uses CompletableFuture for parallel execution with explicit completion handling.
 */
@Component
@AllArgsConstructor
@DisallowConcurrentExecution
public class ReportGenerationJob extends QuartzJobBean {

    private static final Logger LOGGER = LoggerFactory.getLogger(ReportGenerationJob.class);

    private final ReportGenerationService reportGenerationService;
    private final ApplicationProperties applicationProperties;
    private final ReportGenerationRepository reportGenerationRepository;

    @Override
    protected void executeInternal(@NotNull JobExecutionContext context) {
        LOGGER.info("Report Generation Job started");

        if (!applicationProperties.getJob().getReportGenerationJob().isEnabled()) {
            LOGGER.info("Report Generation Job is disabled. Skipping execution.");
            return;
        }

        try {
            LOGGER.info("Querying pending report instances");

            List<Long> pendingReportIds = queryPendingReports();

            if (pendingReportIds.isEmpty()) {
                LOGGER.info("No pending reports found. Exiting.");
                return;
            }

            LOGGER.info("Found {} pending reports to process", pendingReportIds.size());

            int successCount = 0;
            int failureCount = 0;

            for (Long reportId : pendingReportIds) {
                try {
                    processReport(reportId);
                    successCount++;
                    LOGGER.info("Successfully processed report ID: {}", reportId);
                } catch (Exception e) {
                    failureCount++;
                    LOGGER.error("Failed to process report ID: {}. Error: {}", reportId, e.getMessage(), e);
                }
            }

            LOGGER.info(
                "Report Generation Job completed. Success: {}, Failures: {}",
                successCount,
                failureCount
            );

        } catch (Exception e) {
            LOGGER.error("Error during Report Generation Job execution: {}", e.getMessage(), e);
        }

        LOGGER.info("Report Generation Job ended");
    }

    /**
     * Query for reports in PENDING status.
     *
     * @return List of report IDs in PENDING status
     */
    protected List<Long> queryPendingReports() {
        return reportGenerationRepository.findByStatus(ReportStatus.PENDING)
            .stream()
            .map(ReportGeneration::getId)
            .toList();
    }


    protected void processReport(Long reportId)  {
        LOGGER.info("Processing report ID: {}", reportId);
        LOGGER.info("Chiamo il servizio executeAsyncGeneration");
        reportGenerationService.executeAsyncGeneration(reportId);
    }
}

