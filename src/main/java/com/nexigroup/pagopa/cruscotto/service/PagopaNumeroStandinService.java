package com.nexigroup.pagopa.cruscotto.service;

import com.nexigroup.pagopa.cruscotto.service.dto.PagopaNumeroStandinDTO;
import java.util.List;

/**
 * Service Interface for managing {@link com.nexigroup.pagopa.cruscotto.domain.PagopaNumeroStandin}.
 * 
 * Provides operations for retrieving raw Stand-In data used in KPI B.3 drilldown analysis.
 */
public interface PagopaNumeroStandinService {

    /**
     * Find all PagopaNumeroStandin records related to a specific KPI B.3 analytic data entry.
     * This represents the final drilldown level showing the raw data from PagoPA API.
     * 
     * @param analyticDataId the ID of the KpiB3AnalyticData record
     * @return the list of PagopaNumeroStandinDTO records
     */
    List<PagopaNumeroStandinDTO> findByAnalyticDataId(Long analyticDataId);

    /**
     * Convert a PagopaNumeroStandin entity to DTO.
     * 
     * @param entity the entity to convert
     * @return the converted DTO
     */
    PagopaNumeroStandinDTO convertToDTO(com.nexigroup.pagopa.cruscotto.domain.PagopaNumeroStandin entity);
}