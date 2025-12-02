package com.nexigroup.pagopa.cruscotto.service.impl;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;

import com.nexigroup.pagopa.cruscotto.domain.QAnagStationAnagInstitution;
import com.querydsl.jpa.JPAExpressions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.nexigroup.pagopa.cruscotto.domain.AnagPartner;
import com.nexigroup.pagopa.cruscotto.domain.QAnagPartner;
import com.nexigroup.pagopa.cruscotto.domain.QAnagStation;
import com.nexigroup.pagopa.cruscotto.domain.enumeration.PartnerStatus;
import com.nexigroup.pagopa.cruscotto.repository.AnagPartnerRepository;
import com.nexigroup.pagopa.cruscotto.service.AnagPartnerService;
import com.nexigroup.pagopa.cruscotto.service.dto.AnagPartnerDTO;
import com.nexigroup.pagopa.cruscotto.service.dto.PartnerIdentificationDTO;
import com.nexigroup.pagopa.cruscotto.service.mapper.AnagPartnerMapper;
import com.nexigroup.pagopa.cruscotto.service.qdsl.QdslUtility;
import com.nexigroup.pagopa.cruscotto.service.qdsl.QueryBuilder;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.Tuple;
import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.JPQLQuery;
import com.querydsl.jpa.impl.JPAUpdateClause;

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
    public Page<PartnerIdentificationDTO> findAll(String fiscalCode, String nameFilter, Pageable pageable) {
        log.debug("Request to get all AnagPartner");

        JPQLQuery<PartnerIdentificationDTO> jpql = queryBuilder.<PartnerIdentificationDTO>createQuery().from(QAnagPartner.anagPartner);
        BooleanBuilder predicate = new BooleanBuilder();
        if (nameFilter != null && !nameFilter.isEmpty()) {
            predicate.or(QAnagPartner.anagPartner.name.likeIgnoreCase("%" + nameFilter + "%"));
        }

        if (fiscalCode != null && !fiscalCode.isEmpty()) {
            predicate.or(QAnagPartner.anagPartner.fiscalCode.likeIgnoreCase("%" + fiscalCode + "%"));
        }
        
        jpql.where(predicate);

        long size = jpql.fetchCount();

        JPQLQuery<PartnerIdentificationDTO> jpqlSelected = jpql.select(
            Projections.fields(
            		PartnerIdentificationDTO.class,
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

        List<PartnerIdentificationDTO> list = jpqlSelected.fetch();

        return new PageImpl<>(list, pageable, size);
    }

    @Override
    public void saveAll(List<AnagPartnerDTO> partners) {
        log.debug("Request to save all partners");
        AtomicInteger i = new AtomicInteger(0);

        partners.forEach(partnerDTO -> {
            AnagPartner partnerExample = new AnagPartner();
            partnerExample.setFiscalCode(partnerDTO.getPartnerIdentification().getFiscalCode());
            partnerExample.setCreatedDate(null);
            partnerExample.setLastModifiedDate(null);
            partnerExample.setQualified(null);

            AnagPartner anagPartner = anagPartnerRepository.findOne(Example.of(partnerExample)).orElse(new AnagPartner());
            anagPartner.setInstitutionsCount(0L);
            anagPartner.setName(partnerDTO.getPartnerIdentification().getName());
            anagPartner.setFiscalCode(partnerDTO.getPartnerIdentification().getFiscalCode());

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

    /**
     * Updates the qualification status of a partner identified by the given ID.
     *
     * @param id the unique identifier of the partner whose qualification status is to be updated
     * @param qualified the new qualification status to set for the partner; true if the partner is qualified, false otherwise
     */
    @Override
    public void changePartnerQualified(Long id, boolean qualified) {
        log.debug("Request to update Partner {}", id);
        JPAUpdateClause jpql = queryBuilder.updateQuery(QAnagPartner.anagPartner);

        jpql.set(QAnagPartner.anagPartner.qualified, qualified).where(QAnagPartner.anagPartner.id.eq(id)).execute();
    }

    @Override
    public void updateLastAnalysisDate(Long partnerId, java.time.Instant lastAnalysisDate) {
        anagPartnerRepository.findById(partnerId).ifPresent(partner -> {
            ZoneId zoneId = ZoneId.systemDefault();
        	LocalDate localDate = Optional.ofNullable(lastAnalysisDate)
                                              .map(instant -> instant.atZone(zoneId).toLocalDate())
                                              .orElse(null);
        	partner.setLastAnalysisDate(localDate);
            anagPartnerRepository.save(partner);
        });
    }

    @Override
    public void updateAnalysisPeriodDates(Long partnerId, java.time.LocalDate startDate, java.time.LocalDate endDate) {
        anagPartnerRepository.findById(partnerId).ifPresent(partner -> {
            partner.setAnalysisPeriodStartDate(startDate);
            partner.setAnalysisPeriodEndDate(endDate);
            anagPartnerRepository.save(partner);
        });
    }

    @Override
    public void updateStationsCount(Long partnerId, Long stationsCount) {
        anagPartnerRepository.findById(partnerId).ifPresent(partner -> {
            partner.setStationsCount(stationsCount);
            anagPartnerRepository.save(partner);
        });
    }

	@Override
	public Page<AnagPartnerDTO> findAll(Long partnerId, Boolean analyzed, Boolean qualified, String lastAnalysisDate,
			String analysisPeriodStartDate, String analysisPeriodEndDate, Boolean showNotActive, Pageable pageable) {
		log.debug("findAll START");
        JPQLQuery<?> jpql = queryBuilder.createQuery()
            .from(QAnagPartner.anagPartner);
//            .leftJoin(QInstance.instance).on(QInstance.instance.partner.eq(QAnagPartner.anagPartner));

        BooleanBuilder predicate = new BooleanBuilder();

        if (partnerId != null) {
            predicate.and(QAnagPartner.anagPartner.id.eq(partnerId));
        }
        if (analyzed != null ) {
        	boolean analyzedBool= analyzed.booleanValue();
        	if (analyzedBool) {
        		predicate.and(QAnagPartner.anagPartner.lastAnalysisDate.isNotNull());
        	}
        	else
        	{
        		predicate.and(QAnagPartner.anagPartner.lastAnalysisDate.isNull());
        	}
        }
        if (qualified != null) {
            predicate.and(QAnagPartner.anagPartner.qualified.eq(qualified));
        }

        if (lastAnalysisDate != null && !lastAnalysisDate.isEmpty()) {
            predicate.and(QAnagPartner.anagPartner.lastAnalysisDate.stringValue().eq(lastAnalysisDate));
        }
        if (analysisPeriodStartDate != null && !analysisPeriodStartDate.isEmpty()) {
            predicate.and(QAnagPartner.anagPartner.analysisPeriodStartDate.stringValue().goe(analysisPeriodStartDate));
        }
        if (analysisPeriodEndDate != null && !analysisPeriodEndDate.isEmpty()) {
            predicate.and(QAnagPartner.anagPartner.analysisPeriodEndDate.stringValue().loe(analysisPeriodEndDate));
        }
        if (showNotActive == null || !showNotActive) {
        	predicate.and(QAnagPartner.anagPartner.status.stringValue().eq(PartnerStatus.ATTIVO.name()));
        }

        jpql.where(predicate);

        long total = jpql.fetchCount();

        JPQLQuery<AnagPartnerDTO> selected = jpql.select(
            Projections.fields(
            		AnagPartnerDTO.class,
            		Projections.fields(
            				PartnerIdentificationDTO.class,
                            QAnagPartner.anagPartner.id.as("id"),
                            QAnagPartner.anagPartner.fiscalCode.as("fiscalCode"),
                            QAnagPartner.anagPartner.name.as("name")
                        ).as("partnerIdentification"),
            		
                    QAnagPartner.anagPartner.status.as("status"),
                    QAnagPartner.anagPartner.qualified.as("qualified"),
                    QAnagPartner.anagPartner.deactivationDate.as("deactivationDate"),
                    QAnagPartner.anagPartner.lastAnalysisDate.as("lastAnalysisDate"),
                    QAnagPartner.anagPartner.analysisPeriodStartDate.as("analysisPeriodStartDate"),
                    QAnagPartner.anagPartner.analysisPeriodEndDate.as("analysisPeriodEndDate"),
                    QAnagPartner.anagPartner.stationsCount.as("stationsCount"),
                    QAnagPartner.anagPartner.institutionsCount.as("associatedInstitutes")
//                    
  
            )
        );

        selected.offset(pageable.getOffset());
        selected.limit(pageable.getPageSize());

        pageable.getSortOr(Sort.by(Sort.Direction.ASC, "name")).forEach(order -> {
            selected.orderBy(
                new OrderSpecifier<>(
                    order.isAscending() ? Order.ASC : Order.DESC,
                    Expressions.stringPath(order.getProperty()),
                    QdslUtility.toQueryDslNullHandling(order.getNullHandling())
                )
            );
        });

        List<AnagPartnerDTO> result = selected.fetch();
        log.debug("findAll END");
        return new PageImpl<>(result, pageable, total);
	}
    
    @Override
    public void updateInstitutionsCount(Long partnerId, Long institutionsCount) {
        anagPartnerRepository.findById(partnerId).ifPresent(partner -> {
            partner.setInstitutionsCount(institutionsCount);
            anagPartnerRepository.save(partner);
        });
    }

    @Override
    public void updateAllPartnersInstitutionsCount() {
        log.debug("updateAllPartnersInstitutionsCount START");
        
        // Query per ottenere tutti i partner con le loro stazioni e relative istituzioni associate
        // Per ogni partner, somma il numero di istituzioni associate a tutte le sue stazioni
        JPQLQuery<Tuple> query = queryBuilder.<Tuple>createQuery()
            .select(
                QAnagPartner.anagPartner.id,
                QAnagStationAnagInstitution.anagStationAnagInstitution.anagInstitution.id.countDistinct()
            )
            .from(QAnagStationAnagInstitution.anagStationAnagInstitution)
            .innerJoin(QAnagStationAnagInstitution.anagStationAnagInstitution.anagStation, QAnagStation.anagStation) // solo stazioni collegate
            .innerJoin(QAnagStation.anagStation.anagPartner, QAnagPartner.anagPartner) // solo partner con stazioni
            .groupBy(QAnagPartner.anagPartner.id);

        List<Tuple> results = query.fetch();
        
        // Aggiorna il count per ogni partner
        results.forEach(result -> {
            Long partnerId = result.get(QAnagPartner.anagPartner.id);
            Long institutionsCount = result.get(QAnagStationAnagInstitution.anagStationAnagInstitution.anagInstitution.id.countDistinct());

            try {
                updateInstitutionsCount(partnerId, institutionsCount);
                log.debug("Updated partner {} with institutions count: {}", partnerId, institutionsCount);
            } catch (Exception e) {
                log.error("Failed to update institutions count for partner {}: {}", partnerId, e.getMessage());
            }
        });
        
        log.debug("updateAllPartnersInstitutionsCount END");
    }
    
    @Override
    public Optional<AnagPartnerDTO> findOneByFiscalCode(String fiscalCode) {
        return anagPartnerRepository.findOneByFiscalCode(fiscalCode)
            .map(anagPartnerMapper::toDto);
    }
}
