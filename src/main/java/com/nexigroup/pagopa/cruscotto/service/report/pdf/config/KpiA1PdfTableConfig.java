package com.nexigroup.pagopa.cruscotto.service.report.pdf.config;

import com.nexigroup.pagopa.cruscotto.domain.KpiA1DetailResult;
import com.nexigroup.pagopa.cruscotto.domain.enumeration.EvaluationType;
import com.nexigroup.pagopa.cruscotto.service.report.pdf.model.PdfTableColumn;

import java.time.format.DateTimeFormatter;
import java.util.*;

import org.springframework.context.MessageSource;

public class KpiA1PdfTableConfig extends CommonConfig{

    private KpiA1PdfTableConfig() {}

    public static List<PdfTableColumn> columns(
        MessageSource messageSource,
        Locale locale
    ) {
        return List.of(
            new PdfTableColumn(msg(messageSource, locale, "pdf.kpi.table.period"), "evaluationType"),
            new PdfTableColumn(msg(messageSource, locale, "pdf.kpi.table.from"), "evaluationStartDate"),
            new PdfTableColumn(msg(messageSource, locale, "pdf.kpi.table.to"), "evaluationEndDate"),
            new PdfTableColumn(msg(messageSource, locale, "pdf.kpi.table.totalRequests"), "totReq"),
            new PdfTableColumn(msg(messageSource, locale, "pdf.kpi.table.timeoutRequests"), "reqTimeout"),
            new PdfTableColumn(msg(messageSource, locale, "pdf.kpi.table.timeoutPercentage"), "timeoutPercentage"),
            new PdfTableColumn(msg(messageSource, locale, "pdf.kpi.table.outcome"), "outcome")
        );
    }

    private static final Comparator<KpiA1DetailResult> A1_TABLE_ORDER = Comparator
        .<KpiA1DetailResult, Boolean>comparing(
            e -> e.getEvaluationType() == EvaluationType.TOTALE)
        .thenComparing(
            KpiA1DetailResult::getEvaluationStartDate,
            Comparator.nullsLast(Comparator.naturalOrder()));

    public static List<Map<String, Object>> mapA1Rows(List<KpiA1DetailResult> entities) {

        DateTimeFormatter df = DateTimeFormatter.ofPattern("dd/MM/yyyy");

        return entities.stream().sorted(A1_TABLE_ORDER).map(e -> {
            Map<String, Object> row = new HashMap<>();
            row.put("evaluationType", e.getEvaluationType() != null ? e.getEvaluationType().name() : "");
            row.put("evaluationStartDate",
                e.getEvaluationStartDate() != null ? e.getEvaluationStartDate().format(df) : "");
            row.put("evaluationEndDate", e.getEvaluationEndDate() != null ? e.getEvaluationEndDate().format(df) : "");
            row.put("totReq", e.getTotReq() != null ? INT_FMT.format(e.getTotReq()) : "0");
            row.put("reqTimeout", e.getReqTimeout() != null ? INT_FMT.format(e.getReqTimeout()) : "0");
            row.put("timeoutPercentage",
                e.getTimeoutPercentage() != null ? PERC_FMT.format(e.getTimeoutPercentage()) + "%" : "");
            row.put("outcome", e.getOutcome() != null ? e.getOutcome().name() : "");
            return row;
        }).toList();
    }

    private static String msg(MessageSource ms, Locale l, String key) {
        return ms.getMessage(key, null, l);
    }
}
