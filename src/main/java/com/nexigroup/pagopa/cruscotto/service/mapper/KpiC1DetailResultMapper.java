package com.nexigroup.pagopa.cruscotto.service.mapper;

import com.nexigroup.pagopa.cruscotto.domain.KpiC1DetailResult;
import com.nexigroup.pagopa.cruscotto.service.dto.KpiC1DetailResultDTO;
import org.mapstruct.*;

/**
 * Mapper per la conversione tra KpiC1DetailResult e KpiC1DetailResultDTO.
 * Conforme al contratto OpenAPI
 */
@Mapper(componentModel = "spring", uses = {})
public interface KpiC1DetailResultMapper extends EntityMapper<KpiC1DetailResultDTO, KpiC1DetailResult> {

    @Mapping(target = "id", source = "id")
    @Mapping(target = "instanceId", source = "instance.id")
    @Mapping(target = "instanceModuleId", source = "instanceModule.id")
    @Mapping(target = "analysisDate", source = "referenceDate")
    @Mapping(target = "evaluationType", source = "evaluationType")
    @Mapping(target = "evaluationStartDate", source = "evaluationStartDate")
    @Mapping(target = "evaluationEndDate", source = "evaluationEndDate")
    @Mapping(target = "totalInstitutions", source = "totalInstitutions")
    @Mapping(target = "okTotalInstitutions", source = "compliantInstitutions")
    // percentageOkInstitutions must represent (compliantInstitutions / totalInstitutions) * 100 per spec
    @Mapping(target = "percentageOkInstitutions", source = "percentageCompliantInstitutions")
    @Mapping(target = "outcome", source = "outcome")
    @Mapping(target = "kpiC1ResultId", source = "kpiC1Result.id")
    KpiC1DetailResultDTO toDto(KpiC1DetailResult kpiC1DetailResult);

    @Mapping(target = "id", source = "id")
    @Mapping(target = "instance", ignore = true) // Will be set programmatically
    @Mapping(target = "instanceModule", ignore = true) // Will be set programmatically
    @Mapping(target = "referenceDate", source = "analysisDate")
    @Mapping(target = "evaluationType", source = "evaluationType")
    @Mapping(target = "evaluationStartDate", source = "evaluationStartDate")
    @Mapping(target = "evaluationEndDate", source = "evaluationEndDate")
    @Mapping(target = "outcome", source = "outcome")
    @Mapping(target = "kpiC1Result", ignore = true) // Will be set programmatically
    @Mapping(target = "cfInstitution", ignore = true)
    @Mapping(target = "compliant", ignore = true)
    @Mapping(target = "totalPositions", ignore = true)
    @Mapping(target = "sentMessages", ignore = true)
    @Mapping(target = "sendingPercentage", ignore = true)
    @Mapping(target = "configuredThreshold", ignore = true)
    @Mapping(target = "totalInstitutions", ignore = true)
    @Mapping(target = "compliantInstitutions", ignore = true)
    @Mapping(target = "percentageCompliantInstitutions", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "createdDate", ignore = true)
    @Mapping(target = "lastModifiedBy", ignore = true)
    @Mapping(target = "lastModifiedDate", ignore = true)
    KpiC1DetailResult toEntity(KpiC1DetailResultDTO kpiC1DetailResultDTO);

    default KpiC1DetailResult fromId(Long id) {
        if (id == null) {
            return null;
        }
        KpiC1DetailResult kpiC1DetailResult = new KpiC1DetailResult();
        kpiC1DetailResult.setId(id);
        return kpiC1DetailResult;
    }
}