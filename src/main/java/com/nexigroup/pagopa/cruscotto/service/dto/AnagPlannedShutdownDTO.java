package com.nexigroup.pagopa.cruscotto.service.dto;

import com.nexigroup.pagopa.cruscotto.domain.AnagPlannedShutdown;
import com.nexigroup.pagopa.cruscotto.domain.enumeration.TypePlanned;
import com.nexigroup.pagopa.cruscotto.service.validation.ValidationGroups;
import jakarta.validation.constraints.NotNull;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.time.Instant;

/**
 * A DTO for the {@link AnagPlannedShutdown} entity.
 */
@Getter
@Setter
@EqualsAndHashCode
public class AnagPlannedShutdownDTO implements Serializable {

    private static final long serialVersionUID = 6591513253390502398L;

    private Long id;

    @NotNull(groups = {ValidationGroups.PlannedShutdownJob.class})
    private TypePlanned typePlanned;

    @NotNull(groups = {ValidationGroups.PlannedShutdownJob.class})
    private boolean standInd = false;

    @NotNull(groups = {ValidationGroups.PlannedShutdownJob.class})
    private Instant shutdownStartDate;

    @NotNull(groups = {ValidationGroups.PlannedShutdownJob.class})
    private Instant shutdownEndDate;

    @NotNull(groups = {ValidationGroups.PlannedShutdownJob.class})
    private Long year;

    @NotNull(groups = {ValidationGroups.PlannedShutdownJob.class})
    private Long externalId;


    private Long partnerId;

    @NotNull(groups = {ValidationGroups.PlannedShutdownJob.class})
    private String partnerFiscalCode;

    private String partnerName;

    private Long stationId;

    @NotNull(groups = {ValidationGroups.PlannedShutdownJob.class})
    private String stationName;

    @Override
    public String toString() {
        return "AnagPlannedShutdownDTO{" +
            "id=" + id +
            ", typePlanned=" + typePlanned +
            ", standInd=" + standInd +
            ", shutdownStartDate=" + shutdownStartDate +
            ", shutdownEndDate=" + shutdownEndDate +
            ", year=" + year +
            ", externalId=" + externalId +
            ", partnerId=" + partnerId +
            ", partnerFiscalCode='" + partnerFiscalCode + '\'' +
            ", partnerName='" + partnerName + '\'' +
            ", stationId=" + stationId +
            ", stationName='" + stationName + '\'' +
            '}';
    }
}
