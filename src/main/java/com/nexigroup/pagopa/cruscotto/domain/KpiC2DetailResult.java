package com.nexigroup.pagopa.cruscotto.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * A KpiC2DetailResult.
 *
 * Rappresenta i risultati di dettaglio del KPI C.2,
 * con informazioni sulle istituzioni, pagamenti, notifiche e valutazioni.
 */
@Entity
@Table(name = "KPI_C2_DETAIL_RESULT")
@Getter
@Setter
@DynamicUpdate
@DynamicInsert
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class KpiC2DetailResult implements Serializable {

    private static final long serialVersionUID = 1L;

    // === Chiave primaria ===
    @Id
    @Column(name = "CO_ID")
    @SequenceGenerator(
        name = "SQCRUSC8_KPIC2DETARESU",
        sequenceName = "SQCRUSC8_KPIC2DETARESU",
        allocationSize = 1
    )
    @GeneratedValue(generator = "SQCRUSC8_KPIC2DETARESU", strategy = GenerationType.SEQUENCE)
    private Long id;

    // === Relazioni e chiavi esterne ===

    @NotNull
    @Column(name = "CO_INSTANCE_ID", nullable = false)
    private Long instanceId;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "CO_INSTANCE_ID", nullable = false, insertable = false, updatable = false)
    private Instance instance;

    @NotNull
    @Column(name = "CO_INSTANCE_MODULE_ID", nullable = false)
    private Long instanceModuleId;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "CO_INSTANCE_MODULE_ID", nullable = false, insertable = false, updatable = false)
    private InstanceModule instanceModule;

    @Column(name = "CO_KPI_C2_RESULT_ID")
    private Long kpiC2ResultId;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "CO_KPI_C2_RESULT_ID", insertable = false, updatable = false)
    private KpiC2Result kpiC2Result;

    // === Dati temporali ===

    @NotNull
    @Column(name = "DT_ANALISYS_DATE", nullable = false)
    private LocalDate analysisDate;

    @NotNull
    @Column(name = "DT_EVALUATION_START_DATE", nullable = false)
    private LocalDate evaluationStartDate;

    @NotNull
    @Column(name = "DT_EVALUATION_END_DATE", nullable = false)
    private LocalDate evaluationEndDate;

    // === Dati di valutazione ===

    @NotNull
    @Size(max = 50)
    @Column(name = "TE_EVALUATION_TYPE", nullable = false, length = 50)
    private String evaluationType;

    @Size(max = 50)
    @Column(name = "TE_OUTCOME", length = 50)
    private String outcome;

    // === Dati di aggregazione KPI ===

    @Column(name = "CO_TOT_INSTITUTION")
    private Integer totalInstitution;

    @Column(name = "CO_TOT_INSTITUTION_SEND")
    private Integer totalInstitutionSend;

    @Column(name = "CO_PER_INSTITUTION_SEND")
    private Double percentInstitutionSend;

    @Column(name = "CO_TOT_PAYMENT")
    private Integer totalPayment;

    @Column(name = "CO_TOT_NOTIFICATION")
    private Integer totalNotification;

    @Column(name = "CO_PER_ENTI_OK")
    private Double percentEntiOk;

    // === Metodi standard ===

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof KpiC2DetailResult)) return false;
        return id != null && id.equals(((KpiC2DetailResult) o).id);
    }

    @Override
    public int hashCode() {
        return 31;
    }

    @Override
    public String toString() {
        return "KpiC2DetailResult{" +
            "id=" + id +
            ", instanceId=" + instanceId +
            ", instanceModuleId=" + instanceModuleId +
            ", analysisDate=" + analysisDate +
            ", evaluationType='" + evaluationType + '\'' +
            ", evaluationStartDate=" + evaluationStartDate +
            ", evaluationEndDate=" + evaluationEndDate +
            ", totalInstitution=" + totalInstitution +
            ", totalInstitutionSend=" + totalInstitutionSend +
            ", percentInstitutionSend=" + percentInstitutionSend +
            ", totalPayment=" + totalPayment +
            ", totalNotification=" + totalNotification +
            ", percentEntiOk=" + percentEntiOk +
            ", outcome='" + outcome + '\'' +
            ", kpiC2ResultId=" + kpiC2ResultId +
            '}';
    }
}

