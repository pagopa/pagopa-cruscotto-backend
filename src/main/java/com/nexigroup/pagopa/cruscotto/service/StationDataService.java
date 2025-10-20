package com.nexigroup.pagopa.cruscotto.service;

import com.nexigroup.pagopa.cruscotto.service.dto.StationDataDTO;

import java.time.LocalDate;
import java.util.List;

/**
 * Service interface for retrieving station data for KPI B.6 analysis
 */
public interface StationDataService {
    
    /**
     * Find all active stations for a partner in the given period
     * 
     * @param partnerFiscalCode Partner fiscal code
     * @param startDate Analysis start date
     * @param endDate Analysis end date
     * @return List of station data
     */
    List<StationDataDTO> findActiveStationsForPartner(String partnerFiscalCode, LocalDate startDate, LocalDate endDate);
    
    /**
     * Find all stations (active and inactive) for a partner
     * 
     * @param partnerFiscalCode Partner fiscal code
     * @return List of all station data
     */
    List<StationDataDTO> findAllStationsForPartner(String partnerFiscalCode);
}