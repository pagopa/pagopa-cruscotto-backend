package com.nexigroup.pagopa.cruscotto.service.mapper;

import com.nexigroup.pagopa.cruscotto.domain.KpiC1Result;
import com.nexigroup.pagopa.cruscotto.service.dto.KpiC1ResultDTO;
import org.mapstruct.*;

/**
 * Mapper per la conversione tra KpiC1Result e KpiC1ResultDTO.
 * Conforme al contratto OpenAPI
 */
@Mapper(componentModel = "spring", uses = {})
public interface KpiC1ResultMapper extends EntityMapper<KpiC1ResultDTO, KpiC1Result> {

    @Mapping(target = "id", source = "id")
    @Mapping(target = "instanceId", source = "instance.id")
    @Mapping(target = "instanceModuleId", source = "instanceModule.id")
    @Mapping(target = "analysisDate", source = "referenceDate")
    @Mapping(target = "eligibilityThreshold", source = "configuredThreshold")
    @Mapping(target = "tolerance", ignore = true) // Sarà valorizzato dal service usando notificationTolerance della configurazione
    @Mapping(target = "evaluationType", constant = "MESE") // TODO: mappare dal campo corretto se disponibile
    @Mapping(target = "outcome", source = "outcome")
    KpiC1ResultDTO toDto(KpiC1Result kpiC1Result);

    @Mapping(target = "id", source = "id")
    @Mapping(target = "instance", ignore = true) // Will be set programmatically
    @Mapping(target = "instanceModule", ignore = true) // Will be set programmatically
    @Mapping(target = "referenceDate", source = "analysisDate")
    @Mapping(target = "outcome", source = "outcome")
    @Mapping(target = "configuredThreshold", source = "eligibilityThreshold")
    @Mapping(target = "compliant", ignore = true)
    @Mapping(target = "compliantInstitutions", ignore = true)
    @Mapping(target = "totalInstitutions", ignore = true)
    @Mapping(target = "compliancePercentage", ignore = true)
    @Mapping(target = "totalPositions", ignore = true)
    @Mapping(target = "sentMessages", ignore = true)
    @Mapping(target = "globalSendingPercentage", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "createdDate", ignore = true)
    @Mapping(target = "lastModifiedBy", ignore = true)
    @Mapping(target = "lastModifiedDate", ignore = true)
    KpiC1Result toEntity(KpiC1ResultDTO kpiC1ResultDTO);

    default KpiC1Result fromId(Long id) {
        if (id == null) {
            return null;
        }
        KpiC1Result kpiC1Result = new KpiC1Result();
        kpiC1Result.setId(id);
        return kpiC1Result;
    }
}