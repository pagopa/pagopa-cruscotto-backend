package com.nexigroup.pagopa.cruscotto.service.report.pdf.model;

import java.util.List;

public class PdfKpiSummaryItem {
    private final String code;        // es: "A.1"
    private final String title;        // es: "Rispetto SLA disponibilità servizio di pagamento"
    private final String description; // testo localizzato
    private final boolean compliant;  // true = conforme

    public PdfKpiSummaryItem(
            String code,
            String title,
            String description,
            boolean compliant
    ) {
        this.code = code;
        this.title = title;
        this.description = description;
        this.compliant = compliant;
    }

    public String getCode() { return code; }
    public String getTitle() { return title; }
    public String getDescription() { return description; }
    public boolean isCompliant() { return compliant; }
}