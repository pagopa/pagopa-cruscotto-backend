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
    @Mapping(target = "analysisDate", source = "analysisEndDate")
    @Mapping(target = "evaluationType", source = "evaluationType")
    @Mapping(target = "outcome", source = "outcome")
    @Mapping(target = "data", source = "additionalMetrics")
    @Mapping(target = "createdBy", source = "createdBy")
    @Mapping(target = "createdDate", source = "createdDate")
    @Mapping(target = "lastModifiedBy", source = "lastModifiedBy")
    @Mapping(target = "lastModifiedDate", source = "lastModifiedDate")
    KpiResultDTO toDto(KpiResult kpiResult);

    @Mapping(target = "id", source = "id")
    @Mapping(target = "moduleCode", source = "moduleCode")
    @Mapping(target = "instanceId", source = "instanceId")
    @Mapping(target = "instanceModuleId", source = "instanceModuleId")
    @Mapping(target = "analysisEndDate", source = "analysisDate")
    @Mapping(target = "evaluationType", source = "evaluationType")
    @Mapping(target = "outcome", source = "outcome")
    @Mapping(target = "additionalMetrics", source = "data")
    @Mapping(target = "createdBy", source = "createdBy")
    @Mapping(target = "createdDate", source = "createdDate")
    @Mapping(target = "lastModifiedBy", source = "lastModifiedBy")
    @Mapping(target = "lastModifiedDate", source = "lastModifiedDate")
    @Mapping(target = "analysisStartDate", ignore = true)
    @Mapping(target = "partnerFiscalCode", ignore = true)
    @Mapping(target = "targetValue", ignore = true)
    @Mapping(target = "actualValue", ignore = true)
    @Mapping(target = "tolerance", ignore = true)
    @Mapping(target = "outcomePercentage", ignore = true)
    @Mapping(target = "calculationDate", ignore = true)
    @Mapping(target = "totalEntities", ignore = true)
    @Mapping(target = "compliantEntities", ignore = true)
    @Mapping(target = "nonCompliantEntities", ignore = true)
    KpiResult toEntity(KpiResultDTO kpiResultDTO);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "analysisEndDate", source = "analysisDate")
    @Mapping(target = "additionalMetrics", source = "data")
    @Mapping(target = "analysisStartDate", ignore = true)
    @Mapping(target = "partnerFiscalCode", ignore = true)
    @Mapping(target = "targetValue", ignore = true)
    @Mapping(target = "actualValue", ignore = true)
    @Mapping(target = "tolerance", ignore = true)
    @Mapping(target = "outcomePercentage", ignore = true)
    @Mapping(target = "calculationDate", ignore = true)
    @Mapping(target = "totalEntities", ignore = true)
    @Mapping(target = "compliantEntities", ignore = true)
    @Mapping(target = "nonCompliantEntities", ignore = true)
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