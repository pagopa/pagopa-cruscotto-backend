package com.nexigroup.pagopa.cruscotto.service.dto;

import com.nexigroup.pagopa.cruscotto.domain.enumeration.EvaluationType;
import com.nexigroup.pagopa.cruscotto.domain.enumeration.OutcomeStatus;
import java.time.LocalDate;

import org.springframework.lang.NonNull;

/**
 * A DTO for the {@link com.nexigroup.pagopa.cruscotto.domain.KpiB1DetailResult} entity.
 */
public class KpiB1DetailResultDTO {

    private Long id;

    @NonNull
    private Long instanceId;

    @NonNull
    private Long instanceModuleId;

    @NonNull
    private LocalDate analysisDate;

    @NonNull
    private EvaluationType evaluationType;

    @NonNull
    private LocalDate evaluationStartDate;

    @NonNull
    private LocalDate evaluationEndDate;

    private Long totalTransactions;

    private Integer uniqueEntities;

    @NonNull
    private OutcomeStatus outcome;

    @NonNull
    private Long kpiB1ResultId;

    public KpiB1DetailResultDTO() {
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

    public LocalDate getEvaluationStartDate() {
        return evaluationStartDate;
    }

    public void setEvaluationStartDate(LocalDate evaluationStartDate) {
        this.evaluationStartDate = evaluationStartDate;
    }

    public LocalDate getEvaluationEndDate() {
        return evaluationEndDate;
    }

    public void setEvaluationEndDate(LocalDate evaluationEndDate) {
        this.evaluationEndDate = evaluationEndDate;
    }

    public Long getTotalTransactions() {
        return totalTransactions;
    }

    public void setTotalTransactions(Long totalTransactions) {
        this.totalTransactions = totalTransactions;
    }

    public Integer getUniqueEntities() {
        return uniqueEntities;
    }

    public void setUniqueEntities(Integer uniqueEntities) {
        this.uniqueEntities = uniqueEntities;
    }

    public OutcomeStatus getOutcome() {
        return outcome;
    }

    public void setOutcome(OutcomeStatus outcome) {
        this.outcome = outcome;
    }

    public Long getKpiB1ResultId() {
        return kpiB1ResultId;
    }

    public void setKpiB1ResultId(Long kpiB1ResultId) {
        this.kpiB1ResultId = kpiB1ResultId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        KpiB1DetailResultDTO that = (KpiB1DetailResultDTO) o;

        if (id != null ? !id.equals(that.id) : that.id != null) return false;
        if (instanceId != null ? !instanceId.equals(that.instanceId) : that.instanceId != null) return false;
        if (instanceModuleId != null ? !instanceModuleId.equals(that.instanceModuleId) : that.instanceModuleId != null) return false;
        if (analysisDate != null ? !analysisDate.equals(that.analysisDate) : that.analysisDate != null) return false;
        if (evaluationType != that.evaluationType) return false;
        if (evaluationStartDate != null ? !evaluationStartDate.equals(that.evaluationStartDate) : that.evaluationStartDate != null) return false;
        if (evaluationEndDate != null ? !evaluationEndDate.equals(that.evaluationEndDate) : that.evaluationEndDate != null) return false;
        if (totalTransactions != null ? !totalTransactions.equals(that.totalTransactions) : that.totalTransactions != null) return false;
        if (uniqueEntities != null ? !uniqueEntities.equals(that.uniqueEntities) : that.uniqueEntities != null) return false;
        if (outcome != that.outcome) return false;
        return kpiB1ResultId != null ? kpiB1ResultId.equals(that.kpiB1ResultId) : that.kpiB1ResultId == null;
    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (instanceId != null ? instanceId.hashCode() : 0);
        result = 31 * result + (instanceModuleId != null ? instanceModuleId.hashCode() : 0);
        result = 31 * result + (analysisDate != null ? analysisDate.hashCode() : 0);
        result = 31 * result + (evaluationType != null ? evaluationType.hashCode() : 0);
        result = 31 * result + (evaluationStartDate != null ? evaluationStartDate.hashCode() : 0);
        result = 31 * result + (evaluationEndDate != null ? evaluationEndDate.hashCode() : 0);
        result = 31 * result + (totalTransactions != null ? totalTransactions.hashCode() : 0);
        result = 31 * result + (uniqueEntities != null ? uniqueEntities.hashCode() : 0);
        result = 31 * result + (outcome != null ? outcome.hashCode() : 0);
        result = 31 * result + (kpiB1ResultId != null ? kpiB1ResultId.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "KpiB1DetailResultDTO{" +
                "id=" + id +
                ", instanceId=" + instanceId +
                ", instanceModuleId=" + instanceModuleId +
                ", analysisDate=" + analysisDate +
                ", evaluationType=" + evaluationType +
                ", evaluationStartDate=" + evaluationStartDate +
                ", evaluationEndDate=" + evaluationEndDate +
                ", totalTransactions=" + totalTransactions +
                ", uniqueEntities=" + uniqueEntities +
                ", outcome=" + outcome +
                ", kpiB1ResultId=" + kpiB1ResultId +
                '}';
    }
}