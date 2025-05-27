package com.nexigroup.pagopa.cruscotto.service;

import com.nexigroup.pagopa.cruscotto.domain.KpiConfiguration;
import com.nexigroup.pagopa.cruscotto.domain.enumeration.ModuleCode;
import com.nexigroup.pagopa.cruscotto.service.dto.KpiConfigurationDTO;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Service Interface for managing {@link KpiConfiguration}.
 */
public interface KpiConfigurationService {
    /**
     * Finds and retrieves a KPI configuration based on the provided module code.
     *
     * @param code the code of the module for which the KPI configuration is retrieved
     * @return an {@link Optional} containing the {@link KpiConfigurationDTO} if found, or an empty {@link Optional} if no configuration exists for the specified code
     */
    Optional<KpiConfigurationDTO> findKpiConfigurationByCode(ModuleCode code);

    /**
     * Fetches all KPI configurations in a paginated format according to the provided pageable details.
     *
     * @param pageable the pagination and sorting information
     * @return a paginated list of {@link KpiConfigurationDTO} containing the details of KPI configurations
     */
    Page<KpiConfigurationDTO> findAll(Pageable pageable);
}
