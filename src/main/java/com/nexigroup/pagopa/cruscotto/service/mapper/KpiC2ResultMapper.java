package com.nexigroup.pagopa.cruscotto.service.mapper;

import com.nexigroup.pagopa.cruscotto.domain.KpiC2Result;
import com.nexigroup.pagopa.cruscotto.service.dto.KpiC2ResultDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

/**
 * Mapper for the entity {@link KpiC2Result} and its DTO {@link KpiC2ResultDTO}.
 */
@Mapper(componentModel = "spring")
public interface KpiC2ResultMapper extends EntityMapper<KpiC2ResultDTO, KpiC2Result> {

    @Mapping(source = "instance.id", target = "instanceId")
    @Mapping(source = "instanceModule.id", target = "instanceModuleId")
    KpiC2ResultDTO toDto(KpiC2Result entity);

    @Mapping(target = "instance", ignore = true)
    @Mapping(target = "instanceModule", ignore = true)
    KpiC2Result toEntity(KpiC2ResultDTO dto);
}
