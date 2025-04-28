package com.nexigroup.pagopa.cruscotto.domain.enumeration;

public enum TaxonomyField {
    TAKINGS_IDENTIFIER("DATI SPECIFICI INCASSO"),
    VALIDITY_START_DATE("DATA INIZIO VALIDITA"),
    VALIDITY_END_DATE("DATA FINE VALIDITA");

    public final String field;

    TaxonomyField(String field) {
        this.field = field;
    }
}
