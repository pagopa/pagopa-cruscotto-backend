package com.nexigroup.pagopa.cruscotto.repository.impl;

import com.nexigroup.pagopa.cruscotto.domain.QAnagStation;
import com.nexigroup.pagopa.cruscotto.domain.QKpiB2DetailResult;
import com.nexigroup.pagopa.cruscotto.repository.KpiB2DetailResultRepositoryCustom;
import com.nexigroup.pagopa.cruscotto.service.dto.KpiB2DetailResultDTO;
import com.querydsl.core.types.Expression;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.JPQLQuery;
import java.util.List;
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;
import org.springframework.stereotype.Repository;

@Repository
public class KpiB2DetailResultRepositoryImpl extends QuerydslRepositorySupport implements KpiB2DetailResultRepositoryCustom {

    private final QKpiB2DetailResult qKpiB2DetailResult = QKpiB2DetailResult.kpiB2DetailResult;
    private final QAnagStation qAnagStation = QAnagStation.anagStation;

    public KpiB2DetailResultRepositoryImpl() {
        super(QKpiB2DetailResult.class);
    }

    @Override
    public List<KpiB2DetailResultDTO> findByKpiB2ResultId(long kpiB2ResultId) {
        JPQLQuery<KpiB2DetailResultDTO> query = from(qKpiB2DetailResult)
            .leftJoin(qAnagStation)
            .on(qKpiB2DetailResult.station.id.eq(qAnagStation.id))
            .where(qKpiB2DetailResult.kpiB2Result.id.eq(kpiB2ResultId))
            .select(buildProjectionFindByKpiB2ResultId());

        return query.fetch();
    }

    /**
     * Costruisce la proiezione per KpiB2DetailResultDTO
     *
     * @return La proiezione per KpiB2DetailResultDTO.
     */
    private Expression<KpiB2DetailResultDTO> buildProjectionFindByKpiB2ResultId() {
        return Projections.bean(
            KpiB2DetailResultDTO.class,
            qKpiB2DetailResult.id.as("id"),
            qKpiB2DetailResult.instance.id.as("instanceId"),
            qKpiB2DetailResult.instanceModule.id.as("instanceModuleId"),
            qKpiB2DetailResult.analysisDate.as("analysisDate"),
            qKpiB2DetailResult.station.id.as("stationId"),
            qKpiB2DetailResult.method.as("method"),
            qKpiB2DetailResult.evaluationType.as("evaluationType"),
            qKpiB2DetailResult.evaluationStartDate.as("evaluationStartDate"),
            qKpiB2DetailResult.evaluationEndDate.as("evaluationEndDate"),
            qKpiB2DetailResult.totReq.as("totReq"),
            qKpiB2DetailResult.avgTime.as("avgTime"),
            qKpiB2DetailResult.overTimeLimit.as("overTimeLimit"),
            qKpiB2DetailResult.outcome.as("outcome"),
            qKpiB2DetailResult.kpiB2Result.id.as("kpiB2ResultId"),
            qAnagStation.name.as("stationName")
        );
    }
}
