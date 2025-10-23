package com.nexigroup.pagopa.cruscotto.service.mapper;

import com.nexigroup.pagopa.cruscotto.domain.KpiB6DetailResult;
import com.nexigroup.pagopa.cruscotto.service.dto.KpiB6DetailResultDTO;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

/**
 * Mapper for the entity {@link KpiB6DetailResult} and its DTO {@link KpiB6DetailResultDTO}.
 */
@Mapper(componentModel = "spring")
public interface KpiB6DetailResultMapper extends EntityMapper<KpiB6DetailResultDTO, KpiB6DetailResult> {
    
    @Mapping(target = "instanceId", source = "instance.id")
    @Mapping(target = "instanceModuleId", source = "instanceModule.id")
    @Mapping(target = "kpiB6ResultId", source = "kpiB6Result.id")
    KpiB6DetailResultDTO toDto(KpiB6DetailResult kpiB6DetailResult);

    @Mapping(target = "instance", ignore = true)
    @Mapping(target = "instanceModule", ignore = true)
    @Mapping(target = "kpiB6Result", ignore = true)
    KpiB6DetailResult toEntity(KpiB6DetailResultDTO kpiB6DetailResultDTO);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "instance", ignore = true)
    @Mapping(target = "instanceModule", ignore = true)
    @Mapping(target = "kpiB6Result", ignore = true)
    void partialUpdate(@MappingTarget KpiB6DetailResult entity, KpiB6DetailResultDTO dto);

    default KpiB6DetailResult fromId(Long id) {
        if (id == null) {
            return null;
        }
        KpiB6DetailResult kpiB6DetailResult = new KpiB6DetailResult();
        kpiB6DetailResult.setId(id);
        return kpiB6DetailResult;
    }
}