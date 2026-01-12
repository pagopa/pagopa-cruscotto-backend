package com.nexigroup.pagopa.cruscotto.service.report.pdf.config;

import com.nexigroup.pagopa.cruscotto.domain.KpiA2DetailResult;
import com.nexigroup.pagopa.cruscotto.service.report.pdf.model.PdfTableColumn;

import java.time.format.DateTimeFormatter;
import java.util.*;

import org.springframework.context.MessageSource;

public class KpiA2PdfTableConfig extends CommonConfig{

    private KpiA2PdfTableConfig() {}

    public static List<PdfTableColumn> columns(
        MessageSource messageSource,
        Locale locale
    ) {
        return List.of(
            new PdfTableColumn(msg(messageSource, locale, "pdf.kpi.table.period"), "evaluationType"),
            new PdfTableColumn(msg(messageSource, locale, "pdf.kpi.table.from"), "evaluationStartDate"),
            new PdfTableColumn(msg(messageSource, locale, "pdf.kpi.table.to"), "evaluationEndDate"),
            new PdfTableColumn(msg(messageSource, locale, "pdf.kpi.table.totPayments"), "totPayments"),
            new PdfTableColumn(msg(messageSource, locale, "pdf.kpi.table.totIncorrectPayments"), "totIncorrectPayments"),
            new PdfTableColumn(msg(messageSource, locale, "pdf.kpi.table.errorPercentage"), "errorPercentage"),
            new PdfTableColumn(msg(messageSource, locale, "pdf.kpi.table.outcome"), "outcome")
        );
    }

    private static final Comparator<KpiA2DetailResult> A2_TABLE_ORDER = Comparator
        .comparing(
            KpiA2DetailResult::getEvaluationStartDate,
            Comparator.nullsLast(Comparator.naturalOrder()));


    public static List<Map<String, Object>> mapA2Rows(List<KpiA2DetailResult> entities) {

        DateTimeFormatter df = DateTimeFormatter.ofPattern("dd/MM/yyyy");

        return entities.stream().sorted(A2_TABLE_ORDER).map(e -> {
            Map<String, Object> row = new HashMap<>();
            row.put("evaluationType", "TOTALE");
            row.put("evaluationStartDate",
                e.getEvaluationStartDate() != null ? e.getEvaluationStartDate().format(df) : "");
            row.put("evaluationEndDate", e.getEvaluationEndDate() != null ? e.getEvaluationEndDate().format(df) : "");
            row.put("totPayments", e.getTotPayments() != null ? INT_FMT.format(e.getTotPayments()) : "0");
            row.put("totIncorrectPayments", e.getTotIncorrectPayments() != null ? INT_FMT.format(e.getTotIncorrectPayments()) : "0");
            row.put("errorPercentage",
                e.getErrorPercentage() != null ? PERC_FMT.format(e.getErrorPercentage()) + "%" : "");
            row.put("outcome", e.getOutcome() != null ? e.getOutcome().name() : "");
            return row;
        }).toList();
    }

    private static String msg(MessageSource ms, Locale l, String key) {
        return ms.getMessage(key, null, l);
    }
}
