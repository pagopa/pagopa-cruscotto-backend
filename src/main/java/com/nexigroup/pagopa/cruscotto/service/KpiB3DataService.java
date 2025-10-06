package com.nexigroup.pagopa.cruscotto.service;


import com.nexigroup.pagopa.cruscotto.domain.PagopaNumeroStandin;
import com.nexigroup.pagopa.cruscotto.domain.enumeration.OutcomeStatus;
import com.nexigroup.pagopa.cruscotto.service.dto.InstanceDTO;
import com.nexigroup.pagopa.cruscotto.service.dto.InstanceModuleDTO;
import com.nexigroup.pagopa.cruscotto.service.dto.KpiConfigurationDTO;
import java.time.LocalDate;
import java.util.List;

/**
 * Service for handling KPI B.3 data operations.
 */
public interface KpiB3DataService {

    /**
     * Save KPI B.3 results in the three tables (Result, DetailResult, AnalyticData)
     * This method is transactional and will handle the delete and save operations correctly.
     * 
     * @param instanceDTO the instance
     * @param instanceModuleDTO the instance module
     * @param kpiConfigurationDTO the KPI configuration
     * @param analysisDate the analysis date
     * @param outcome the calculation outcome
     * @param standInData the Stand-In aggregated data from database
     */
    void saveKpiB3Results(InstanceDTO instanceDTO, InstanceModuleDTO instanceModuleDTO, 
                         KpiConfigurationDTO kpiConfigurationDTO, LocalDate analysisDate, 
                         OutcomeStatus outcome, List<PagopaNumeroStandin> standInData);
}