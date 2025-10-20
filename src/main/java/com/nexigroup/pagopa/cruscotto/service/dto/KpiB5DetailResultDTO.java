package com.nexigroup.pagopa.cruscotto.service.dto;

import com.nexigroup.pagopa.cruscotto.domain.enumeration.OutcomeStatus;
import jakarta.validation.constraints.NotNull;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import lombok.Getter;
import lombok.Setter;

/**
 * A DTO for the {@link com.nexigroup.pagopa.cruscotto.domain.KpiB5DetailResult} entity.
 */
@Getter
@Setter
public class KpiB5DetailResultDTO implements Serializable {

    private Long id;

    @NotNull
    private Long instanceId;

    @NotNull
    private Long instanceModuleId;

    @NotNull
    private Long kpiB5ResultId;

    @NotNull
    private LocalDate analysisDate;

    @NotNull
    private Integer stationsPresent;

    @NotNull
    private Integer stationsWithoutSpontaneous;

    @NotNull
    private BigDecimal percentageNoSpontaneous;

    @NotNull
    private OutcomeStatus outcome;

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof KpiB5DetailResultDTO)) {
            return false;
        }
        return id != null && id.equals(((KpiB5DetailResultDTO) o).id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }

    @Override
    public String toString() {
        return "KpiB5DetailResultDTO{" +
            "id=" + id +
            ", instanceId=" + instanceId +
            ", instanceModuleId=" + instanceModuleId +
            ", kpiB5ResultId=" + kpiB5ResultId +
            ", analysisDate=" + analysisDate +
            ", stationsPresent=" + stationsPresent +
            ", stationsWithoutSpontaneous=" + stationsWithoutSpontaneous +
            ", percentageNoSpontaneous=" + percentageNoSpontaneous +
            ", outcome=" + outcome +
            "}";
    }
}