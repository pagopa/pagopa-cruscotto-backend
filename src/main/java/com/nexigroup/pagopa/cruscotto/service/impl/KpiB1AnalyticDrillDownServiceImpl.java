package com.nexigroup.pagopa.cruscotto.service.impl;

import com.nexigroup.pagopa.cruscotto.domain.KpiB1AnalyticData;
import com.nexigroup.pagopa.cruscotto.domain.KpiB1AnalyticDrillDown;
import com.nexigroup.pagopa.cruscotto.domain.QKpiB1AnalyticDrillDown;
import com.nexigroup.pagopa.cruscotto.repository.KpiB1AnalyticDataRepository;
import com.nexigroup.pagopa.cruscotto.repository.KpiB1AnalyticDrillDownRepository;
import com.nexigroup.pagopa.cruscotto.service.KpiB1AnalyticDrillDownService;
import com.nexigroup.pagopa.cruscotto.service.dto.KpiB1AnalyticDrillDownDTO;
import com.nexigroup.pagopa.cruscotto.service.qdsl.QueryBuilder;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.JPQLQuery;

import java.util.List;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link KpiB1AnalyticDrillDown}.
 */
@Service
@Transactional
public class KpiB1AnalyticDrillDownServiceImpl implements KpiB1AnalyticDrillDownService {

    private final KpiB1AnalyticDrillDownRepository kpiB1AnalyticDrillDownRepository;
    private final KpiB1AnalyticDataRepository kpiB1AnalyticDataRepository;
    private final QueryBuilder queryBuilder;


    public KpiB1AnalyticDrillDownServiceImpl(
        KpiB1AnalyticDrillDownRepository kpiB1AnalyticDrillDownRepository,
        KpiB1AnalyticDataRepository kpiB1AnalyticDataRepository,
        QueryBuilder queryBuilder
    ) {
        this.kpiB1AnalyticDrillDownRepository = kpiB1AnalyticDrillDownRepository;
        this.kpiB1AnalyticDataRepository = kpiB1AnalyticDataRepository;
        this.queryBuilder = queryBuilder;
    }

    /**
     * Save all kpiB1AnalyticDrillDown.
     *
     * @param kpiB1AnalyticDrillDownDTOS the entities to save.
     */
    @Override
    public void saveAll(List<KpiB1AnalyticDrillDownDTO> kpiB1AnalyticDrillDownDTOS) {
        for (KpiB1AnalyticDrillDownDTO kpiB1AnalyticDrillDownDTO : kpiB1AnalyticDrillDownDTOS) {
            KpiB1AnalyticData kpiB1AnalyticData = kpiB1AnalyticDataRepository
                .findById(kpiB1AnalyticDrillDownDTO.getKpiB1AnalyticDataId())
                .orElseThrow(() -> new IllegalArgumentException("KpiB1AnalyticData not found"));

            KpiB1AnalyticDrillDown kpiB1AnalyticDrillDown = getKpiB1AnalyticDrillDown(
                kpiB1AnalyticDrillDownDTO,
                kpiB1AnalyticData
            );

            kpiB1AnalyticDrillDownRepository.save(kpiB1AnalyticDrillDown);
        }
    }

    private static @NotNull KpiB1AnalyticDrillDown getKpiB1AnalyticDrillDown(
        KpiB1AnalyticDrillDownDTO kpiB1AnalyticDrillDownDTO,
        KpiB1AnalyticData kpiB1AnalyticData
    ) {
        KpiB1AnalyticDrillDown kpiB1AnalyticDrillDown = new KpiB1AnalyticDrillDown();
        kpiB1AnalyticDrillDown.setKpiB1AnalyticData(kpiB1AnalyticData);
        kpiB1AnalyticDrillDown.setDataDate(kpiB1AnalyticDrillDownDTO.getDataDate());
        kpiB1AnalyticDrillDown.setPartnerFiscalCode(kpiB1AnalyticDrillDownDTO.getPartnerFiscalCode());
        kpiB1AnalyticDrillDown.setInstitutionFiscalCode(kpiB1AnalyticDrillDownDTO.getInstitutionFiscalCode());
        kpiB1AnalyticDrillDown.setTransactionCount(kpiB1AnalyticDrillDownDTO.getTransactionCount());
        kpiB1AnalyticDrillDown.setStationCode(kpiB1AnalyticDrillDownDTO.getStationCode());

        return kpiB1AnalyticDrillDown;
    }

    @Override
    public int deleteByKpiB1AnalyticDataIds(List<Long> analyticDataIds) {
        if (analyticDataIds == null || analyticDataIds.isEmpty()) {
            return 0;
        }
        return kpiB1AnalyticDrillDownRepository.deleteByKpiB1AnalyticDataIds(analyticDataIds);
    }

    @Override
    public List<KpiB1AnalyticDrillDownDTO> findByAnalyticDataId(Long analyticDataId) {
        final QKpiB1AnalyticDrillDown qKpiB1AnalyticDrillDown = QKpiB1AnalyticDrillDown.kpiB1AnalyticDrillDown;

        JPQLQuery<KpiB1AnalyticDrillDownDTO> query = queryBuilder
            .createQuery()
            .from(qKpiB1AnalyticDrillDown)
            .where(qKpiB1AnalyticDrillDown.kpiB1AnalyticData.id.eq(analyticDataId))
            .orderBy(qKpiB1AnalyticDrillDown.dataDate.asc(),
                     qKpiB1AnalyticDrillDown.institutionFiscalCode.asc(),
                     qKpiB1AnalyticDrillDown.stationCode.asc())
            .select(
                Projections.fields(
                    KpiB1AnalyticDrillDownDTO.class,
                    qKpiB1AnalyticDrillDown.id.as("id"),
                    qKpiB1AnalyticDrillDown.kpiB1AnalyticData.id.as("kpiB1AnalyticDataId"),
                    qKpiB1AnalyticDrillDown.institutionFiscalCode.as("institutionFiscalCode"),
                    qKpiB1AnalyticDrillDown.stationCode.as("stationCode"),
                    qKpiB1AnalyticDrillDown.dataDate.as("dataDate"),
                    qKpiB1AnalyticDrillDown.transactionCount.as("transactionCount")
                )
            );

        return query.fetch();
    }
}