package com.nexigroup.pagopa.cruscotto.domain;

import jakarta.persistence.*;
import lombok.Data;

import java.time.Instant;

@Entity
@Table(name = "kpi_b2_analytic_drilldown")
@Data
public class KpiB2AnalyticDrillDown {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SQCRUSC8_KPIB2ANALDRILLDOWN")
    @SequenceGenerator(name = "SQCRUSC8_KPIB2ANALDRILLDOWN", sequenceName = "SQCRUSC8_KPIB2ANALDRILLDOWN", allocationSize = 1)
    private Long id;

    @Column(name = "kpi_b2_analytic_data_id")
    private Long kpiB2AnalyticDataId;

    @Column(name = "from_hour")
    private Instant fromHour;

    @Column(name = "end_hour")
    private Instant endHour;

    @Column(name = "total_requests")
    private Long totalRequests;

    @Column(name = "ok_requests")
    private Long okRequests;

    @Column(name = "average_time_ms")
    private Double averageTimeMs;

    // Getters and setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getKpiB2AnalyticDataId() { return kpiB2AnalyticDataId; }
    public void setKpiB2AnalyticDataId(Long kpiB2AnalyticDataId) { this.kpiB2AnalyticDataId = kpiB2AnalyticDataId; }
    public Instant getFromHour() { return fromHour; }
    public void setFromHour(Instant fromHour) { this.fromHour = fromHour; }
    public Instant getEndHour() { return endHour; }
    public void setEndHour(Instant endHour) { this.endHour = endHour; }
    public Long getTotalRequests() { return totalRequests; }
    public void setTotalRequests(Long totalRequests) { this.totalRequests = totalRequests; }
    public Long getOkRequests() { return okRequests; }
    public void setOkRequests(Long okRequests) { this.okRequests = okRequests; }
    public Double getAverageTimeMs() { return averageTimeMs; }
    public void setAverageTimeMs(Double averageTimeMs) { this.averageTimeMs = averageTimeMs; }
}
