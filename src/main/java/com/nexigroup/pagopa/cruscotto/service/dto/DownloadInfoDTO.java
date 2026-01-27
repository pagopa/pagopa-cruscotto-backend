package com.nexigroup.pagopa.cruscotto.service.dto;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * DTO for download information.
 * Contains download URL, file metadata, and expiration timestamp.
 */
public class DownloadInfoDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    private String downloadUrl;
    private String fileName;
    private Long fileSizeBytes;
    private LocalDateTime expiresAt;

    // Constructors

    public DownloadInfoDTO() {}

    public DownloadInfoDTO(String downloadUrl, String fileName, Long fileSizeBytes, LocalDateTime expiresAt) {
        this.downloadUrl = downloadUrl;
        this.fileName = fileName;
        this.fileSizeBytes = fileSizeBytes;
        this.expiresAt = expiresAt;
    }

    // Getters and Setters

    public String getDownloadUrl() {
        return downloadUrl;
    }

    public void setDownloadUrl(String downloadUrl) {
        this.downloadUrl = downloadUrl;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public Long getFileSizeBytes() {
        return fileSizeBytes;
    }

    public void setFileSizeBytes(Long fileSizeBytes) {
        this.fileSizeBytes = fileSizeBytes;
    }

    public LocalDateTime getExpiresAt() {
        return expiresAt;
    }

    public void setExpiresAt(LocalDateTime expiresAt) {
        this.expiresAt = expiresAt;
    }

    @Override
    public String toString() {
        return (
            "DownloadInfoDTO{" +
            "downloadUrl='" +
            downloadUrl +
            '\'' +
            ", fileName='" +
            fileName +
            '\'' +
            ", fileSizeBytes=" +
            fileSizeBytes +
            ", expiresAt=" +
            expiresAt +
            '}'
        );
    }
}
