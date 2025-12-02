package com.nexigroup.pagopa.cruscotto.service.dto;

import com.nexigroup.pagopa.cruscotto.service.validation.ValidationGroups;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.io.Serializable;

@Data
public class KpiA2AnalyticIncorrectTaxonomyDataDTO implements Serializable {

    private Long id;

    private Long kpiA2AnalyticDataId;

    private String transferCategory;

    private Long totPayments;

    private Long totIncorrectPayments;

    private java.time.Instant fromHour;

    private java.time.Instant endHour;
}
