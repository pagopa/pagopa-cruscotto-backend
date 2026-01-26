package com.nexigroup.pagopa.cruscotto.service.report.pdf.config;

import com.nexigroup.pagopa.cruscotto.domain.KpiB9DetailResult;
import com.nexigroup.pagopa.cruscotto.domain.enumeration.EvaluationType;
import com.nexigroup.pagopa.cruscotto.service.report.pdf.model.PdfTableColumn;

import java.time.format.DateTimeFormatter;
import java.util.*;

import org.springframework.context.MessageSource;

public class KpiB9PdfTableConfig extends CommonConfig {

    private KpiB9PdfTableConfig() {}

    public static List<PdfTableColumn> columns(
        MessageSource messageSource,
        Locale locale
    ) {
        return List.of(
            new PdfTableColumn(msg(messageSource, locale, "pdf.kpi.table.period"), "evaluationType"),
            new PdfTableColumn(msg(messageSource, locale, "pdf.kpi.table.from"), "evaluationStartDate"),
            new PdfTableColumn(msg(messageSource, locale, "pdf.kpi.table.to"), "evaluationEndDate"),

            new PdfTableColumn(msg(messageSource, locale, "pdf.kpi.table.totalRes"), "totRes"),
            new PdfTableColumn(msg(messageSource, locale, "pdf.kpi.table.resKo"), "resKo"),
            new PdfTableColumn(msg(messageSource, locale, "pdf.kpi.table.resKoPercentage"), "resKoPercentage"),

            new PdfTableColumn(msg(messageSource, locale, "pdf.kpi.table.outcome"), "outcome")
        );
    }

    private static final Comparator<KpiB9DetailResult> B9_TABLE_ORDER =
        Comparator.<KpiB9DetailResult, Boolean>comparing(
            e -> e.getEvaluationType() == EvaluationType.TOTALE
        ).thenComparing(
            KpiB9DetailResult::getAnalysisDate,
            Comparator.nullsLast(Comparator.naturalOrder())
        );

    public static List<Map<String, Object>> mapB9Rows(List<KpiB9DetailResult> entities) {

        DateTimeFormatter df = DateTimeFormatter.ofPattern("dd/MM/yyyy");

        return entities.stream()
            .sorted(B9_TABLE_ORDER)
            .map(e -> {
                Map<String, Object> row = new HashMap<>();

                row.put("evaluationType",
                    e.getEvaluationType() != null ? e.getEvaluationType().name() : "");

                row.put("evaluationStartDate",
                    e.getEvaluationStartDate() != null ? e.getEvaluationStartDate().format(df) : "");

                row.put("evaluationEndDate",
                    e.getEvaluationEndDate() != null ? e.getEvaluationEndDate().format(df) : "");

                row.put("totRes",
                    e.getTotRes() != null ? INT_FMT.format(e.getTotRes()) : "0");

                row.put("resKo",
                    e.getResKo() != null ? INT_FMT.format(e.getResKo()) : "0");

                row.put("resKoPercentage",
                    e.getResKoPercentage() != null ? DEC_FMT.format(e.getResKoPercentage()) : "0,00");

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
