package com.nexigroup.pagopa.cruscotto.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.nexigroup.pagopa.cruscotto.domain.enumeration.OutcomeStatus;
import com.nexigroup.pagopa.cruscotto.domain.enumeration.EvaluationType;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * JPA entity for KPI_C1_DETAIL_RESULT table
 * Rappresenta i risultati dettagliati per CF_INSTITUTION del KPI C.1 (Invio notifiche tramite servizio IO)
 */
@Entity
@Table(name = "KPI_C1_DETAIL_RESULT")
@Getter
@Setter
@DynamicUpdate
@DynamicInsert
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class KpiC1DetailResult extends AbstractAuditingEntity<Long> {

    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "CO_ID")
    @SequenceGenerator(name = "SQCRUSC8_KPIC1DETARESU", sequenceName = "SQCRUSC8_KPIC1DETARESU", allocationSize = 1)
    @GeneratedValue(generator = "SQCRUSC8_KPIC1DETARESU", strategy = GenerationType.SEQUENCE)
    private Long id;

    @JsonIgnore
    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "CO_INSTANCE_ID", nullable = false)
    private Instance instance;

    @JsonIgnore
    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "CO_INSTANCE_MODULE_ID", nullable = false)
    private InstanceModule instanceModule;

    @NotNull
    @Column(name = "DT_REFERENCE_DATE", nullable = false)
    private LocalDate referenceDate;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "TE_EVALUATION_TYPE", nullable = false)
    private EvaluationType evaluationType;

    @NotNull
    @Column(name = "DT_EVALUATION_START_DATE", nullable = false)
    private LocalDate evaluationStartDate;

    @NotNull
    @Column(name = "DT_EVALUATION_END_DATE", nullable = false)
    private LocalDate evaluationEndDate;

    @NotNull
    @Size(max = 50)
    @Column(name = "TE_CF_INSTITUTION", nullable = false, length = 50)
    private String cfInstitution;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "TE_OUTCOME", nullable = false, length = 50)
    private OutcomeStatus outcome;

    @Column(name = "FL_COMPLIANT")
    private Boolean compliant;

    @Column(name = "CO_TOTAL_INSTITUTIONS")
    private Long totalInstitutions = 0L;

    @Column(name = "CO_COMPLIANT_INSTITUTIONS")
    private Long compliantInstitutions = 0L;

    @Column(name = "CO_PERCENTAGE_COMPLIANT_INSTITUTIONS", precision = 5, scale = 2)
    private BigDecimal percentageCompliantInstitutions;

    @Column(name = "CO_TOTAL_POSITIONS", nullable = false)
    private Long totalPositions = 0L;

    @Column(name = "CO_SENT_MESSAGES", nullable = false)
    private Long sentMessages = 0L;

    @Column(name = "CO_SENDING_PERCENTAGE", precision = 5, scale = 2)
    private BigDecimal sendingPercentage;

    @Column(name = "CO_CONFIGURED_THRESHOLD", precision = 5, scale = 2)
    private BigDecimal configuredThreshold;

    @JsonIgnore
    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "CO_KPI_C1_RESULT_ID", nullable = false)
    private KpiC1Result kpiC1Result;

    /**
     * Default constructor
     */
    public KpiC1DetailResult() {
        this.totalPositions = 0L;
        this.sentMessages = 0L;
        this.totalInstitutions = 0L;
        this.compliantInstitutions = 0L;
    }

    /**
     * Constructor per la creazione del risultato dettagliato
     */
    public KpiC1DetailResult(Instance instance, InstanceModule instanceModule, LocalDate referenceDate,
                             EvaluationType evaluationType, LocalDate evaluationStartDate, LocalDate evaluationEndDate,
                             String cfInstitution, OutcomeStatus outcome, Boolean compliant, 
                             BigDecimal configuredThreshold, KpiC1Result kpiC1Result) {
        this();
        this.instance = instance;
        this.instanceModule = instanceModule;
        this.referenceDate = referenceDate;
        this.evaluationType = evaluationType;
        this.evaluationStartDate = evaluationStartDate;
        this.evaluationEndDate = evaluationEndDate;
        this.cfInstitution = cfInstitution;
        this.outcome = outcome;
        this.compliant = compliant;
        this.configuredThreshold = configuredThreshold;
        this.kpiC1Result = kpiC1Result;
    }

    /**
     * Aggiorna i dati del dettaglio
     */
    public void updateDetails(Long totalPositions, Long sentMessages) {
        this.totalPositions = totalPositions;
        this.sentMessages = sentMessages;

        // Calcola percentuale invio per questo CF_INSTITUTION
        if (totalPositions > 0) {
            this.sendingPercentage = BigDecimal.valueOf(sentMessages)
                    .multiply(BigDecimal.valueOf(100))
                    .divide(BigDecimal.valueOf(totalPositions), 2, java.math.RoundingMode.HALF_UP);
        }
    }

    /**
     * Aggiorna i dati degli enti
     */
    public void updateInstitutions(Long totalInstitutions, Long compliantInstitutions) {
        this.totalInstitutions = totalInstitutions;
        this.compliantInstitutions = compliantInstitutions;

        // Calcola percentuale enti complianti
        if (totalInstitutions > 0) {
            this.percentageCompliantInstitutions = BigDecimal.valueOf(compliantInstitutions)
                    .multiply(BigDecimal.valueOf(100))
                    .divide(BigDecimal.valueOf(totalInstitutions), 2, java.math.RoundingMode.HALF_UP);
        }
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        KpiC1DetailResult other = (KpiC1DetailResult) obj;
        return new EqualsBuilder()
                .append(id, other.id)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder()
                .append(id)
                .toHashCode();
    }

    @Override
    public String toString() {
        return "KpiC1DetailResult{" +
                "id=" + id +
                ", referenceDate=" + referenceDate +
                ", cfInstitution='" + cfInstitution + '\'' +
                ", outcome=" + outcome +
                ", compliant=" + compliant +
                ", totalPositions=" + totalPositions +
                ", sentMessages=" + sentMessages +
                ", sendingPercentage=" + sendingPercentage +
                ", configuredThreshold=" + configuredThreshold +
                "}";
    }
}