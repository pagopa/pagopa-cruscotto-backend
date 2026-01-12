package com.nexigroup.pagopa.cruscotto.service.report.pdf.config;

import com.nexigroup.pagopa.cruscotto.domain.KpiB2DetailResult;
import com.nexigroup.pagopa.cruscotto.domain.enumeration.EvaluationType;
import com.nexigroup.pagopa.cruscotto.service.report.pdf.model.PdfTableColumn;

import java.time.format.DateTimeFormatter;
import java.util.*;

import org.springframework.context.MessageSource;

public class KpiB2PdfTableConfig extends CommonConfig {

    private KpiB2PdfTableConfig() {}

    public static List<PdfTableColumn> columns(
        MessageSource messageSource,
        Locale locale
    ) {
        return List.of(
            new PdfTableColumn(msg(messageSource, locale, "pdf.kpi.table.period"), "evaluationType"),
            new PdfTableColumn(msg(messageSource, locale, "pdf.kpi.table.from"), "evaluationStartDate"),
            new PdfTableColumn(msg(messageSource, locale, "pdf.kpi.table.to"), "evaluationEndDate"),

            new PdfTableColumn(msg(messageSource, locale, "pdf.kpi.table.totReq"), "totReq"),
            new PdfTableColumn(msg(messageSource, locale, "pdf.kpi.table.avgTime"), "avgTime"),
            new PdfTableColumn(msg(messageSource, locale, "pdf.kpi.table.overTimeLimit"), "overTimeLimit"),
            new PdfTableColumn(msg(messageSource, locale, "pdf.kpi.table.outcome"), "outcome")
        );
    }

    private static final Comparator<KpiB2DetailResult> B2_TABLE_ORDER =
        Comparator.<KpiB2DetailResult, Boolean>comparing(
            e -> e.getEvaluationType() == EvaluationType.TOTALE
        ).thenComparing(
            KpiB2DetailResult::getEvaluationStartDate,
            Comparator.nullsLast(Comparator.naturalOrder())
        );

    public static List<Map<String, Object>> mapB2Rows(List<KpiB2DetailResult> entities) {

        DateTimeFormatter df = DateTimeFormatter.ofPattern("dd/MM/yyyy");

        return entities.stream()
            .sorted(B2_TABLE_ORDER)
            .map(e -> {
                Map<String, Object> row = new HashMap<>();

                row.put("evaluationType",
                    e.getEvaluationType() != null ? e.getEvaluationType().name() : "");

                row.put("evaluationStartDate",
                    e.getEvaluationStartDate() != null ? e.getEvaluationStartDate().format(df) : "");

                row.put("evaluationEndDate",
                    e.getEvaluationEndDate() != null ? e.getEvaluationEndDate().format(df) : "");

                row.put("totReq",
                    e.getTotReq() != null ? INT_FMT.format(e.getTotReq()) : "0");

                row.put("avgTime",
                    e.getAvgTime() != null ? DEC_FMT.format(e.getAvgTime()) : "0,00");

                row.put("overTimeLimit",
                    e.getOverTimeLimit() != null ? DEC_FMT.format(e.getOverTimeLimit()) : "0,00");

                row.put("outcome",
                    e.getOutcome() != null ? e.getOutcome().name() : "");

                return row;
            })
            .toList();
    }

    private static String msg(MessageSource ms, Locale l, String key) {
        return ms.getMessage(key, null, l);
    }
}
