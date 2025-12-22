package com.nexigroup.pagopa.cruscotto.service.report.pdf.factory;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;

import com.nexigroup.pagopa.cruscotto.domain.enumeration.EvaluationType;
import com.nexigroup.pagopa.cruscotto.service.report.pdf.model.PdfKpiTableDescriptor;
import com.nexigroup.pagopa.cruscotto.domain.KpiA1AnalyticDrillDown;
import com.nexigroup.pagopa.cruscotto.domain.KpiA1DetailResult;
import com.nexigroup.pagopa.cruscotto.domain.KpiA2DetailResult;
import com.nexigroup.pagopa.cruscotto.domain.KpiB3DetailResult;
// import com.nexigroup.pagopa.cruscotto.domain.KpiA2DetailResult;
import com.nexigroup.pagopa.cruscotto.service.report.pdf.config.a1.KpiA1DrilldownPdfTableConfig;
import com.nexigroup.pagopa.cruscotto.service.report.pdf.config.a1.KpiA1PdfTableConfig;
// import com.nexigroup.pagopa.cruscotto.service.report.pdf.config.a2.KpiA2PdfTableConfig;
import com.nexigroup.pagopa.cruscotto.service.report.pdf.config.a2.KpiA2PdfTableConfig;
import com.nexigroup.pagopa.cruscotto.service.report.pdf.config.b3.KpiB3PdfTableConfig;

@Service
public class PdfKpiTableFactory {

    private final MessageSource messageSource;

    public PdfKpiTableFactory(MessageSource messageSource) {
        this.messageSource = messageSource;
    }

    private static final int MAX_ROWS_PER_PAGE = 25;

    public List<PdfKpiTableDescriptor> splitTable(PdfKpiTableDescriptor table) {
        List<Map<String, Object>> rows = table.getRows();
        List<PdfKpiTableDescriptor> pages = new java.util.ArrayList<>();

        for (int i = 0; i < rows.size(); i += MAX_ROWS_PER_PAGE) {
            int end = Math.min(i + MAX_ROWS_PER_PAGE, rows.size());

            PdfKpiTableDescriptor page = new PdfKpiTableDescriptor(
                    table.getKpiCode(),
                    table.getColumns(),
                    rows.subList(i, end)
            );

            pages.add(page);
        }

        return pages;
    }

    private static final DecimalFormat INT_FMT = new DecimalFormat(
            "#,###",
            DecimalFormatSymbols.getInstance(Locale.ITALY));

    private static final DecimalFormat PERC_FMT = new DecimalFormat(
            "#,##0.####",
            DecimalFormatSymbols.getInstance(Locale.ITALY));

    /* A.1 */
    public PdfKpiTableDescriptor buildA1(List<KpiA1DetailResult> rows, Locale locale) {
        return new PdfKpiTableDescriptor(
            "A.1",
            KpiA1PdfTableConfig.columns(messageSource, locale),
            mapA1Rows(rows));
    }

    // public PdfKpiTableDescriptor buildA1Drilldown(
    //         List<KpiA1AnalyticDrillDown> rows,
    //         Locale locale) {
    //     return new PdfKpiTableDescriptor(
    //             "A.1_DRILLDOWN",
    //             KpiA1DrilldownPdfTableConfig.columns(messageSource, locale),
    //             mapA1DrilldownRows(rows));
    // }
    
    /* A.2 */
    public PdfKpiTableDescriptor buildA2(List<KpiA2DetailResult> rows, Locale locale) {
        return new PdfKpiTableDescriptor(
            "A.2",
            KpiA2PdfTableConfig.columns(messageSource, locale),
            mapA2Rows(rows)
        );
    }

    /* B.3 */
    public PdfKpiTableDescriptor buildB3(List<KpiB3DetailResult> rows, Locale locale) {
        return new PdfKpiTableDescriptor(
            "B.3",
            KpiB3PdfTableConfig.columns(messageSource, locale),
            mapB3Rows(rows)
        );
    }

    /*
     * =========================
     * MAPPING A.1 ROWS
     * =========================
     */

    private static final Comparator<KpiA1DetailResult> A1_TABLE_ORDER = Comparator
            .<KpiA1DetailResult, Boolean>comparing(
                    e -> e.getEvaluationType() == EvaluationType.TOTALE)
            .thenComparing(
                    KpiA1DetailResult::getEvaluationStartDate,
                    Comparator.nullsLast(Comparator.naturalOrder()));

    private List<Map<String, Object>> mapA1Rows(List<KpiA1DetailResult> entities) {

        DateTimeFormatter df = DateTimeFormatter.ofPattern("dd/MM/yyyy");

        return entities.stream().sorted(A1_TABLE_ORDER).map(e -> {
            Map<String, Object> row = new HashMap<>();
            row.put("evaluationType", e.getEvaluationType() != null ? e.getEvaluationType().name() : "");
            row.put("evaluationStartDate",
                    e.getEvaluationStartDate() != null ? e.getEvaluationStartDate().format(df) : "");
            row.put("evaluationEndDate", e.getEvaluationEndDate() != null ? e.getEvaluationEndDate().format(df) : "");
            row.put("totReq", e.getTotReq() != null ? INT_FMT.format(e.getTotReq()) : "0");
            row.put("reqTimeout", e.getReqTimeout() != null ? INT_FMT.format(e.getReqTimeout()) : "0");
            row.put("timeoutPercentage",
                    e.getTimeoutPercentage() != null ? PERC_FMT.format(e.getTimeoutPercentage()) + "%" : "");
            row.put("outcome", e.getOutcome() != null ? e.getOutcome().name() : "");
            return row;
        }).toList();
    }

    // private List<Map<String, Object>> mapA1DrilldownRows(List<KpiA1AnalyticDrillDown> entities) {

    //     DateTimeFormatter tf = DateTimeFormatter.ofPattern("HH:mm").withZone(ZoneId.systemDefault());

    //     return entities.stream().map(e -> {
    //         Map<String, Object> row = new HashMap<>();
    //         row.put("fromHour", tf.format(e.getFromHour()));
    //         row.put("toHour", tf.format(e.getToHour()));
    //         row.put("totalRequests", INT_FMT.format(e.getTotalRequests()));
    //         row.put("okRequests", INT_FMT.format(e.getOkRequests()));
    //         row.put("reqTimeout", INT_FMT.format(e.getReqTimeout()));
    //         return row;
    //     })
    //     .toList();
    // }

    /*
     * =========================
     * MAPPING A.2 ROWS
     * =========================
     */

    private static final Comparator<KpiA2DetailResult> A2_TABLE_ORDER = Comparator
            // .<KpiA2DetailResult, Boolean>comparing(
            //         e -> e.getEvaluationType() == EvaluationType.TOTALE)
            .comparing(
                    KpiA2DetailResult::getEvaluationStartDate,
                    Comparator.nullsLast(Comparator.naturalOrder()));

    private List<Map<String, Object>> mapA2Rows(List<KpiA2DetailResult> entities) {

        DateTimeFormatter df = DateTimeFormatter.ofPattern("dd/MM/yyyy");

        return entities.stream().sorted(A2_TABLE_ORDER).map(e -> {
            Map<String, Object> row = new HashMap<>();
            row.put("evaluationType", "TOTALE");
            row.put("evaluationStartDate",
                    e.getEvaluationStartDate() != null ? e.getEvaluationStartDate().format(df) : "");
            row.put("evaluationEndDate", e.getEvaluationEndDate() != null ? e.getEvaluationEndDate().format(df) : "");
            row.put("totPayments", e.getTotPayments() != null ? INT_FMT.format(e.getTotPayments()) : "0");
            row.put("totIncorrectPayments", e.getTotIncorrectPayments() != null ? INT_FMT.format(e.getTotIncorrectPayments()) : "0");
            row.put("errorPercentage",
                    e.getErrorPercentage() != null ? PERC_FMT.format(e.getErrorPercentage()) + "%" : "");
            row.put("outcome", e.getOutcome() != null ? e.getOutcome().name() : "");
            return row;
        }).toList();
    }

    /*
     * =========================
     * MAPPING B.3 ROWS
     * =========================
     */

    private static final Comparator<KpiB3DetailResult> B3_TABLE_ORDER = Comparator
            .<KpiB3DetailResult, Boolean>comparing(
                    e -> e.getEvaluationType() == EvaluationType.TOTALE)
            .thenComparing(
                    KpiB3DetailResult::getEvaluationStartDate,
                    Comparator.nullsLast(Comparator.naturalOrder()));

    private List<Map<String, Object>> mapB3Rows(List<KpiB3DetailResult> entities) {

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
}