package com.nexigroup.pagopa.cruscotto.service;

import com.nexigroup.pagopa.cruscotto.service.dto.PagopaTransactionDTO;
import java.time.LocalDate;
import java.util.List;

/**
 * Service Interface for managing PagoPA transaction data from PAGOPA_TRANSAZIONI table.
 */
public interface PagopaTransazioniService {

    /**
     * Find all transaction records in the specified period for a given partner.
     *
     * @param partnerFiscalCode the partner fiscal code
     * @param startDate the start date of the period
     * @param endDate the end date of the period
     * @return list of PagopaTransazioniDTO
     */
    List<PagopaTransactionDTO> findAllRecordIntoPeriodForPartner(
            String partnerFiscalCode, 
            LocalDate startDate, 
            LocalDate endDate);

    /**
     * Find all transaction records for a specific entity in the specified period.
     *
     * @param entityCode the entity code
     * @param startDate the start date of the period
     * @param endDate the end date of the period
     * @return list of PagopaTransazioniDTO
     */
    List<PagopaTransactionDTO> findAllRecordIntoPeriodForEntity(
            String entityCode, 
            LocalDate startDate, 
            LocalDate endDate);

    /**
     * Count unique entities for a partner in the specified period.
     *
     * @param partnerFiscalCode the partner fiscal code
     * @param startDate the start date of the period
     * @param endDate the end date of the period
     * @return count of unique entities
     */
    long countUniqueEntitiesForPartnerInPeriod(
            String partnerFiscalCode, 
            LocalDate startDate, 
            LocalDate endDate);

    /**
     * Sum total transactions for a partner in the specified period.
     *
     * @param partnerFiscalCode the partner fiscal code
     * @param startDate the start date of the period
     * @param endDate the end date of the period
     * @return total number of transactions
     */
    long sumTotalTransactionsForPartnerInPeriod(
            String partnerFiscalCode, 
            LocalDate startDate, 
            LocalDate endDate);
}