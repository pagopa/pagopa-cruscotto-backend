package com.nexigroup.pagopa.cruscotto.service.report.pdf.model;

import java.io.Serializable;

public class PdfTableColumn implements Serializable {

    private String label;   // Header visibile
    private String field;   // Campo DTO (reflection-safe)

    public PdfTableColumn(String label, String field) {
        this.label = label;
        this.field = field;
    }

    public String getLabel() {
        return label;
    }

    public String getField() {
        return field;
    }
}
