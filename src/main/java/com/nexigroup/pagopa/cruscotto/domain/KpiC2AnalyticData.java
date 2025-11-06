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
 * A KpiC2AnalyticData.
 *
 * Rappresenta i dati analitici per il KPI C.2.
 * Contiene informazioni sui conteggi e sulle percentuali relative alle istituzioni,
 * ai pagamenti e alle notifiche analizzate.
 */
@Entity
@Table(name = "KPI_C2_ANALYTIC_DATA")
@Getter
@Setter
@DynamicUpdate
@DynamicInsert
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class KpiC2AnalyticData implements Serializable {

    private static final long serialVersionUID = 1L;

    // === Chiave primaria ===
    @Id
    @Column(name = "CO_ID")
    @SequenceGenerator(
        name = "SQCRUSC8_KPIC2ANALDATA",
        sequenceName = "SQCRUSC8_KPIC2ANALDATA",
        allocationSize = 1
    )
    @GeneratedValue(generator = "SQCRUSC8_KPIC2ANALDATA", strategy = GenerationType.SEQUENCE)
    private Long id;

    // === Relazioni e chiavi esterne ===


    @JsonIgnore
    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "CO_INSTANCE_ID", nullable = false)
    private Instance instance;


    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "CO_INSTANCE_MODULE_ID", nullable = false)
    private InstanceModule instanceModule;


    @JsonIgnore
    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "CO_KPI_C2_DETAIL_RESULT_ID", nullable = false)
    private KpiC2DetailResult kpiC2DetailResult;

    // === Dati temporali ===

    @NotNull
    @Column(name = "DT_ANALISYS_DATE", nullable = false)
    private LocalDate analysisDate;



    @NotNull
    @Column(name = "DT_EVALUATION_DATE", nullable = false)
    private LocalDate evaluationDate;
    // === Dati specifici KPI ===


    @Column(name = "CO_NUM_INSTITUTION")
    private Long numInstitution;

    @Column(name = "CO_NUM_INSTITUTION_SEND")
    private Long numInstitutionSend;

    @Column(name = "CO_PER_INSTITUTION_SEND")
    private BigDecimal perInstitutionSend;

    @Column(name = "CO_NUM_PAYMENT")
    private Long numPayment;

    @Column(name = "CO_NUM_NOTIFICATION")
    private long numNotification;

    @Column(name = "CO_PER_NOTIFICATION")
    private BigDecimal perNotification;

    // === Metodi standard ===

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof KpiC2AnalyticData)) return false;
        return id != null && id.equals(((KpiC2AnalyticData) o).id);
    }

    @Override
    public int hashCode() {
        return 31;
    }

    @Override
    public String toString() {
        return "KpiC2AnalyticData{" +
            "id=" + id +
            ", instance=" + instance +
            ", instanceModule=" + instanceModule +
            ", kpiC2DetailResult=" + kpiC2DetailResult +
            ", analysisDate=" + analysisDate +
            ", evaluationDate=" + evaluationDate +
            ", numInstitution=" + numInstitution +
            ", numInstitutionSend=" + numInstitutionSend +
            ", perInstitutionSend=" + perInstitutionSend +
            ", numPayment=" + numPayment +
            ", numNotification=" + numNotification +
            ", perNotification=" + perNotification +
            '}';
    }
}
