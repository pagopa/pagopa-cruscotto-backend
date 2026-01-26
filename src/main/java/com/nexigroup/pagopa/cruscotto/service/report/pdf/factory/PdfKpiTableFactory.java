package com.nexigroup.pagopa.cruscotto.service.report.pdf.factory;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import com.nexigroup.pagopa.cruscotto.domain.*;
import com.nexigroup.pagopa.cruscotto.service.report.pdf.config.*;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;

import com.nexigroup.pagopa.cruscotto.domain.enumeration.EvaluationType;
import com.nexigroup.pagopa.cruscotto.service.report.pdf.model.PdfKpiTableDescriptor;

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
    /* A.1 */
    public PdfKpiTableDescriptor buildA1(List<KpiA1DetailResult> rows, Locale locale) {
        return new PdfKpiTableDescriptor(
            "A.1",
            KpiA1PdfTableConfig.columns(messageSource, locale),
            KpiA1PdfTableConfig.mapA1Rows(rows));
    }

    /* A.2 */
    public PdfKpiTableDescriptor buildA2(List<KpiA2DetailResult> rows, Locale locale) {
        return new PdfKpiTableDescriptor(
            "A.2",
            KpiA2PdfTableConfig.columns(messageSource, locale),
            KpiA2PdfTableConfig.mapA2Rows(rows)
        );
    }

    // ========================= B1 =========================
    public PdfKpiTableDescriptor buildB1(List<KpiB1DetailResult> rows, Locale locale) {
        return new PdfKpiTableDescriptor(
            "B.1",
            KpiB1PdfTableConfig.columns(messageSource, locale),
            KpiB1PdfTableConfig.mapB1Rows(rows)
        );
    }

    // ========================= B2 =========================
    public PdfKpiTableDescriptor buildB2(List<KpiB2DetailResult> rows, Locale locale) {
        return new PdfKpiTableDescriptor(
            "B.2",
            KpiB2PdfTableConfig.columns(messageSource, locale),
            KpiB2PdfTableConfig.mapB2Rows(rows)
        );
    }

    // ========================= B3 =========================
    public PdfKpiTableDescriptor buildB3(List<KpiB3DetailResult> rows, Locale locale) {
        return new PdfKpiTableDescriptor(
            "B.3",
            KpiB3PdfTableConfig.columns(messageSource, locale),
            KpiB3PdfTableConfig.mapB3Rows(rows)
        );
    }

    // ========================= B4 =========================
    public PdfKpiTableDescriptor buildB4(List<KpiB4DetailResult> rows, Locale locale) {
        return new PdfKpiTableDescriptor(
            "B.4",
            KpiB4PdfTableConfig.columns(messageSource, locale),
            KpiB4PdfTableConfig.mapB4Rows(rows)
        );
    }

    // ========================= B5 =========================
    public PdfKpiTableDescriptor buildB5(List<KpiB5DetailResult> rows, Locale locale) {
        return new PdfKpiTableDescriptor(
            "B.5",
            KpiB5PdfTableConfig.columns(messageSource, locale),
            KpiB5PdfTableConfig.mapB5Rows(rows)
        );
    }

    // ========================= B8 =========================
    public PdfKpiTableDescriptor buildB8(List<KpiB8DetailResult> rows, Locale locale) {
        return new PdfKpiTableDescriptor(
            "B.8",
            KpiB8PdfTableConfig.columns(messageSource, locale),
            KpiB8PdfTableConfig.mapB8Rows(rows)
        );
    }

    // ========================= B9 =========================
    public PdfKpiTableDescriptor buildB9(List<KpiB9DetailResult> rows, Locale locale) {
        return new PdfKpiTableDescriptor(
            "B.9",
            KpiB9PdfTableConfig.columns(messageSource, locale),
            KpiB9PdfTableConfig.mapB9Rows(rows)
        );
    }

    // ========================= C1 =========================
    public PdfKpiTableDescriptor buildC1(List<KpiC1DetailResult> rows, Locale locale) {
        return new PdfKpiTableDescriptor(
            "C.1",
            KpiC1PdfTableConfig.columns(messageSource, locale),
            KpiC1PdfTableConfig.mapC1Rows(rows)
        );
    }

    // ========================= C2 =========================
    public PdfKpiTableDescriptor buildC2(List<KpiC2DetailResult> rows, Locale locale) {
        return new PdfKpiTableDescriptor(
            "C.2",
            KpiC2PdfTableConfig.columns(messageSource, locale),
            KpiC2PdfTableConfig.mapC2Rows(rows)
        );
    }








}
