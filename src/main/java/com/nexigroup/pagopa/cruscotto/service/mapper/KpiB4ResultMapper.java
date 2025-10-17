package com.nexigroup.pagopa.cruscotto.service.mapper;

import com.nexigroup.pagopa.cruscotto.domain.KpiB4Result;
import com.nexigroup.pagopa.cruscotto.service.dto.KpiB4ResultDTO;
import org.mapstruct.Mapper;

/**
 * Mapper for the entity {@link KpiB4Result} and its DTO {@link KpiB4ResultDTO}.
 */
@Mapper(componentModel = "spring")
public interface KpiB4ResultMapper extends EntityMapper<KpiB4ResultDTO, KpiB4Result> {}