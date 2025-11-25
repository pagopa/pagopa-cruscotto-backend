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
 * A KpiC2Evidence.
 *
 * Rappresenta le evidenze di KPI C.2,
 * con informazioni sui partner, pagamenti e notifiche.
 */
@Entity
@Table(name = "KPI_C2_ANALYTIC_DRILLDOWN")
@Getter
@Setter
@DynamicUpdate
@DynamicInsert
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class KpiC2AnalyticDrillDown implements Serializable {

    private static final long serialVersionUID = 1L;

    // === Chiave primaria ===
    @Id
    @Column(name = "CO_ID")
    @SequenceGenerator(
        name = "SQCRUSC8_KPIC2ANALDD",
        sequenceName = "SQCRUSC8_KPIC2ANALDD",
        allocationSize = 1
    )
    @GeneratedValue(generator = "SQCRUSC8_KPIC2ANALDD", strategy = GenerationType.SEQUENCE)
    private Long id;

    // === Relazioni e chiavi esterne ===


    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "CO_INSTANCE_ID", nullable = false)
    private Instance instance;


    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "CO_INSTANCE_MODULE_ID", nullable = false)
    private InstanceModule instanceModule;

    // === Dati principali ===

    @NotNull
    @Column(name = "DT_ANALISYS_DATE", nullable = false)
    private LocalDate analysisDate;

    @NotNull
    @Column(name = "DT_EVALUATION_DATE", nullable = false)
    private LocalDate evaluationDate;

    @NotNull
    @Size(max = 255)
    @Column(name = "CF_INSTITUTION", nullable = false, length = 255)
    private String institutionCf;

    @Column(name = "CO_NUM_PAYMENT")
    private Long numPayment;

    @Column(name = "CO_NUM_NOTIFICATION")
    private Long numNotification;

    @Column(name = "CO_PER_NOTIFICATION")
    private BigDecimal percentNotification;

    @JsonIgnore
    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "CO_KPI_C2_ANALYTIC_DATA_ID", nullable = false)
    private KpiC2AnalyticData kpiC2AnalyticData;

    // === Metodi standard ===

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof KpiC2AnalyticDrillDown)) return false;
        return id != null && id.equals(((KpiC2AnalyticDrillDown) o).id);
    }

    @Override
    public int hashCode() {
        return 31;
    }

    @Override
    public String toString() {
        return "KpiC2AnalyticDrillDown{" +
            "id=" + id +
            ", instance=" + instance +
            ", instanceModule=" + instanceModule +
            ", analysisDate=" + analysisDate +
            ", evaluationDate=" + evaluationDate +
            ", institutionCf='" + institutionCf + '\'' +
            ", numPayment=" + numPayment +
            ", numNotification=" + numNotification +
            ", percentNotification=" + percentNotification +
            ", kpiC2AnalyticData=" + kpiC2AnalyticData +
            '}';
    }
}
