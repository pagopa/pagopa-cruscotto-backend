package com.nexigroup.pagopa.cruscotto.service.mapper;

import com.nexigroup.pagopa.cruscotto.domain.KpiB4DetailResult;
import com.nexigroup.pagopa.cruscotto.service.dto.KpiB4DetailResultDTO;
import org.mapstruct.Mapper;

/**
 * Mapper for the entity {@link KpiB4DetailResult} and its DTO {@link KpiB4DetailResultDTO}.
 */
@Mapper(componentModel = "spring")
public interface KpiB4DetailResultMapper extends EntityMapper<KpiB4DetailResultDTO, KpiB4DetailResult> {}