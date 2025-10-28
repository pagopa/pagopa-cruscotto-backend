package com.nexigroup.pagopa.cruscotto.service.mapper;

import com.nexigroup.pagopa.cruscotto.domain.KpiAnalyticData;
import com.nexigroup.pagopa.cruscotto.service.dto.KpiAnalyticDataDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link KpiAnalyticData} and its DTO {@link KpiAnalyticDataDTO}.
 */
@Mapper(componentModel = "spring")
public interface KpiAnalyticDataMapper extends EntityMapper<KpiAnalyticDataDTO, KpiAnalyticData> {

    @Mapping(target = "id", source = "id")
    @Mapping(target = "moduleCode", source = "moduleCode")
    @Mapping(target = "instanceId", source = "instanceId")
    @Mapping(target = "instanceModuleId", source = "instanceModuleId")
    @Mapping(target = "kpiDetailResultId", source = "kpiDetailResultId")
    @Mapping(target = "analysisDate", source = "analysisDate")
    @Mapping(target = "dataDate", source = "dataDate")
    @Mapping(target = "analyticData", source = "analyticData")
    @Mapping(target = "createdBy", source = "createdBy")
    @Mapping(target = "createdDate", source = "createdDate")
    @Mapping(target = "lastModifiedBy", source = "lastModifiedBy")
    @Mapping(target = "lastModifiedDate", source = "lastModifiedDate")
    KpiAnalyticDataDTO toDto(KpiAnalyticData kpiAnalyticData);

    @Mapping(target = "id", source = "id")
    @Mapping(target = "moduleCode", source = "moduleCode")
    @Mapping(target = "instanceId", source = "instanceId")
    @Mapping(target = "instanceModuleId", source = "instanceModuleId")
    @Mapping(target = "kpiDetailResultId", source = "kpiDetailResultId")
    @Mapping(target = "analysisDate", source = "analysisDate")
    @Mapping(target = "dataDate", source = "dataDate")
    @Mapping(target = "analyticData", source = "analyticData")
    @Mapping(target = "createdBy", source = "createdBy")
    @Mapping(target = "createdDate", source = "createdDate")
    @Mapping(target = "lastModifiedBy", source = "lastModifiedBy")
    @Mapping(target = "lastModifiedDate", source = "lastModifiedDate")
    KpiAnalyticData toEntity(KpiAnalyticDataDTO kpiAnalyticDataDTO);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "createdDate", ignore = true)
    void partialUpdate(@MappingTarget KpiAnalyticData kpiAnalyticData, KpiAnalyticDataDTO kpiAnalyticDataDTO);

    default KpiAnalyticData fromId(Long id) {
        if (id == null) {
            return null;
        }
        KpiAnalyticData kpiAnalyticData = new KpiAnalyticData();
        kpiAnalyticData.setId(id);
        return kpiAnalyticData;
    }
}