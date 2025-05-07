package com.nexigroup.pagopa.cruscotto.service;


import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.nexigroup.pagopa.cruscotto.domain.Instance;
import com.nexigroup.pagopa.cruscotto.service.bean.InstanceRequestBean;
import com.nexigroup.pagopa.cruscotto.service.dto.InstanceDTO;
import com.nexigroup.pagopa.cruscotto.service.filter.InstanceFilter;

import java.util.List;
import java.util.Optional;

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
    
    Optional<InstanceDTO> findOne(Long id);
    
    InstanceDTO saveNew(InstanceRequestBean instance);
    
    InstanceDTO update(InstanceRequestBean instance);
    
    InstanceDTO delete(Long id);

    List<InstanceDTO> findInstanceToCalculate(Integer limit);
}
