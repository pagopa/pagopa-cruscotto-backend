package com.nexigroup.pagopa.cruscotto.service;

import com.nexigroup.pagopa.cruscotto.domain.AnagStation;
import com.nexigroup.pagopa.cruscotto.service.dto.AnagStationDTO;
import java.util.List;

/**
 * Service Interface for managing {@link AnagStation}.
 */
public interface AnagStationService {
    /**
     * Save all stations.
     *
     * @param stations the entities to save.
     */
    void saveAll(List<AnagStationDTO> stations);

    /**
     * Get id station by name.
     *
     * @param name the name.
     * @return the id.
     */
    long findIdByNameOrCreate(String name, long idPartner);
}
