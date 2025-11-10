package com.nexigroup.pagopa.cruscotto.service.mapper;

import com.nexigroup.pagopa.cruscotto.domain.KpiC2AnalyticData;
import com.nexigroup.pagopa.cruscotto.service.dto.KpiC2AnalyticDataDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

/**
 * Mapper for the entity {@link KpiC2AnalyticData} and its DTO {@link KpiC2AnalyticDataDTO}.
 */
@Mapper(componentModel = "spring")
public interface KpiC2AnalyticDataMapper extends EntityMapper<KpiC2AnalyticDataDTO, KpiC2AnalyticData> {

    @Mapping(source = "kpiC2DetailResult.id", target = "kpiC2DetailResultId")
    @Mapping(source = "instance.id", target = "instanceId")
    @Mapping(source = "instanceModule.id", target = "instanceModuleId")
    KpiC2AnalyticDataDTO toDto(KpiC2AnalyticData entity);

    @Mapping(target = "instance", ignore = true)
    @Mapping(target = "instanceModule", ignore = true)
    @Mapping(target = "kpiC2DetailResult", ignore = true)
    KpiC2AnalyticData toEntity(KpiC2AnalyticDataDTO dto);
}
