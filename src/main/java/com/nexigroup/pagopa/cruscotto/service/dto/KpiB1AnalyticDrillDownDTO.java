package com.nexigroup.pagopa.cruscotto.service.dto;

import lombok.Data;
import java.io.Serializable;
import java.time.LocalDate;

/**
 * A DTO for the {@link com.nexigroup.pagopa.cruscotto.domain.KpiB1AnalyticDrillDown} entity.
 */
@Data
public class KpiB1AnalyticDrillDownDTO implements Serializable {

    private Long id;

    private Long kpiB1AnalyticDataId;

    private LocalDate dataDate;

    private String partnerFiscalCode;

    private String institutionFiscalCode;

    private Integer transactionCount;

    private String stationCode;

}