package com.nexigroup.pagopa.cruscotto.service;

import com.nexigroup.pagopa.cruscotto.domain.AnagPartner;
import com.nexigroup.pagopa.cruscotto.domain.enumeration.TypePlanned;
import com.nexigroup.pagopa.cruscotto.service.dto.AnagPlannedShutdownDTO;
import java.time.LocalDate;
import java.util.List;

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
     * Save all planned shutdown.
     *
     * @param anagPlannedShutdownDTOS the entities to save.
     */
    void saveAll(List<AnagPlannedShutdownDTO> anagPlannedShutdownDTOS);

    List<AnagPlannedShutdownDTO> findAllByTypePlannedIntoPeriod(
        Long partnerId,
        Long stationId,
        TypePlanned typePlanned,
        LocalDate startDate,
        LocalDate endDate
    );
}
