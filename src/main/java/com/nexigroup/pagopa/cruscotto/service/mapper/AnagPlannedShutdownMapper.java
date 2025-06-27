package com.nexigroup.pagopa.cruscotto.service.mapper;

import com.nexigroup.pagopa.cruscotto.domain.AnagPlannedShutdown;
import com.nexigroup.pagopa.cruscotto.service.dto.AnagPlannedShutdownDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

/**
 * Mapper for the entity {@link AnagPlannedShutdown} and its DTO {@link AnagPlannedShutdownDTO}.
 */
@Mapper(componentModel = "spring", uses = { AnagPartnerMapper.class, AnagStationMapper.class })
public interface AnagPlannedShutdownMapper  extends EntityMapper<AnagPlannedShutdownDTO, AnagPlannedShutdown> {

    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "createdDate", ignore = true)
    @Mapping(target = "lastModifiedBy", ignore = true)
    @Mapping(target = "lastModifiedDate", ignore = true)
    @Mapping(source = "partnerId", target = "anagPartner")
    @Mapping(source = "stationId", target = "anagStation")
    AnagPlannedShutdown toEntity(AnagPlannedShutdownDTO anagPlannedShutdownDTO);


    @Mapping(source = "anagPartner.id", target = "partnerId")
    @Mapping(source = "anagPartner.name", target = "partnerName")
    @Mapping(source = "anagPartner.fiscalCode", target = "partnerFiscalCode")
    @Mapping(source = "anagStation.id", target = "stationId")
    @Mapping(source = "anagStation.name", target = "stationName")
    AnagPlannedShutdownDTO toDto(AnagPlannedShutdown anagPlannedShutdown);

    default AnagPlannedShutdown fromId(Long id) {
        if (id == null) {
            return null;
        }
        AnagPlannedShutdown anagPlannedShutdown = new AnagPlannedShutdown();
        anagPlannedShutdown.setId(id);
        return anagPlannedShutdown;
    }

}
