package com.nexigroup.pagopa.cruscotto.domain.enumeration;

/**
 * The ReportStatus enumeration.
 * Represents the lifecycle status of a report generation request.
 */
public enum ReportStatus {
    /**
     * Report generation request has been created and is awaiting processing
     */
    PENDING,

    /**
     * Report generation is currently in progress
     */
    IN_PROGRESS,

    /**
     * Report generation completed successfully and file is available for download
     */
    COMPLETED,

    /**
     * Report generation failed due to an error
     */
    FAILED,
}
