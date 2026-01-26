package com.nexigroup.pagopa.cruscotto.service.report.pdf.model;

import java.io.Serializable;
import java.util.List;

public class PdfTableModel implements Serializable {

    private List<PdfTableColumn> columns;
    private List<?> rows;

    public PdfTableModel(List<PdfTableColumn> columns, List<?> rows) {
        this.columns = columns;
        this.rows = rows;
    }

    public List<PdfTableColumn> getColumns() {
        return columns;
    }

    public List<?> getRows() {
        return rows;
    }
}