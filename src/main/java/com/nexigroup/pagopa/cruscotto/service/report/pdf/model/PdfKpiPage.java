package com.nexigroup.pagopa.cruscotto.service.report.pdf.model;

public class PdfKpiPage {

    private final PdfKpiSummaryItem kpi;
    private final PdfKpiTableDescriptor table;
    private final boolean firstPage;
    private final boolean pageBreakBefore;

    public PdfKpiPage(
            PdfKpiSummaryItem kpi,
            PdfKpiTableDescriptor table,
            boolean firstPage,
            boolean pageBreakBefore
    ) {
        this.kpi = kpi;
        this.table = table;
        this.firstPage = firstPage;
        this.pageBreakBefore = pageBreakBefore;
    }

    public PdfKpiSummaryItem getKpi() { return kpi; }

    public PdfKpiTableDescriptor getTable() { return table; }

    public boolean isFirstPage() { return firstPage; }

    public boolean isPageBreakBefore() { return pageBreakBefore; }
}