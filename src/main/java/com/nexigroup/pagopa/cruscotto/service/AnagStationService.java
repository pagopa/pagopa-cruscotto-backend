package com.nexigroup.pagopa.cruscotto.service;

import com.nexigroup.pagopa.cruscotto.domain.AnagStation;
import com.nexigroup.pagopa.cruscotto.service.dto.AnagStationDTO;
import com.nexigroup.pagopa.cruscotto.service.filter.StationFilter;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Service Interface for managing {@link AnagStation}.
 */
public interface AnagStationService {
    /**
     * Save all stations.
     *
     * @param stations the stations to save.
     */
    void saveAll(List<AnagStationDTO> stations);

    /**
     * Get all the stations by filter.
     *
     * @param filter the pagination filter.
     * @param pageable the pagination information.
     * @return the list of stations.
     */
    Page<AnagStationDTO> findAll(StationFilter filter, Pageable pageable);

    /**
     * Get id station by name.
     *
     * @param name the name.
     * @return the id.
     */
    long findIdByNameOrCreate(String name, long idPartner);
}
