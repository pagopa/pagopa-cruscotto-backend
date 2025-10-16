package com.nexigroup.pagopa.cruscotto.service.mapper;

import com.nexigroup.pagopa.cruscotto.domain.PagopaApiLogDrilldown;
import com.nexigroup.pagopa.cruscotto.service.dto.PagopaAPILogDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

/**
 * Mapper for the entity {@link PagopaApiLogDrilldown} and its DTO {@link PagopaAPILogDTO}.
 */
@Mapper(componentModel = "spring")
public interface PagopaApiLogDrilldownMapper extends EntityMapper<PagopaAPILogDTO, PagopaApiLogDrilldown> {
    
    @Mapping(source = "partnerFiscalCode", target = "cfPartner")
    @Mapping(source = "dataDate", target = "date")
    @Mapping(source = "stationCode", target = "station")
    @Mapping(source = "fiscalCode", target = "cfEnte")
    @Mapping(source = "api", target = "api")
    @Mapping(source = "totalRequests", target = "totReq")
    @Mapping(source = "okRequests", target = "reqOk")
    @Mapping(source = "koRequests", target = "reqKo")
    PagopaAPILogDTO toDto(PagopaApiLogDrilldown entity);

    @Mapping(source = "cfPartner", target = "partnerFiscalCode")
    @Mapping(source = "date", target = "dataDate")
    @Mapping(source = "station", target = "stationCode")
    @Mapping(source = "cfEnte", target = "fiscalCode")
    @Mapping(source = "api", target = "api")
    @Mapping(source = "totReq", target = "totalRequests")
    @Mapping(source = "reqOk", target = "okRequests")
    @Mapping(source = "reqKo", target = "koRequests")
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "instance", ignore = true)
    @Mapping(target = "instanceModule", ignore = true)
    @Mapping(target = "station", ignore = true)
    @Mapping(target = "kpiB4AnalyticData", ignore = true)
    @Mapping(target = "analysisDate", ignore = true)
    PagopaApiLogDrilldown toEntity(PagopaAPILogDTO dto);
}