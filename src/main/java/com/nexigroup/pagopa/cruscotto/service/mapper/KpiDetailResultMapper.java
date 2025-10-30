package com.nexigroup.pagopa.cruscotto.service.mapper;

import com.nexigroup.pagopa.cruscotto.domain.KpiDetailResult;
import com.nexigroup.pagopa.cruscotto.service.dto.KpiDetailResultDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link KpiDetailResult} and its DTO {@link KpiDetailResultDTO}.
 */
@Mapper(componentModel = "spring")
public interface KpiDetailResultMapper extends EntityMapper<KpiDetailResultDTO, KpiDetailResult> {

    @Mapping(target = "id", source = "id")
    @Mapping(target = "moduleCode", source = "moduleCode")
    @Mapping(target = "instanceId", source = "instanceId")
    @Mapping(target = "instanceModuleId", source = "instanceModuleId")
    @Mapping(target = "analysisDate", source = "analysisDate")
    @Mapping(target = "outcome", source = "outcome")
    @Mapping(target = "kpiResultId", source = "kpiResultId")
    @Mapping(target = "additionalData", source = "additionalData")
    @Mapping(target = "createdBy", source = "createdBy")
    @Mapping(target = "createdDate", source = "createdDate")
    @Mapping(target = "lastModifiedBy", source = "lastModifiedBy")
    @Mapping(target = "lastModifiedDate", source = "lastModifiedDate")
    KpiDetailResultDTO toDto(KpiDetailResult kpiDetailResult);

    @Mapping(target = "id", source = "id")
    @Mapping(target = "moduleCode", source = "moduleCode")
    @Mapping(target = "instanceId", source = "instanceId")
    @Mapping(target = "instanceModuleId", source = "instanceModuleId")
    @Mapping(target = "analysisDate", source = "analysisDate")
    @Mapping(target = "outcome", source = "outcome")
    @Mapping(target = "kpiResultId", source = "kpiResultId")
    @Mapping(target = "additionalData", source = "additionalData")
    @Mapping(target = "createdBy", source = "createdBy")
    @Mapping(target = "createdDate", source = "createdDate")
    @Mapping(target = "lastModifiedBy", source = "lastModifiedBy")
    @Mapping(target = "lastModifiedDate", source = "lastModifiedDate")
    KpiDetailResult toEntity(KpiDetailResultDTO kpiDetailResultDTO);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "createdDate", ignore = true)
    KpiDetailResult partialUpdate(KpiDetailResultDTO kpiDetailResultDTO, @MappingTarget KpiDetailResult kpiDetailResult);

    default KpiDetailResult fromId(Long id) {
        if (id == null) {
            return null;
        }
        KpiDetailResult kpiDetailResult = new KpiDetailResult();
        kpiDetailResult.setId(id);
        return kpiDetailResult;
    }
}