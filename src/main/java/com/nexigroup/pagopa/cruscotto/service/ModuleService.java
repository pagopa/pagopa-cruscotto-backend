package com.nexigroup.pagopa.cruscotto.service;


import com.nexigroup.pagopa.cruscotto.domain.Module;
import com.nexigroup.pagopa.cruscotto.service.dto.ModuleDTO;

import java.util.Optional;

/**
 * Service Interface for managing {@link Module}.
 */
public interface ModuleService {
    
    Optional<ModuleDTO> findOne(Long id);
    
    void delete(Long id);
}
