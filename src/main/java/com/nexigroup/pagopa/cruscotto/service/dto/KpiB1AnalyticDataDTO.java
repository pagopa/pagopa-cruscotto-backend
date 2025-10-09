package com.nexigroup.pagopa.cruscotto.service.dto;

import java.time.LocalDate;

import org.springframework.lang.NonNull;

/**
 * A DTO for the {@link com.nexigroup.pagopa.cruscotto.domain.KpiB1AnalyticData} entity.
 */
public class KpiB1AnalyticDataDTO {

    private Long id;

    @NonNull
    private Long instanceId;

    @NonNull
    private Long instanceModuleId;

    @NonNull
    private LocalDate analysisDate;

    @NonNull
    private String entityCode;

    @NonNull
    private LocalDate evaluationStartDate;

    @NonNull
    private LocalDate evaluationEndDate;

    private Long totalTransactions;

    private Integer uniqueStations;

    @NonNull
    private Long kpiB1DetailResultId;

    public KpiB1AnalyticDataDTO() {
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

    public String getEntityCode() {
        return entityCode;
    }

    public void setEntityCode(String entityCode) {
        this.entityCode = entityCode;
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

    public Integer getUniqueStations() {
        return uniqueStations;
    }

    public void setUniqueStations(Integer uniqueStations) {
        this.uniqueStations = uniqueStations;
    }

    public Long getKpiB1DetailResultId() {
        return kpiB1DetailResultId;
    }

    public void setKpiB1DetailResultId(Long kpiB1DetailResultId) {
        this.kpiB1DetailResultId = kpiB1DetailResultId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        KpiB1AnalyticDataDTO that = (KpiB1AnalyticDataDTO) o;

        if (id != null ? !id.equals(that.id) : that.id != null) return false;
        if (instanceId != null ? !instanceId.equals(that.instanceId) : that.instanceId != null) return false;
        if (instanceModuleId != null ? !instanceModuleId.equals(that.instanceModuleId) : that.instanceModuleId != null) return false;
        if (analysisDate != null ? !analysisDate.equals(that.analysisDate) : that.analysisDate != null) return false;
        if (entityCode != null ? !entityCode.equals(that.entityCode) : that.entityCode != null) return false;
        if (evaluationStartDate != null ? !evaluationStartDate.equals(that.evaluationStartDate) : that.evaluationStartDate != null) return false;
        if (evaluationEndDate != null ? !evaluationEndDate.equals(that.evaluationEndDate) : that.evaluationEndDate != null) return false;
        if (totalTransactions != null ? !totalTransactions.equals(that.totalTransactions) : that.totalTransactions != null) return false;
        if (uniqueStations != null ? !uniqueStations.equals(that.uniqueStations) : that.uniqueStations != null) return false;
        return kpiB1DetailResultId != null ? kpiB1DetailResultId.equals(that.kpiB1DetailResultId) : that.kpiB1DetailResultId == null;
    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (instanceId != null ? instanceId.hashCode() : 0);
        result = 31 * result + (instanceModuleId != null ? instanceModuleId.hashCode() : 0);
        result = 31 * result + (analysisDate != null ? analysisDate.hashCode() : 0);
        result = 31 * result + (entityCode != null ? entityCode.hashCode() : 0);
        result = 31 * result + (evaluationStartDate != null ? evaluationStartDate.hashCode() : 0);
        result = 31 * result + (evaluationEndDate != null ? evaluationEndDate.hashCode() : 0);
        result = 31 * result + (totalTransactions != null ? totalTransactions.hashCode() : 0);
        result = 31 * result + (uniqueStations != null ? uniqueStations.hashCode() : 0);
        result = 31 * result + (kpiB1DetailResultId != null ? kpiB1DetailResultId.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "KpiB1AnalyticDataDTO{" +
                "id=" + id +
                ", instanceId=" + instanceId +
                ", instanceModuleId=" + instanceModuleId +
                ", analysisDate=" + analysisDate +
                ", entityCode='" + entityCode + '\'' +
                ", evaluationStartDate=" + evaluationStartDate +
                ", evaluationEndDate=" + evaluationEndDate +
                ", totalTransactions=" + totalTransactions +
                ", uniqueStations=" + uniqueStations +
                ", kpiB1DetailResultId=" + kpiB1DetailResultId +
                '}';
    }
}