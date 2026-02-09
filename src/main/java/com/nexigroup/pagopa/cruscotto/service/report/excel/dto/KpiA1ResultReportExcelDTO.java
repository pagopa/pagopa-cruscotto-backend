package com.nexigroup.pagopa.cruscotto.service.report.excel.dto;

import lombok.Data;

import java.sql.Date;
import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDate;

@Data
public class KpiA1ResultReportExcelDTO {

    private Long kpiA1AnalyticDataId;
    private Instant fromHour;
    private Instant toHour;
    private Long totalRequests;
    private Long okRequests;
    private Long reqTimeout;

    private String teOutcome;
    private LocalDate dtEvaluationStartDate;

    public KpiA1ResultReportExcelDTO(
        Long kpiA1AnalyticDataId,
        Instant fromHour,
        Instant toHour,
        Long totalRequests,
        Long okRequests,
        Long reqTimeout,
        String teOutcome,
        LocalDate dtEvaluationStartDate) {

        this.kpiA1AnalyticDataId = kpiA1AnalyticDataId;
        this.fromHour = fromHour;
        this.toHour = toHour;
        this.totalRequests = totalRequests;
        this.okRequests = okRequests;
        this.reqTimeout = reqTimeout;
        this.teOutcome = teOutcome;
        this.dtEvaluationStartDate = dtEvaluationStartDate;
    }
}

