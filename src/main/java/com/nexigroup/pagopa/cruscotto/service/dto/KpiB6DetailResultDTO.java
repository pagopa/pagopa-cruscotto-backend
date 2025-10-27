package com.nexigroup.pagopa.cruscotto.service.dto;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.Objects;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.JsonNode;

/**
 * A DTO for the KpiB6DetailResult entity.
 * This is a wrapper around KpiDetailResultDTO to maintain API compatibility.
 */
public class KpiB6DetailResultDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;
    private Long instanceId;
    private Long instanceModuleId;
    private Long anagStationId;
    private Long kpiB6ResultId;
    private LocalDate analysisDate;
    private Integer totalStations;
    private Integer stationsWithPaymentOptions;
    private Integer difference;
    private Double percentageDifference;
    private String outcome;

    public KpiB6DetailResultDTO() {}

    /**
     * Constructor to create KpiB6DetailResultDTO from generic KpiDetailResultDTO
     */
    public KpiB6DetailResultDTO(KpiDetailResultDTO genericDto) {
        if (genericDto != null) {
            this.id = genericDto.getId();
            this.instanceId = genericDto.getInstanceId();
            this.instanceModuleId = genericDto.getInstanceModuleId();
            this.kpiB6ResultId = genericDto.getKpiResultId();
            this.analysisDate = genericDto.getAnalysisDate();
            this.outcome = genericDto.getOutcome();
            
            // Parse JSON data field to extract B6-specific fields
            if (genericDto.getData() != null) {
                try {
                    ObjectMapper mapper = new ObjectMapper();
                    JsonNode dataNode = mapper.readTree(genericDto.getData());
                    
                    if (dataNode.has("anagStationId")) {
                        this.anagStationId = dataNode.get("anagStationId").asLong();
                    }
                    if (dataNode.has("totalStations")) {
                        this.totalStations = dataNode.get("totalStations").asInt();
                    }
                    if (dataNode.has("stationsWithPaymentOptions")) {
                        this.stationsWithPaymentOptions = dataNode.get("stationsWithPaymentOptions").asInt();
                    }
                    if (dataNode.has("difference")) {
                        this.difference = dataNode.get("difference").asInt();
                    }
                    if (dataNode.has("percentageDifference")) {
                        this.percentageDifference = dataNode.get("percentageDifference").asDouble();
                    }
                } catch (Exception e) {
                    // Handle JSON parsing errors gracefully
                }
            }
        }
    }

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

    public Long getAnagStationId() {
        return anagStationId;
    }

    public void setAnagStationId(Long anagStationId) {
        this.anagStationId = anagStationId;
    }

    public Long getKpiB6ResultId() {
        return kpiB6ResultId;
    }

    public void setKpiB6ResultId(Long kpiB6ResultId) {
        this.kpiB6ResultId = kpiB6ResultId;
    }

    public LocalDate getAnalysisDate() {
        return analysisDate;
    }

    public void setAnalysisDate(LocalDate analysisDate) {
        this.analysisDate = analysisDate;
    }

    public Integer getTotalStations() {
        return totalStations;
    }

    public void setTotalStations(Integer totalStations) {
        this.totalStations = totalStations;
    }

    public Integer getStationsWithPaymentOptions() {
        return stationsWithPaymentOptions;
    }

    public void setStationsWithPaymentOptions(Integer stationsWithPaymentOptions) {
        this.stationsWithPaymentOptions = stationsWithPaymentOptions;
    }

    public Integer getDifference() {
        return difference;
    }

    public void setDifference(Integer difference) {
        this.difference = difference;
    }

    public Double getPercentageDifference() {
        return percentageDifference;
    }

    public void setPercentageDifference(Double percentageDifference) {
        this.percentageDifference = percentageDifference;
    }

    public String getOutcome() {
        return outcome;
    }

    public void setOutcome(String outcome) {
        this.outcome = outcome;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof KpiB6DetailResultDTO)) {
            return false;
        }

        KpiB6DetailResultDTO that = (KpiB6DetailResultDTO) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "KpiB6DetailResultDTO{" +
                "id=" + id +
                ", instanceId=" + instanceId +
                ", instanceModuleId=" + instanceModuleId +
                ", anagStationId=" + anagStationId +
                ", kpiB6ResultId=" + kpiB6ResultId +
                ", analysisDate=" + analysisDate +
                ", totalStations=" + totalStations +
                ", stationsWithPaymentOptions=" + stationsWithPaymentOptions +
                ", difference=" + difference +
                ", percentageDifference=" + percentageDifference +
                ", outcome='" + outcome + '\'' +
                '}';
    }
}