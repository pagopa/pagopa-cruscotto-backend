package com.nexigroup.pagopa.cruscotto.service.report.pdf.page;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import com.nexigroup.pagopa.cruscotto.service.report.pdf.factory.PdfKpiTableFactory;
import com.nexigroup.pagopa.cruscotto.service.report.pdf.model.PdfKpiPage;
import com.nexigroup.pagopa.cruscotto.service.report.pdf.model.PdfKpiSummaryItem;
import com.nexigroup.pagopa.cruscotto.service.report.pdf.model.PdfKpiTableDescriptor;

@Service
public class PdfKpiPageBuilder {

    private final PdfKpiTableFactory tableFactory;

    public PdfKpiPageBuilder(PdfKpiTableFactory tableFactory) {
        this.tableFactory = tableFactory;
    }

    public List<PdfKpiPage> buildPages(
            PdfKpiSummaryItem kpi,
            PdfKpiTableDescriptor rawTable
    ) {

        List<PdfKpiPage> pages = new ArrayList<>();

        // KPI senza tabella → una sola pagina
        if (rawTable == null || rawTable.getRows().isEmpty()) {
            pages.add(
                new PdfKpiPage(
                    kpi,
                    null,
                    true,
                    true   // stacca dal contenuto precedente
                )
            );
            return pages;
        }

        List<PdfKpiTableDescriptor> chunks = tableFactory.splitTable(rawTable);

        for (int i = 0; i < chunks.size(); i++) {
            pages.add(
                new PdfKpiPage(
                    kpi,
                    chunks.get(i),
                    i == 0,      // header solo sulla prima
                    i == 0       // page-break SOLO sulla prima
                )
            );
        }

        return pages;
    }
}