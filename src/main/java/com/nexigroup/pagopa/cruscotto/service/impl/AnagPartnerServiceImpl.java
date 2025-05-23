package com.nexigroup.pagopa.cruscotto.service.impl;

import com.nexigroup.pagopa.cruscotto.domain.AnagPartner;
import com.nexigroup.pagopa.cruscotto.domain.Instance;
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
import org.springframework.core.env.Environment;
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

    @Value("${spring.jpa.properties.hibernate.jdbc.batch_size:100}")
    private String batchSize;

    public AnagPartnerServiceImpl(QueryBuilder queryBuilder, AnagPartnerRepository anagPartnerRepository) {
        this.queryBuilder = queryBuilder;
        this.anagPartnerRepository = anagPartnerRepository;
    }

    /**
     * Get all the partner by filter.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Override
    public Page<AnagPartnerDTO> findAll(String nameFilter, Pageable pageable) {
        log.debug("Request to get all AnagPartner");

        JPQLQuery<AnagPartner> jpql = queryBuilder.<AnagPartner>createQuery().from(QAnagPartner.anagPartner);

        if (nameFilter != null && !nameFilter.isEmpty()) {
            jpql.where(QAnagPartner.anagPartner.name.containsIgnoreCase(nameFilter));
        }

        long size = jpql.fetchCount();

        JPQLQuery<AnagPartnerDTO> jpqlSelected = jpql.select(
            Projections.fields(
                AnagPartnerDTO.class,
                QAnagPartner.anagPartner.id.as("id"),
                QAnagPartner.anagPartner.fiscalCode.as("fiscalCode"),
                QAnagPartner.anagPartner.name.as("name"),
                QAnagPartner.anagPartner.status.as("status")
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
}
