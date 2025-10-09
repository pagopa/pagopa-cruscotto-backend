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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link KpiB1AnalyticDrillDown}.
 */
@Service
@Transactional
public class KpiB1AnalyticDrillDownServiceImpl implements KpiB1AnalyticDrillDownService {

    private static final Logger LOGGER = LoggerFactory.getLogger(KpiB1AnalyticDrillDownServiceImpl.class);

    private final KpiB1AnalyticDataRepository kpiB1AnalyticDataRepository;

    private final KpiB1AnalyticDrillDownRepository kpiB1AnalyticDrillDownRepository;

    private final QueryBuilder queryBuilder;

    public KpiB1AnalyticDrillDownServiceImpl(
        KpiB1AnalyticDataRepository kpiB1AnalyticDataRepository,
        KpiB1AnalyticDrillDownRepository kpiB1AnalyticDrillDownRepository,
        QueryBuilder queryBuilder
    ) {
        this.kpiB1AnalyticDataRepository = kpiB1AnalyticDataRepository;
        this.kpiB1AnalyticDrillDownRepository = kpiB1AnalyticDrillDownRepository;
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
        kpiB1AnalyticDrillDown.setEnte(kpiB1AnalyticDrillDownDTO.getEnte());
        kpiB1AnalyticDrillDown.setStazione(kpiB1AnalyticDrillDownDTO.getStazione());
        kpiB1AnalyticDrillDown.setData(kpiB1AnalyticDrillDownDTO.getData());
        kpiB1AnalyticDrillDown.setTotaleTransazioni(kpiB1AnalyticDrillDownDTO.getTotaleTransazioni());

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
            .orderBy(qKpiB1AnalyticDrillDown.data.asc(),
                     qKpiB1AnalyticDrillDown.ente.asc(),
                     qKpiB1AnalyticDrillDown.stazione.asc())
            .select(
                Projections.fields(
                    KpiB1AnalyticDrillDownDTO.class,
                    qKpiB1AnalyticDrillDown.id.as("id"),
                    qKpiB1AnalyticDrillDown.kpiB1AnalyticData.id.as("kpiB1AnalyticDataId"),
                    qKpiB1AnalyticDrillDown.ente.as("ente"),
                    qKpiB1AnalyticDrillDown.stazione.as("stazione"),
                    qKpiB1AnalyticDrillDown.data.as("data"),
                    qKpiB1AnalyticDrillDown.totaleTransazioni.as("totaleTransazioni")
                )
            );

        return query.fetch();
    }
}