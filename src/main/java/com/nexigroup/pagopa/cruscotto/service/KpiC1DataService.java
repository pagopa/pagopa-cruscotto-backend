package com.nexigroup.pagopa.cruscotto.service;

import com.nexigroup.pagopa.cruscotto.domain.enumeration.OutcomeStatus;
import com.nexigroup.pagopa.cruscotto.service.dto.InstanceDTO;
import com.nexigroup.pagopa.cruscotto.service.dto.InstanceModuleDTO;
import com.nexigroup.pagopa.cruscotto.service.dto.KpiConfigurationDTO;
import com.nexigroup.pagopa.cruscotto.service.dto.PagoPaIODTO;
import java.time.LocalDate;
import java.util.List;

/**
 * Service for handling KPI C.1 data operations.
 */
public interface KpiC1DataService {

    /**
     * Execute complete KPI C.1 calculation for an instance.
     * This method implements the full business logic for KPI C.1.
     * 
     * @param instanceDTO the instance to analyze
     * @param instanceModuleDTO the instance module
     * @param kpiConfigurationDTO the KPI configuration
     * @param analysisDate the analysis date
     * @return the final outcome status (OK/KO)
     */
    OutcomeStatus executeKpiC1Calculation(
        InstanceDTO instanceDTO, 
        InstanceModuleDTO instanceModuleDTO, 
        KpiConfigurationDTO kpiConfigurationDTO, 
        LocalDate analysisDate
    );

    /**
     * Retrieve IO data for a specific partner using double access logic:
     * 1. First try with CF_Partner
     * 2. If not found, try with individual entities without CF_Partner
     * 
     * @param partnerFiscalCode the partner fiscal code
     * @param entiList the list of entities for this partner
     * @param startDate analysis period start date
     * @param endDate analysis period end date
     * @return list of IO data for the partner
     */
    List<PagoPaIODTO> retrieveIODataForPartner(
        String partnerFiscalCode,
        List<String> entiList, 
        LocalDate startDate, 
        LocalDate endDate
    );

    /**
     * Calculate entity compliance for each entity.
     * An entity is compliant if the percentage of messages vs positions meets the tolerance threshold.
     * 
     * @param ioDataList the IO data for all entities
     * @param toleranceThreshold the tolerance threshold (e.g., 100%)
     * @return map of entity -> compliance status
     */
    java.util.Map<String, Boolean> calculateEntityCompliance(
        List<PagoPaIODTO> ioDataList, 
        double toleranceThreshold
    );

    /**
     * Calculate monthly compliance for the KPI.
     * Groups data by month and evaluates compliance for each month.
     * 
     * @param ioDataList the IO data
     * @param entityThreshold the entity threshold (e.g., 50%)
     * @param toleranceThreshold the message tolerance threshold (e.g., 100%)
     * @return monthly compliance results
     */
    java.util.Map<java.time.YearMonth, Boolean> calculateMonthlyCompliance(
        List<PagoPaIODTO> ioDataList,
        double entityThreshold,
        double toleranceThreshold
    );

    /**
     * Calculate total period compliance for the KPI.
     * Evaluates compliance for the entire analysis period.
     * 
     * @param ioDataList the IO data
     * @param entityThreshold the entity threshold (e.g., 50%)
     * @param toleranceThreshold the message tolerance threshold (e.g., 100%)
     * @return total period compliance result
     */
    boolean calculateTotalCompliance(
        List<PagoPaIODTO> ioDataList,
        double entityThreshold,
        double toleranceThreshold
    );

    /**
     * Save KPI C.1 results using the generic services.
     * This saves data to KpiResult, KpiDetailResult, and KpiAnalyticData tables.
     * 
     * @param instanceDTO the instance
     * @param instanceModuleDTO the instance module
     * @param kpiConfigurationDTO the KPI configuration
     * @param analysisDate the analysis date
     * @param outcome the final outcome
     * @param ioDataList the IO data processed
     * @param monthlyCompliance the monthly compliance results
     * @param totalCompliance the total compliance result
     */
    void saveKpiC1Results(
        InstanceDTO instanceDTO,
        InstanceModuleDTO instanceModuleDTO,
        KpiConfigurationDTO kpiConfigurationDTO,
        LocalDate analysisDate,
        OutcomeStatus outcome,
        List<PagoPaIODTO> ioDataList,
        java.util.Map<java.time.YearMonth, Boolean> monthlyCompliance,
        boolean totalCompliance
    );

    /**
     * Get negative evidences (non-compliant records) for detailed analysis.
     * 
     * @param ioDataList the IO data
     * @param toleranceThreshold the tolerance threshold
     * @return list of non-compliant records
     */
    List<PagoPaIODTO> getNegativeEvidences(List<PagoPaIODTO> ioDataList, double toleranceThreshold);
}
