package com.nexigroup.pagopa.cruscotto.service.dto;

import java.time.Instant;

public class KpiA1AnalyticDrillDownDTO {
    private Long id;
    private Long kpiA1AnalyticDataId;
    private Instant fromHour;
    private Instant toHour;
    private Long totalRequests;
    private Long okRequests;
    private Long reqTimeout;

    // Getters and setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getKpiA1AnalyticDataId() { return kpiA1AnalyticDataId; }
    public void setKpiA1AnalyticDataId(Long kpiA1AnalyticDataId) { this.kpiA1AnalyticDataId = kpiA1AnalyticDataId; }
    public Instant getFromHour() { return fromHour; }
    public void setFromHour(Instant fromHour) { this.fromHour = fromHour; }
    public Instant getToHour() { return toHour; }
    public void setToHour(Instant toHour) { this.toHour = toHour; }
    public Long getTotalRequests() { return totalRequests; }
    public void setTotalRequests(Long totalRequests) { this.totalRequests = totalRequests; }
    public Long getOkRequests() { return okRequests; }
    public void setOkRequests(Long okRequests) { this.okRequests = okRequests; }
    public Long getReqTimeout() { return reqTimeout; }
    public void setReqTimeout(Long reqTimeout) { this.reqTimeout = reqTimeout;}
}
