package com.nexigroup.pagopa.cruscotto.service.report.excel.dto;

import lombok.Data;

import java.time.LocalDate;

@Data
public class KpiB1ResultReportExcelDTO {

    private Long kpiB1AnalyticDataId;
    private LocalDate dataDate;
    private String partnerFiscalCode;
    private String institutionFiscalCode;
    private Integer transactionCount;
    private String stationCode;

    private String institutionOutcome;
    private String transactionOutcome;
    private LocalDate dtEvaluationStartDate;

    public KpiB1ResultReportExcelDTO(
        Long kpiB1AnalyticDataId,
        LocalDate dataDate,
        String partnerFiscalCode,
        String institutionFiscalCode,
        Integer transactionCount,
        String stationCode,
        String institutionOutcome,
        String transactionOutcome,
        LocalDate dtEvaluationStartDate) {

        this.kpiB1AnalyticDataId = kpiB1AnalyticDataId;
        this.dataDate = dataDate;
        this.partnerFiscalCode = partnerFiscalCode;
        this.institutionFiscalCode = institutionFiscalCode;
        this.transactionCount = transactionCount;
        this.stationCode = stationCode;
        this.institutionOutcome = institutionOutcome;
        this.transactionOutcome = transactionOutcome;
        this.dtEvaluationStartDate = dtEvaluationStartDate;
    }
}

