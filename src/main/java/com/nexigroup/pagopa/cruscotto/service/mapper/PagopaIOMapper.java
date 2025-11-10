package com.nexigroup.pagopa.cruscotto.service.mapper;

import com.nexigroup.pagopa.cruscotto.domain.PagopaIO;
import com.nexigroup.pagopa.cruscotto.service.dto.PagoPaIODTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

/**
 * Mapper for the entity {@link PagopaIO} and its DTO {@link PagoPaIODTO}.
 */
@Mapper(componentModel = "spring")
public interface PagopaIOMapper extends EntityMapper<PagoPaIODTO, PagopaIO> {

    @Mapping(target = "ente", source = "cfInstitution")
    @Mapping(target = "data", source = "date")
    @Mapping(target = "numeroPosizioni", source = "positionNumber")
    @Mapping(target = "numeroMessaggi", source = "messageNumber")
    PagoPaIODTO toDto(PagopaIO pagopaIO);

    @Mapping(target = "cfInstitution", source = "ente")
    @Mapping(target = "date", source = "data")
    @Mapping(target = "positionNumber", source = "numeroPosizioni")
    @Mapping(target = "messageNumber", source = "numeroMessaggi")
    PagopaIO toEntity(PagoPaIODTO PagoPaIODTO);

    default PagopaIO fromId(Long id) {
        if (id == null) {
            return null;
        }
        PagopaIO pagopaIO = new PagopaIO();
        pagopaIO.setId(id);
        return pagopaIO;
    }
}
