package com.nexigroup.pagopa.cruscotto.service.dto;

import com.nexigroup.pagopa.cruscotto.domain.PagoPaTaxonomyAggregatePosition;
import java.io.Serializable;
import java.time.Instant;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

/**
 * A DTO for the {@link PagoPaTaxonomyAggregatePosition} entity.
 */

@Getter
@Setter
@EqualsAndHashCode
public class PagoPaTaxonomyAggregatePositionDTO implements Serializable {

    private static final long serialVersionUID = 5511187514803642178L;

    private Long id;

    private String cfPartner;

    private String station;

    private String transferCategory;

    private Instant startDate;

    private Instant endDate;

    private Long total;

    @Override
    public String toString() {
        return (
            "PagoPaTaxonomyAggregatePositionDTO [id=" +
            id +
            ", cfPartner=" +
            cfPartner +
            ", station=" +
            station +
            ", transferCategory=" +
            transferCategory +
            ", startDate=" +
            startDate +
            ", endDate=" +
            endDate +
            ", total=" +
            total +
            "]"
        );
    }
}
