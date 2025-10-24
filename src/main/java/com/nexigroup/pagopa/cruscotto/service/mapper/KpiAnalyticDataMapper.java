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
    @Mapping(target = "stationCode", source = "stationCode")
    @Mapping(target = "data", source = "analyticData")
    @Mapping(target = "createdBy", source = "createdBy")
    @Mapping(target = "createdDate", source = "createdDate")
    @Mapping(target = "lastModifiedBy", source = "lastModifiedBy")
    @Mapping(target = "lastModifiedDate", source = "lastModifiedDate")
    KpiAnalyticDataDTO toDto(KpiAnalyticData kpiAnalyticData);

    @Mapping(target = "id", source = "id")
    @Mapping(target = "moduleCode", source = "moduleCode")
    @Mapping(target = "instanceId", source = "instanceId")
    @Mapping(target = "instanceModuleId", source = "instanceModuleId")
    @Mapping(target = "stationCode", source = "stationCode")
    @Mapping(target = "analyticData", source = "data")
    @Mapping(target = "createdBy", source = "createdBy")
    @Mapping(target = "createdDate", source = "createdDate")
    @Mapping(target = "lastModifiedBy", source = "lastModifiedBy")
    @Mapping(target = "lastModifiedDate", source = "lastModifiedDate")
    @Mapping(target = "stationName", ignore = true)
    @Mapping(target = "partnerFiscalCode", ignore = true)
    @Mapping(target = "analysisPeriodStart", ignore = true)
    @Mapping(target = "analysisPeriodEnd", ignore = true)
    KpiAnalyticData toEntity(KpiAnalyticDataDTO kpiAnalyticDataDTO);

    default KpiAnalyticData fromId(Long id) {
        if (id == null) {
            return null;
        }
        KpiAnalyticData kpiAnalyticData = new KpiAnalyticData();
        kpiAnalyticData.setId(id);
        return kpiAnalyticData;
    }
}