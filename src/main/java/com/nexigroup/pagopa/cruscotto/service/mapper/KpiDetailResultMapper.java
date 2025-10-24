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
    @Mapping(target = "stationCode", source = "stationCode")
    @Mapping(target = "outcome", source = "outcome")
    @Mapping(target = "data", source = "additionalData")
    @Mapping(target = "createdBy", source = "createdBy")
    @Mapping(target = "createdDate", source = "createdDate")
    @Mapping(target = "lastModifiedBy", source = "lastModifiedBy")
    @Mapping(target = "lastModifiedDate", source = "lastModifiedDate")
    KpiDetailResultDTO toDto(KpiDetailResult kpiDetailResult);

    @Mapping(target = "id", source = "id")
    @Mapping(target = "moduleCode", source = "moduleCode")
    @Mapping(target = "instanceId", source = "instanceId")
    @Mapping(target = "instanceModuleId", source = "instanceModuleId")
    @Mapping(target = "stationCode", source = "stationCode")
    @Mapping(target = "outcome", source = "outcome")
    @Mapping(target = "additionalData", source = "data")
    @Mapping(target = "createdBy", source = "createdBy")
    @Mapping(target = "createdDate", source = "createdDate")
    @Mapping(target = "lastModifiedBy", source = "lastModifiedBy")
    @Mapping(target = "lastModifiedDate", source = "lastModifiedDate")
    @Mapping(target = "stationName", ignore = true)
    @Mapping(target = "partnerFiscalCode", ignore = true)
    KpiDetailResult toEntity(KpiDetailResultDTO kpiDetailResultDTO);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "createdDate", ignore = true)
    @Mapping(target = "stationName", ignore = true)
    @Mapping(target = "partnerFiscalCode", ignore = true)
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