package com.nexigroup.pagopa.cruscotto.service.report.pdf.config.a2;

import com.nexigroup.pagopa.cruscotto.service.report.pdf.model.PdfTableColumn;
import java.util.List;
import java.util.Locale;

import org.springframework.context.MessageSource;

public class KpiA2PdfTableConfig {

    private KpiA2PdfTableConfig() {}

    public static List<PdfTableColumn> columns(
        MessageSource messageSource,
        Locale locale
    ) {
        return List.of(
            new PdfTableColumn(msg(messageSource, locale, "pdf.kpi.a2.table.period"), "evaluationType"),
            new PdfTableColumn(msg(messageSource, locale, "pdf.kpi.a2.table.from"), "evaluationStartDate"),
            new PdfTableColumn(msg(messageSource, locale, "pdf.kpi.a2.table.to"), "evaluationEndDate"),
            new PdfTableColumn(msg(messageSource, locale, "pdf.kpi.a2.table.totPayments"), "totPayments"),
            new PdfTableColumn(msg(messageSource, locale, "pdf.kpi.a2.table.totIncorrectPayments"), "totIncorrectPayments"),
            new PdfTableColumn(msg(messageSource, locale, "pdf.kpi.a2.table.errorPercentage"), "errorPercentage"),
            new PdfTableColumn(msg(messageSource, locale, "pdf.kpi.a2.table.outcome"), "outcome")
        );
    }

    private static String msg(MessageSource ms, Locale l, String key) {
        return ms.getMessage(key, null, l);
    }
}