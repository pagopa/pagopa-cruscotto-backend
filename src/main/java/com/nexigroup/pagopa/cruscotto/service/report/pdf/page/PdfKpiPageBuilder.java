package com.nexigroup.pagopa.cruscotto.service.report.pdf.page;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.nexigroup.pagopa.cruscotto.service.report.pdf.model.PdfKpiPage;
import com.nexigroup.pagopa.cruscotto.service.report.pdf.model.PdfKpiSummaryItem;
import com.nexigroup.pagopa.cruscotto.service.report.pdf.model.PdfKpiTableDescriptor;

public class PdfKpiPageBuilder {

    /**
     * Costruisce le pagine KPI (positive o negative)
     */
    public List<PdfKpiPage> buildPages(
        List<PdfKpiSummaryItem> kpis,
        Map<String, List<PdfKpiTableDescriptor>> kpiTables
    ) {

        List<PdfKpiPage> pages = new ArrayList<>();

        for (PdfKpiSummaryItem kpi : kpis) {
            List<PdfKpiTableDescriptor> tables = kpiTables.get(kpi.getCode());

            if (tables != null && !tables.isEmpty()) {
                addPagesWithTables(pages, kpi, tables);
            } else {
                pages.add(new PdfKpiPage(kpi, null, true, true));
            }
        }

        return pages;
    }

    private void addPagesWithTables(
        List<PdfKpiPage> pages,
        PdfKpiSummaryItem kpi,
        List<PdfKpiTableDescriptor> tables
    ) {
        for (int i = 0; i < tables.size(); i++) {
            pages.add(
                new PdfKpiPage(
                    kpi,
                    tables.get(i),
                    i == 0, // firstPage
                    i == 0  // pageBreakBefore
                )
            );
        }
    }
}
