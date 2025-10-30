package com.nexigroup.pagopa.cruscotto.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.nexigroup.pagopa.cruscotto.domain.enumeration.EvaluationType;
import com.nexigroup.pagopa.cruscotto.domain.enumeration.OutcomeStatus;
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
 * A KpiC2Result.
 *
 * Rappresenta i risultati dei KPI C.2,
 * con soglie di eligibilità e valutazioni.
 */
@Entity
@Table(name = "KPI_C2_RESULT")
@Getter
@Setter
@DynamicUpdate
@DynamicInsert
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class KpiC2Result implements Serializable {

    private static final long serialVersionUID = 1L;

    // === Chiave primaria ===
    @Id
    @Column(name = "CO_ID")
    @SequenceGenerator(
        name = "SQCRUSC8_KPIC2RESU",
        sequenceName = "SQCRUSC8_KPIC2RESU",
        allocationSize = 1
    )
    @GeneratedValue(generator = "SQCRUSC8_KPIC2RESU", strategy = GenerationType.SEQUENCE)
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
    @Column(name = "CO_ELIGIBILITY_THRESHOLD", nullable = false)
    private Double eligibilityThreshold;

    @NotNull
    @Column(name = "CO_TOLERANCE", nullable = false)
    private Double tolerance;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "TE_EVALUATION_TYPE", nullable = false)
    private EvaluationType evaluationType;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "TE_OUTCOME", nullable = false)
    private OutcomeStatus outcome;

    // === Metodi standard ===
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof KpiC2Result)) return false;
        return id != null && id.equals(((KpiC2Result) o).id);
    }

    @Override
    public int hashCode() {
        return 31;
    }

    @Override
    public String toString() {
        return "KpiC2Result{" +
            "id=" + id +
            ", instanceId=" + instanceId +
            ", instanceModuleId=" + instanceModuleId +
            ", analysisDate=" + analysisDate +
            ", eligibilityThreshold=" + eligibilityThreshold +
            ", tolerance=" + tolerance +
            ", evaluationType='" + evaluationType + '\'' +
            ", outcome='" + outcome + '\'' +
            '}';
    }
}
