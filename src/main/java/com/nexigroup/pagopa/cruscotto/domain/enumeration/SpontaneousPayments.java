package com.nexigroup.pagopa.cruscotto.domain.enumeration;

/**
 * The SpontaneousPayments enumeration.
 */
public enum SpontaneousPayments {
    ATTIVI("ATTIVI"),
    NON_ATTIVI("NON ATTIVI");

    private final String value;

    SpontaneousPayments(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    @Override
    public String toString() {
        return value;
    }

    public static SpontaneousPayments fromBoolean(Boolean spontaneous) {
        if (spontaneous == null) {
            return NON_ATTIVI;
        }
        return spontaneous ? ATTIVI : NON_ATTIVI;
    }

    public Boolean toBoolean() {
        return this == ATTIVI;
    }
}