package com.nexigroup.pagopa.cruscotto.service.impl;

import java.util.List;

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
import com.nexigroup.pagopa.cruscotto.service.AnagInstitutionService;
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

@Service
public class AnagInstitutionServiceImpl implements AnagInstitutionService {

	private final Logger log = LoggerFactory.getLogger(AnagInstitutionServiceImpl.class);
			
    @Autowired
    private AnagInstitutionRepository anagInstitutionRepository;
    
    @Autowired
    private QueryBuilder queryBuilder;

    @Override
    public AnagInstitution findByInstitutionCode(String institutionCode) {
        return anagInstitutionRepository.findByFiscalCode(institutionCode);
    }

    @Override
    public void saveAll(java.util.List<CreditorInstitution> creditorInstitutions) {
        java.util.concurrent.atomic.AtomicInteger i = new java.util.concurrent.atomic.AtomicInteger(0);
        java.util.List<AnagInstitution> toSave = new java.util.ArrayList<>();
        for (CreditorInstitution ci : creditorInstitutions) {
            AnagInstitution example = new AnagInstitution();
            example.setFiscalCode(ci.getCreditorInstitutionCode());
            example.setName(null);
            example.setEnabled(null);
            AnagInstitution anagInstitution = anagInstitutionRepository.findOne(org.springframework.data.domain.Example.of(example)).orElse(new AnagInstitution());
            anagInstitution.setFiscalCode(ci.getCreditorInstitutionCode());
            anagInstitution.setName(ci.getBusinessName());
            anagInstitution.setEnabled(ci.getEnabled() != null ? ci.getEnabled() : true);
            toSave.add(anagInstitution);
            if (i.getAndIncrement() % 50 == 0) {
                anagInstitutionRepository.flush();
            }
        }
        anagInstitutionRepository.saveAll(toSave);
    }
    
    @Override
   	public Page<InstitutionIdentificationDTO> findAll(InstitutionFilter filter, Pageable pageable) {

   		log.debug("Request to get all Institutions by filter: {}", filter);
   		
           BooleanBuilder builder = new BooleanBuilder();

           if (filter.getFiscalCode() != null) {
               builder.and(QAnagInstitution.anagInstitution.fiscalCode.eq(filter.getFiscalCode()));
           }
           
           if (filter.getName() != null) {
           	builder.and(QAnagInstitution.anagInstitution.name.eq(filter.getName()));
           }

           JPQLQuery<AnagInstitution> jpql = queryBuilder.<AnagInstitution>createQuery().from(QAnagInstitution.anagInstitution).where(builder);

           long size = jpql.fetchCount();

           JPQLQuery<InstitutionIdentificationDTO> jpqlSelected = jpql.select(
               Projections.fields(
               		InstitutionIdentificationDTO.class,
               	QAnagInstitution.anagInstitution.id.as("id"),
               	QAnagInstitution.anagInstitution.fiscalCode.as("fiscalCode"),
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
   	public Page<AnagInstitutionDTO> findAll(AnagInstitutionFilter filter, Pageable pageable) {
   		log.debug("Request to get all Institutions by filter: {}", filter);
   		
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
               				QAnagInstitution.anagInstitution.fiscalCode.as("fiscalCode")
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
}
