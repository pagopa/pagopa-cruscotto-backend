package com.nexigroup.pagopa.cruscotto.service;

import com.nexigroup.pagopa.cruscotto.service.dto.ReportAsyncAcceptedDTO;
import com.nexigroup.pagopa.cruscotto.service.dto.ReportFilterDTO;
import com.nexigroup.pagopa.cruscotto.service.dto.ReportGenerationRequestDTO;
import com.nexigroup.pagopa.cruscotto.service.dto.ReportGenerationResponseDTO;
import com.nexigroup.pagopa.cruscotto.service.exception.DuplicateReportException;
import com.nexigroup.pagopa.cruscotto.service.exception.ReportGenerationException;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Service Interface for managing report generation.
 */
public interface ReportGenerationService {
    /**
     * Check if an active report exists for the given instance.
     * Active means: PENDING, IN_PROGRESS, or COMPLETED status.
     *
     * @param instanceId the instance ID
     * @return true if an active report exists, false otherwise
     */
    boolean activeReportExistsForInstance(Long instanceId);

    /**
     * Get the active report for the given instance.
     *
     * @param instanceId the instance ID
     * @return the active report, or null if no active report exists
     */
    ReportGenerationResponseDTO getActiveReportByInstance(Long instanceId);

    /**
     * Get all reports for the given instance.
     *
     * @param instanceId the instance ID
     * @return list of all reports for the instance
     */
    List<ReportGenerationResponseDTO> getAllReportsForInstance(Long instanceId);

    /**
     * Schedule reports to be generated asynchronously for multiple instances.
     * The reports will be picked up by the scheduled job and processed.
     * Processes all instanceIds in the request, creating one report per instance.
     *
     * @param request the report generation request (may contain multiple instanceIds)
     * @return list of scheduled reports with PENDING status (one per instance)
     * @throws DuplicateReportException if an active report already exists for any instance
     */
    List<ReportAsyncAcceptedDTO> scheduleAsyncReport(ReportGenerationRequestDTO request) throws DuplicateReportException;

    /**
     * Execute async generation for a report.
     * This method is called by the scheduled job to process PENDING reports.
     *
     * @param reportId the report ID
     * @throws ReportGenerationException if report generation fails
     */
    void executeAsyncGeneration(Long reportId) throws ReportGenerationException;

    /**
     * Get the status of a report.
     *
     * @param reportId the report ID
     * @return the report status
     */
    ReportGenerationResponseDTO getReportStatus(Long reportId);

    /**
     * Get the download URL for a report.
     *
     * @param reportId the report ID
     * @return the download URL (SAS URL with limited validity)
     */
    // String getDownloadUrl(Long reportId);

    /**
     * List reports with optional filtering and pagination.
     *
     * @param filter the filter criteria
     * @param pageable the pagination information
     * @return page of reports
     */
    // Page<ReportGenerationResponseDTO> listReports(ReportFilterDTO filter, Pageable pageable);

    /**
     * Delete a report and its associated file from storage.
     *
     * @param reportId the report ID
     */
    // void deleteReport(Long reportId);
}
