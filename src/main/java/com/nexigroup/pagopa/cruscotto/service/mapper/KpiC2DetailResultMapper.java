package com.nexigroup.pagopa.cruscotto.service.mapper;

import com.nexigroup.pagopa.cruscotto.domain.KpiC2DetailResult;
import com.nexigroup.pagopa.cruscotto.service.dto.KpiC2DetailResultDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

/**
 * Mapper for the entity {@link KpiC2DetailResult} and its DTO {@link KpiC2DetailResultDTO}.
 */
@Mapper(componentModel = "spring")
public interface KpiC2DetailResultMapper extends EntityMapper<KpiC2DetailResultDTO, KpiC2DetailResult> {

    @Mapping(source = "kpiC2Result.id", target = "kpiC2ResultId")
    @Mapping(source = "instance.id", target = "instanceId")
    @Mapping(source = "instanceModule.id", target = "instanceModuleId")
    KpiC2DetailResultDTO toDto(KpiC2DetailResult entity);

    @Mapping(target = "instance", ignore = true)
    @Mapping(target = "instanceModule", ignore = true)
    @Mapping(target = "kpiC2Result", ignore = true)
    KpiC2DetailResult toEntity(KpiC2DetailResultDTO dto);
}
