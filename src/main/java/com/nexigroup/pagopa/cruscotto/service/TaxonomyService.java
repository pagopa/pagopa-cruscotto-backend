package com.nexigroup.pagopa.cruscotto.service;

import com.nexigroup.pagopa.cruscotto.domain.Taxonomy;
import com.nexigroup.pagopa.cruscotto.service.dto.TaxonomyDTO;
import com.nexigroup.pagopa.cruscotto.service.filter.TaxonomyFilter;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

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

    List<String> getAllTakingsIdentifiers();

    /**
     * Finds all {@link TaxonomyDTO} entities matching the given filter and pagination information.
     *
     * @param filter the criteria used to filter the taxonomies
     * @param pageable the pagination information for the result set
     * @return a paginated list of matching {@link TaxonomyDTO} entities
     */
    Page<TaxonomyDTO> findAll(TaxonomyFilter filter, Pageable pageable);

    /**
     * Find a single Taxonomy entity based on its unique identifier.
     *
     * @param id the unique identifier of the Taxonomy to retrieve
     * @return an {@link Optional} containing the {@link TaxonomyDTO} if a matching entity exists, or an empty {@link Optional} if not found
     */
    Optional<TaxonomyDTO> findOne(Long id);
}
