package com.nexigroup.pagopa.cruscotto.service.report.pdf.config;

import com.nexigroup.pagopa.cruscotto.domain.KpiC1DetailResult;
import com.nexigroup.pagopa.cruscotto.domain.enumeration.EvaluationType;
import com.nexigroup.pagopa.cruscotto.service.report.pdf.model.PdfTableColumn;

import java.time.format.DateTimeFormatter;
import java.util.*;

import org.springframework.context.MessageSource;

public class KpiC1PdfTableConfig extends CommonConfig {

    private KpiC1PdfTableConfig() {}

    public static List<PdfTableColumn> columns(
        MessageSource messageSource,
        Locale locale
    ) {
        return List.of(
            new PdfTableColumn(msg(messageSource, locale, "pdf.kpi.table.period"), "evaluationType"),
            new PdfTableColumn(msg(messageSource, locale, "pdf.kpi.table.from"), "evaluationStartDate"),
            new PdfTableColumn(msg(messageSource, locale, "pdf.kpi.table.to"), "evaluationEndDate"),

            new PdfTableColumn(msg(messageSource, locale, "pdf.kpi.table.cfInstitution"), "cfInstitution"),

            new PdfTableColumn(msg(messageSource, locale, "pdf.kpi.table.totalPositions"), "totalPositions"),
            new PdfTableColumn(msg(messageSource, locale, "pdf.kpi.table.sentMessages"), "sentMessages"),
            new PdfTableColumn(msg(messageSource, locale, "pdf.kpi.table.sendingPercentage"), "sendingPercentage"),

            new PdfTableColumn(msg(messageSource, locale, "pdf.kpi.table.configuredThreshold"), "configuredThreshold"),
            new PdfTableColumn(msg(messageSource, locale, "pdf.kpi.table.outcome"), "outcome")
        );
    }

    private static final Comparator<KpiC1DetailResult> C1_TABLE_ORDER =
        Comparator.<KpiC1DetailResult, Boolean>comparing(
            e -> e.getEvaluationType() == EvaluationType.TOTALE
        ).thenComparing(
            KpiC1DetailResult::getCfInstitution,
            Comparator.nullsLast(String::compareTo)
        );

    public static List<Map<String, Object>> mapC1Rows(List<KpiC1DetailResult> entities) {

        DateTimeFormatter df = DateTimeFormatter.ofPattern("dd/MM/yyyy");

        return entities.stream()
            .sorted(C1_TABLE_ORDER)
            .map(e -> {
                Map<String, Object> row = new HashMap<>();

                row.put("evaluationType",
                    e.getEvaluationType() != null ? e.getEvaluationType().name() : "");

                row.put("evaluationStartDate",
                    e.getEvaluationStartDate() != null ? e.getEvaluationStartDate().format(df) : "");

                row.put("evaluationEndDate",
                    e.getEvaluationEndDate() != null ? e.getEvaluationEndDate().format(df) : "");

                row.put("cfInstitution",
                    e.getCfInstitution() != null ? e.getCfInstitution() : "");

                row.put("totalPositions",
                    e.getTotalPositions() != null ? INT_FMT.format(e.getTotalPositions()) : "0");

                row.put("sentMessages",
                    e.getSentMessages() != null ? INT_FMT.format(e.getSentMessages()) : "0");

                row.put("sendingPercentage",
                    e.getSendingPercentage() != null ? DEC_FMT.format(e.getSendingPercentage()) : "0,00");

                row.put("configuredThreshold",
                    e.getConfiguredThreshold() != null ? DEC_FMT.format(e.getConfiguredThreshold()) : "0,00");

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
