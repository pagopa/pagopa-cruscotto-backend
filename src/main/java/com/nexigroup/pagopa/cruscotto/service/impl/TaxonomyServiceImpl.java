package com.nexigroup.pagopa.cruscotto.service.impl;

import com.nexigroup.pagopa.cruscotto.domain.QTaxonomy;
import com.nexigroup.pagopa.cruscotto.domain.Taxonomy;
import com.nexigroup.pagopa.cruscotto.repository.TaxonomyRepository;
import com.nexigroup.pagopa.cruscotto.service.TaxonomyService;
import com.nexigroup.pagopa.cruscotto.service.dto.TaxonomyDTO;
import com.nexigroup.pagopa.cruscotto.service.filter.TaxonomyFilter;
import com.nexigroup.pagopa.cruscotto.service.mapper.TaxonomyMapper;
import com.nexigroup.pagopa.cruscotto.service.qdsl.QdslUtility;
import com.nexigroup.pagopa.cruscotto.service.qdsl.QueryBuilder;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.QBean;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.JPQLQuery;
import java.util.List;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link Taxonomy}.
 */

@Service
@Transactional
public class TaxonomyServiceImpl implements TaxonomyService {

    private static final String ID_FIELD = "id";
    private static final String INSTITUTION_TYPE_FIELD = "institutionType";
    private static final String AREA_NAME_FIELD = "areaName";
    private static final String SERVICE_TYPE_FIELD = "serviceType";
    private static final String TAKINGS_IDENTIFIER_FIELD = "takingsIdentifier";
    private static final String VALIDITY_START_DATE_FIELD = "validityStartDate";
    private static final String VALIDITY_END_DATE_FIELD = "validityEndDate";
    private static final String CREATED_BY_FIELD = "createdBy";
    private static final String CREATED_DATE_FIELD = "createdDate";
    private static final String LAST_MODIFIED_BY_FIELD = "lastModifiedBy";
    private static final String LAST_MODIFIED_DATE_FIELD = "lastModifiedDate";

    private final Logger log = LoggerFactory.getLogger(TaxonomyServiceImpl.class);

    private final TaxonomyRepository taxonomyRepository;

    private final TaxonomyMapper taxonomyMapper;

    private final QueryBuilder queryBuilder;

    public TaxonomyServiceImpl(TaxonomyRepository taxonomyRepository, TaxonomyMapper taxonomyMapper, QueryBuilder queryBuilder) {
        this.taxonomyRepository = taxonomyRepository;
        this.taxonomyMapper = taxonomyMapper;
        this.queryBuilder = queryBuilder;
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
        return taxonomyRepository.count();
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

    @Override
    public List<String> getAllUpdatedTakingsIdentifiers() {
        return taxonomyRepository.findAllUpdatedTakingsIdentifiers();
    }

    /**
     * Retrieves a paginated result of TaxonomyDTO based on the provided filter and pagination configuration.
     *
     * @param filter   the filter criteria for retrieving taxonomy entities
     * @param pageable the pagination and sorting information
     * @return a page of TaxonomyDTO matching the filter criteria
     */
    @Override
    public Page<TaxonomyDTO> findAll(TaxonomyFilter filter, Pageable pageable) {
        QTaxonomy qTaxonomy = QTaxonomy.taxonomy;
        BooleanBuilder predicate = new BooleanBuilder();

        if (filter.getTakingsIdentifier() != null) {
            predicate.and(qTaxonomy.takingsIdentifier.contains(filter.getTakingsIdentifier()));
        }

        JPQLQuery<Taxonomy> jpql = queryBuilder.<Taxonomy>createQuery().from(qTaxonomy).where(predicate);

        long size = jpql.fetchCount();

        JPQLQuery<TaxonomyDTO> jpqlSelected = jpql.select(buildInstanceProjection(qTaxonomy));

        jpqlSelected.offset(pageable.getOffset());
        jpqlSelected.limit(pageable.getPageSize());

        pageable
            .getSortOr(Sort.by(Sort.Direction.ASC, "id"))
            .forEach(order -> {
                jpqlSelected.orderBy(
                    new OrderSpecifier<>(
                        order.isAscending() ? Order.ASC : Order.DESC,
                        Expressions.stringPath(order.getProperty()),
                        QdslUtility.toQueryDslNullHandling(order.getNullHandling())
                    )
                );
            });

        List<TaxonomyDTO> list = jpqlSelected.fetch();

        return new PageImpl<>(list, pageable, size);
    }

    private QBean<TaxonomyDTO> buildInstanceProjection(QTaxonomy qTaxonomy) {
        return Projections.fields(
            TaxonomyDTO.class,
            qTaxonomy.id.as(ID_FIELD),
            qTaxonomy.institutionType.as(INSTITUTION_TYPE_FIELD),
            qTaxonomy.areaName.as(AREA_NAME_FIELD),
            qTaxonomy.serviceType.as(SERVICE_TYPE_FIELD),
            qTaxonomy.takingsIdentifier.as(TAKINGS_IDENTIFIER_FIELD),
            qTaxonomy.validityStartDate.as(VALIDITY_START_DATE_FIELD),
            qTaxonomy.validityEndDate.as(VALIDITY_END_DATE_FIELD),
            qTaxonomy.createdBy.as(CREATED_BY_FIELD),
            qTaxonomy.createdDate.as(CREATED_DATE_FIELD),
            qTaxonomy.lastModifiedBy.as(LAST_MODIFIED_BY_FIELD),
            qTaxonomy.lastModifiedDate.as(LAST_MODIFIED_DATE_FIELD)
        );
    }

    /**
     * Find a single Taxonomy entity based on its unique identifier.
     *
     * @param id the unique identifier of the Taxonomy to retrieve
     * @return an {@link Optional} containing the {@link TaxonomyDTO} if a matching entity exists, or an empty {@link Optional} if not found
     */
    @Override
    public Optional<TaxonomyDTO> findOne(Long id) {
        return taxonomyRepository.findById(id).map(taxonomyMapper::toDto);
    }
}
