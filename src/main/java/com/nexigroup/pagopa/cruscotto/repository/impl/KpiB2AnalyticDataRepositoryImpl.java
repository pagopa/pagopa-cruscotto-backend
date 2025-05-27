package com.nexigroup.pagopa.cruscotto.repository.impl;

import com.nexigroup.pagopa.cruscotto.domain.KpiB2AnalyticData;
import com.nexigroup.pagopa.cruscotto.domain.QAnagStation;
import com.nexigroup.pagopa.cruscotto.domain.QKpiB2AnalyticData;
import com.nexigroup.pagopa.cruscotto.repository.KpiB2AnalyticDataRepositoryCustom;
import com.nexigroup.pagopa.cruscotto.service.dto.KpiB2AnalyticDataDTO;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.JPQLQuery;
import java.util.List;
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;
import org.springframework.stereotype.Repository;

@Repository
public class KpiB2AnalyticDataRepositoryImpl extends QuerydslRepositorySupport implements KpiB2AnalyticDataRepositoryCustom {

    private final QKpiB2AnalyticData qKpiB2AnalyticData = QKpiB2AnalyticData.kpiB2AnalyticData;

    public KpiB2AnalyticDataRepositoryImpl() {
        super(KpiB2AnalyticData.class);
    }

    @Override
    public List<KpiB2AnalyticDataDTO> findByDetailResultId(long detailResultId) {
        QKpiB2AnalyticData kpiB2AnalyticData = QKpiB2AnalyticData.kpiB2AnalyticData; // Alias per KpiB2AnalyticData
        QAnagStation anagStation = QAnagStation.anagStation; // Alias per AnagStation

        JPQLQuery<KpiB2AnalyticDataDTO> query = from(kpiB2AnalyticData)
            .leftJoin(anagStation)
            .on(kpiB2AnalyticData.station.id.eq(anagStation.id)) // Join tra KpiB2AnalyticData e AnagStation
            .where(kpiB2AnalyticData.kpiB2DetailResult.id.eq(detailResultId))
            .select(
                Projections.bean(
                    KpiB2AnalyticDataDTO.class,
                    kpiB2AnalyticData.id.as("id"),
                    kpiB2AnalyticData.instance.id.as("instanceId"),
                    kpiB2AnalyticData.instanceModule.id.as("instanceModuleId"),
                    kpiB2AnalyticData.analysisDate.as("analysisDate"),
                    kpiB2AnalyticData.station.id.as("stationId"),
                    kpiB2AnalyticData.method.as("method"),
                    kpiB2AnalyticData.evaluationDate.as("evaluationDate"),
                    kpiB2AnalyticData.totReq.as("totReq"),
                    kpiB2AnalyticData.reqOk.as("reqOk"),
                    kpiB2AnalyticData.reqTimeout.as("reqTimeout"),
                    kpiB2AnalyticData.avgTime.as("avgTime"),
                    kpiB2AnalyticData.kpiB2DetailResult.id.as("kpiB2DetailResultId"),
                    anagStation.name.as("stationName")
                )
            );

        return query.fetch();
    }
}
