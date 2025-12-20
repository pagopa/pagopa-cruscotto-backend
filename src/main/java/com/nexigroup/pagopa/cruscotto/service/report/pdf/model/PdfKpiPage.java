package com.nexigroup.pagopa.cruscotto.service.report.pdf.model;

public class PdfKpiPage {

    private final PdfKpiSummaryItem kpi;
    private final PdfKpiTableDescriptor table;
    private final boolean firstPage;

    public PdfKpiPage(
            PdfKpiSummaryItem kpi,
            PdfKpiTableDescriptor table,
            boolean firstPage
    ) {
        this.kpi = kpi;
        this.table = table;
        this.firstPage = firstPage;
    }

    public PdfKpiSummaryItem getKpi() {
        return kpi;
    }

    public PdfKpiTableDescriptor getTable() {
        return table;
    }

    public boolean isFirstPage() {
        return firstPage;
    }
}