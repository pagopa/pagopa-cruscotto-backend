package com.nexigroup.pagopa.cruscotto.service;


import com.nexigroup.pagopa.cruscotto.domain.Taxonomy;
import com.nexigroup.pagopa.cruscotto.service.dto.TaxonomyDTO;

import java.util.List;

/**
 * Service Interface for managing {@link Taxonomy}.
 */
public interface TaxonomyService {

    /**
     * Save a taxonomy.
     *
     * @param taxonomyDTO the entity to save.
     * @return the persisted entity.
     */
    TaxonomyDTO save(TaxonomyDTO taxonomyDTO);

    /**
     * Save all taxonomies.
     *
     * @param taxonomies the entities to save.
     */
    void saveAll(List<TaxonomyDTO> taxonomies);

    /**
     * Count all Taxonomy.
     *
     */
    Long countAll();

    /**
     * Delete the all Taxonomy.
     *
     */
    void deleteAll();
}
