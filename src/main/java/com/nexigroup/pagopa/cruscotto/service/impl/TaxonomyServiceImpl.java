package com.nexigroup.pagopa.cruscotto.service.impl;

import com.nexigroup.pagopa.cruscotto.domain.*;
import com.nexigroup.pagopa.cruscotto.repository.TaxonomyRepository;
import com.nexigroup.pagopa.cruscotto.service.TaxonomyService;
import com.nexigroup.pagopa.cruscotto.service.dto.TaxonomyDTO;
import com.nexigroup.pagopa.cruscotto.service.mapper.TaxonomyMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


/**
 * Service Implementation for managing {@link Taxonomy}.
 */
@Service
@Transactional
public class TaxonomyServiceImpl implements TaxonomyService {

    private final Logger log = LoggerFactory.getLogger(TaxonomyServiceImpl.class);

    private final TaxonomyRepository taxonomyRepository;

    private final TaxonomyMapper taxonomyMapper;

    public TaxonomyServiceImpl(TaxonomyRepository taxonomyRepository, TaxonomyMapper taxonomyMapper) {
        this.taxonomyRepository = taxonomyRepository;
        this.taxonomyMapper = taxonomyMapper;
    }


    /**
     * Save a taxonomy.
     *
     * @param taxonomyDTO the entity to save.
     * @return the persisted entity.
     */
    @Override
    public TaxonomyDTO save(TaxonomyDTO taxonomyDTO) {
        log.debug("Request to save Taxonomy : {}", taxonomyDTO);

        Taxonomy taxonomy = new Taxonomy();
        taxonomy.setTakingsIdentifier(taxonomyDTO.getTakingsIdentifier());
        taxonomy.setValidityStartDate(taxonomyDTO.getValidityStartDate());
        taxonomy.setValidityEndDate(taxonomyDTO.getValidityEndDate());

        taxonomy = taxonomyRepository.save(taxonomy);

        return taxonomyMapper.toDto(taxonomy);
    }

    /**
     * Save all taxonomies.
     *
     * @param taxonomies the entities to save.
     */
    @Override
    public void saveAll(List<TaxonomyDTO> taxonomies) {
       taxonomyRepository.saveAll(taxonomyMapper.toEntity(taxonomies));
    }

    /**
     * Count all Taxonomy.
     *
     */
    @Override
    @Transactional(readOnly = true)
    public Long countAll() {
        return taxonomyRepository.count() ;
    }

    /**
     * Delete the all Taxonomy.
     *
     */
    @Override
    public void deleteAll() {
        log.debug("Request to delete all Taxonomy");
        taxonomyRepository.delete();
    }

}
