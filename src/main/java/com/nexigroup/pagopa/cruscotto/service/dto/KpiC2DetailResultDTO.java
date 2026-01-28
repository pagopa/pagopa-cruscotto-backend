package com.nexigroup.pagopa.cruscotto.service.dto;

import com.nexigroup.pagopa.cruscotto.domain.enumeration.EvaluationType;
import com.nexigroup.pagopa.cruscotto.domain.enumeration.OutcomeStatus;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * A DTO for the {@link com.nexigroup.pagopa.cruscotto.domain.KpiB4DetailResult} entity.
 */
@Getter
@Setter
public class KpiC2DetailResultDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    // === Chiave primaria ===
    @NotNull
    private Long id;

    // === Relazioni e chiavi esterne ===

    @NotNull
    private Long instanceId;


    @NotNull
    private Long instanceModuleId;




    // === Dati temporali ===

    @NotNull
    private LocalDate analysisDate;

    @NotNull
    private LocalDate evaluationStartDate;

    @NotNull
    private LocalDate evaluationEndDate;

    // === Dati di valutazione ===

    @NotNull
    private EvaluationType evaluationType;



    @NotNull
    private Long totalInstitution;
    @NotNull
    private Long totalInstitutionSend;
    @NotNull
    private BigDecimal percentInstitutionSend;

    @NotNull
    private Long totalPayment;
    @NotNull
    private Long totalNotification;
    @NotNull
    private BigDecimal percentEntiOk;

    @NotNull
    private OutcomeStatus outcome;


    private Long kpiC2ResultId;


    @Override
    public int hashCode() {
        return 31;
    }


}
