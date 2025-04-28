package com.nexigroup.pagopa.cruscotto.service;


import com.nexigroup.pagopa.cruscotto.domain.Instance;
import com.nexigroup.pagopa.cruscotto.service.dto.InstanceDTO;
import com.nexigroup.pagopa.cruscotto.service.filter.InstanceFilter;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Service Interface for managing {@link Instance}.
 */
public interface InstanceService {

    /**
     * Get all the instance by filter.
     *
     * @param filter the pagination filter.
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Page<InstanceDTO> findAll(InstanceFilter filter, Pageable pageable);
}
