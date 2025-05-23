package com.nexigroup.pagopa.cruscotto.service;

import com.nexigroup.pagopa.cruscotto.domain.AnagPartner;
import com.nexigroup.pagopa.cruscotto.service.dto.AnagPartnerDTO;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Service Interface for managing {@link AnagPartner}.
 */
public interface AnagPartnerService {
    /**
     * Get all the partner.
     *
     * @param nameFilter filter by partner name.
     * @param pageable   the pagination information.
     * @return the list of entities.
     */
    Page<AnagPartnerDTO> findAll(String nameFilter, Pageable pageable);

    /**
     * Save all partners.
     *
     * @param partners the entities to save.
     */
    void saveAll(List<AnagPartnerDTO> partners);
}
