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
@Table(name = "KPI_C2_EVIDENCE")
@Getter
@Setter
@DynamicUpdate
@DynamicInsert
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class KpiC2Evidence implements Serializable {

    private static final long serialVersionUID = 1L;

    // === Chiave primaria ===
    @Id
    @Column(name = "CO_ID")
    @SequenceGenerator(
        name = "SQCRUSC8_KPIC2EVIDE",
        sequenceName = "SQCRUSC8_KPIC2EVIDE",
        allocationSize = 1
    )
    @GeneratedValue(generator = "SQCRUSC8_KPIC2EVIDE", strategy = GenerationType.SEQUENCE)
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

    // === Dati principali ===

    @NotNull
    @Column(name = "DT_ANALISYS_DATE", nullable = false)
    private LocalDate analysisDate;

    @NotNull
    @Size(max = 255)
    @Column(name = "CF_PARTNER", nullable = false, length = 255)
    private String partnerCf;

    @Column(name = "CO_NUM_PAYMENT")
    private Integer numPayment;

    @Column(name = "CO_NUM_NOTIFICATION")
    private Integer numNotification;

    @Column(name = "CO_PER_NOTIFICATION")
    private Double percentNotification;

    @JsonIgnore
    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "CO_KPI_C2_ANALYTIC_DATA_ID", nullable = false)
    private KpiC2AnalyticData kpiC2AnalyticData;

    // === Metodi standard ===

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof KpiC2Evidence)) return false;
        return id != null && id.equals(((KpiC2Evidence) o).id);
    }

    @Override
    public int hashCode() {
        return 31;
    }

    @Override
    public String toString() {
        return "KpiC2Evidence{" +
            "id=" + id +
            ", instanceId=" + instanceId +
            ", instance=" + instance +
            ", instanceModuleId=" + instanceModuleId +
            ", instanceModule=" + instanceModule +
            ", analysisDate=" + analysisDate +
            ", partnerCf='" + partnerCf + '\'' +
            ", numPayment=" + numPayment +
            ", numNotification=" + numNotification +
            ", percentNotification=" + percentNotification +
            ", kpiC2AnalyticData=" + kpiC2AnalyticData +
            '}';
    }
}
