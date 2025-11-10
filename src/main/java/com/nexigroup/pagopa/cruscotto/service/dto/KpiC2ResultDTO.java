package com.nexigroup.pagopa.cruscotto.service.dto;

import com.nexigroup.pagopa.cruscotto.domain.enumeration.EvaluationType;
import com.nexigroup.pagopa.cruscotto.domain.enumeration.OutcomeStatus;
import jakarta.persistence.Column;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * A DTO for the {@link com.nexigroup.pagopa.cruscotto.domain.KpiB5Result} entity.
 */
@Getter
@Setter
public class KpiC2ResultDTO implements Serializable {

    private Long id;

    @NotNull
    private Long instanceId;

    @NotNull
    private Long instanceModuleId;

    @NotNull
    private LocalDate analysisDate;

    @NotNull
    private Double institutionTolerance;

    @NotNull
    private Double notificationTolerance;

    @NotNull
    private OutcomeStatus outcome;

    @NotNull
    private EvaluationType evaluationType;


    @Override
    public String toString() {
        return "KpiC2ResultDTO{" +
            "id=" + id +
            ", instanceId=" + instanceId +
            ", instanceModuleId=" + instanceModuleId +
            ", analysisDate=" + analysisDate +
            ", institutionTolerance=" + institutionTolerance +
            ", notificationTolerance=" + notificationTolerance +
            ", outcome=" + outcome +
            ", evaluationType=" + evaluationType +
            '}';
    }
}
