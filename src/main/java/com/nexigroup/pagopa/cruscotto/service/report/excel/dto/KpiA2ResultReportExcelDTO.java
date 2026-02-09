package com.nexigroup.pagopa.cruscotto.service.report.excel.dto;

import lombok.Data;

import java.time.Instant;
import java.time.LocalDate;
@Data
public class KpiA2ResultReportExcelDTO {

    private Long kpiA2AnalyticDataId;
    private String transferCategory;
    private Long totIncorrectPayments;
    private Instant fromHour;
    private Instant endHour;
    private Long totPayments;

    private String teOutcome;
    private LocalDate dtEvaluationStartDate;

    public KpiA2ResultReportExcelDTO(
        Long kpiA2AnalyticDataId,
        String transferCategory,
        Long totIncorrectPayments,
        Instant fromHour,
        Instant endHour,
        Long totPayments,
        String teOutcome,
        LocalDate dtEvaluationStartDate) {

        this.kpiA2AnalyticDataId = kpiA2AnalyticDataId;
        this.transferCategory = transferCategory;
        this.totIncorrectPayments = totIncorrectPayments;
        this.fromHour = fromHour;
        this.endHour = endHour;
        this.totPayments = totPayments;
        this.teOutcome = teOutcome;
        this.dtEvaluationStartDate = dtEvaluationStartDate;
    }
}
