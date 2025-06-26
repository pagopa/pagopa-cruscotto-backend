package com.nexigroup.pagopa.cruscotto.service.impl;

import com.nexigroup.pagopa.cruscotto.domain.AnagPartner;
import com.nexigroup.pagopa.cruscotto.domain.QAnagPartner;
import com.nexigroup.pagopa.cruscotto.domain.enumeration.PartnerStatus;
import com.nexigroup.pagopa.cruscotto.repository.AnagPartnerRepository;
import com.nexigroup.pagopa.cruscotto.service.AnagPartnerService;
import com.nexigroup.pagopa.cruscotto.service.dto.AnagPartnerDTO;
import com.nexigroup.pagopa.cruscotto.service.mapper.AnagPartnerMapper;
import com.nexigroup.pagopa.cruscotto.service.qdsl.QdslUtility;
import com.nexigroup.pagopa.cruscotto.service.qdsl.QueryBuilder;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.JPQLQuery;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link AnagPartner}.
 */
@Service
@Transactional
public class AnagPartnerServiceImpl implements AnagPartnerService {

    private final Logger log = LoggerFactory.getLogger(AnagPartnerServiceImpl.class);

    private final QueryBuilder queryBuilder;

    private final AnagPartnerRepository anagPartnerRepository;

    private final AnagPartnerMapper anagPartnerMapper;

    @Value("${spring.jpa.properties.hibernate.jdbc.batch_size:100}")
    private String batchSize;

    public AnagPartnerServiceImpl(
        QueryBuilder queryBuilder,
        AnagPartnerRepository anagPartnerRepository,
        AnagPartnerMapper anagPartnerMapper
    ) {
        this.queryBuilder = queryBuilder;
        this.anagPartnerRepository = anagPartnerRepository;
        this.anagPartnerMapper = anagPartnerMapper;
    }

    /**
     * Retrieves a paginated list of AnagPartnerDTO objects based on the provided criteria.
     *
     * @param fiscalCode the fiscal code to filter the partners, can be null or empty to retrieve all.
     * @param nameFilter a string to filter partners by their name, can be null or empty to retrieve all.
     * @param pageable an object containing pagination and sorting information.
     * @return a paginated list of AnagPartnerDTO objects matching the given criteria.
     */
    @Override
    public Page<AnagPartnerDTO> findAll(String fiscalCode, String nameFilter, Pageable pageable) {
        log.debug("Request to get all AnagPartner");

        JPQLQuery<AnagPartner> jpql = queryBuilder.<AnagPartner>createQuery().from(QAnagPartner.anagPartner);
        BooleanBuilder predicate = new BooleanBuilder();
        if (nameFilter != null && !nameFilter.isEmpty()) {
            predicate.or(QAnagPartner.anagPartner.name.likeIgnoreCase("%" + nameFilter + "%"));
        }

        if (fiscalCode != null && !fiscalCode.isEmpty()) {
            predicate.or(QAnagPartner.anagPartner.fiscalCode.likeIgnoreCase("%" + fiscalCode + "%"));
        }

        jpql.where(predicate);

        long size = jpql.fetchCount();

        JPQLQuery<AnagPartnerDTO> jpqlSelected = jpql.select(
            Projections.fields(
                AnagPartnerDTO.class,
                QAnagPartner.anagPartner.id.as("id"),
                QAnagPartner.anagPartner.fiscalCode.as("fiscalCode"),
                QAnagPartner.anagPartner.name.as("name"),
                QAnagPartner.anagPartner.status.as("status"),
                QAnagPartner.anagPartner.qualified.as("qualified"),
                QAnagPartner.anagPartner.deactivationDate.as("deactivationDate"),
                QAnagPartner.anagPartner.createdBy.as("createdBy"),
                QAnagPartner.anagPartner.createdDate.as("createdDate"),
                QAnagPartner.anagPartner.lastModifiedBy.as("lastModifiedBy"),
                QAnagPartner.anagPartner.lastModifiedDate.as("lastModifiedDate")
            )
        );

        jpqlSelected.offset(pageable.getOffset());
        jpqlSelected.limit(pageable.getPageSize());

        pageable
            .getSortOr(Sort.by(Sort.Direction.ASC, "name"))
            .forEach(order -> {
                jpqlSelected.orderBy(
                    new OrderSpecifier<>(
                        order.isAscending() ? Order.ASC : Order.DESC,
                        Expressions.stringPath(order.getProperty()),
                        QdslUtility.toQueryDslNullHandling(order.getNullHandling())
                    )
                );
            });

        List<AnagPartnerDTO> list = jpqlSelected.fetch();

        return new PageImpl<>(list, pageable, size);
    }

    @Override
    public void saveAll(List<AnagPartnerDTO> partners) {
        log.debug("Request to save all partners");
        AtomicInteger i = new AtomicInteger(0);

        partners.forEach(partnerDTO -> {
            AnagPartner partnerExample = new AnagPartner();
            partnerExample.setFiscalCode(partnerDTO.getFiscalCode());
            partnerExample.setCreatedDate(null);
            partnerExample.setLastModifiedDate(null);
            partnerExample.setQualified(null);

            AnagPartner anagPartner = anagPartnerRepository.findOne(Example.of(partnerExample)).orElse(new AnagPartner());
            anagPartner.setName(partnerDTO.getName());
            anagPartner.setFiscalCode(partnerDTO.getFiscalCode());

            if (partnerDTO.getStatus().compareTo(PartnerStatus.NON_ATTIVO) == 0 && anagPartner.getDeactivationDate() == null) {
                anagPartner.setDeactivationDate(LocalDate.now());
            }

            anagPartner.setStatus(partnerDTO.getStatus());

            anagPartnerRepository.save(anagPartner);

            if (i.getAndIncrement() % Integer.parseInt(batchSize) == Integer.parseInt(batchSize)) {
                anagPartnerRepository.flush();
            }
        });
    }

    /**
     * Find a single AnagPartner entity based on its unique identifier.
     *
     * @param id the unique identifier of the AnagPartner to retrieve
     * @return an {@link Optional} containing the {@link AnagPartnerDTO} if a matching entity exists, or an empty {@link Optional} if not found
     */
    @Override
    public Optional<AnagPartnerDTO> findOne(Long id) {
        return anagPartnerRepository.findById(id).map(anagPartnerMapper::toDto);
    }
}
