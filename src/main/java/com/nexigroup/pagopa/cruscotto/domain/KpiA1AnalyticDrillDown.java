package com.nexigroup.pagopa.cruscotto.domain;

import jakarta.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "kpi_a1_analytic_drilldown")
public class KpiA1AnalyticDrillDown implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "kpi_a1_analytic_data_id", nullable = false)
    private Long kpiA1AnalyticDataId;

    @Column(name = "from_hour", nullable = false)
    private String fromHour;

    @Column(name = "to_hour", nullable = false)
    private String toHour;

    @Column(name = "total_requests", nullable = false)
    private Long totalRequests;

    @Column(name = "ok_requests", nullable = false)
    private Long okRequests;

    @Column(name = "average_time_ms", nullable = false)
    private Double averageTimeMs;

    // Getters and setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getKpiA1AnalyticDataId() { return kpiA1AnalyticDataId; }
    public void setKpiA1AnalyticDataId(Long kpiA1AnalyticDataId) { this.kpiA1AnalyticDataId = kpiA1AnalyticDataId; }
    public String getFromHour() { return fromHour; }
    public void setFromHour(String fromHour) { this.fromHour = fromHour; }
    public String getToHour() { return toHour; }
    public void setToHour(String toHour) { this.toHour = toHour; }
    public Long getTotalRequests() { return totalRequests; }
    public void setTotalRequests(Long totalRequests) { this.totalRequests = totalRequests; }
    public Long getOkRequests() { return okRequests; }
    public void setOkRequests(Long okRequests) { this.okRequests = okRequests; }
    public Double getAverageTimeMs() { return averageTimeMs; }
    public void setAverageTimeMs(Double averageTimeMs) { this.averageTimeMs = averageTimeMs; }
}
