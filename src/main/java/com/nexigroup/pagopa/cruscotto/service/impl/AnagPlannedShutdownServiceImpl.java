package com.nexigroup.pagopa.cruscotto.service.impl;

import com.nexigroup.pagopa.cruscotto.domain.*;
import com.nexigroup.pagopa.cruscotto.domain.enumeration.PartnerStatus;
import com.nexigroup.pagopa.cruscotto.domain.enumeration.StationStatus;
import com.nexigroup.pagopa.cruscotto.domain.enumeration.TypePlanned;
import com.nexigroup.pagopa.cruscotto.repository.AnagPartnerRepository;
import com.nexigroup.pagopa.cruscotto.repository.AnagPlannedShutdownRepository;
import com.nexigroup.pagopa.cruscotto.repository.AnagStationRepository;
import com.nexigroup.pagopa.cruscotto.service.AnagPlannedShutdownService;
import com.nexigroup.pagopa.cruscotto.service.GenericServiceException;
import com.nexigroup.pagopa.cruscotto.service.bean.ShutdownRequestBean;
import com.nexigroup.pagopa.cruscotto.service.dto.AnagPlannedShutdownDTO;
import com.nexigroup.pagopa.cruscotto.service.filter.AnagPlannedShutdownFilter;
import com.nexigroup.pagopa.cruscotto.service.mapper.AnagPlannedShutdownMapper;
import com.nexigroup.pagopa.cruscotto.service.qdsl.QdslUtility;
import com.nexigroup.pagopa.cruscotto.service.qdsl.QueryBuilder;
import com.nexigroup.pagopa.cruscotto.service.util.UserUtils;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.JPQLQuery;
import java.time.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.*;
import org.springframework.data.domain.Example;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link AnagPlannedShutdown}.
 */
@Service
@Transactional
public class AnagPlannedShutdownServiceImpl implements AnagPlannedShutdownService {

    private static final String ANAG_PLANNED_SHUTODOWN = "shutdown";

    private static final Logger LOGGER = LoggerFactory.getLogger(AnagPlannedShutdownServiceImpl.class);

    private final AnagPlannedShutdownRepository anagPlannedShutdownRepository;

    private final AnagPartnerRepository anagPartnerRepository;

    private final AnagStationRepository anagStationRepository;

    private final QueryBuilder queryBuilder;

    private final AnagPlannedShutdownMapper shutdownMapper;

    private final UserUtils userUtils;

    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");

    public AnagPlannedShutdownServiceImpl(
        AnagPlannedShutdownRepository anagPlannedShutdownRepository,
        AnagPartnerRepository anagPartnerRepository,
        AnagStationRepository anagStationRepository,
        QueryBuilder queryBuilder,
        AnagPlannedShutdownMapper shutdownMapper,
        UserUtils userUtils
    ) {
        this.anagPlannedShutdownRepository = anagPlannedShutdownRepository;
        this.anagPartnerRepository = anagPartnerRepository;
        this.anagStationRepository = anagStationRepository;
        this.queryBuilder = queryBuilder;
        this.shutdownMapper = shutdownMapper;
        this.userUtils = userUtils;
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
        LOGGER.debug("Request to get all shutdowns by filter: {}", filter);

        BooleanBuilder builder = new BooleanBuilder();

        if (StringUtils.isNotBlank(filter.getPartnerId())) {
            builder.and(QAnagPlannedShutdown.anagPlannedShutdown.anagPartner.id.eq(Long.valueOf(filter.getPartnerId())))
                .and(QAnagPlannedShutdown.anagPlannedShutdown.typePlanned.eq(filter.getTypePlanned()));
        }
        if (filter.getTypePlanned()!=null) {
            builder.and(QAnagPlannedShutdown.anagPlannedShutdown.typePlanned.eq(filter.getTypePlanned()));
        }

        JPQLQuery<AnagPlannedShutdown> jpql = queryBuilder
            .<AnagPlannedShutdown>createQuery()
            .from(QAnagPlannedShutdown.anagPlannedShutdown)
            .where(builder);

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
        return anagPlannedShutdownRepository.findById(id).map(shutdownMapper::toDto);
    }

    /**
     * Save a new shutdown.
     *
     * @param shutdownToCreate the shutdown to be saved.
     * @return the saved shutdown.
     */
    @Override
    public AnagPlannedShutdownDTO saveNew(ShutdownRequestBean shutdownToCreate) {
        AuthUser loggedUser = userUtils.getLoggedUser();

        ZoneId zoneId = ZoneId.systemDefault();

        ZonedDateTime now = ZonedDateTime.now(ZoneId.systemDefault());

        AnagPartner partner = anagPartnerRepository
            .findById(Long.valueOf(shutdownToCreate.getPartnerId()))
            .orElseThrow(() ->
                new GenericServiceException(
                    String.format("Partner with id %s not exist", shutdownToCreate.getPartnerId()),
                    ANAG_PLANNED_SHUTODOWN,
                    "shutdown.partnerNotExists"
                )
            );

        AnagStation station = anagStationRepository
            .findById(Long.valueOf(shutdownToCreate.getStationId()))
            .orElseThrow(() ->
                new GenericServiceException(
                    String.format("Station with id %s not exist", shutdownToCreate.getStationId()),
                    ANAG_PLANNED_SHUTODOWN,
                    "shutdown.partnerNotExists"
                )
            );

        AnagPlannedShutdown shutdown = new AnagPlannedShutdown();
        shutdown.setTypePlanned(TypePlanned.NON_PROGRAMMATO);
        shutdown.setAnagPartner(partner);
        shutdown.setAnagStation(station);
        LocalDateTime startDateTime = LocalDateTime.parse(shutdownToCreate.getShutdownStartDate(), formatter);
        Instant startInstant = startDateTime.atZone(zoneId).toInstant();
        shutdown.setShutdownStartDate(startInstant);
        LocalDateTime endDateTime = LocalDateTime.parse(shutdownToCreate.getShutdownEndDate(), formatter);
        Instant endInstant = endDateTime.atZone(zoneId).toInstant();
        shutdown.setShutdownEndDate(endInstant);
        shutdown.setStandInd(true);
        shutdown.setCreatedBy(loggedUser.getLogin());
        shutdown.setYear((long) now.getYear());
        AnagPlannedShutdown anagPlannedShutdown = anagPlannedShutdownRepository.save(shutdown);
        return shutdownMapper.toDto(anagPlannedShutdown);
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
                if (anagPlannedShutdown.getTypePlanned().equals(TypePlanned.PROGRAMMATO)) {
                    throw new GenericServiceException(
                        String.format(
                            "Shutdown with id %s cannot be deleted because it is of %s type",
                            id,
                            anagPlannedShutdown.getTypePlanned().name()
                        ),
                        ANAG_PLANNED_SHUTODOWN,
                        "shutdown.cannotBeDeleted"
                    );
                }
                anagPlannedShutdownRepository.deleteById(id);
                LOGGER.debug("Logical deleting of shutdown with id {}", id);
                return anagPlannedShutdown;
            })
            .orElseThrow(() ->
                new GenericServiceException(
                    String.format("Shutdown with id %s not exist", id),
                    ANAG_PLANNED_SHUTODOWN,
                    "shutdown.notExists"
                )
            );
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

    /**
     * Update a shutdown.
     *
     * @param shutdownToUpdate the shutdown to save.
     * @return the persisted shutdown.
     */
    @Override
    public AnagPlannedShutdownDTO update(ShutdownRequestBean shutdownToUpdate) {
        ZoneId zoneId = ZoneId.systemDefault();

        return Optional.of(anagPlannedShutdownRepository.findById(shutdownToUpdate.getId()))
            .filter(Optional::isPresent)
            .map(Optional::get)
            .map(shutdown -> {
                if (shutdown.getTypePlanned().equals(TypePlanned.PROGRAMMATO)) {
                    throw new GenericServiceException(
                        String.format(
                            "Shutdown with id %s cannot be updated because it is of %s type",
                            shutdownToUpdate.getId(),
                            shutdown.getTypePlanned()
                        ),
                        ANAG_PLANNED_SHUTODOWN,
                        "shutdown.cannotBeUpdated"
                    );
                }
                AnagPartner partner = anagPartnerRepository
                    .findById(Long.valueOf(shutdownToUpdate.getPartnerId()))
                    .orElseThrow(() ->
                        new GenericServiceException(
                            String.format("Partner with id %s not exist", shutdownToUpdate.getPartnerId()),
                            ANAG_PLANNED_SHUTODOWN,
                            "shutdown.partnerNotExists"
                        )
                    );
                shutdown.setAnagPartner(partner);

                AnagStation station = anagStationRepository
                    .findById(Long.valueOf(shutdownToUpdate.getStationId()))
                    .orElseThrow(() ->
                        new GenericServiceException(
                            String.format("Station with id %s not exist", shutdownToUpdate.getStationId()),
                            ANAG_PLANNED_SHUTODOWN,
                            "shutdown.stationNotExists"
                        )
                    );
                shutdown.setAnagStation(station);
                LocalDateTime startDateTime = LocalDateTime.parse(shutdownToUpdate.getShutdownStartDate(), formatter);
                Instant startInstant = startDateTime.atZone(zoneId).toInstant();
                shutdown.setShutdownStartDate(startInstant);
                LocalDateTime endDateTime = LocalDateTime.parse(shutdownToUpdate.getShutdownEndDate(), formatter);
                Instant endInstant = endDateTime.atZone(zoneId).toInstant();
                shutdown.setShutdownEndDate(endInstant);
                shutdown.setShutdownEndDate(endInstant);
                return shutdown;
            })
            .map(shutdownMapper::toDto)
            .orElseThrow(() ->
                new GenericServiceException(
                    String.format("Shutdown with id %s not exist", shutdownToUpdate.getId()),
                    ANAG_PLANNED_SHUTODOWN,
                    "shutdown.notExists"
                )
            );
    }

    @Override
    public List<AnagPlannedShutdownDTO> findAllByTypePlannedIntoPeriod(
        Long partnerId,
        Long stationId,
        TypePlanned typePlanned,
        LocalDate startDate,
        LocalDate endDate
    ) {
        QAnagPlannedShutdown anagPlannedShutdown = QAnagPlannedShutdown.anagPlannedShutdown;

        LocalDateTime startDateTime = startDate.atStartOfDay();
        LocalDateTime endDateTime = endDate.atTime(23, 59, 59, 0);

        LOGGER.info(startDateTime.toString());
        LOGGER.info(endDateTime.toString());

        return queryBuilder
            .createQueryFactory()
            .select(
                Projections.fields(
                    AnagPlannedShutdownDTO.class,
                    anagPlannedShutdown.id.as("id"),
                    anagPlannedShutdown.typePlanned.as("typePlanned"),
                    anagPlannedShutdown.standInd.as("standInd"),
                    anagPlannedShutdown.shutdownStartDate.as("shutdownStartDate"),
                    anagPlannedShutdown.shutdownEndDate.as("shutdownEndDate"),
                    anagPlannedShutdown.year.as("year"),
                    anagPlannedShutdown.externalId.as("externalId")
                )
            )
            .from(anagPlannedShutdown)
            .where(
                anagPlannedShutdown.anagPartner.id
                    .eq(partnerId)
                    .and(anagPlannedShutdown.anagStation.id.eq(stationId))
                    .and(anagPlannedShutdown.typePlanned.eq(typePlanned))
                    .and(
                        anagPlannedShutdown.shutdownStartDate
                            .between(
                                startDateTime.atZone(ZoneOffset.systemDefault()).toInstant(),
                                endDateTime.atZone(ZoneOffset.systemDefault()).toInstant()
                            )
                            .or(
                                anagPlannedShutdown.shutdownEndDate.between(
                                    startDateTime.atZone(ZoneOffset.systemDefault()).toInstant(),
                                    endDateTime.atZone(ZoneOffset.systemDefault()).toInstant()
                                )
                            )
                    )
            )
            .fetch();
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
