package com.nexigroup.pagopa.cruscotto.service.mapper;

import com.nexigroup.pagopa.cruscotto.domain.KpiResult;
import com.nexigroup.pagopa.cruscotto.service.dto.KpiResultDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link KpiResult} and its DTO {@link KpiResultDTO}.
 */
@Mapper(componentModel = "spring")
public interface KpiResultMapper extends EntityMapper<KpiResultDTO, KpiResult> {

    @Mapping(target = "id", source = "id")
    @Mapping(target = "moduleCode", source = "moduleCode")
    @Mapping(target = "instanceId", source = "instanceId")
    @Mapping(target = "instanceModuleId", source = "instanceModuleId")
    @Mapping(target = "analysisDate", source = "analysisDate")
    @Mapping(target = "outcome", source = "outcome")
    @Mapping(target = "additionalData", source = "additionalData")
    @Mapping(target = "createdBy", source = "createdBy")
    @Mapping(target = "createdDate", source = "createdDate")
    @Mapping(target = "lastModifiedBy", source = "lastModifiedBy")
    @Mapping(target = "lastModifiedDate", source = "lastModifiedDate")
    KpiResultDTO toDto(KpiResult kpiResult);

    @Mapping(target = "id", source = "id")
    @Mapping(target = "moduleCode", source = "moduleCode")
    @Mapping(target = "instanceId", source = "instanceId")
    @Mapping(target = "instanceModuleId", source = "instanceModuleId")
    @Mapping(target = "analysisDate", source = "analysisDate")
    @Mapping(target = "outcome", source = "outcome")
    @Mapping(target = "additionalData", source = "additionalData")
    @Mapping(target = "createdBy", source = "createdBy")
    @Mapping(target = "createdDate", source = "createdDate")
    @Mapping(target = "lastModifiedBy", source = "lastModifiedBy")
    @Mapping(target = "lastModifiedDate", source = "lastModifiedDate")
    KpiResult toEntity(KpiResultDTO kpiResultDTO);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "analysisDate", source = "analysisDate")
    @Mapping(target = "additionalData", source = "additionalData")
    void partialUpdate(@MappingTarget KpiResult entity, KpiResultDTO dto);

    default KpiResult fromId(Long id) {
        if (id == null) {
            return null;
        }
        KpiResult kpiResult = new KpiResult();
        kpiResult.setId(id);
        return kpiResult;
    }
}