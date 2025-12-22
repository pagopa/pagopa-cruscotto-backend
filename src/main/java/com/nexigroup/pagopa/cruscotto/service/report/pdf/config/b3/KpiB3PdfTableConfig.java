package com.nexigroup.pagopa.cruscotto.service.report.pdf.config.b3;

import com.nexigroup.pagopa.cruscotto.service.report.pdf.model.PdfTableColumn;
import java.util.List;
import java.util.Locale;

import org.springframework.context.MessageSource;

public class KpiB3PdfTableConfig {

    private KpiB3PdfTableConfig() {}

    public static List<PdfTableColumn> columns(
        MessageSource messageSource,
        Locale locale
    ) {
        return List.of(
            new PdfTableColumn(msg(messageSource, locale, "pdf.kpi.b3.table.period"), "evaluationType"),
            new PdfTableColumn(msg(messageSource, locale, "pdf.kpi.b3.table.from"), "evaluationStartDate"),
            new PdfTableColumn(msg(messageSource, locale, "pdf.kpi.b3.table.to"), "evaluationEndDate"),
            new PdfTableColumn(msg(messageSource, locale, "pdf.kpi.b3.table.totalStandIn"), "totalStandIn"),
            new PdfTableColumn(msg(messageSource, locale, "pdf.kpi.b3.table.outcome"), "outcome")
        );
    }

    private static String msg(MessageSource ms, Locale l, String key) {
        return ms.getMessage(key, null, l);
    }
}