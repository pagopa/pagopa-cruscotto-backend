package com.nexigroup.pagopa.cruscotto.service.report.pdf.config;

import com.nexigroup.pagopa.cruscotto.domain.KpiB3DetailResult;
import com.nexigroup.pagopa.cruscotto.domain.enumeration.EvaluationType;
import com.nexigroup.pagopa.cruscotto.service.report.pdf.model.PdfTableColumn;

import java.time.format.DateTimeFormatter;
import java.util.*;

import org.springframework.context.MessageSource;

public class KpiB3PdfTableConfig extends CommonConfig{

    private KpiB3PdfTableConfig() {}

    public static List<PdfTableColumn> columns(
        MessageSource messageSource,
        Locale locale
    ) {
        return List.of(
            new PdfTableColumn(msg(messageSource, locale, "pdf.kpi.table.period"), "evaluationType"),
            new PdfTableColumn(msg(messageSource, locale, "pdf.kpi.table.from"), "evaluationStartDate"),
            new PdfTableColumn(msg(messageSource, locale, "pdf.kpi.table.to"), "evaluationEndDate"),
            new PdfTableColumn(msg(messageSource, locale, "pdf.kpi.table.totalStandIn"), "totalStandIn"),
            new PdfTableColumn(msg(messageSource, locale, "pdf.kpi.table.outcome"), "outcome")
        );
    }

    private static final Comparator<KpiB3DetailResult> B3_TABLE_ORDER = Comparator
        .<KpiB3DetailResult, Boolean>comparing(
            e -> e.getEvaluationType() == EvaluationType.TOTALE)
        .thenComparing(
            KpiB3DetailResult::getEvaluationStartDate,
            Comparator.nullsLast(Comparator.naturalOrder()));


    public static  List<Map<String, Object>> mapB3Rows(List<KpiB3DetailResult> entities) {

        DateTimeFormatter df = DateTimeFormatter.ofPattern("dd/MM/yyyy");

        return entities.stream().sorted(B3_TABLE_ORDER).map(e -> {
            Map<String, Object> row = new HashMap<>();
            row.put("evaluationType", e.getEvaluationType() != null ? e.getEvaluationType().name() : "");
            row.put("evaluationStartDate",
                e.getEvaluationStartDate() != null ? e.getEvaluationStartDate().format(df) : "");
            row.put("evaluationEndDate", e.getEvaluationEndDate() != null ? e.getEvaluationEndDate().format(df) : "");
            row.put("totalStandIn", e.getTotalStandIn() != null ? INT_FMT.format(e.getTotalStandIn()) : "0");
            row.put("outcome", e.getOutcome() != null ? e.getOutcome().name() : "");
            return row;
        }).toList();
    }
    private static String msg(MessageSource ms, Locale l, String key) {
        return ms.getMessage(key, null, l);
    }
}
