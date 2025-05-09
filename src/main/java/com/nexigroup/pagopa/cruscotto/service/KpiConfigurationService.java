package com.nexigroup.pagopa.cruscotto.service;

import com.nexigroup.pagopa.cruscotto.domain.KpiConfiguration;
import com.nexigroup.pagopa.cruscotto.domain.enumeration.ModuleCode;
import com.nexigroup.pagopa.cruscotto.service.dto.KpiConfigurationDTO;
import java.util.Optional;

/**
 * Service Interface for managing {@link KpiConfiguration}.
 */
public interface KpiConfigurationService {
    /**
     * Get configuration for a module.
     *
     * @param code the code of module.
     * @return the entity kpiConfiguration.
     */
    Optional<KpiConfigurationDTO> findKpiConfigurationByCode(ModuleCode code);
}
