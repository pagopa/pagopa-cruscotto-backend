package com.nexigroup.pagopa.cruscotto.service.impl;

import java.util.List;

import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.nexigroup.pagopa.cruscotto.domain.AnagInstitution;
import com.nexigroup.pagopa.cruscotto.domain.QAnagInstitution;
import com.nexigroup.pagopa.cruscotto.domain.QAnagPartner;
import com.nexigroup.pagopa.cruscotto.domain.QAnagStation;
import com.nexigroup.pagopa.cruscotto.domain.QAnagStationAnagInstitution;
import com.nexigroup.pagopa.cruscotto.repository.AnagInstitutionRepository;
import com.nexigroup.pagopa.cruscotto.job.cache.CreditorInstitution;
import com.nexigroup.pagopa.cruscotto.job.cache.CreditorInstitutionsResponse;
import com.nexigroup.pagopa.cruscotto.job.client.PagoPaCacheClient;
import com.nexigroup.pagopa.cruscotto.service.AnagInstitutionService;
import com.nexigroup.pagopa.cruscotto.service.validation.ValidationGroups;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import com.nexigroup.pagopa.cruscotto.service.dto.AnagInstitutionDTO;
import com.nexigroup.pagopa.cruscotto.service.dto.InstitutionIdentificationDTO;
import com.nexigroup.pagopa.cruscotto.service.filter.AnagInstitutionFilter;
import com.nexigroup.pagopa.cruscotto.service.filter.InstitutionFilter;
import com.nexigroup.pagopa.cruscotto.service.qdsl.QdslUtility;
import com.nexigroup.pagopa.cruscotto.service.qdsl.QueryBuilder;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.JPQLQuery;
import org.apache.commons.lang3.time.StopWatch;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Set;
import java.util.concurrent.TimeUnit;

@Service
public class AnagInstitutionServiceImpl implements AnagInstitutionService {

    private static final String REQUEST_GET_ALL_INSTITUTIONS = "Request to get all Institutions by filter: {}";
    private static final String FISCAL_CODE = "fiscalCode";

	private final Logger log = LoggerFactory.getLogger(AnagInstitutionServiceImpl.class);

    @Autowired
    private AnagInstitutionRepository anagInstitutionRepository;

    @Autowired
    private QueryBuilder queryBuilder;

    @Autowired
    private PagoPaCacheClient pagoPaCacheClient;

    @Override
    public AnagInstitution findByInstitutionCode(String institutionCode) {
    	return anagInstitutionRepository.findByFiscalCode(institutionCode);
    }

    @Override
    public void saveAll(java.util.List<CreditorInstitution> creditorInstitutions) {
        // Load all existing institutions once to avoid N+1 queries
        java.util.Map<String, AnagInstitution> existingByFiscalCode = anagInstitutionRepository.findAll()
            .stream()
            .collect(java.util.stream.Collectors.toMap(AnagInstitution::getFiscalCode, inst -> inst));

        java.util.List<AnagInstitution> toSave = new java.util.ArrayList<>();

        for (CreditorInstitution ci : creditorInstitutions) {
            // Get existing or create new
            AnagInstitution anagInstitution = existingByFiscalCode.get(ci.getCreditorInstitutionCode());
            boolean isNew = (anagInstitution == null);

            if (isNew) {
                anagInstitution = new AnagInstitution();
            }

            // Set fields - JPA will detect if they changed and issue UPDATE only if needed
            String newName = ci.getBusinessName();
            Boolean newEnabled = ci.getEnabled() != null ? ci.getEnabled() : true;

            // Only add to save list if it's new OR if values actually changed
            boolean hasChanges = isNew ||
                !java.util.Objects.equals(anagInstitution.getName(), newName) ||
                !java.util.Objects.equals(anagInstitution.getEnabled(), newEnabled);

            if (hasChanges || isNew) {
                anagInstitution.setFiscalCode(ci.getCreditorInstitutionCode());
                anagInstitution.setName(newName);
                anagInstitution.setEnabled(newEnabled);
                toSave.add(anagInstitution);
            }
        }

        // Batch save only entities that are new or changed
        if (!toSave.isEmpty()) {
            anagInstitutionRepository.saveAll(toSave);
            anagInstitutionRepository.flush();
        }
    }

    @Override
   	public Page<InstitutionIdentificationDTO> findAll(InstitutionFilter filter, Pageable pageable) {

   		log.debug(REQUEST_GET_ALL_INSTITUTIONS, filter);

           BooleanBuilder builder = new BooleanBuilder();

           if (filter.getFiscalCode() != null) {
               builder.or(QAnagInstitution.anagInstitution.fiscalCode.likeIgnoreCase("%"+filter.getFiscalCode()+"%"));
               //predicate.or(QAnagPartner.anagPartner.name.likeIgnoreCase("%" + nameFilter + "%"));
           }

           if (filter.getName() != null) {
           	builder.or(QAnagInstitution.anagInstitution.name.likeIgnoreCase("%"+filter.getName()+"%"));
           }

           JPQLQuery<AnagInstitution> jpql = queryBuilder.<AnagInstitution>createQuery().from(QAnagInstitution.anagInstitution).where(builder);

           long size = jpql.fetchCount();

           JPQLQuery<InstitutionIdentificationDTO> jpqlSelected = jpql.select(
               Projections.fields(
               		InstitutionIdentificationDTO.class,
               	QAnagInstitution.anagInstitution.id.as("id"),
               	QAnagInstitution.anagInstitution.fiscalCode.as(FISCAL_CODE),
               	QAnagInstitution.anagInstitution.name.as("name")
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

           List<InstitutionIdentificationDTO> list = jpqlSelected.fetch();

           return new PageImpl<>(list, pageable, size);

   	}
    @Override
    public List<AnagInstitutionDTO> findAllNoPaging(AnagInstitutionFilter filter) {
        log.debug(REQUEST_GET_ALL_INSTITUTIONS, filter);

        BooleanBuilder builder = createBuilder(filter);

        JPQLQuery<AnagInstitution> jpql = queryBuilder.<AnagInstitution>createQuery()
            .from(QAnagInstitution.anagInstitution)
            .leftJoin(QAnagStationAnagInstitution.anagStationAnagInstitution).on(QAnagStationAnagInstitution.anagStationAnagInstitution.anagInstitution.eq(QAnagInstitution.anagInstitution))
            .leftJoin(QAnagStation.anagStation).on(QAnagStationAnagInstitution.anagStationAnagInstitution.anagStation.eq(QAnagStation.anagStation))
            .leftJoin(QAnagPartner.anagPartner).on(QAnagStation.anagStation.anagPartner.eq(QAnagPartner.anagPartner))
            .where(builder);


        JPQLQuery<AnagInstitutionDTO> jpqlSelected = jpql.select(
            Projections.fields(
                AnagInstitutionDTO.class,
                Projections.fields(InstitutionIdentificationDTO.class,
                    QAnagInstitution.anagInstitution.id.as("id"),
                    QAnagInstitution.anagInstitution.name.as("name"),
                    QAnagInstitution.anagInstitution.fiscalCode.as(FISCAL_CODE)
                ).as("institutionIdentification"),
                QAnagStation.anagStation.name.as("stationName"),
                QAnagPartner.anagPartner.name.as("partnerName"),
                QAnagPartner.anagPartner.fiscalCode.as("partnerFiscalCode"),
                QAnagInstitution.anagInstitution.enabled.as("enabled"),
                QAnagStationAnagInstitution.anagStationAnagInstitution.aca.as("aca"),
                QAnagStationAnagInstitution.anagStationAnagInstitution.standin.as("standIn")
            )
        );

        List<AnagInstitutionDTO> list = jpqlSelected.fetch();
        return list;
    }

    private static @NotNull BooleanBuilder createBuilder(AnagInstitutionFilter filter) {
        BooleanBuilder builder = new BooleanBuilder();

        if (filter.getInstitutionId() != null) {
            builder.and(QAnagInstitution.anagInstitution.id.eq(filter.getInstitutionId()));
        }

        if (filter.getPartnerId() != null) {
            builder.and(QAnagStation.anagStation.anagPartner.id.eq(filter.getPartnerId()));
        }

        if (filter.getStationId() != null) {
            builder.and(QAnagStation.anagStation.id.eq(filter.getStationId()));
        }

        if (filter.getShowNotEnabled() == null ||  (filter.getShowNotEnabled() != null && !filter.getShowNotEnabled().booleanValue())) {
            builder.and(QAnagInstitution.anagInstitution.enabled.eq(true));
        }
        return builder;
    }

    @Override
   	public Page<AnagInstitutionDTO> findAll(AnagInstitutionFilter filter, Pageable pageable) {
   		log.debug(REQUEST_GET_ALL_INSTITUTIONS, filter);

        BooleanBuilder builder = createBuilder(filter);

        JPQLQuery<AnagInstitution> jpql = queryBuilder.<AnagInstitution>createQuery()
               .from(QAnagInstitution.anagInstitution)
               .leftJoin(QAnagStationAnagInstitution.anagStationAnagInstitution).on(QAnagStationAnagInstitution.anagStationAnagInstitution.anagInstitution.eq(QAnagInstitution.anagInstitution))
               .leftJoin(QAnagStation.anagStation).on(QAnagStationAnagInstitution.anagStationAnagInstitution.anagStation.eq(QAnagStation.anagStation))
               .leftJoin(QAnagPartner.anagPartner).on(QAnagStation.anagStation.anagPartner.eq(QAnagPartner.anagPartner))
               .where(builder);

           long size = jpql.fetchCount();

           JPQLQuery<AnagInstitutionDTO> jpqlSelected = jpql.select(
               Projections.fields(
               		AnagInstitutionDTO.class,
               		Projections.fields(InstitutionIdentificationDTO.class,
               				QAnagInstitution.anagInstitution.id.as("id"),
               				QAnagInstitution.anagInstitution.name.as("name"),
               				QAnagInstitution.anagInstitution.fiscalCode.as(FISCAL_CODE)
               				).as("institutionIdentification"),
               		QAnagStation.anagStation.name.as("stationName"),
               		QAnagPartner.anagPartner.name.as("partnerName"),
               		QAnagPartner.anagPartner.fiscalCode.as("partnerFiscalCode"),
               		QAnagInstitution.anagInstitution.enabled.as("enabled"),
               		QAnagStationAnagInstitution.anagStationAnagInstitution.aca.as("aca"),
               		QAnagStationAnagInstitution.anagStationAnagInstitution.standin.as("standIn")
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

           List<AnagInstitutionDTO> list = jpqlSelected.fetch();

           return new PageImpl<>(list, pageable, size);
   	}

    @Override
    @Transactional
    public void loadFromPagoPA() {
        log.info("Call PagoPA to get creditorInstitutions");
        CreditorInstitutionsResponse response = pagoPaCacheClient.creditorInstitutions();
        int size = response.getCreditorInstitutions() != null ? response.getCreditorInstitutions().size() : 0;
        log.info("{} records will be processed for institutions", size);

        List<CreditorInstitution> institutions = new ArrayList<>();

        try (ValidatorFactory factory = Validation.buildDefaultValidatorFactory()) {
            Validator validator = factory.getValidator();
            if (size > 0) {
                for (CreditorInstitution ci : response.getCreditorInstitutions().values()) {
                    Set<ConstraintViolation<CreditorInstitution>> violations = validator.validate(
                        ci,
                        ValidationGroups.RegistryJob.class
                    );
                    if (violations.isEmpty()) {
                        institutions.add(ci);
                    } else {
                        log.error("Invalid partner {}", ci);
                        violations.forEach(violation -> log.error("{}: {}", violation.getPropertyPath(), violation.getMessage()));
                    }
                }
            }

            log.info("After validation {} records will be saved", institutions.size());

            StopWatch stopWatch = new StopWatch();
            stopWatch.start();

            saveAll(institutions);

            stopWatch.stop();

            log.info("Saved {} rows institution to database into {} seconds", institutions.size(), stopWatch.getTime(TimeUnit.SECONDS));
        }
    }
}
