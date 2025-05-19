package com.nexigroup.pagopa.cruscotto.service.mapper;

import com.nexigroup.pagopa.cruscotto.domain.AnagStation;
import com.nexigroup.pagopa.cruscotto.service.dto.AnagStationDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

/**
 * Mapper for the entity {@link AnagStation} and its DTO {@link AnagStationDTO}.
 */
@Mapper(componentModel = "spring", uses = { })
public interface AnagStationMapper extends EntityMapper<AnagStationDTO, AnagStation> {

    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "createdDate", ignore = true)
    @Mapping(target = "lastModifiedBy", ignore = true)
    @Mapping(target = "lastModifiedDate", ignore = true)
    @Mapping(target = "anagInstitutions", ignore = true)
    @Mapping(target = "anagPlannedShutdowns", ignore = true)
    AnagStation toEntity(AnagStationDTO anagStationDTO);

    AnagStationDTO toDto(AnagStation anagStation);

    default AnagStation fromId(Long id) {
        if (id == null) {
            return null;
        }
        AnagStation anagStation = new AnagStation();
        anagStation.setId(id);
        return anagStation;
    }
}
