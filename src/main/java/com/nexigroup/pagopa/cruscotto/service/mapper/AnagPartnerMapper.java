package com.nexigroup.pagopa.cruscotto.service.mapper;

import com.nexigroup.pagopa.cruscotto.domain.AnagPartner;
import com.nexigroup.pagopa.cruscotto.service.dto.AnagPartnerDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

/**
 * Mapper for the entity {@link AnagPartner} and its DTO {@link AnagPartnerDTO}.
 */
@Mapper(componentModel = "spring", uses = { })
public interface AnagPartnerMapper extends EntityMapper<AnagPartnerDTO, AnagPartner> {

    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "createdDate", ignore = true)
    @Mapping(target = "lastModifiedBy", ignore = true)
    @Mapping(target = "lastModifiedDate", ignore = true)
    @Mapping(target = "anagStations", ignore = true)
    @Mapping(target = "anagInstitutions", ignore = true)
    @Mapping(target = "anagPlannedShutdowns", ignore = true)
    @Mapping(target = "lastAnalysisDate", ignore = true)
    @Mapping(source = "fiscalCode", target = "partnerIdentification.fiscalCode")
    @Mapping(source = "name", target = "partnerIdentification.name")
    AnagPartner toEntity(AnagPartnerDTO anagPartnerDTO);

    @Mapping(target = "lastAnalysisDate", ignore = true)
    @Mapping(source = "partnerIdentification.fiscalCode", target = "fiscalCode")
    @Mapping(source = "partnerIdentification.name", target = "name")
    AnagPartnerDTO toDto(AnagPartner anagPartner);

    default AnagPartner fromId(Long id) {
        if (id == null) {
            return null;
        }
        AnagPartner anagPartner = new AnagPartner();
        anagPartner.setId(id);
        return anagPartner;
    }
}
