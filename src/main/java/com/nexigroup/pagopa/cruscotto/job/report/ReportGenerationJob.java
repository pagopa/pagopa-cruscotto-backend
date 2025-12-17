package com.nexigroup.pagopa.cruscotto.job.report;

import com.nexigroup.pagopa.cruscotto.config.ApplicationProperties;
// import com.nexigroup.pagopa.cruscotto.service.ReportGenerationService;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
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

    // private final ReportGenerationService reportGenerationService;
    private final ApplicationProperties applicationProperties;

    @Override
    protected void executeInternal(@NotNull JobExecutionContext context) {
        LOGGER.info("Report Generation Job started");

        if (!applicationProperties.getJob().getReportGenerationJob().isEnabled()) {
            LOGGER.info("Report Generation Job is disabled. Skipping execution.");
            return;
        }

        try {
            // Query PENDING reports - per ora loggiamo solo
            LOGGER.info("Query report istanza pending");

            // Simuliamo una lista di report ID in stato PENDING
            // TODO: sostituire con query reale quando la tabella sarà disponibile
            List<Long> pendingReportIds = queryPendingReports();

            if (pendingReportIds.isEmpty()) {
                LOGGER.info("No pending reports found. Exiting.");
                return;
            }

            LOGGER.info("Found {} pending reports to process", pendingReportIds.size());

            // Process reports in parallel using CompletableFuture
            // Questo approccio rende ESPLICITO quando tutti i task paralleli sono completati
            AtomicInteger successCount = new AtomicInteger(0);
            AtomicInteger failureCount = new AtomicInteger(0);

            // Crea un CompletableFuture per ogni report ID
            List<CompletableFuture<Void>> futures = pendingReportIds
                .stream()
                .map(reportId ->
                    CompletableFuture.runAsync(() -> {
                        try {
                            LOGGER.info("Processing report ID: {}", reportId);

                            // TODO: Decommentare quando ReportGenerationService sarà implementato dal collega
                            LOGGER.info("Chiamo il servizio executeAsyncGeneration");
                            // reportGenerationService.executeAsyncGeneration(reportId);

                            successCount.incrementAndGet();
                            LOGGER.info("Successfully processed report ID: {}", reportId);
                        } catch (Exception e) {
                            failureCount.incrementAndGet();
                            LOGGER.error("Failed to process report ID: {}. Error: {}", reportId, e.getMessage(), e);
                        }
                    })
                )
                .collect(Collectors.toList());

            // Aspetta ESPLICITAMENTE che TUTTI i CompletableFuture siano completati
            CompletableFuture<Void> allOf = CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]));

            // Blocca fino al completamento di tutti i task
            allOf.join(); // ⬅️ QUESTO RENDE ESPLICITO: "Aspetto che tutti finiscano"

            LOGGER.info(
                "Report Generation Job completed. Success: {}, Failures: {}",
                successCount.get(),
                failureCount.get()
            );
        } catch (Exception e) {
            LOGGER.error("Error during Report Generation Job execution: {}", e.getMessage(), e);
        }

        LOGGER.info("Report Generation Job ended");
    }

    /**
     * Query for reports in PENDING status.
     * TODO: Implementare query reale quando la tabella report sarà disponibile
     *
     * @return List of report IDs in PENDING status
     */
    private List<Long> queryPendingReports() {
        // TODO: Query reale da implementare
        // return reportRepository.findByStatus(ReportStatus.PENDING);

        // Per ora ritorniamo una lista vuota
        return List.of();
    }
}

