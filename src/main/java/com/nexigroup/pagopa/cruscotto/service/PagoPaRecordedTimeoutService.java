package com.nexigroup.pagopa.cruscotto.service;

import com.nexigroup.pagopa.cruscotto.domain.PagoPaRecordedTimeout;
import com.nexigroup.pagopa.cruscotto.service.dto.PagoPaRecordedTimeoutDTO;
import com.nexigroup.pagopa.cruscotto.service.filter.PagoPaRecordedTimeoutFilter;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

/**
 * Service Interface for managing {@link PagoPaRecordedTimeout}.
 */
public interface PagoPaRecordedTimeoutService {
    /**
     * Get station and method into a period for a partner.
     *
     * @param fiscalCodePartner the fiscal code of a partner.
     * @param startDate the start date of the period.
     * @param endDate the end date of the period.
     * @return the map contains Station and list method.
     */
    Map<String, List<String>> findAllStationAndMethodIntoPeriodForPartner(String fiscalCodePartner, LocalDate startDate, LocalDate endDate);

    Long sumRecordIntoPeriodForPartnerStationAndMethod(
        String fiscalCodePartner,
        String station,
        String method,
        LocalDate startDate,
        LocalDate endDate
    );

    Long sumRecordIntoPeriodForPartner(
        String fiscalCodePartner,
        LocalDate startDate,
        LocalDate endDate
    );

    List<PagoPaRecordedTimeoutDTO> findAllRecordIntoDayForPartnerStationAndMethod(
        String fiscalCodePartner,
        String station,
        String method,
        LocalDate day
    );
    
    public List<PagoPaRecordedTimeoutDTO> findAllRecordIntoPeriodForPartner(
        String fiscalCodePartner,
        LocalDate startDay,
        LocalDate endDay
    );
    
    List<PagoPaRecordedTimeoutDTO> findAllRecordIntoDayForPartner(String fiscalCodePartner, java.time.LocalDate day);

    Page<PagoPaRecordedTimeoutDTO> findAll(PagoPaRecordedTimeoutFilter filter, Pageable pageable);

}
