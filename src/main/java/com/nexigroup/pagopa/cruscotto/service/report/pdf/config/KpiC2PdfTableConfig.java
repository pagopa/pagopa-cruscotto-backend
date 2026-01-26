package com.nexigroup.pagopa.cruscotto.service.report.pdf.config;

import com.nexigroup.pagopa.cruscotto.domain.KpiC2DetailResult;
import com.nexigroup.pagopa.cruscotto.domain.enumeration.EvaluationType;
import com.nexigroup.pagopa.cruscotto.service.report.pdf.model.PdfTableColumn;

import java.time.format.DateTimeFormatter;
import java.util.*;

import org.springframework.context.MessageSource;

public class KpiC2PdfTableConfig extends CommonConfig {

    private KpiC2PdfTableConfig() {}

    public static List<PdfTableColumn> columns(
        MessageSource messageSource,
        Locale locale
    ) {
        return List.of(
            new PdfTableColumn(msg(messageSource, locale, "pdf.kpi.table.period"), "evaluationType"),
            new PdfTableColumn(msg(messageSource, locale, "pdf.kpi.table.from"), "evaluationStartDate"),
            new PdfTableColumn(msg(messageSource, locale, "pdf.kpi.table.to"), "evaluationEndDate"),

            new PdfTableColumn(msg(messageSource, locale, "pdf.kpi.table.totalInstitution"), "totalInstitution"),
            new PdfTableColumn(msg(messageSource, locale, "pdf.kpi.table.totalInstitutionSend"), "totalInstitutionSend"),
            new PdfTableColumn(msg(messageSource, locale, "pdf.kpi.table.percentInstitutionSend"), "percentInstitutionSend"),

            new PdfTableColumn(msg(messageSource, locale, "pdf.kpi.table.totalPayment"), "totalPayment"),
            new PdfTableColumn(msg(messageSource, locale, "pdf.kpi.table.totalNotification"), "totalNotification"),
            new PdfTableColumn(msg(messageSource, locale, "pdf.kpi.table.percentEntiOk"), "percentEntiOk"),

            new PdfTableColumn(msg(messageSource, locale, "pdf.kpi.table.outcome"), "outcome")
        );
    }

    private static final Comparator<KpiC2DetailResult> C2_TABLE_ORDER =
        Comparator.<KpiC2DetailResult, Boolean>comparing(
            e -> e.getEvaluationType() == EvaluationType.TOTALE
        ).thenComparing(
            KpiC2DetailResult::getAnalysisDate,
            Comparator.nullsLast(Comparator.naturalOrder())
        );

    public static List<Map<String, Object>> mapC2Rows(List<KpiC2DetailResult> entities) {

        DateTimeFormatter df = DateTimeFormatter.ofPattern("dd/MM/yyyy");

        return entities.stream()
            .sorted(C2_TABLE_ORDER)
            .map(e -> {
                Map<String, Object> row = new HashMap<>();

                row.put("evaluationType",
                    e.getEvaluationType() != null ? e.getEvaluationType().name() : "");

                row.put("evaluationStartDate",
                    e.getEvaluationStartDate() != null ? e.getEvaluationStartDate().format(df) : "");

                row.put("evaluationEndDate",
                    e.getEvaluationEndDate() != null ? e.getEvaluationEndDate().format(df) : "");

                row.put("totalInstitution",
                    e.getTotalInstitution() != null ? INT_FMT.format(e.getTotalInstitution()) : "0");

                row.put("totalInstitutionSend",
                    e.getTotalInstitutionSend() != null ? INT_FMT.format(e.getTotalInstitutionSend()) : "0");

                row.put("percentInstitutionSend",
                    e.getPercentInstitutionSend() != null ? DEC_FMT.format(e.getPercentInstitutionSend()) : "0,00");

                row.put("totalPayment",
                    e.getTotalPayment() != null ? INT_FMT.format(e.getTotalPayment()) : "0");

                row.put("totalNotification",
                    e.getTotalNotification() != null ? INT_FMT.format(e.getTotalNotification()) : "0");

                row.put("percentEntiOk",
                    e.getPercentEntiOk() != null ? DEC_FMT.format(e.getPercentEntiOk()) : "0,00");

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
