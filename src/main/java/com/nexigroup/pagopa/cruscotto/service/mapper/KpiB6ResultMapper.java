package com.nexigroup.pagopa.cruscotto.service.mapper;

import com.nexigroup.pagopa.cruscotto.domain.KpiB6Result;
import com.nexigroup.pagopa.cruscotto.service.dto.KpiB6ResultDTO;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

/**
 * Mapper for the entity {@link KpiB6Result} and its DTO {@link KpiB6ResultDTO}.
 */
@Mapper(componentModel = "spring")
public interface KpiB6ResultMapper extends EntityMapper<KpiB6ResultDTO, KpiB6Result> {
    
    @Mapping(target = "instanceId", source = "instance.id")
    @Mapping(target = "instanceModuleId", source = "instanceModule.id")
    KpiB6ResultDTO toDto(KpiB6Result kpiB6Result);

    @Mapping(target = "instance", ignore = true)
    @Mapping(target = "instanceModule", ignore = true)
    KpiB6Result toEntity(KpiB6ResultDTO kpiB6ResultDTO);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "instance", ignore = true)
    @Mapping(target = "instanceModule", ignore = true)
    void partialUpdate(@MappingTarget KpiB6Result entity, KpiB6ResultDTO dto);

    default KpiB6Result fromId(Long id) {
        if (id == null) {
            return null;
        }
        KpiB6Result kpiB6Result = new KpiB6Result();
        kpiB6Result.setId(id);
        return kpiB6Result;
    }
}