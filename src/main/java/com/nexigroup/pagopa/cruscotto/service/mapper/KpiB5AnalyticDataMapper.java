package com.nexigroup.pagopa.cruscotto.service.mapper;

import com.nexigroup.pagopa.cruscotto.domain.KpiB5AnalyticData;
import com.nexigroup.pagopa.cruscotto.service.dto.KpiB5AnalyticDataDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

/**
 * Mapper for the entity {@link KpiB5AnalyticData} and its DTO {@link KpiB5AnalyticDataDTO}.
 */
@Mapper(componentModel = "spring")
public interface KpiB5AnalyticDataMapper extends EntityMapper<KpiB5AnalyticDataDTO, KpiB5AnalyticData> {
    
    @Mapping(source = "kpiB5DetailResult.id", target = "kpiB5DetailResultId")
    @Mapping(source = "instance.id", target = "instanceId")
    @Mapping(source = "instanceModule.id", target = "instanceModuleId")
    KpiB5AnalyticDataDTO toDto(KpiB5AnalyticData entity);
    
    @Mapping(target = "instance", ignore = true)
    @Mapping(target = "instanceModule", ignore = true)
    @Mapping(target = "kpiB5DetailResult", ignore = true)
    KpiB5AnalyticData toEntity(KpiB5AnalyticDataDTO dto);
}