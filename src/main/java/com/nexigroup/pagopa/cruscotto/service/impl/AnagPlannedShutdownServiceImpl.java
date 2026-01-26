package com.nexigroup.pagopa.cruscotto.service.impl;

import com.nexigroup.pagopa.cruscotto.domain.AnagPartner;
import com.nexigroup.pagopa.cruscotto.domain.AnagPlannedShutdown;
import com.nexigroup.pagopa.cruscotto.domain.AnagStation;
import com.nexigroup.pagopa.cruscotto.domain.AuthUser;
import com.nexigroup.pagopa.cruscotto.domain.QAnagPlannedShutdown;
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
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link AnagPlannedShutdown}.
 */
@Service
@Transactional
public class AnagPlannedShutdownServiceImpl implements AnagPlannedShutdownService {

    private static final String ANAG_PLANNED_SHUTODOWN = "shutdown";
    private static final String ANAG_PLANNED_TYPE = "typePlanned";
    private static final String ANAG_PLANNED_SHUTDOWN_START_DATE = "shutdownStartDate";
    private static final String ANAG_PLANNED_SHUTDOWN_END_DATE = "shutdownEndDate";
    private static final String ANAG_PLANNED_EXTERNAL_ID = "externalId";

    private static final Logger LOGGER = LoggerFactory.getLogger(AnagPlannedShutdownServiceImpl.class);

    private final AnagPlannedShutdownRepository anagPlannedShutdownRepository;

    private final AnagPartnerRepository anagPartnerRepository;

    private final AnagStationRepository anagStationRepository;

    private final QueryBuilder queryBuilder;

    private final AnagPlannedShutdownMapper shutdownMapper;

    private final UserUtils userUtils;

    static final DateTimeFormatter formatterDateTime = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");
    static final DateTimeFormatter formatterDate = DateTimeFormatter.ofPattern("dd/MM/yyyy");

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
            builder.and(QAnagPlannedShutdown.anagPlannedShutdown.anagPartner.id.eq(Long.valueOf(filter.getPartnerId())));
        }

        if (StringUtils.isNotBlank(filter.getTypePlanned())) {
            builder.and(QAnagPlannedShutdown.anagPlannedShutdown.typePlanned.eq(TypePlanned.valueOf(filter.getTypePlanned())));
        }

        if (StringUtils.isNotBlank(filter.getYear())) {
            builder.and(QAnagPlannedShutdown.anagPlannedShutdown.year.eq(Long.valueOf(filter.getYear())));
        }

        if (StringUtils.isNotBlank(filter.getShutdownStartDate()) || StringUtils.isNotBlank(filter.getShutdownEndDate())) {
            LocalDateTime startDateTime = StringUtils.isNotBlank(filter.getShutdownStartDate())
                ? LocalDate.parse(filter.getShutdownStartDate(), formatterDate).atStartOfDay()
                : LocalDate.of(Long.valueOf(filter.getYear()).intValue(), 1, 1).atStartOfDay();

            LocalDateTime endDateTime = LocalDate.parse(
                StringUtils.isNotBlank(filter.getShutdownEndDate())
                    ? filter.getShutdownEndDate()
                    : LocalDate.of(Long.valueOf(filter.getYear()).intValue(), 12, 31).format(formatterDate),
                formatterDate
            ).atTime(23, 59, 59, 0);

            builder.and(
                QAnagPlannedShutdown.anagPlannedShutdown.shutdownStartDate
                    .between(
                        startDateTime.atZone(ZoneId.systemDefault()).toInstant(),
                        endDateTime.atZone(ZoneId.systemDefault()).toInstant()
                    )
                    .or(
                        QAnagPlannedShutdown.anagPlannedShutdown.shutdownEndDate.between(
                            startDateTime.atZone(ZoneId.systemDefault()).toInstant(),
                            endDateTime.atZone(ZoneId.systemDefault()).toInstant()
                        )
                    )
            );
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
                QAnagPlannedShutdown.anagPlannedShutdown.typePlanned.as(ANAG_PLANNED_TYPE),
                QAnagPlannedShutdown.anagPlannedShutdown.shutdownStartDate.as(ANAG_PLANNED_SHUTDOWN_START_DATE),
                QAnagPlannedShutdown.anagPlannedShutdown.shutdownEndDate.as(ANAG_PLANNED_SHUTDOWN_END_DATE),
                QAnagPlannedShutdown.anagPlannedShutdown.standInd.as("standIn"),
                QAnagPlannedShutdown.anagPlannedShutdown.year.as("year"),
                QAnagPlannedShutdown.anagPlannedShutdown.externalId.as(ANAG_PLANNED_EXTERNAL_ID),
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
            .getSortOr(Sort.by(Sort.Direction.ASC, ANAG_PLANNED_SHUTDOWN_START_DATE))
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
                    "shutdown.stationNotExists"
                )
            );

        AnagPlannedShutdown shutdown = new AnagPlannedShutdown();
        shutdown.setTypePlanned(TypePlanned.NON_PROGRAMMATO);
        shutdown.setAnagPartner(partner);
        shutdown.setAnagStation(station);
        LocalDateTime startDateTime = LocalDateTime.parse(shutdownToCreate.getShutdownStartDate(), formatterDateTime);
        Instant startInstant = startDateTime.atZone(zoneId).toInstant();
        shutdown.setShutdownStartDate(startInstant);
        LocalDateTime endDateTime = LocalDateTime.parse(shutdownToCreate.getShutdownEndDate(), formatterDateTime);
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
    public long delete(TypePlanned typePlanned, long year) {
        return queryBuilder
            .createQueryFactory()
            .delete(QAnagPlannedShutdown.anagPlannedShutdown)
            .where(
                QAnagPlannedShutdown.anagPlannedShutdown.typePlanned
                    .eq(TypePlanned.PROGRAMMATO)
                    .and(QAnagPlannedShutdown.anagPlannedShutdown.year.eq(year))
            )
            .execute();
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
            AnagPartner partner = anagPartnerRepository
                .findOneByFiscalCode(anagPlannedShutdownDTO.getPartnerFiscalCode())
                .orElseGet(() -> {
                    AnagPartner partnerSaved = new AnagPartner();
                    partnerSaved.setFiscalCode(anagPlannedShutdownDTO.getPartnerFiscalCode());
                    partnerSaved.setName(anagPlannedShutdownDTO.getPartnerName());
                    partnerSaved.setStatus(PartnerStatus.NON_ATTIVO);
                    return anagPartnerRepository.save(partnerSaved);
                });

            AnagStation station = anagStationRepository
                .findOneByName(anagPlannedShutdownDTO.getStationName())
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
                LocalDateTime startDateTime = LocalDateTime.parse(shutdownToUpdate.getShutdownStartDate(), formatterDateTime);
                Instant startInstant = startDateTime.atZone(zoneId).toInstant();
                shutdown.setShutdownStartDate(startInstant);
                LocalDateTime endDateTime = LocalDateTime.parse(shutdownToUpdate.getShutdownEndDate(), formatterDateTime);
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
                    anagPlannedShutdown.typePlanned.as(ANAG_PLANNED_TYPE),
                    anagPlannedShutdown.standInd.as("standInd"),
                    anagPlannedShutdown.shutdownStartDate.as(ANAG_PLANNED_SHUTDOWN_START_DATE),
                    anagPlannedShutdown.shutdownEndDate.as(ANAG_PLANNED_SHUTDOWN_END_DATE),
                    anagPlannedShutdown.year.as("year"),
                    anagPlannedShutdown.externalId.as(ANAG_PLANNED_EXTERNAL_ID)
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
                                startDateTime.atZone(ZoneId.systemDefault()).toInstant(),
                                endDateTime.atZone(ZoneId.systemDefault()).toInstant()
                            )
                            .or(
                                anagPlannedShutdown.shutdownEndDate.between(
                                    startDateTime.atZone(ZoneId.systemDefault()).toInstant(),
                                    endDateTime.atZone(ZoneId.systemDefault()).toInstant()
                                )
                            )
                    )
            )
            .fetch();
    }

    @Override
    public List<AnagPlannedShutdownDTO> findAllByTypePlannedIntoPeriod(
        Long partnerId,
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
                    anagPlannedShutdown.typePlanned.as(ANAG_PLANNED_TYPE),
                    anagPlannedShutdown.standInd.as("standInd"),
                    anagPlannedShutdown.shutdownStartDate.as(ANAG_PLANNED_SHUTDOWN_START_DATE),
                    anagPlannedShutdown.shutdownEndDate.as(ANAG_PLANNED_SHUTDOWN_END_DATE),
                    anagPlannedShutdown.year.as("year"),
                    anagPlannedShutdown.externalId.as(ANAG_PLANNED_EXTERNAL_ID),
                    anagPlannedShutdown.anagStation.name.as("stationName")
                )
            )
            .from(anagPlannedShutdown)
            .where(
                anagPlannedShutdown.anagPartner.id
                    .eq(partnerId)
                    .and(anagPlannedShutdown.typePlanned.eq(typePlanned))
                    .and(
                        anagPlannedShutdown.shutdownStartDate
                            .between(
                                startDateTime.atZone(ZoneId.systemDefault()).toInstant(),
                                endDateTime.atZone(ZoneId.systemDefault()).toInstant()
                            )
                            .or(
                                anagPlannedShutdown.shutdownEndDate.between(
                                    startDateTime.atZone(ZoneId.systemDefault()).toInstant(),
                                    endDateTime.atZone(ZoneId.systemDefault()).toInstant()
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
