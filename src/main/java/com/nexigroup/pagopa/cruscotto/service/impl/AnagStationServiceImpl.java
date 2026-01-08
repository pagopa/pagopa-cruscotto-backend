package com.nexigroup.pagopa.cruscotto.service.impl;

import com.nexigroup.pagopa.cruscotto.domain.*;
import com.nexigroup.pagopa.cruscotto.domain.enumeration.StationStatus;
import com.nexigroup.pagopa.cruscotto.job.cache.Station;
import com.nexigroup.pagopa.cruscotto.job.cache.CreditorInstitutionStation;
import com.nexigroup.pagopa.cruscotto.job.cache.CreditorInstitutionStationsResponse;
import com.nexigroup.pagopa.cruscotto.job.client.PagoPaCacheClient;
import com.nexigroup.pagopa.cruscotto.repository.AnagInstitutionRepository;
import com.nexigroup.pagopa.cruscotto.repository.AnagPartnerRepository;
import com.nexigroup.pagopa.cruscotto.repository.AnagStationAnagInstitutionRepository;
import com.nexigroup.pagopa.cruscotto.repository.AnagStationRepository;
import com.nexigroup.pagopa.cruscotto.service.AnagPartnerService;
import com.nexigroup.pagopa.cruscotto.service.AnagStationService;
import com.nexigroup.pagopa.cruscotto.service.dto.AnagStationDTO;
import com.nexigroup.pagopa.cruscotto.service.filter.AnagStationFilter;
import com.nexigroup.pagopa.cruscotto.service.filter.StationFilter;
import com.nexigroup.pagopa.cruscotto.service.mapper.AnagStationMapper;
import com.nexigroup.pagopa.cruscotto.service.qdsl.QdslUtility;
import com.nexigroup.pagopa.cruscotto.service.qdsl.QueryBuilder;
import com.nexigroup.pagopa.cruscotto.service.validation.ValidationGroups;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.JPQLQuery;
import jakarta.persistence.EntityManager;
import java.time.LocalDate;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.StopWatch;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.*;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link AnagStation}.
 */
@Service
@Transactional
public class AnagStationServiceImpl implements AnagStationService {

    @Override
    public Optional<AnagStation> findOneByName(String name) {
        return anagStationRepository.findOneByName(name);
    }

    private final Logger log = LoggerFactory.getLogger(AnagStationServiceImpl.class);

    private final AnagStationRepository anagStationRepository;

    private final AnagPartnerRepository anagPartnerRepository;
    
    private final AnagStationAnagInstitutionRepository anagStationAnagInstitutionRepository;

    private final AnagInstitutionRepository anagInstitutionRepository;

    private final QueryBuilder queryBuilder;

    private final AnagStationMapper anagStationMapper;

    private final PagoPaCacheClient pagoPaCacheClient;

    private final AnagPartnerService anagPartnerService;

    private final JdbcTemplate jdbcTemplate;

    private final EntityManager entityManager;

    @Value("${spring.jpa.properties.hibernate.jdbc.batch_size:100}")
    private String batchSize;

    public AnagStationServiceImpl(
        AnagStationRepository anagStationRepository,
        AnagPartnerRepository anagPartnerRepository,
        AnagStationAnagInstitutionRepository anagStationAnagInstitutionRepository,
        AnagInstitutionRepository anagInstitutionRepository,
        QueryBuilder queryBuilder,
        AnagStationMapper anagStationMapper,
        PagoPaCacheClient pagoPaCacheClient,
        AnagPartnerService anagPartnerService,
        JdbcTemplate jdbcTemplate,
        EntityManager entityManager
    ) {
        this.anagStationRepository = anagStationRepository;
        this.anagPartnerRepository = anagPartnerRepository;
        this.anagStationAnagInstitutionRepository = anagStationAnagInstitutionRepository;
        this.anagInstitutionRepository = anagInstitutionRepository;
        this.queryBuilder = queryBuilder;
        this.anagStationMapper = anagStationMapper;
        this.pagoPaCacheClient = pagoPaCacheClient;
        this.anagPartnerService = anagPartnerService;
        this.jdbcTemplate = jdbcTemplate;
        this.entityManager = entityManager;
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

	@Override
	public void updateAllStationsAssociatedInstitutionsCount() {
		 log.debug("updateAllStationsAssociatedInstitutionsCount START");
	        
	        // get all counts by station
	        List<Object[]> institutionCounts = anagStationAnagInstitutionRepository.countInstitutionsByStation();
	        
	        // result to map
	        Map<Long, Long> countsByStationId = institutionCounts.stream()
	            .collect(Collectors.toMap(
	                result -> (Long) result[0],  // stationId
	                result -> (Long) result[1]   // count
	            ));
	        
	        // update each station
	        countsByStationId.forEach((stationId, count) -> {
	            try {
	                anagStationRepository.updateAssociatedInstitutesCount(stationId, count.intValue());
	                
	            } catch (Exception e) {
	                log.error("can not update station {}: {}", stationId, e.getMessage());
	            }
	        });
	        
	        log.debug("updateAllStationsAssociatedInstitutionsCount END");
		
	}

    @Override
    @Transactional
    public void loadFromPagoPA() {
        log.info("Call PagoPA to get stations");

        Station[] response = pagoPaCacheClient.stations();

        List<AnagStationDTO> stationDTOS = new ArrayList<>();

        log.info("{} records will be saved ", response.length);

        if (ArrayUtils.isNotEmpty(response)) {
            try (ValidatorFactory factory = Validation.buildDefaultValidatorFactory()) {
                Validator validator = factory.getValidator();
                for (Station station : response) {
                    log.info("{}", station);

                    AnagStationDTO stationDTO = new AnagStationDTO();
                    stationDTO.setName(station.getStationCode());
                    stationDTO.setPartnerFiscalCode(station.getBrokerCode());
                    stationDTO.setPrimitiveVersion(station.getPrimitiveVersion());
                    stationDTO.setPaymentOption(station.getIsPaymentOptionsEnabled());
                    stationDTO.setStatus(
                        BooleanUtils.toBooleanDefaultIfNull(station.getEnabled(), false) ? StationStatus.ATTIVA : StationStatus.NON_ATTIVA
                    );

                    Set<ConstraintViolation<AnagStationDTO>> violations = validator.validate(stationDTO, ValidationGroups.StationJob.class);

                    if (violations.isEmpty()) {
                        stationDTOS.add(stationDTO);
                    } else {
                        log.error("Invalid station {}", stationDTO);
                        violations.forEach(violation -> log.error("{}: {}", violation.getPropertyPath(), violation.getMessage()));
                    }
                }
            }

            log.info("After validation {} records will be saved", stationDTOS.size());

            StopWatch stopWatch = new StopWatch();
            stopWatch.start();

            saveAll(stationDTOS);

            Map<String, Long> partnerStationCounts = new HashMap<>();
            for (AnagStationDTO stationDTO : stationDTOS) {
                String partnerFiscalCode = stationDTO.getPartnerFiscalCode();
                partnerStationCounts.put(partnerFiscalCode, partnerStationCounts.getOrDefault(partnerFiscalCode, 0L) + 1);
            }
            partnerStationCounts.forEach((fiscalCode, count) -> {
                anagPartnerService.findOneByFiscalCode(fiscalCode).ifPresent(partnerDTO -> {
                    Long partnerId = partnerDTO.getPartnerIdentification() != null ? partnerDTO.getPartnerIdentification().getId() : null;
                    if (partnerId != null) {
                        anagPartnerService.updateStationsCount(partnerId, count);
                    }
                });
            });

            stopWatch.stop();

            log.info("Saved {} rows stations to database into {} seconds", stationDTOS.size(), stopWatch.getTime(TimeUnit.SECONDS));
        }
    }

    @Override
    @Transactional
    public void loadAssociationsFromPagoPA() {
        log.info("Call PagoPA to get creditorInstitutionStations");
        CreditorInstitutionStationsResponse response = pagoPaCacheClient.creditorInstitutionStations();
        int size = response.getCreditorInstitutionStations() != null ? response.getCreditorInstitutionStations().size() : 0;
        log.info("{} records will be processed for institution-station associations", size);

        List<AnagStationAnagInstitution> associations = new ArrayList<>();

        if (size > 0) {
            log.info("Loading all stations and institutions into memory for efficient lookup");
            Map<String, AnagStation> stationsByName = anagStationRepository.findAll()
                .stream()
                .collect(Collectors.toMap(AnagStation::getName, station -> station));

            Map<String, AnagInstitution> institutionsByCode = anagInstitutionRepository.findAll()
                .stream()
                .collect(Collectors.toMap(AnagInstitution::getFiscalCode, institution -> institution));

            log.info("Loaded {} stations and {} institutions into memory", stationsByName.size(), institutionsByCode.size());

            stationsByName.values().stream().limit(3).forEach(s ->
                log.debug("Sample station - Name: {}, ID: {}", s.getName(), s.getId())
            );
            institutionsByCode.values().stream().limit(3).forEach(i ->
                log.debug("Sample institution - FiscalCode: {}, ID: {}", i.getFiscalCode(), i.getId())
            );

            for (CreditorInstitutionStation cis : response.getCreditorInstitutionStations().values()) {
                AnagStation station = stationsByName.get(cis.getStationCode());
                AnagInstitution institution = institutionsByCode.get(cis.getCreditorInstitutionCode());
                if (station != null && institution != null) {
                    AnagStationAnagInstitution association = new AnagStationAnagInstitution();
                    association.setAnagStation(station);
                    association.setAnagInstitution(institution);
                    association.setAca(cis.getAca());
                    association.setStandin(cis.getStandin());
                    associations.add(association);
                } else {
                    log.warn("Station or Institution not found for codes: {} / {}", cis.getStationCode(), cis.getCreditorInstitutionCode());
                }
            }

            List<AnagStationAnagInstitution> activeAssociations = new ArrayList<>();
            for (AnagStationAnagInstitution assoc : associations) {
                if (Boolean.TRUE.equals(assoc.getAnagInstitution().getEnabled())) {
                    activeAssociations.add(assoc);
                }
            }

            log.info("After validation {} records will be saved", activeAssociations.size());

            activeAssociations.stream().limit(3).forEach(assoc ->
                log.debug("Sample association - Station: {} (ID: {}), Institution: {} (ID: {})",
                    assoc.getAnagStation().getName(), assoc.getAnagStation().getId(),
                    assoc.getAnagInstitution().getFiscalCode(), assoc.getAnagInstitution().getId())
            );

            StopWatch stopWatch = new StopWatch();
            stopWatch.start();

            log.info("Deleting all existing station-institution associations");
            anagStationAnagInstitutionRepository.deleteAllInBatch();
            anagStationAnagInstitutionRepository.flush();

            if (activeAssociations.isEmpty()) {
                log.warn("No active associations to insert");
            } else {
                String sql = "INSERT INTO anag_station_anag_institution (co_anag_station_id, co_anag_institution_id, aca, standin) VALUES (?, ?, ?, ?)";

                log.info("Preparing to batch insert {} associations", activeAssociations.size());

                try {
                    int[][] updateCounts = jdbcTemplate.batchUpdate(sql, activeAssociations, 100,
                        (ps, assoc) -> {
                            Long stationId = assoc.getAnagStation().getId();
                            Long institutionId = assoc.getAnagInstitution().getId();

                            if (stationId == null || institutionId == null) {
                                log.error("Null ID found - Station: {}, Institution: {}",
                                    assoc.getAnagStation().getName(),
                                    assoc.getAnagInstitution().getFiscalCode());
                                throw new IllegalStateException("Cannot insert association with null IDs");
                            }

                            ps.setLong(1, stationId);
                            ps.setLong(2, institutionId);
                            ps.setBoolean(3, Boolean.TRUE.equals(assoc.getAca()));
                            ps.setBoolean(4, Boolean.TRUE.equals(assoc.getStandin()));
                        });

                    int totalInserted = Arrays.stream(updateCounts).flatMapToInt(Arrays::stream).sum();
                    log.info("Batch inserted {} associations using native SQL (expected: {})", totalInserted, activeAssociations.size());
                } catch (Exception e) {
                    log.error("Error during batch insert: {}", e.getMessage(), e);
                    throw e;
                }
            }

            // Flush and clear to ensure count queries see the JDBC-inserted data
            entityManager.flush();
            entityManager.clear();
            log.debug("EntityManager flushed and cleared after batch insert");

            updateAllStationsAssociatedInstitutionsCount();
            anagPartnerService.updateAllPartnersInstitutionsCount();
            stopWatch.stop();

            log.info("Saved {} rows active institution-station associations to database into {} seconds",
                activeAssociations.size(), stopWatch.getTime(TimeUnit.SECONDS));
        }
    }

}
