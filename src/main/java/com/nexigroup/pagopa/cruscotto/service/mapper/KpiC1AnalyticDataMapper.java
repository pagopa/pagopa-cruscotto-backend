package com.nexigroup.pagopa.cruscotto.service.mapper;

import com.nexigroup.pagopa.cruscotto.domain.KpiC1AnalyticData;
import com.nexigroup.pagopa.cruscotto.service.dto.KpiC1AnalyticDataDTO;
import org.mapstruct.*;

/**
 * Mapper per la conversione tra KpiC1AnalyticData e KpiC1AnalyticDataDTO.
 * Conforme allo schema OpenAPI.
 */
@Mapper(componentModel = "spring", uses = {})
public interface KpiC1AnalyticDataMapper extends EntityMapper<KpiC1AnalyticDataDTO, KpiC1AnalyticData> {

    @Mapping(target = "id", source = "id")
    @Mapping(target = "instanceId", source = "instance.id")
    @Mapping(target = "instanceModuleId", source = "instanceModule.id")
    @Mapping(target = "stationId", source = "cfInstitution")
    @Mapping(target = "kpiC1DetailResultId", ignore = true) // valorizzato post-mapping in service
    @Mapping(target = "analysisDate", source = "referenceDate")
    @Mapping(target = "dataDate", source = "data")
    @Mapping(target = "institutionCount", ignore = true) // calcolato dinamicamente
    @Mapping(target = "koInstitutionCount", ignore = true) // calcolato dinamicamente
    @Mapping(target = "meetsTolerance", source = "meetsTolerance")
    KpiC1AnalyticDataDTO toDto(KpiC1AnalyticData kpiC1AnalyticData);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "instance", ignore = true) // Will be set programmatically
    @Mapping(target = "instanceModule", ignore = true) // Will be set programmatically
    @Mapping(target = "referenceDate", source = "analysisDate")
    @Mapping(target = "data", source = "dataDate")
    @Mapping(target = "cfInstitution", source = "stationId")
    @Mapping(target = "positionNumber", constant = "0L") // Not available in contract
    @Mapping(target = "messageNumber", constant = "0L") // Not available in contract
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "createdDate", ignore = true)
    @Mapping(target = "lastModifiedBy", ignore = true)
    @Mapping(target = "lastModifiedDate", ignore = true)
    KpiC1AnalyticData toEntity(KpiC1AnalyticDataDTO kpiC1AnalyticDataDTO);

    default KpiC1AnalyticData fromId(Long id) {
        if (id == null) {
            return null;
        }
        KpiC1AnalyticData kpiC1AnalyticData = new KpiC1AnalyticData();
        kpiC1AnalyticData.setId(id);
        return kpiC1AnalyticData;
    }
}