package com.nexigroup.pagopa.cruscotto.service.mapper;

import com.nexigroup.pagopa.cruscotto.domain.PagopaSpontaneous;
import com.nexigroup.pagopa.cruscotto.service.dto.PagopaSpontaneiDTO;
import org.mapstruct.Mapper;

/**
 * Mapper for the entity {@link PagopaSpontaneous} and its DTO {@link PagopaSpontaneiDTO}.
 */
@Mapper(componentModel = "spring")
public interface PagopaSpontaneiMapper extends EntityMapper<PagopaSpontaneiDTO, PagopaSpontaneous> {}