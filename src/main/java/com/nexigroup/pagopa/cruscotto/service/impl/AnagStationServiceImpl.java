package com.nexigroup.pagopa.cruscotto.service.impl;

import com.nexigroup.pagopa.cruscotto.domain.*;
import com.nexigroup.pagopa.cruscotto.domain.enumeration.StationStatus;
import com.nexigroup.pagopa.cruscotto.repository.AnagPartnerRepository;
import com.nexigroup.pagopa.cruscotto.repository.AnagStationRepository;
import com.nexigroup.pagopa.cruscotto.service.AnagStationService;
import com.nexigroup.pagopa.cruscotto.service.dto.AnagStationDTO;
import com.nexigroup.pagopa.cruscotto.service.filter.AnagStationFilter;
import com.nexigroup.pagopa.cruscotto.service.filter.StationFilter;
import com.nexigroup.pagopa.cruscotto.service.mapper.AnagStationMapper;
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
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link AnagStation}.
 */
@Service
@Transactional
public class AnagStationServiceImpl implements AnagStationService {

    private final Logger log = LoggerFactory.getLogger(AnagStationServiceImpl.class);

    private final AnagStationRepository anagStationRepository;

    private final AnagPartnerRepository anagPartnerRepository;

    private final QueryBuilder queryBuilder;

    private final AnagStationMapper anagStationMapper;

    @Value("${spring.jpa.properties.hibernate.jdbc.batch_size:100}")
    private String batchSize;

    public AnagStationServiceImpl(
        AnagStationRepository anagStationRepository,
        AnagPartnerRepository anagPartnerRepository,
        QueryBuilder queryBuilder,
        AnagStationMapper anagStationMapper
    ) {
        this.anagStationRepository = anagStationRepository;
        this.anagPartnerRepository = anagPartnerRepository;
        this.queryBuilder = queryBuilder;
        this.anagStationMapper = anagStationMapper;
    }

    @Override
    public void saveAll(List<AnagStationDTO> stations) {
        log.debug("Request to save all stations");
        AtomicInteger i = new AtomicInteger(0);

        stations.forEach(stationDTO -> {
            AnagStation stationExample = new AnagStation();
            stationExample.setName(stationDTO.getName());
            stationExample.setCreatedDate(null);
            stationExample.setLastModifiedDate(null);
            stationExample.setPaymentOption(null);

            AnagPartner partnerExample = new AnagPartner();
            partnerExample.setFiscalCode(stationDTO.getPartnerFiscalCode());
            partnerExample.setCreatedDate(null);
            partnerExample.setLastModifiedDate(null);
            partnerExample.setQualified(null);

            AnagPartner anagPartner = anagPartnerRepository.findOne(Example.of(partnerExample)).orElse(new AnagPartner());

            AnagStation anagStation = anagStationRepository.findOne(Example.of(stationExample)).orElse(new AnagStation());
            anagStation.setName(stationDTO.getName());
            anagStation.setActivationDate(stationDTO.getActivationDate());
            anagStation.setTypeConnection(stationDTO.getTypeConnection());
            anagStation.setPrimitiveVersion(stationDTO.getPrimitiveVersion());
            anagStation.setPaymentOption(stationDTO.getPaymentOption());
            anagStation.setAssociatedInstitutes(0);
            anagStation.setAnagPartner(anagPartner);

            if (stationDTO.getStatus().compareTo(StationStatus.NON_ATTIVA) == 0 && anagStation.getDeactivationDate() == null) {
                anagStation.setDeactivationDate(LocalDate.now());
            }

            anagStation.setStatus(stationDTO.getStatus());

            anagStationRepository.save(anagStation);

            if (i.getAndIncrement() % Integer.parseInt(batchSize) == Integer.parseInt(batchSize)) {
                anagStationRepository.flush();
            }
        });
    }

    @Override
    public long findIdByNameOrCreate(String name, long idPartner) {
        Long id = queryBuilder
            .<AnagStationDTO>createQuery()
            .from(QAnagStation.anagStation)
            .leftJoin(QAnagPartner.anagPartner)
            .on(QAnagStation.anagStation.anagPartner.id.eq(QAnagPartner.anagPartner.id))
            .where(QAnagStation.anagStation.name.eq(name).and(QAnagStation.anagStation.anagPartner.id.eq(idPartner)))
            .select(QAnagStation.anagStation.id)
            .fetchOne();

        if (id == null) {
            AnagPartner partnerExample = new AnagPartner();
            partnerExample.setId(idPartner);
            partnerExample.setCreatedDate(null);
            partnerExample.setLastModifiedDate(null);
            partnerExample.setQualified(null);

            AnagPartner anagPartner = anagPartnerRepository
                .findOne(Example.of(partnerExample))
                .orElseThrow(() -> new NullPointerException("Partner not found"));

            AnagStation anagStation = new AnagStation();
            anagStation.setName(name);
            anagStation.setAnagPartner(anagPartner);
            anagStation.setStatus(StationStatus.NON_ATTIVA);
            anagStation.setAssociatedInstitutes(0);

            anagStation = anagStationRepository.save(anagStation);

            id = anagStation.getId();
        }
        return id;
    }

    @Override
    public Page<AnagStationDTO> findAll(StationFilter filter, Pageable pageable) {
        log.debug("Request to get all Stations by filter: {}", filter);

        BooleanBuilder builder = new BooleanBuilder();

        if (StringUtils.isNotBlank(filter.getPartnerId())) {
            builder.and(QAnagStation.anagStation.anagPartner.id.eq(Long.valueOf(filter.getPartnerId())));
        }

        JPQLQuery<AnagStation> jpql = queryBuilder.<AnagStation>createQuery().from(QAnagStation.anagStation).where(builder);

        long size = jpql.fetchCount();

        JPQLQuery<AnagStationDTO> jpqlSelected = jpql.select(
            Projections.fields(
                AnagStationDTO.class,
                QAnagStation.anagStation.id.as("id"),
                QAnagStation.anagStation.name.as("name"),
                QAnagStation.anagStation.activationDate.as("activationDate"),
                QAnagStation.anagStation.anagPartner.id.as("partnerId"),
                QAnagStation.anagStation.anagPartner.fiscalCode.as("partnerFiscalCode"),
                QAnagStation.anagStation.anagPartner.name.as("partnerName"),
                QAnagStation.anagStation.typeConnection.as("typeConnection"),
                QAnagStation.anagStation.primitiveVersion.as("primitiveVersion"),
                QAnagStation.anagStation.paymentOption.as("paymentOption"),
                QAnagStation.anagStation.associatedInstitutes.as("associatedInstitutes"),
                QAnagStation.anagStation.status.as("status"),
                QAnagStation.anagStation.deactivationDate.as("deactivationDate"),
                QAnagStation.anagStation.createdBy.as("createdBy"),
                QAnagStation.anagStation.createdDate.as("createdDate"),
                QAnagStation.anagStation.lastModifiedBy.as("lastModifiedBy"),
                QAnagStation.anagStation.lastModifiedDate.as("lastModifiedDate")
            )
        );

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

        List<AnagStationDTO> list = jpqlSelected.fetch();

        return new PageImpl<>(list, pageable, size);
    }

    /**
     * Find a single AnagStation entity based on its unique identifier.
     *
     * @param id the unique identifier of the AnagStation to retrieve
     * @return an {@link Optional} containing the {@link AnagStationDTO} if a matching entity exists, or an empty {@link Optional} if not found
     */
    @Override
    public Optional<AnagStationDTO> findOne(Long id) {
        return anagStationRepository.findById(id).map(anagStationMapper::toDto);
    }

	@Override
	public Page<AnagStationDTO> findAll(AnagStationFilter filter, Pageable pageable) {
		log.debug("Request to get all Stations by filter: {}", filter);

        BooleanBuilder builder = new BooleanBuilder();

        if (filter.getPartnerId() != null) {
            builder.and(QAnagStation.anagStation.anagPartner.id.eq(Long.valueOf(filter.getPartnerId())));
        }
        
        if (filter.getStationId() != null) {
        	builder.and(QAnagStation.anagStation.id.eq(Long.valueOf(filter.getStationId())));
        }
        
        if (filter.getShowNotActive() == null ||  (filter.getShowNotActive() != null && !filter.getShowNotActive().booleanValue())) {
        	builder.and(QAnagStation.anagStation.status.stringValue().eq(StationStatus.ATTIVA.name()));
        }

        JPQLQuery<AnagStation> jpql = queryBuilder.<AnagStation>createQuery().from(QAnagStation.anagStation).where(builder);

        long size = jpql.fetchCount();

        JPQLQuery<AnagStationDTO> jpqlSelected = jpql.select(
            Projections.fields(
                AnagStationDTO.class,
                QAnagStation.anagStation.id.as("id"),
                QAnagStation.anagStation.name.as("name"),
                QAnagStation.anagStation.activationDate.as("activationDate"),
                QAnagStation.anagStation.anagPartner.id.as("partnerId"),
                QAnagStation.anagStation.anagPartner.fiscalCode.as("partnerFiscalCode"),
                QAnagStation.anagStation.anagPartner.name.as("partnerName"),
                QAnagStation.anagStation.typeConnection.as("typeConnection"),
                QAnagStation.anagStation.primitiveVersion.as("primitiveVersion"),
                QAnagStation.anagStation.paymentOption.as("paymentOption"),
                QAnagStation.anagStation.associatedInstitutes.as("associatedInstitutes"),
                QAnagStation.anagStation.status.as("status"),
                QAnagStation.anagStation.deactivationDate.as("deactivationDate"),
                QAnagStation.anagStation.createdBy.as("createdBy"),
                QAnagStation.anagStation.createdDate.as("createdDate"),
                QAnagStation.anagStation.lastModifiedBy.as("lastModifiedBy"),
                QAnagStation.anagStation.lastModifiedDate.as("lastModifiedDate")
            )
        );

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

        List<AnagStationDTO> list = jpqlSelected.fetch();

        return new PageImpl<>(list, pageable, size);
	}

}
