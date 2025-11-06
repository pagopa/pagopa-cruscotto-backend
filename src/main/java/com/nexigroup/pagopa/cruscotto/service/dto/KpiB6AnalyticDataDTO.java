package com.nexigroup.pagopa.cruscotto.service.dto;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.Objects;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.JsonNode;

/**
 * A DTO for the KpiB6AnalyticData entity.
 * This is a wrapper around KpiAnalyticDataDTO to maintain API compatibility.
 */
public class KpiB6AnalyticDataDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;
    private Long instanceId;
    private Long instanceModuleId;
    private Long anagStationId;
    private Long kpiB6DetailResultId;
    private String eventId;
    private String eventType;
    private LocalDate analysisDate;
    private String stationCode;
    private String paymentOption;

    public KpiB6AnalyticDataDTO() {}

    /**
     * Constructor to create KpiB6AnalyticDataDTO from generic KpiAnalyticDataDTO
     */
    public KpiB6AnalyticDataDTO(KpiAnalyticDataDTO genericDto) {
        if (genericDto != null) {
            this.id = genericDto.getId();
            this.instanceId = genericDto.getInstanceId();
            this.instanceModuleId = genericDto.getInstanceModuleId();
            this.kpiB6DetailResultId = genericDto.getKpiDetailResultId();
            this.analysisDate = genericDto.getAnalysisDate();
            
            // Parse JSON data field to extract B6-specific fields
            if (genericDto.getAnalyticData() != null) {
                try {
                    ObjectMapper mapper = new ObjectMapper();
                    JsonNode dataNode = mapper.readTree(genericDto.getAnalyticData());
                    
                    // Parse station code from JSON data - stored as string in new framework
                    if (dataNode.has("stationCode")) {
                        this.stationCode = dataNode.get("stationCode").asText();
                    }
                    
                    if (dataNode.has("anagStationId")) {
                        this.anagStationId = dataNode.get("anagStationId").asLong();
                    }
                    if (dataNode.has("eventId")) {
                        this.eventId = dataNode.get("eventId").asText();
                    }
                    if (dataNode.has("eventType")) {
                        this.eventType = dataNode.get("eventType").asText();
                    }
                    // Map paymentOptionsEnabled (boolean) to paymentOption (string) for backward compatibility
                    if (dataNode.has("paymentOptionsEnabled")) {
                        boolean paymentEnabled = dataNode.get("paymentOptionsEnabled").asBoolean();
                        this.paymentOption = paymentEnabled ? "SI" : "NO";
                    } else if (dataNode.has("paymentOption")) {
                        this.paymentOption = dataNode.get("paymentOption").asText();
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

    public Long getKpiB6DetailResultId() {
        return kpiB6DetailResultId;
    }

    public void setKpiB6DetailResultId(Long kpiB6DetailResultId) {
        this.kpiB6DetailResultId = kpiB6DetailResultId;
    }

    public String getEventId() {
        return eventId;
    }

    public void setEventId(String eventId) {
        this.eventId = eventId;
    }

    public String getEventType() {
        return eventType;
    }

    public void setEventType(String eventType) {
        this.eventType = eventType;
    }

    public LocalDate getAnalysisDate() {
        return analysisDate;
    }

    public void setAnalysisDate(LocalDate analysisDate) {
        this.analysisDate = analysisDate;
    }

    public String getStationCode() {
        return stationCode;
    }

    public void setStationCode(String stationCode) {
        this.stationCode = stationCode;
    }

    public String getPaymentOption() {
        return paymentOption;
    }

    public void setPaymentOption(String paymentOption) {
        this.paymentOption = paymentOption;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof KpiB6AnalyticDataDTO)) {
            return false;
        }

        KpiB6AnalyticDataDTO that = (KpiB6AnalyticDataDTO) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "KpiB6AnalyticDataDTO{" +
                "id=" + id +
                ", instanceId=" + instanceId +
                ", instanceModuleId=" + instanceModuleId +
                ", anagStationId=" + anagStationId +
                ", kpiB6DetailResultId=" + kpiB6DetailResultId +
                ", eventId='" + eventId + '\'' +
                ", eventType='" + eventType + '\'' +
                ", analysisDate=" + analysisDate +
                ", stationCode=" + stationCode +
                ", paymentOption='" + paymentOption + '\'' +
                '}';
    }
}