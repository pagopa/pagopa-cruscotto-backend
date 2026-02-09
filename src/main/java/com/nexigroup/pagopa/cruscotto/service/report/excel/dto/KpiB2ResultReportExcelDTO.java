package com.nexigroup.pagopa.cruscotto.service.report.excel.dto;

import lombok.Data;

import java.time.Instant;
import java.time.LocalDate;

@Data
public class KpiB2ResultReportExcelDTO {

    private Long kpiB2AnalyticDataId;
    private Instant fromHour;
    private Instant endHour;
    private Long totalRequests;
    private Long okRequests;
    private Double averageTimeMs;

    private String teOutcome;
    private LocalDate dtEvaluationStartDate;

    public KpiB2ResultReportExcelDTO(
        Long kpiB2AnalyticDataId,
        Instant fromHour,
        Instant endHour,
        Long totalRequests,
        Long okRequests,
        Double averageTimeMs,
        String teOutcome,
        LocalDate dtEvaluationStartDate) {

        this.kpiB2AnalyticDataId = kpiB2AnalyticDataId;
        this.fromHour = fromHour;
        this.endHour = endHour;
        this.totalRequests = totalRequests;
        this.okRequests = okRequests;
        this.averageTimeMs = averageTimeMs;
        this.teOutcome = teOutcome;
        this.dtEvaluationStartDate = dtEvaluationStartDate;
    }
}

