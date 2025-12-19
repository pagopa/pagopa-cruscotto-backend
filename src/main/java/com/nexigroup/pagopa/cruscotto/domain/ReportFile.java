package com.nexigroup.pagopa.cruscotto.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.hypersistence.utils.hibernate.type.json.JsonBinaryType;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;

/**
 * A ReportFile entity.
 * Represents the metadata for a report ZIP file stored in Azure Blob Storage.
 */
@Entity
@Table(name = "report_file")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@TypeDef(name = "jsonb", typeClass = JsonBinaryType.class)
public class ReportFile implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @NotNull
    @Size(max = 500)
    @Column(name = "blob_name", length = 500, nullable = false, unique = true)
    private String blobName;

    @NotNull
    @Size(max = 255)
    @Column(name = "blob_container", length = 255, nullable = false)
    private String blobContainer;

    @NotNull
    @Size(max = 500)
    @Column(name = "file_name", length = 500, nullable = false)
    private String fileName;

    @NotNull
    @Size(max = 100)
    @Column(name = "content_type", length = 100, nullable = false)
    private String contentType;

    @NotNull
    @Column(name = "file_size_bytes", nullable = false)
    private Long fileSizeBytes;

    @NotNull
    @Size(max = 64)
    @Column(name = "checksum", length = 64, nullable = false)
    private String checksum;

    @NotNull
    @Column(name = "upload_date", nullable = false)
    private LocalDateTime uploadDate;

    @NotNull
    @Column(name = "expiry_date", nullable = false)
    private LocalDateTime expiryDate;

    @Column(name = "deleted")
    private Boolean deleted = false;

    @Type(type = "jsonb")
    @Column(name = "package_contents", columnDefinition = "jsonb", nullable = false)
    private List<String> packageContents;

    @OneToOne
    @JoinColumn(name = "report_generation_id", unique = true, nullable = false)
    @JsonIgnoreProperties(value = { "reportFile", "instance" }, allowSetters = true)
    private ReportGeneration reportGeneration;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public ReportFile id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getBlobName() {
        return this.blobName;
    }

    public ReportFile blobName(String blobName) {
        this.setBlobName(blobName);
        return this;
    }

    public void setBlobName(String blobName) {
        this.blobName = blobName;
    }

    public String getBlobContainer() {
        return this.blobContainer;
    }

    public ReportFile blobContainer(String blobContainer) {
        this.setBlobContainer(blobContainer);
        return this;
    }

    public void setBlobContainer(String blobContainer) {
        this.blobContainer = blobContainer;
    }

    public String getFileName() {
        return this.fileName;
    }

    public ReportFile fileName(String fileName) {
        this.setFileName(fileName);
        return this;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getContentType() {
        return this.contentType;
    }

    public ReportFile contentType(String contentType) {
        this.setContentType(contentType);
        return this;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    public Long getFileSizeBytes() {
        return this.fileSizeBytes;
    }

    public ReportFile fileSizeBytes(Long fileSizeBytes) {
        this.setFileSizeBytes(fileSizeBytes);
        return this;
    }

    public void setFileSizeBytes(Long fileSizeBytes) {
        this.fileSizeBytes = fileSizeBytes;
    }

    public String getChecksum() {
        return this.checksum;
    }

    public ReportFile checksum(String checksum) {
        this.setChecksum(checksum);
        return this;
    }

    public void setChecksum(String checksum) {
        this.checksum = checksum;
    }

    public LocalDateTime getUploadDate() {
        return this.uploadDate;
    }

    public ReportFile uploadDate(LocalDateTime uploadDate) {
        this.setUploadDate(uploadDate);
        return this;
    }

    public void setUploadDate(LocalDateTime uploadDate) {
        this.uploadDate = uploadDate;
    }

    public LocalDateTime getExpiryDate() {
        return this.expiryDate;
    }

    public ReportFile expiryDate(LocalDateTime expiryDate) {
        this.setExpiryDate(expiryDate);
        return this;
    }

    public void setExpiryDate(LocalDateTime expiryDate) {
        this.expiryDate = expiryDate;
    }

    public Boolean getDeleted() {
        return this.deleted;
    }

    public ReportFile deleted(Boolean deleted) {
        this.setDeleted(deleted);
        return this;
    }

    public void setDeleted(Boolean deleted) {
        this.deleted = deleted;
    }

    public List<String> getPackageContents() {
        return this.packageContents;
    }

    public ReportFile packageContents(List<String> packageContents) {
        this.setPackageContents(packageContents);
        return this;
    }

    public void setPackageContents(List<String> packageContents) {
        this.packageContents = packageContents;
    }

    public ReportGeneration getReportGeneration() {
        return this.reportGeneration;
    }

    public void setReportGeneration(ReportGeneration reportGeneration) {
        this.reportGeneration = reportGeneration;
    }

    public ReportFile reportGeneration(ReportGeneration reportGeneration) {
        this.setReportGeneration(reportGeneration);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ReportFile)) {
            return false;
        }
        return id != null && id.equals(((ReportFile) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "ReportFile{" +
            "id=" + getId() +
            ", blobName='" + getBlobName() + "'" +
            ", blobContainer='" + getBlobContainer() + "'" +
            ", fileName='" + getFileName() + "'" +
            ", contentType='" + getContentType() + "'" +
            ", fileSizeBytes=" + getFileSizeBytes() +
            ", checksum='" + getChecksum() + "'" +
            ", uploadDate='" + getUploadDate() + "'" +
            ", expiryDate='" + getExpiryDate() + "'" +
            ", deleted=" + getDeleted() +
            "}";
    }
}
