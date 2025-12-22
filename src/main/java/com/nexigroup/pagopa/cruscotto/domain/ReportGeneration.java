package com.nexigroup.pagopa.cruscotto.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.nexigroup.pagopa.cruscotto.domain.enumeration.ReportStatus;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Map;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

/**
 * A ReportGeneration entity.
 * Represents a report generation request and its lifecycle.
 * Only one active report (PENDING, IN_PROGRESS, or COMPLETED) can exist per instance.
 */
@Entity
@Table(name = "report_generation")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class ReportGeneration implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @NotNull
    @ManyToOne(optional = false)
    @JsonIgnoreProperties(value = { "modules", "createdBy", "lastModifiedBy" }, allowSetters = true)
    private Instance instance;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "status", length = 20, nullable = false)
    private ReportStatus status;

    @NotNull
    @Size(max = 5)
    @Column(name = "language", length = 5, nullable = false)
    private String language;

    @NotNull
    @Column(name = "start_date", nullable = false)
    private LocalDate startDate;

    @NotNull
    @Column(name = "end_date", nullable = false)
    private LocalDate endDate;

    @NotNull
    @Column(name = "requested_date", nullable = false)
    private LocalDateTime requestedDate;

    @Column(name = "generation_start_date")
    private LocalDateTime generationStartDate;

    @Column(name = "generation_end_date")
    private LocalDateTime generationEndDate;

    @NotNull
    @Size(max = 255)
    @Column(name = "requested_by", length = 255, nullable = false)
    private String requestedBy;

    @Column(name = "error_message", columnDefinition = "text")
    private String errorMessage;

    @Column(name = "retry_count")
    private Integer retryCount = 0;

    @Column(name = "last_retry_date")
    private LocalDateTime lastRetryDate;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "parameters", columnDefinition = "jsonb")
    private Map<String, Object> parameters;

    @OneToOne(mappedBy = "reportGeneration", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnoreProperties(value = { "reportGeneration" }, allowSetters = true)
    private ReportFile reportFile;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public ReportGeneration id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Instance getInstance() {
        return this.instance;
    }

    public void setInstance(Instance instance) {
        this.instance = instance;
    }

    public ReportGeneration instance(Instance instance) {
        this.setInstance(instance);
        return this;
    }

    public ReportStatus getStatus() {
        return this.status;
    }

    public ReportGeneration status(ReportStatus status) {
        this.setStatus(status);
        return this;
    }

    public void setStatus(ReportStatus status) {
        this.status = status;
    }

    public String getLanguage() {
        return this.language;
    }

    public ReportGeneration language(String language) {
        this.setLanguage(language);
        return this;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public LocalDate getStartDate() {
        return this.startDate;
    }

    public ReportGeneration startDate(LocalDate startDate) {
        this.setStartDate(startDate);
        return this;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public LocalDate getEndDate() {
        return this.endDate;
    }

    public ReportGeneration endDate(LocalDate endDate) {
        this.setEndDate(endDate);
        return this;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }

    public LocalDateTime getRequestedDate() {
        return this.requestedDate;
    }

    public ReportGeneration requestedDate(LocalDateTime requestedDate) {
        this.setRequestedDate(requestedDate);
        return this;
    }

    public void setRequestedDate(LocalDateTime requestedDate) {
        this.requestedDate = requestedDate;
    }

    public LocalDateTime getGenerationStartDate() {
        return this.generationStartDate;
    }

    public ReportGeneration generationStartDate(LocalDateTime generationStartDate) {
        this.setGenerationStartDate(generationStartDate);
        return this;
    }

    public void setGenerationStartDate(LocalDateTime generationStartDate) {
        this.generationStartDate = generationStartDate;
    }

    public LocalDateTime getGenerationEndDate() {
        return this.generationEndDate;
    }

    public ReportGeneration generationEndDate(LocalDateTime generationEndDate) {
        this.setGenerationEndDate(generationEndDate);
        return this;
    }

    public void setGenerationEndDate(LocalDateTime generationEndDate) {
        this.generationEndDate = generationEndDate;
    }

    public String getRequestedBy() {
        return this.requestedBy;
    }

    public ReportGeneration requestedBy(String requestedBy) {
        this.setRequestedBy(requestedBy);
        return this;
    }

    public void setRequestedBy(String requestedBy) {
        this.requestedBy = requestedBy;
    }

    public String getErrorMessage() {
        return this.errorMessage;
    }

    public ReportGeneration errorMessage(String errorMessage) {
        this.setErrorMessage(errorMessage);
        return this;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public Integer getRetryCount() {
        return this.retryCount;
    }

    public ReportGeneration retryCount(Integer retryCount) {
        this.setRetryCount(retryCount);
        return this;
    }

    public void setRetryCount(Integer retryCount) {
        this.retryCount = retryCount;
    }

    public LocalDateTime getLastRetryDate() {
        return this.lastRetryDate;
    }

    public ReportGeneration lastRetryDate(LocalDateTime lastRetryDate) {
        this.setLastRetryDate(lastRetryDate);
        return this;
    }

    public void setLastRetryDate(LocalDateTime lastRetryDate) {
        this.lastRetryDate = lastRetryDate;
    }

    public Map<String, Object> getParameters() {
        return this.parameters;
    }

    public ReportGeneration parameters(Map<String, Object> parameters) {
        this.setParameters(parameters);
        return this;
    }

    public void setParameters(Map<String, Object> parameters) {
        this.parameters = parameters;
    }

    public ReportFile getReportFile() {
        return this.reportFile;
    }

    public void setReportFile(ReportFile reportFile) {
        if (this.reportFile != null) {
            this.reportFile.setReportGeneration(null);
        }
        if (reportFile != null) {
            reportFile.setReportGeneration(this);
        }
        this.reportFile = reportFile;
    }

    public ReportGeneration reportFile(ReportFile reportFile) {
        this.setReportFile(reportFile);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ReportGeneration)) {
            return false;
        }
        return id != null && id.equals(((ReportGeneration) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "ReportGeneration{" +
            "id=" + getId() +
            ", status='" + getStatus() + "'" +
            ", language='" + getLanguage() + "'" +
            ", startDate='" + getStartDate() + "'" +
            ", endDate='" + getEndDate() + "'" +
            ", requestedDate='" + getRequestedDate() + "'" +
            ", generationStartDate='" + getGenerationStartDate() + "'" +
            ", generationEndDate='" + getGenerationEndDate() + "'" +
            ", requestedBy='" + getRequestedBy() + "'" +
            ", retryCount=" + getRetryCount() +
            "}";
    }
}
