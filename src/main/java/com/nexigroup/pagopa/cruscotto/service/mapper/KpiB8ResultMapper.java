package com.nexigroup.pagopa.cruscotto.service.mapper;

import com.nexigroup.pagopa.cruscotto.domain.KpiB8Result;
import com.nexigroup.pagopa.cruscotto.service.dto.KpiB8ResultDTO;
import org.mapstruct.Mapper;

/**
 * Mapper for the entity {@link KpiB8Result} and its DTO {@link KpiB8ResultDTO}.
 */
@Mapper(componentModel = "spring")
public interface KpiB8ResultMapper extends EntityMapper<KpiB8ResultDTO, KpiB8Result> {}
