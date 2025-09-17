package com.nexigroup.pagopa.cruscotto.service.dto;

import com.nexigroup.pagopa.cruscotto.domain.PagoPaPaymentReceiptDrilldown;
import java.io.Serializable;
import java.time.Instant;
import java.time.LocalDate;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

/**
 * A DTO for the {@link PagoPaPaymentReceiptDrilldown} entity.
 */
@Getter
@Setter
@EqualsAndHashCode
public class PagoPaPaymentReceiptDrilldownDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;

    private Long instanceId;

    private Long instanceModuleId;

    private Long stationId;

    private String stationName;

    private LocalDate analysisDate;

    private LocalDate evaluationDate;

    private Instant startTime;

    private Instant endTime;

    private String timeSlot; // "00:00-00:15"

    private Long totRes;

    private Long resOk;

    private Long resKo;

    private Double resKoPercentage;

    @Override
    public String toString() {
        return "PagoPaPaymentReceiptDrilldownDTO{" +
            "id=" + id +
            ", instanceId=" + instanceId +
            ", stationId=" + stationId +
            ", stationName='" + stationName + '\'' +
            ", evaluationDate=" + evaluationDate +
            ", timeSlot='" + timeSlot + '\'' +
            ", totRes=" + totRes +
            ", resOk=" + resOk +
            ", resKo=" + resKo +
            ", resKoPercentage=" + resKoPercentage +
            '}';
    }
}