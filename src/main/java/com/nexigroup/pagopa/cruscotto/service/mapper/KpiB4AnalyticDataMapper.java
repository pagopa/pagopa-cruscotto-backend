package com.nexigroup.pagopa.cruscotto.service.mapper;

import com.nexigroup.pagopa.cruscotto.domain.KpiB4AnalyticData;
import com.nexigroup.pagopa.cruscotto.service.dto.KpiB4AnalyticDataDTO;
import org.mapstruct.Mapper;

/**
 * Mapper for the entity {@link KpiB4AnalyticData} and its DTO {@link KpiB4AnalyticDataDTO}.
 */
@Mapper(componentModel = "spring")
public interface KpiB4AnalyticDataMapper extends EntityMapper<KpiB4AnalyticDataDTO, KpiB4AnalyticData> {}