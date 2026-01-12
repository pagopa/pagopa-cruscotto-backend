package com.nexigroup.pagopa.cruscotto.service.report.pdf.config;

import com.nexigroup.pagopa.cruscotto.domain.KpiB5DetailResult;
import com.nexigroup.pagopa.cruscotto.service.report.pdf.model.PdfTableColumn;

import java.time.format.DateTimeFormatter;
import java.util.*;

import org.springframework.context.MessageSource;

public class KpiB5PdfTableConfig extends CommonConfig {

    private KpiB5PdfTableConfig() {}

    public static List<PdfTableColumn> columns(
        MessageSource messageSource,
        Locale locale
    ) {
        return List.of(
            new PdfTableColumn(msg(messageSource, locale, "pdf.kpi.table.analysisDate"), "analysisDate"),
            new PdfTableColumn(msg(messageSource, locale, "pdf.kpi.table.stationsPresent"), "stationsPresent"),
            new PdfTableColumn(msg(messageSource, locale, "pdf.kpi.table.stationsWithoutSpontaneous"), "stationsWithoutSpontaneous"),
            new PdfTableColumn(msg(messageSource, locale, "pdf.kpi.table.percentageNoSpontaneous"), "percentageNoSpontaneous"),
            new PdfTableColumn(msg(messageSource, locale, "pdf.kpi.table.outcome"), "outcome")
        );
    }

    public static List<Map<String, Object>> mapB5Rows(List<KpiB5DetailResult> entities) {

        DateTimeFormatter df = DateTimeFormatter.ofPattern("dd/MM/yyyy");

        return entities.stream()
            .sorted(Comparator.comparing(KpiB5DetailResult::getAnalysisDate))
            .map(e -> {
                Map<String, Object> row = new HashMap<>();

                row.put("analysisDate",
                    e.getAnalysisDate() != null ? e.getAnalysisDate().format(df) : "");

                row.put("stationsPresent",
                    e.getStationsPresent() != null ? INT_FMT.format(e.getStationsPresent()) : "0");

                row.put("stationsWithoutSpontaneous",
                    e.getStationsWithoutSpontaneous() != null
                        ? INT_FMT.format(e.getStationsWithoutSpontaneous())
                        : "0");

                row.put("percentageNoSpontaneous",
                    e.getPercentageNoSpontaneous() != null
                        ? PERC_FMT.format(e.getPercentageNoSpontaneous())
                        : "0");

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
