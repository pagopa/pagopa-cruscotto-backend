package com.nexigroup.pagopa.cruscotto.service;

import com.nexigroup.pagopa.cruscotto.domain.AnagPartner;
import com.nexigroup.pagopa.cruscotto.service.dto.AnagPartnerDTO;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Service Interface for managing {@link AnagPartner}.
 */
public interface AnagPartnerService {
    /**
     * Retrieves a paginated list of AnagPartnerDTO objects based on the provided criteria.
     *
     * @param fiscalCode the fiscal code to filter the partners, can be null or empty to retrieve all.
     * @param nameFilter a string to filter partners by their name can be null or empty to retrieve all.
     * @param pageable an object containing pagination and sorting information.
     * @return a paginated list of AnagPartnerDTO objects matching the given criteria.
     */
    Page<AnagPartnerDTO> findAll(String fiscalCode, String nameFilter, Pageable pageable);

    /**
     * Save all partners.
     *
     * @param partners the entities to save.
     */
    void saveAll(List<AnagPartnerDTO> partners);

    /**
     * Find a single AnagPartner entity based on its unique identifier.
     *
     * @param id the unique identifier of the AnagPartner to retrieve
     * @return an {@link Optional} containing the {@link AnagPartnerDTO} if a matching entity exists, or an empty {@link Optional} if not found
     */
    Optional<AnagPartnerDTO> findOne(Long id);
}
