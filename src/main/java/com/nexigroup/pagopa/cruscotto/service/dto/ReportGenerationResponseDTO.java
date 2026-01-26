package com.nexigroup.pagopa.cruscotto.service.dto;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * DTO for report generation response.
 */
public class ReportGenerationResponseDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;
    private Long instanceId;
    private String instanceName;
    private String status;
    private String language;
    private LocalDate startDate;
    private LocalDate endDate;
    private LocalDateTime requestedDate;
    private LocalDateTime generationStartDate;
    private LocalDateTime generationEndDate;
    private String requestedBy;
    private String errorMessage;
    private Integer retryCount;
    private LocalDateTime lastRetryDate;
    private String downloadUrl;
    private Long fileSizeBytes;
    private String fileName;
    private List<String> packageContents;

    // Getters and Setters

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getInstanceId() {
        return instanceId;
    }

    public void setInstanceId(Long instanceId) {
        this.instanceId = instanceId;
    }

    public String getInstanceName() {
        return instanceName;
    }

    public void setInstanceName(String instanceName) {
        this.instanceName = instanceName;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }

    public LocalDateTime getRequestedDate() {
        return requestedDate;
    }

    public void setRequestedDate(LocalDateTime requestedDate) {
        this.requestedDate = requestedDate;
    }

    public LocalDateTime getGenerationStartDate() {
        return generationStartDate;
    }

    public void setGenerationStartDate(LocalDateTime generationStartDate) {
        this.generationStartDate = generationStartDate;
    }

    public LocalDateTime getGenerationEndDate() {
        return generationEndDate;
    }

    public void setGenerationEndDate(LocalDateTime generationEndDate) {
        this.generationEndDate = generationEndDate;
    }

    public String getRequestedBy() {
        return requestedBy;
    }

    public void setRequestedBy(String requestedBy) {
        this.requestedBy = requestedBy;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public Integer getRetryCount() {
        return retryCount;
    }

    public void setRetryCount(Integer retryCount) {
        this.retryCount = retryCount;
    }

    public LocalDateTime getLastRetryDate() {
        return lastRetryDate;
    }

    public void setLastRetryDate(LocalDateTime lastRetryDate) {
        this.lastRetryDate = lastRetryDate;
    }

    public String getDownloadUrl() {
        return downloadUrl;
    }

    public void setDownloadUrl(String downloadUrl) {
        this.downloadUrl = downloadUrl;
    }

    public Long getFileSizeBytes() {
        return fileSizeBytes;
    }

    public void setFileSizeBytes(Long fileSizeBytes) {
        this.fileSizeBytes = fileSizeBytes;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public List<String> getPackageContents() {
        return packageContents;
    }

    public void setPackageContents(List<String> packageContents) {
        this.packageContents = packageContents;
    }

    @Override
    public String toString() {
        return (
            "ReportGenerationResponseDTO{" +
            "id=" +
            id +
            ", instanceId=" +
            instanceId +
            ", instanceName='" +
            instanceName +
            '\'' +
            ", status='" +
            status +
            '\'' +
            ", language='" +
            language +
            '\'' +
            ", startDate=" +
            startDate +
            ", endDate=" +
            endDate +
            ", requestedDate=" +
            requestedDate +
            ", requestedBy='" +
            requestedBy +
            '\'' +
            '}'
        );
    }
}
