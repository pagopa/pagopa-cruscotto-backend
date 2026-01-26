package com.nexigroup.pagopa.cruscotto.service.report.pdf.model;

import java.util.List;
import java.util.Map;

public class PdfKpiTableDescriptor {

    private final String kpiCode;
    private final List<PdfTableColumn> columns;
    private final List<Map<String, Object>> rows;

    public PdfKpiTableDescriptor(
        String kpiCode,
        List<PdfTableColumn> columns,
        List<Map<String, Object>> rows
    ) {
        this.kpiCode = kpiCode;
        this.columns = columns;
        this.rows = rows;
    }

    public String getKpiCode() {
        return kpiCode;
    }

    public List<PdfTableColumn> getColumns() {
        return columns;
    }

    public List<Map<String, Object>> getRows() {
        return rows;
    }
}