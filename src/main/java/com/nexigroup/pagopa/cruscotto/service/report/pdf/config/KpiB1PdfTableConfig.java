package com.nexigroup.pagopa.cruscotto.service.report.pdf.config;

import com.nexigroup.pagopa.cruscotto.domain.KpiB1DetailResult;
import com.nexigroup.pagopa.cruscotto.domain.enumeration.EvaluationType;
import com.nexigroup.pagopa.cruscotto.service.report.pdf.model.PdfTableColumn;

import java.time.format.DateTimeFormatter;
import java.util.*;

import org.springframework.context.MessageSource;

public class KpiB1PdfTableConfig extends CommonConfig {

    private KpiB1PdfTableConfig() {}

    public static List<PdfTableColumn> columns(
        MessageSource messageSource,
        Locale locale
    ) {
        return List.of(
            new PdfTableColumn(msg(messageSource, locale, "pdf.kpi.table.period"), "evaluationType"),
            new PdfTableColumn(msg(messageSource, locale, "pdf.kpi.table.from"), "evaluationStartDate"),
            new PdfTableColumn(msg(messageSource, locale, "pdf.kpi.table.to"), "evaluationEndDate"),

            new PdfTableColumn(msg(messageSource, locale, "pdf.kpi.table.totalInstitutions"), "totalInstitutions"),
            new PdfTableColumn(msg(messageSource, locale, "pdf.kpi.table.institutionDifference"), "institutionDifference"),
            new PdfTableColumn(msg(messageSource, locale, "pdf.kpi.table.institutionDiffPerc"), "institutionDifferencePercentage"),
            new PdfTableColumn(msg(messageSource, locale, "pdf.kpi.table.institutionOutcome"), "institutionOutcome"),

            new PdfTableColumn(msg(messageSource, locale, "pdf.kpi.table.totalTransactions"), "totalTransactions"),
            new PdfTableColumn(msg(messageSource, locale, "pdf.kpi.table.transactionDifference"), "transactionDifference"),
            new PdfTableColumn(msg(messageSource, locale, "pdf.kpi.table.transactionDiffPerc"), "transactionDifferencePercentage"),
            new PdfTableColumn(msg(messageSource, locale, "pdf.kpi.table.transactionOutcome"), "transactionOutcome")
        );
    }

    private static final Comparator<KpiB1DetailResult> B1_TABLE_ORDER =
        Comparator.<KpiB1DetailResult, Boolean>comparing(
            e -> e.getEvaluationType() == EvaluationType.TOTALE
        ).thenComparing(
            KpiB1DetailResult::getEvaluationStartDate,
            Comparator.nullsLast(Comparator.naturalOrder())
        );

    public static List<Map<String, Object>> mapB1Rows(List<KpiB1DetailResult> entities) {

        DateTimeFormatter df = DateTimeFormatter.ofPattern("dd/MM/yyyy");

        return entities.stream()
            .sorted(B1_TABLE_ORDER)
            .map(e -> {
                Map<String, Object> row = new HashMap<>();

                row.put("evaluationType",
                    e.getEvaluationType() != null ? e.getEvaluationType().name() : "");

                row.put("evaluationStartDate",
                    e.getEvaluationStartDate() != null ? e.getEvaluationStartDate().format(df) : "");

                row.put("evaluationEndDate",
                    e.getEvaluationEndDate() != null ? e.getEvaluationEndDate().format(df) : "");

                row.put("totalInstitutions",
                    e.getTotalInstitutions() != null ? INT_FMT.format(e.getTotalInstitutions()) : "0");

                row.put("institutionDifference",
                    e.getInstitutionDifference() != null ? INT_FMT.format(e.getInstitutionDifference()) : "0");

                row.put("institutionDifferencePercentage",
                    e.getInstitutionDifferencePercentage() != null
                        ? DEC_FMT.format(e.getInstitutionDifferencePercentage())
                        : "0");

                row.put("institutionOutcome",
                    e.getInstitutionOutcome() != null ? e.getInstitutionOutcome().name() : "");

                row.put("totalTransactions",
                    e.getTotalTransactions() != null ? INT_FMT.format(e.getTotalTransactions()) : "0");

                row.put("transactionDifference",
                    e.getTransactionDifference() != null ? INT_FMT.format(e.getTransactionDifference()) : "0");

                row.put("transactionDifferencePercentage",
                    e.getTransactionDifferencePercentage() != null
                        ? DEC_FMT.format(e.getTransactionDifferencePercentage())
                        : "0");

                row.put("transactionOutcome",
                    e.getTransactionOutcome() != null ? e.getTransactionOutcome().name() : "");

                return row;
            })
            .toList();
    }

    private static String msg(MessageSource ms, Locale l, String key) {
        return ms.getMessage(key, null, l);
    }
}
