package com.nexigroup.pagopa.cruscotto.service.report.pdf.config.a1;

import com.nexigroup.pagopa.cruscotto.service.report.pdf.model.PdfTableColumn;
import org.springframework.context.MessageSource;

import java.util.List;
import java.util.Locale;

public class KpiA1DrilldownPdfTableConfig {

    private KpiA1DrilldownPdfTableConfig() {}

    public static List<PdfTableColumn> columns(
            MessageSource messageSource,
            Locale locale
    ) {
        return List.of(
            new PdfTableColumn(msg(messageSource, locale, "pdf.kpi.a1.drilldown.from"), "fromHour"),
            new PdfTableColumn(msg(messageSource, locale, "pdf.kpi.a1.drilldown.to"), "toHour"),
            new PdfTableColumn(msg(messageSource, locale, "pdf.kpi.a1.drilldown.totalRequests"), "totalRequests"),
            new PdfTableColumn(msg(messageSource, locale, "pdf.kpi.a1.drilldown.okRequests"), "okRequests"),
            new PdfTableColumn(msg(messageSource, locale, "pdf.kpi.a1.drilldown.timeoutRequests"), "reqTimeout")
        );
    }

    private static String msg(MessageSource ms, Locale l, String key) {
        return ms.getMessage(key, null, l);
    }
}
