package com.nexigroup.pagopa.cruscotto.service.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.io.Serializable;
import lombok.Getter;
import lombok.Setter;

/**
 * A DTO for the {@link com.nexigroup.pagopa.cruscotto.domain.PagopaSpontaneous} entity.
 */
@Getter
@Setter
public class PagopaSpontaneiDTO implements Serializable {

    private Long id;
    
    private Long kpiB5AnalyticDataId;
    
    private Long partnerId;
    
    private String partnerName;

    @NotNull
    @Size(max = 255)
    private String partnerFiscalCode;

    @NotNull
    @Size(max = 255)
    private String stationCode;

    @NotNull
    @Size(max = 255)
    private String fiscalCode;

    @NotNull
    private String spontaneousPayments; // "ATTIVI" or "NON ATTIVI"

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof PagopaSpontaneiDTO)) {
            return false;
        }
        return id != null && id.equals(((PagopaSpontaneiDTO) o).id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }

    @Override
    public String toString() {
        return "PagopaSpontaneiDTO{" +
            "id=" + id +
            ", kpiB5AnalyticDataId=" + kpiB5AnalyticDataId +
            ", partnerId=" + partnerId +
            ", partnerName='" + partnerName + '\'' +
            ", partnerFiscalCode='" + partnerFiscalCode + '\'' +
            ", stationCode='" + stationCode + '\'' +
            ", fiscalCode='" + fiscalCode + '\'' +
            ", spontaneousPayments='" + spontaneousPayments + '\'' +
            "}";
    }
}