package com.nexigroup.pagopa.cruscotto.service.dto;

import com.nexigroup.pagopa.cruscotto.domain.enumeration.EvaluationType;
import com.nexigroup.pagopa.cruscotto.domain.enumeration.OutcomeStatus;
import java.time.LocalDate;

import org.springframework.lang.NonNull;

/**
 * A DTO for the {@link com.nexigroup.pagopa.cruscotto.domain.KpiB1Result} entity.
 */
public class KpiB1ResultDTO {

    private Long id;

    @NonNull
    private Long instanceId;

    @NonNull
    private Long instanceModuleId;

    @NonNull
    private LocalDate analysisDate;

    private EvaluationType evaluationType;

    private Integer minEntitiesThreshold;

    private Long minTransactionsThreshold;

    @NonNull
    private OutcomeStatus outcome;

    public KpiB1ResultDTO() {
    }

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

    public Long getInstanceModuleId() {
        return instanceModuleId;
    }

    public void setInstanceModuleId(Long instanceModuleId) {
        this.instanceModuleId = instanceModuleId;
    }

    public LocalDate getAnalysisDate() {
        return analysisDate;
    }

    public void setAnalysisDate(LocalDate analysisDate) {
        this.analysisDate = analysisDate;
    }

    public EvaluationType getEvaluationType() {
        return evaluationType;
    }

    public void setEvaluationType(EvaluationType evaluationType) {
        this.evaluationType = evaluationType;
    }

    public Integer getMinEntitiesThreshold() {
        return minEntitiesThreshold;
    }

    public void setMinEntitiesThreshold(Integer minEntitiesThreshold) {
        this.minEntitiesThreshold = minEntitiesThreshold;
    }

    public Long getMinTransactionsThreshold() {
        return minTransactionsThreshold;
    }

    public void setMinTransactionsThreshold(Long minTransactionsThreshold) {
        this.minTransactionsThreshold = minTransactionsThreshold;
    }

    public OutcomeStatus getOutcome() {
        return outcome;
    }

    public void setOutcome(OutcomeStatus outcome) {
        this.outcome = outcome;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        KpiB1ResultDTO that = (KpiB1ResultDTO) o;

        if (id != null ? !id.equals(that.id) : that.id != null) return false;
        if (instanceId != null ? !instanceId.equals(that.instanceId) : that.instanceId != null) return false;
        if (instanceModuleId != null ? !instanceModuleId.equals(that.instanceModuleId) : that.instanceModuleId != null) return false;
        if (analysisDate != null ? !analysisDate.equals(that.analysisDate) : that.analysisDate != null) return false;
        if (evaluationType != that.evaluationType) return false;
        if (minEntitiesThreshold != null ? !minEntitiesThreshold.equals(that.minEntitiesThreshold) : that.minEntitiesThreshold != null) return false;
        if (minTransactionsThreshold != null ? !minTransactionsThreshold.equals(that.minTransactionsThreshold) : that.minTransactionsThreshold != null) return false;
        return outcome == that.outcome;
    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (instanceId != null ? instanceId.hashCode() : 0);
        result = 31 * result + (instanceModuleId != null ? instanceModuleId.hashCode() : 0);
        result = 31 * result + (analysisDate != null ? analysisDate.hashCode() : 0);
        result = 31 * result + (evaluationType != null ? evaluationType.hashCode() : 0);
        result = 31 * result + (minEntitiesThreshold != null ? minEntitiesThreshold.hashCode() : 0);
        result = 31 * result + (minTransactionsThreshold != null ? minTransactionsThreshold.hashCode() : 0);
        result = 31 * result + (outcome != null ? outcome.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "KpiB1ResultDTO{" +
                "id=" + id +
                ", instanceId=" + instanceId +
                ", instanceModuleId=" + instanceModuleId +
                ", analysisDate=" + analysisDate +
                ", evaluationType=" + evaluationType +
                ", minEntitiesThreshold=" + minEntitiesThreshold +
                ", minTransactionsThreshold=" + minTransactionsThreshold +
                ", outcome=" + outcome +
                '}';
    }
}