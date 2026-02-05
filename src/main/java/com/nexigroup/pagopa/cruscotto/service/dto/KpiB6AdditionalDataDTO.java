package com.nexigroup.pagopa.cruscotto.service.dto;

import com.nexigroup.pagopa.cruscotto.domain.enumeration.EvaluationType;
import lombok.*;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class KpiB6AdditionalDataDTO implements Serializable {

    private int activeStations;
    private int stationsWithPaymentOptions;
    private int difference;
    private BigDecimal percentageDifference;

    private EvaluationType evaluationType;
    private LocalDate evaluationStartDate;
    private LocalDate evaluationEndDate;
}
