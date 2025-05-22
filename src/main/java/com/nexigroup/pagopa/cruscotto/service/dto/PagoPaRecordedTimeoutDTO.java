package com.nexigroup.pagopa.cruscotto.service.dto;

import com.nexigroup.pagopa.cruscotto.domain.PagoPaRecordedTimeout;

import java.io.Serializable;
import java.time.Instant;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

/**
 * A DTO for the {@link PagoPaRecordedTimeout} entity.
 */

@Getter
@Setter
@EqualsAndHashCode
public class PagoPaRecordedTimeoutDTO implements Serializable {

    private static final long serialVersionUID = 6591513253390502398L;
    

    private Long id;

    private String cfPartner;

    private String station;

    private String method;

    private Instant startDate;

    private Instant endDate;

    private Long totReq;

    private Long reqOk;

    private Long reqTimeout;

    private Double avgTime;
    

    @Override
    public String toString() {
        return (
            "PagoPaRecordedTimeoutDTO{" +
            "id=" +
            id +
            ", cfPartner='" +
            cfPartner +
            '\'' +
            ", station='" +
            station +
            '\'' +
            ", method='" +
            method +
            '\'' +
            ", startDate=" +
            startDate +
            ", endDate=" +
            endDate +
            ", totReq=" +
            totReq +
            ", reqOk=" +
            reqOk +
            ", reqTimeout=" +
            reqTimeout +
            ", avgTime=" +
            avgTime +
            '}'
        );
    }
}
