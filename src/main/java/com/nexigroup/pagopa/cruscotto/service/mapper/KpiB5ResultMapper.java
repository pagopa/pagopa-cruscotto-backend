package com.nexigroup.pagopa.cruscotto.service.mapper;

import com.nexigroup.pagopa.cruscotto.domain.KpiB5Result;
import com.nexigroup.pagopa.cruscotto.service.dto.KpiB5ResultDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

/**
 * Mapper for the entity {@link KpiB5Result} and its DTO {@link KpiB5ResultDTO}.
 */
@Mapper(componentModel = "spring")
public interface KpiB5ResultMapper extends EntityMapper<KpiB5ResultDTO, KpiB5Result> {
    
    @Mapping(source = "instance.id", target = "instanceId")
    @Mapping(source = "instanceModule.id", target = "instanceModuleId")
    KpiB5ResultDTO toDto(KpiB5Result entity);
    
    @Mapping(target = "instance", ignore = true)
    @Mapping(target = "instanceModule", ignore = true)
    KpiB5Result toEntity(KpiB5ResultDTO dto);
}