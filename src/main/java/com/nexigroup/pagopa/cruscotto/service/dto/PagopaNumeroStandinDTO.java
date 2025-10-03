package com.nexigroup.pagopa.cruscotto.service.dto;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Objects;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

/**
 * A DTO for the {@link com.nexigroup.pagopa.cruscotto.domain.PagopaNumeroStandin} entity.
 * 
 * Used for displaying raw Stand-In data as the final drilldown level in KPI B.3 analysis.
 */
@Getter
@Setter
public class PagopaNumeroStandinDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;

    @NotNull
    @Size(max = 255)
    private String stationCode;

    @NotNull
    private LocalDateTime intervalStart;

    @NotNull
    private LocalDateTime intervalEnd;

    @NotNull
    private Integer standInCount;

    @Size(max = 50)
    private String eventType;

    @NotNull
    private LocalDateTime dataDate;

    @NotNull
    private LocalDateTime dataOraEvento;

    private LocalDateTime loadTimestamp;

    // Partner information fields
    private Long partnerId;
    
    private String partnerName;
    
    @Size(max = 255)
    private String partnerFiscalCode;

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof PagopaNumeroStandinDTO)) {
            return false;
        }

        PagopaNumeroStandinDTO that = (PagopaNumeroStandinDTO) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "PagopaNumeroStandinDTO{" +
            "id=" + id +
            ", stationCode='" + stationCode + "'" +
            ", intervalStart='" + intervalStart + "'" +
            ", intervalEnd='" + intervalEnd + "'" +
            ", standInCount=" + standInCount +
            ", eventType='" + eventType + "'" +
            ", dataDate='" + dataDate + "'" +
            ", dataOraEvento='" + dataOraEvento + "'" +
            ", loadTimestamp='" + loadTimestamp + "'" +
            ", partnerId=" + partnerId +
            ", partnerName='" + partnerName + "'" +
            ", partnerFiscalCode='" + partnerFiscalCode + "'" +
            "}";
    }
}