package com.nexigroup.pagopa.cruscotto.repository.impl;

import com.nexigroup.pagopa.cruscotto.domain.KpiB2AnalyticData;
import com.nexigroup.pagopa.cruscotto.domain.QAnagStation;
import com.nexigroup.pagopa.cruscotto.domain.QKpiB2AnalyticData;
import com.nexigroup.pagopa.cruscotto.repository.KpiB2AnalyticDataRepositoryCustom;
import com.nexigroup.pagopa.cruscotto.service.dto.KpiB2AnalyticDataDTO;
import com.querydsl.core.types.Expression;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.JPQLQuery;
import java.util.List;
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;
import org.springframework.stereotype.Repository;

@Repository
public class KpiB2AnalyticDataRepositoryImpl extends QuerydslRepositorySupport implements KpiB2AnalyticDataRepositoryCustom {

    private final QKpiB2AnalyticData qKpiB2AnalyticData = QKpiB2AnalyticData.kpiB2AnalyticData;
    private final QAnagStation qAnagStation = QAnagStation.anagStation;

    public KpiB2AnalyticDataRepositoryImpl() {
        super(KpiB2AnalyticData.class);
    }

    @Override
    public List<KpiB2AnalyticDataDTO> findByDetailResultId(long detailResultId) {
        JPQLQuery<KpiB2AnalyticDataDTO> query = from(qKpiB2AnalyticData)
            .leftJoin(qAnagStation)
            .on(qKpiB2AnalyticData.station.id.eq(qAnagStation.id)) // Join KpiB2AnalyticData con AnagStation
            .where(qKpiB2AnalyticData.kpiB2DetailResult.id.eq(detailResultId)) // Filtro per detailResultId
            .select(buildProjectionFindByDetailResultId());
        return query.fetch();
    }

    /**
     * Costruisce la proiezione per KpiB2AnalyticDataDTO.
     *
     * @return La proiezione per KpiB2AnalyticDataDTO.
     */
    private Expression<KpiB2AnalyticDataDTO> buildProjectionFindByDetailResultId() {
        return Projections.bean(
            KpiB2AnalyticDataDTO.class,
            qKpiB2AnalyticData.id.as("id"),
            qKpiB2AnalyticData.instance.id.as("instanceId"),
            qKpiB2AnalyticData.instanceModule.id.as("instanceModuleId"),
            qKpiB2AnalyticData.analysisDate.as("analysisDate"),
            qKpiB2AnalyticData.station.id.as("stationId"),
            qKpiB2AnalyticData.method.as("method"),
            qKpiB2AnalyticData.evaluationDate.as("evaluationDate"),
            qKpiB2AnalyticData.totReq.as("totReq"),
            qKpiB2AnalyticData.reqOk.as("reqOk"),
            qKpiB2AnalyticData.reqTimeout.as("reqTimeout"),
            qKpiB2AnalyticData.avgTime.as("avgTime"),
            qKpiB2AnalyticData.kpiB2DetailResult.id.as("kpiB2DetailResultId"),
            qAnagStation.name.as("stationName")
        );
    }
}
