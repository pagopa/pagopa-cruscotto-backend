package com.nexigroup.pagopa.cruscotto.service.filter;

import lombok.Getter;
import lombok.Setter;

import java.io.Serial;
import java.io.Serializable;

@Getter
@Setter
public class PagoPaTaxonomyAggregatePositionFilter implements Serializable {

    @Serial
    private static final long serialVersionUID = -4792519674728619332L;

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
