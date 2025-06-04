package com.nexigroup.pagopa.cruscotto.service;

import com.nexigroup.pagopa.cruscotto.domain.Module;
import com.nexigroup.pagopa.cruscotto.service.bean.ModuleRequestBean;
import com.nexigroup.pagopa.cruscotto.service.dto.ModuleDTO;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Service Interface for managing {@link Module}.
 */
public interface ModuleService {

    /**
     * Get all the modules
     *
     * @param pageable the pagination information.
     * @return the list of modulea.
     */
    Page<ModuleDTO> findAll(Pageable pageable);

    Optional<ModuleDTO> findOne(Long id);

    void delete(Long id);

    Page<ModuleDTO> findAllWithoutConfiguration(Pageable pageable);

    ModuleDTO saveNew(ModuleRequestBean moduleToCreate);

    ModuleDTO update(ModuleRequestBean moduleToUpdate);
}
