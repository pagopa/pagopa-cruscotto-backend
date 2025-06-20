package com.nexigroup.pagopa.cruscotto.service.filter;

import lombok.Getter;
import lombok.Setter;

import java.io.Serial;
import java.io.Serializable;

@Getter
@Setter
public class PagoPaRecordedTimeoutFilter implements Serializable {

    @Serial
    private static final long serialVersionUID = 8152821250695511570L;

    private String cfPartner;

    private String station;

    @Override
    public String toString() {
        return "PagoPaPaymentReceiptFilter{" +
            "cfPartner='" + cfPartner + '\'' +
            ", station='" + station + '\'' +
            '}';
    }
}
