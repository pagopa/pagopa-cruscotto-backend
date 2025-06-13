package com.nexigroup.pagopa.cruscotto.service;

import com.nexigroup.pagopa.cruscotto.domain.PagoPaPaymentReceipt;
import com.nexigroup.pagopa.cruscotto.service.dto.PagoPaPaymentReceiptDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.List;

/**
 * Service Interface for managing {@link PagoPaPaymentReceipt}.
 */

public interface PagoPaPaymentReceiptService {
    /**
     * Get station and method into a period for a partner.
     *
     * @param fiscalCodePartner the fiscal code of a partner.
     * @param startDate the start date of the period.
     * @param endDate the end date of the period.
     * @return the map contains Station and list method.
     */
    List<String> findAllStationIntoPeriodForPartner(String fiscalCodePartner, LocalDate startDate, LocalDate endDate);

    List<PagoPaPaymentReceiptDTO> findAllRecordIntoDayForPartnerAndStation(String fiscalCodePartner, String station, LocalDate day);

    Page<PagoPaPaymentReceiptDTO> findAll(Pageable pageable);
}
