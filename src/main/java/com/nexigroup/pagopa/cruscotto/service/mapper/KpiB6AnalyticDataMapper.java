package com.nexigroup.pagopa.cruscotto.service.mapper;

import com.nexigroup.pagopa.cruscotto.domain.KpiB6AnalyticData;
import com.nexigroup.pagopa.cruscotto.service.dto.KpiB6AnalyticDataDTO;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

/**
 * Mapper for the entity {@link KpiB6AnalyticData} and its DTO {@link KpiB6AnalyticDataDTO}.
 */
@Mapper(componentModel = "spring")
public interface KpiB6AnalyticDataMapper extends EntityMapper<KpiB6AnalyticDataDTO, KpiB6AnalyticData> {
    
    @Mapping(target = "instanceId", source = "instance.id")
    @Mapping(target = "instanceModuleId", source = "instanceModule.id")
    @Mapping(target = "kpiB6DetailResultId", source = "kpiB6DetailResult.id")
    KpiB6AnalyticDataDTO toDto(KpiB6AnalyticData kpiB6AnalyticData);

    @Mapping(target = "instance", ignore = true)
    @Mapping(target = "instanceModule", ignore = true)
    @Mapping(target = "kpiB6DetailResult", ignore = true)
    KpiB6AnalyticData toEntity(KpiB6AnalyticDataDTO kpiB6AnalyticDataDTO);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "instance", ignore = true)
    @Mapping(target = "instanceModule", ignore = true)
    @Mapping(target = "kpiB6DetailResult", ignore = true)
    void partialUpdate(@MappingTarget KpiB6AnalyticData entity, KpiB6AnalyticDataDTO dto);

    default KpiB6AnalyticData fromId(Long id) {
        if (id == null) {
            return null;
        }
        KpiB6AnalyticData kpiB6AnalyticData = new KpiB6AnalyticData();
        kpiB6AnalyticData.setId(id);
        return kpiB6AnalyticData;
    }
}