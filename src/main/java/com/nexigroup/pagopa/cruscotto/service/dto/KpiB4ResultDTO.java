package com.nexigroup.pagopa.cruscotto.service.dto;

import com.nexigroup.pagopa.cruscotto.domain.enumeration.EvaluationType;
import com.nexigroup.pagopa.cruscotto.domain.enumeration.OutcomeStatus;
import jakarta.validation.constraints.NotNull;
import java.io.Serializable;
import java.time.LocalDate;
import lombok.Getter;
import lombok.Setter;

/**
 * A DTO for the {@link com.nexigroup.pagopa.cruscotto.domain.KpiB4Result} entity.
 */
@Getter
@Setter
public class KpiB4ResultDTO implements Serializable {

    private Long id;

    @NotNull
    private Long instanceId;

    @NotNull
    private Long instanceModuleId;

    @NotNull
    private LocalDate analysisDate;

    @NotNull
    private Boolean excludePlannedShutdown;

    @NotNull
    private Boolean excludeUnplannedShutdown;

    @NotNull
    private Double eligibilityThreshold;

    @NotNull
    private Double tolerance;

    @NotNull
    private EvaluationType evaluationType;

    @NotNull
    private OutcomeStatus outcome;

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof KpiB4ResultDTO)) {
            return false;
        }

        KpiB4ResultDTO kpiB4ResultDTO = (KpiB4ResultDTO) o;
        if (this.id == null) {
            return false;
        }
        return this.id.equals(kpiB4ResultDTO.id);
    }

    @Override
    public int hashCode() {
        return this.id != null ? this.id.hashCode() : 0;
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "KpiB4ResultDTO{" +
            "id=" + id +
            ", instanceId=" + instanceId +
            ", instanceModuleId=" + instanceModuleId +
            ", analysisDate='" + analysisDate + "'" +
            ", excludePlannedShutdown=" + excludePlannedShutdown +
            ", excludeUnplannedShutdown=" + excludeUnplannedShutdown +
            ", eligibilityThreshold=" + eligibilityThreshold +
            ", tolerance=" + tolerance +
            ", evaluationType='" + evaluationType + "'" +
            ", outcome='" + outcome + "'" +
            "}";
    }
}