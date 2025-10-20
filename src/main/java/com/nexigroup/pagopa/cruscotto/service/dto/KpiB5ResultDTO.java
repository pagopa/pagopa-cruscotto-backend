package com.nexigroup.pagopa.cruscotto.service.dto;

import com.nexigroup.pagopa.cruscotto.domain.enumeration.OutcomeStatus;
import jakarta.validation.constraints.NotNull;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import lombok.Getter;
import lombok.Setter;

/**
 * A DTO for the {@link com.nexigroup.pagopa.cruscotto.domain.KpiB5Result} entity.
 */
@Getter
@Setter
public class KpiB5ResultDTO implements Serializable {

    private Long id;

    @NotNull
    private Long instanceId;

    @NotNull
    private Long instanceModuleId;

    @NotNull
    private LocalDate analysisDate;

    @NotNull
    private BigDecimal thresholdIndex;

    @NotNull
    private BigDecimal toleranceIndex;

    @NotNull
    private OutcomeStatus outcome;

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof KpiB5ResultDTO)) {
            return false;
        }
        return id != null && id.equals(((KpiB5ResultDTO) o).id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }

    @Override
    public String toString() {
        return "KpiB5ResultDTO{" +
            "id=" + id +
            ", instanceId=" + instanceId +
            ", instanceModuleId=" + instanceModuleId +
            ", analysisDate=" + analysisDate +
            ", thresholdIndex=" + thresholdIndex +
            ", toleranceIndex=" + toleranceIndex +
            ", outcome=" + outcome +
            "}";
    }
}