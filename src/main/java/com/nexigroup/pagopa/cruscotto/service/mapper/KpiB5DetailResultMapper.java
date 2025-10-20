package com.nexigroup.pagopa.cruscotto.service.mapper;

import com.nexigroup.pagopa.cruscotto.domain.KpiB5DetailResult;
import com.nexigroup.pagopa.cruscotto.service.dto.KpiB5DetailResultDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

/**
 * Mapper for the entity {@link KpiB5DetailResult} and its DTO {@link KpiB5DetailResultDTO}.
 */
@Mapper(componentModel = "spring")
public interface KpiB5DetailResultMapper extends EntityMapper<KpiB5DetailResultDTO, KpiB5DetailResult> {
    
    @Mapping(source = "kpiB5Result.id", target = "kpiB5ResultId")
    @Mapping(source = "instance.id", target = "instanceId")
    @Mapping(source = "instanceModule.id", target = "instanceModuleId")
    KpiB5DetailResultDTO toDto(KpiB5DetailResult entity);
    
    @Mapping(target = "instance", ignore = true)
    @Mapping(target = "instanceModule", ignore = true)
    @Mapping(target = "kpiB5Result", ignore = true)
    KpiB5DetailResult toEntity(KpiB5DetailResultDTO dto);
}