package com.nexigroup.pagopa.cruscotto.service;


import com.nexigroup.pagopa.cruscotto.domain.AnagPartner;
import com.nexigroup.pagopa.cruscotto.domain.enumeration.TypePlanned;
import com.nexigroup.pagopa.cruscotto.service.dto.AnagPlannedShutdownDTO;
import com.nexigroup.pagopa.cruscotto.service.filter.AnagPlannedShutdownFilter;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

/**
 * Service Interface for managing {@link AnagPartner}.
 */
public interface AnagPlannedShutdownService {

    /**
     * Count all the plannedShutdowns by type planned and year.
     *
     * @param typePlanned the type planned.
     * @param year the year.
     * @return the count of entities.
     */
    Long count(TypePlanned typePlanned, long year);

    /**
     * Delete all the plannedShutdowns by type planned and year.
     *
     * @param typePlanned the type planned.
     * @param year the year.
     */
    void delete(TypePlanned typePlanned, long year);

    /**
     * Delete one shutdown by id.
     *
     * @param id the year.
     */
    void delete(Long id);


    /**
     * Save all planned shutdown.
     *
     * @param anagPlannedShutdownDTOS the entities to save.
     */
    void saveAll(List<AnagPlannedShutdownDTO> anagPlannedShutdownDTOS);

    /**
     * Get all the shutdowns by filter.
     *
     * @param filter the pagination filter.
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Page<AnagPlannedShutdownDTO> findAll(AnagPlannedShutdownFilter filter, Pageable pageable);

    /**
     * Get one shutdown by its identifier.
     *
     * @param id the shutdown identifier.
     * @return the selected shutdown.
     */
    Optional<AnagPlannedShutdownDTO> findOne(Long id);
}
