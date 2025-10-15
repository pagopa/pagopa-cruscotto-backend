package com.nexigroup.pagopa.cruscotto.service.dto;

import java.io.Serializable;
import java.time.LocalDate;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

/**
 * A DTO for the {@link com.nexigroup.pagopa.cruscotto.domain.PagopaApiLog} entity.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PagopaAPILogDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    private String cfPartner;

    private LocalDate date;

    private String station;

    private String cfEnte;

    private String api;

    private Integer totReq;

    private Integer reqOk;

    private Integer reqKo;
}