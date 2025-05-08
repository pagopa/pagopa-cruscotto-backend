package com.nexigroup.pagopa.cruscotto.service.impl;

import com.nexigroup.pagopa.cruscotto.domain.AnagPartner;
import com.nexigroup.pagopa.cruscotto.domain.AnagPlannedShutdown;
import com.nexigroup.pagopa.cruscotto.domain.AnagStation;
import com.nexigroup.pagopa.cruscotto.domain.QAnagPlannedShutdown;
import com.nexigroup.pagopa.cruscotto.domain.enumeration.PartnerStatus;
import com.nexigroup.pagopa.cruscotto.domain.enumeration.StationStatus;
import com.nexigroup.pagopa.cruscotto.domain.enumeration.TypePlanned;
import com.nexigroup.pagopa.cruscotto.repository.AnagPartnerRepository;
import com.nexigroup.pagopa.cruscotto.repository.AnagPlannedShutdownRepository;
import com.nexigroup.pagopa.cruscotto.repository.AnagStationRepository;
import com.nexigroup.pagopa.cruscotto.service.AnagPlannedShutdownService;
import com.nexigroup.pagopa.cruscotto.service.GenericServiceException;
import com.nexigroup.pagopa.cruscotto.service.dto.AnagPlannedShutdownDTO;
import com.nexigroup.pagopa.cruscotto.service.filter.AnagPlannedShutdownFilter;
import com.nexigroup.pagopa.cruscotto.service.mapper.AnagPlannedShutdownMapper;
import com.nexigroup.pagopa.cruscotto.service.qdsl.QdslUtility;
import com.nexigroup.pagopa.cruscotto.service.qdsl.QueryBuilder;
import java.util.List;
import java.util.Optional;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.JPQLQuery;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


/**
 * Service Implementation for managing {@link AnagPlannedShutdown}.
 */
@Service
@Transactional
public class AnagPlannedShutdownServiceImpl implements AnagPlannedShutdownService {

    private static final String ANAG_PLANNED_SHUTODOWN = "shutdown";

    private final Logger log = LoggerFactory.getLogger(AnagPlannedShutdownServiceImpl.class);

    private final AnagPlannedShutdownRepository anagPlannedShutdownRepository;

    private final AnagPartnerRepository anagPartnerRepository;

    private final AnagStationRepository anagStationRepository;

    private final QueryBuilder queryBuilder;

    private final AnagPlannedShutdownMapper shutdownMapper;

    public AnagPlannedShutdownServiceImpl(
        AnagPlannedShutdownRepository anagPlannedShutdownRepository,
        AnagPartnerRepository anagPartnerRepository,
        AnagStationRepository anagStationRepository,
        QueryBuilder queryBuilder,
        AnagPlannedShutdownMapper shutdownMapper
    ) {
        this.anagPlannedShutdownRepository = anagPlannedShutdownRepository;
        this.anagPartnerRepository = anagPartnerRepository;
        this.anagStationRepository = anagStationRepository;
        this.queryBuilder = queryBuilder;
        this.shutdownMapper = shutdownMapper;
    }


    /**
     * Get all the shutdowns by filter.
     *
     * @param filter the pagination filter.
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Override
    public Page<AnagPlannedShutdownDTO> findAll(AnagPlannedShutdownFilter filter, Pageable pageable) {
        log.debug("Request to get all shutdowns by filter: {}", filter);

        BooleanBuilder builder = new BooleanBuilder();

        if (StringUtils.isNotBlank(filter.getPartnerId())) {
            builder.and(QAnagPlannedShutdown.anagPlannedShutdown.anagPartner.id.eq(Long.valueOf(filter.getPartnerId())));
        }

        JPQLQuery<AnagPlannedShutdown> jpql = queryBuilder.<AnagPlannedShutdown>createQuery().from(QAnagPlannedShutdown.anagPlannedShutdown).where(builder);

        long size = jpql.fetchCount();

        JPQLQuery<AnagPlannedShutdownDTO> jpqlSelected = jpql.select(
            Projections.fields(
                AnagPlannedShutdownDTO.class,
                QAnagPlannedShutdown.anagPlannedShutdown.id.as("id"),
                QAnagPlannedShutdown.anagPlannedShutdown.typePlanned.as("typePlanned"),
                QAnagPlannedShutdown.anagPlannedShutdown.shutdownStartDate.as("shutdownStartDate"),
                QAnagPlannedShutdown.anagPlannedShutdown.shutdownEndDate.as("shutdownEndDate"),
                QAnagPlannedShutdown.anagPlannedShutdown.standInd.as("standIn"),
                QAnagPlannedShutdown.anagPlannedShutdown.year.as("year"),
                QAnagPlannedShutdown.anagPlannedShutdown.externalId.as("externalId"),
                QAnagPlannedShutdown.anagPlannedShutdown.anagPartner.id.as("partnerId"),
                QAnagPlannedShutdown.anagPlannedShutdown.anagPartner.fiscalCode.as("partnerFiscalCode"),
                QAnagPlannedShutdown.anagPlannedShutdown.anagPartner.name.as("partnerName"),
                QAnagPlannedShutdown.anagPlannedShutdown.anagStation.id.as("stationId"),
                QAnagPlannedShutdown.anagPlannedShutdown.anagStation.name.as("stationName")
            )
        );

        jpqlSelected.offset(pageable.getOffset());
        jpqlSelected.limit(pageable.getPageSize());

        pageable
            .getSortOr(Sort.by(Sort.Direction.ASC, "shutdownStartDate"))
            .forEach(order -> {
                jpqlSelected.orderBy(
                    new OrderSpecifier<>(
                        order.isAscending() ? Order.ASC : Order.DESC,
                        Expressions.stringPath(order.getProperty()),
                        QdslUtility.toQueryDslNullHandling(order.getNullHandling())
                    )
                );
            });

        List<AnagPlannedShutdownDTO> list = jpqlSelected.fetch();

        return new PageImpl<>(list, pageable, size);
    }

    /**
     * Get one shutdown by its identifier.
     *
     * @param id the shutdown identifier.
     * @return the selected shutdown.
     */
    @Override
    public Optional<AnagPlannedShutdownDTO> findOne(Long id) {
        return anagPlannedShutdownRepository.findById(id)
            .map(shutdownMapper::toDto);
    }

    /**
     * Count all the plannedShutdowns by type planned and year.
     *
     * @param typePlanned the type planned.
     * @param year the year.
     * @return the count of entities.
     */
    @Override
    public Long count(TypePlanned typePlanned, long year) {
        AnagPlannedShutdown anagPlannedShutdown = new AnagPlannedShutdown();
        anagPlannedShutdown.setTypePlanned(typePlanned);
        anagPlannedShutdown.setYear(year);
        Example<AnagPlannedShutdown> example = Example.of(anagPlannedShutdown);

        return anagPlannedShutdownRepository.count(example);
    }

    /**
     * Delete all the plannedShutdowns by type planned and year.
     *
     * @param typePlanned the type planned.
     * @param year the year.
     */
    @Override
    public void delete(TypePlanned typePlanned, long year) {
        queryBuilder
            .createQueryFactory()
            .delete(QAnagPlannedShutdown.anagPlannedShutdown)
            .where(
                QAnagPlannedShutdown.anagPlannedShutdown.typePlanned
                    .eq(TypePlanned.PROGRAMMATO)
                    .and(QAnagPlannedShutdown.anagPlannedShutdown.year.eq(year))
            );
    }

    @Override
    public void delete(Long id) {
        Optional.of(anagPlannedShutdownRepository.findById(id))
            .filter(Optional::isPresent)
            .map(Optional::get)
            .map(anagPlannedShutdown -> {
                if(anagPlannedShutdown.getTypePlanned().equals(TypePlanned.PROGRAMMATO)) {
                    throw new GenericServiceException(String.format("Shutdown with id %s cannot be deleted because it is of %s type", id, anagPlannedShutdown.getTypePlanned().name()),
                        ANAG_PLANNED_SHUTODOWN, "shutdown.cannotBeDeleted");
                }
                anagPlannedShutdownRepository.deleteById(id);
                log.debug("Logical deleting of shutdown with id {}", id);
                return anagPlannedShutdown;
            })
            .orElseThrow(() -> new GenericServiceException(String.format("Shutdown with id %s not exist", id),
                ANAG_PLANNED_SHUTODOWN, "shutdown.notExists"));
    }

    /**
     * Save all planned shutdown.
     *
     * @param anagPlannedShutdownDTOS the entities to save.
     */
    @Override
    public void saveAll(List<AnagPlannedShutdownDTO> anagPlannedShutdownDTOS) {
        anagPlannedShutdownDTOS.forEach(anagPlannedShutdownDTO -> {
            AnagPartner partnerExample = new AnagPartner();
            partnerExample.setFiscalCode(anagPlannedShutdownDTO.getPartnerFiscalCode());
            partnerExample.setCreatedDate(null);
            partnerExample.setLastModifiedDate(null);

            AnagPartner partner = anagPartnerRepository
                .findOne(Example.of(partnerExample))
                .orElseGet(() -> {
                    AnagPartner partnerSaved = new AnagPartner();
                    partnerSaved.setFiscalCode(anagPlannedShutdownDTO.getPartnerFiscalCode());
                    partnerSaved.setName(anagPlannedShutdownDTO.getPartnerName());
                    partnerSaved.setStatus(PartnerStatus.NON_ATTIVO);
                    return anagPartnerRepository.save(partnerSaved);
                });

            AnagStation stationExample = new AnagStation();
            stationExample.setName(anagPlannedShutdownDTO.getStationName());
            stationExample.setCreatedDate(null);
            stationExample.setLastModifiedDate(null);

            AnagStation station = anagStationRepository
                .findOne(Example.of(stationExample))
                .orElseGet(() -> {
                    AnagStation stationSaved = new AnagStation();
                    stationSaved.setName(anagPlannedShutdownDTO.getStationName());
                    stationSaved.setAssociatedInstitutes(0);
                    stationSaved.setAnagPartner(partner);
                    stationSaved.setStatus(StationStatus.NON_ATTIVA);
                    return anagStationRepository.save(stationSaved);
                });

            AnagPlannedShutdown plannedShutdown = getAnagPlannedShutdown(anagPlannedShutdownDTO, partner, station);

            anagPlannedShutdownRepository.save(plannedShutdown);
        });
    }

    private static @NotNull AnagPlannedShutdown getAnagPlannedShutdown(
        AnagPlannedShutdownDTO anagPlannedShutdownDTO,
        AnagPartner partner,
        AnagStation station
    ) {
        AnagPlannedShutdown plannedShutdown = new AnagPlannedShutdown();
        plannedShutdown.setTypePlanned(anagPlannedShutdownDTO.getTypePlanned());
        plannedShutdown.setStandInd(anagPlannedShutdownDTO.isStandInd());
        plannedShutdown.setShutdownStartDate(anagPlannedShutdownDTO.getShutdownStartDate());
        plannedShutdown.setShutdownEndDate(anagPlannedShutdownDTO.getShutdownEndDate());
        plannedShutdown.setAnagPartner(partner);
        plannedShutdown.setAnagStation(station);
        plannedShutdown.setYear(anagPlannedShutdownDTO.getYear());
        plannedShutdown.setExternalId(anagPlannedShutdownDTO.getExternalId());
        return plannedShutdown;
    }
}
