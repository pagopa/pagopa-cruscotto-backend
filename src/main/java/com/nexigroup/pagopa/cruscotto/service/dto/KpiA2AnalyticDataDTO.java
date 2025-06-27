package com.nexigroup.pagopa.cruscotto.service.dto;

import com.nexigroup.pagopa.cruscotto.domain.KpiA2AnalyticData;
import com.nexigroup.pagopa.cruscotto.service.validation.ValidationGroups;
import jakarta.validation.constraints.NotNull;
import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDate;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * A DTO for the {@link KpiA2AnalyticData} entity.
 */

@Getter
@Setter
@ToString
public class KpiA2AnalyticDataDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 3450035309498282963L;

    private Long id;

    @NotNull(groups = { ValidationGroups.KpiA2Job.class })
    private Long instanceId;

    @NotNull(groups = { ValidationGroups.KpiA2Job.class })
    private Long instanceModuleId;

    @NotNull(groups = { ValidationGroups.KpiA2Job.class })
    private LocalDate analysisDate;

    @NotNull(groups = { ValidationGroups.KpiA2Job.class })
    private LocalDate evaluationDate;

    @NotNull(groups = { ValidationGroups.KpiA2Job.class })
    private Long totPayments;

    @NotNull(groups = { ValidationGroups.KpiA2Job.class })
    private Long totIncorrectPayments;

    private Long kpiA2DetailResultId;
}
