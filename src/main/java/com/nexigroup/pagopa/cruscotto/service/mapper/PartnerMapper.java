package com.nexigroup.pagopa.cruscotto.service.mapper;

import com.nexigroup.pagopa.cruscotto.domain.AnagPartner;
import com.nexigroup.pagopa.cruscotto.domain.Taxonomy;
import com.nexigroup.pagopa.cruscotto.service.dto.AnagPartnerDTO;
import com.nexigroup.pagopa.cruscotto.service.dto.TaxonomyDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

/**
 * Mapper for the entity {@link AnagPartner} and its DTO {@link AnagPartnerDTO}.
 */
@Mapper(componentModel = "spring", uses = { })
public interface PartnerMapper extends EntityMapper<AnagPartnerDTO, AnagPartner> {

    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "createdDate", ignore = true)
    @Mapping(target = "lastModifiedBy", ignore = true)
    @Mapping(target = "lastModifiedDate", ignore = true)
    @Mapping(target = "anagStations", ignore = true)
    @Mapping(target = "anagInstitutions", ignore = true)
    @Mapping(target = "anagPlannedShutdowns", ignore = true)
    AnagPartner toEntity(AnagPartnerDTO anagPartnerDTO);

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
