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
    @Mapping(target= "id", source = "partnerIdentification.id")
    @Mapping(target= "fiscalCode", source = "partnerIdentification.fiscalCode")
    @Mapping(target = "name", source = "partnerIdentification.name")
    AnagPartner toEntity(AnagPartnerDTO anagPartnerDTO);

    @Mapping(target = "lastAnalysisDate", ignore = true)
    @Mapping(target = "partnerIdentification.id", source = "id")
    @Mapping(target = "partnerIdentification.fiscalCode", source = "fiscalCode")
    @Mapping(target = "partnerIdentification.name", source = "name")
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
