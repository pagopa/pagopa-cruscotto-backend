package com.nexigroup.pagopa.cruscotto.service.dto;

import com.nexigroup.pagopa.cruscotto.domain.PagoPaPaymentReceipt;

import java.io.Serializable;
import java.time.Instant;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

/**
 * A DTO for the {@link PagoPaPaymentReceipt} entity.
 */

@Getter
@Setter
@EqualsAndHashCode
public class PagoPaPaymentReceiptDTO implements Serializable {

	private static final long serialVersionUID = 5993091148710454667L;

	private Long id;

    private String cfPartner;

    private String station;

    private Instant startDate;

    private Instant endDate;

    private Long totRes;

    private Long resOk;

    private Long resKo;
    

    @Override
	public String toString() {
		return "PagoPaPaymentReceiptDTO [id=" + id + ", cfPartner=" + cfPartner + ", station=" + station
				+ ", startDate=" + startDate + ", endDate=" + endDate + ", totRes=" + totRes + ", resOk=" + resOk
				+ ", resKo=" + resKo + "]";
	}
}
