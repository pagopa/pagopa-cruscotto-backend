package com.nexigroup.pagopa.cruscotto.service;

import com.nexigroup.pagopa.cruscotto.domain.PagoPaTaxonomyAggregatePosition;
import com.nexigroup.pagopa.cruscotto.service.dto.PagoPaTaxonomyAggregatePositionDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.List;

/**
 * Service Interface for managing {@link PagoPaTaxonomyAggregatePosition}.
 */

public interface PagoPaTaxonomyAggregatePositionService {
    List<PagoPaTaxonomyAggregatePositionDTO> findAllRecordIntoDayForPartner(String fiscalCodePartner, LocalDate day);

    Page<PagoPaTaxonomyAggregatePositionDTO> findAll(Pageable pageable);
}
