package com.nexigroup.pagopa.cruscotto.service.mapper;

import com.nexigroup.pagopa.cruscotto.domain.KpiC2AnalyticDrillDown;
import com.nexigroup.pagopa.cruscotto.domain.KpiC2DetailResult;
import com.nexigroup.pagopa.cruscotto.service.dto.KpiC2AnalyticDrillDownDTO;
import com.nexigroup.pagopa.cruscotto.service.dto.KpiC2DetailResultDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

/**
 * Mapper for the entity {@link KpiC2DetailResult} and its DTO {@link KpiC2DetailResultDTO}.
 */
@Mapper(componentModel = "spring")
public interface KpiC2DrillDownMapper extends EntityMapper<KpiC2AnalyticDrillDownDTO, KpiC2AnalyticDrillDown> {


    KpiC2AnalyticDrillDownDTO toDto(KpiC2AnalyticDrillDown entity);

    KpiC2AnalyticDrillDown toEntity(KpiC2AnalyticDrillDownDTO dto);
}
