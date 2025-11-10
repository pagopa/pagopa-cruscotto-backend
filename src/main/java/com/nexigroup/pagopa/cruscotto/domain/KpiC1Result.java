package com.nexigroup.pagopa.cruscotto.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.nexigroup.pagopa.cruscotto.domain.enumeration.OutcomeStatus;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
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
 * JPA entity for KPI_C1_RESULT table
 * Rappresenta i risultati principali del KPI C.1 (Invio notifiche tramite servizio IO)
 */
@Entity
@Table(name = "KPI_C1_RESULT")
@Getter
@Setter
@DynamicUpdate
@DynamicInsert
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class KpiC1Result extends AbstractAuditingEntity<Long> {

    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "CO_ID")
    @SequenceGenerator(name = "SQCRUSC8_KPIC1RESULT", sequenceName = "SQCRUSC8_KPIC1RESULT", allocationSize = 1)
    @GeneratedValue(generator = "SQCRUSC8_KPIC1RESULT", strategy = GenerationType.SEQUENCE)
    private Long id;

    @JsonIgnore
    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "CO_ID_INSTANCE", nullable = false)
    private Instance instance;

    @JsonIgnore
    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "CO_ID_INSTANCE_MODULE", nullable = false)
    private InstanceModule instanceModule;

    @NotNull
    @Column(name = "DT_REFERENCE_DATE", nullable = false)
    private LocalDate referenceDate;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "TE_OUTCOME", nullable = false, length = 50)
    private OutcomeStatus outcome;

    @Column(name = "FL_COMPLIANT")
    private Boolean compliant;

    @Column(name = "CO_NUMERO_ENTI_COMPLIANTI", nullable = false)
    private Long compliantInstitutions = 0L;

    @Column(name = "CO_NUMERO_ENTI_TOTALI", nullable = false)
    private Long totalInstitutions = 0L;

    @Column(name = "CO_PERCENTUALE_COMPLIANCE", precision = 5, scale = 2)
    private BigDecimal compliancePercentage;

    @Column(name = "CO_NUMERO_POSIZIONI_TOTALI", nullable = false)
    private Long totalPositions = 0L;

    @Column(name = "CO_NUMERO_MESSAGGI_INVIATI", nullable = false)
    private Long sentMessages = 0L;

    @Column(name = "CO_PERCENTUALE_INVIO_GLOBALE", precision = 5, scale = 2)
    private BigDecimal globalSendingPercentage;

    @Column(name = "CO_SOGLIA_CONFIGURATA", precision = 5, scale = 2)
    private BigDecimal configuredThreshold;

    /**
     * Default constructor
     */
    public KpiC1Result() {
        this.compliantInstitutions = 0L;
        this.totalInstitutions = 0L;
        this.totalPositions = 0L;
        this.sentMessages = 0L;
    }

    /**
     * Constructor per la creazione del risultato principale
     */
    public KpiC1Result(Instance instance, InstanceModule instanceModule, LocalDate referenceDate,
                       OutcomeStatus outcome, Boolean compliant, BigDecimal configuredThreshold) {
        this();
        this.instance = instance;
        this.instanceModule = instanceModule;
        this.referenceDate = referenceDate;
        this.outcome = outcome;
        this.compliant = compliant;
        this.configuredThreshold = configuredThreshold;
    }

    /**
     * Aggiorna i totali del risultato
     */
    public void updateTotals(Long compliantInstitutions, Long totalInstitutions,
                               Long totalPositions, Long sentMessages) {
        this.compliantInstitutions = compliantInstitutions;
        this.totalInstitutions = totalInstitutions;
        this.totalPositions = totalPositions;
        this.sentMessages = sentMessages;

        // Calcola percentuale compliance
        if (totalInstitutions > 0) {
            this.compliancePercentage = BigDecimal.valueOf(compliantInstitutions)
                    .multiply(BigDecimal.valueOf(100))
                    .divide(BigDecimal.valueOf(totalInstitutions), 2, java.math.RoundingMode.HALF_UP);
        }

        // Calcola percentuale invio globale
        if (totalPositions > 0) {
            this.globalSendingPercentage = BigDecimal.valueOf(sentMessages)
                    .multiply(BigDecimal.valueOf(100))
                    .divide(BigDecimal.valueOf(totalPositions), 2, java.math.RoundingMode.HALF_UP);
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
        KpiC1Result other = (KpiC1Result) obj;
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
        return "KpiC1Result{" +
                "id=" + id +
                ", referenceDate=" + referenceDate +
                ", outcome=" + outcome +
                ", compliant=" + compliant +
                ", compliantInstitutions=" + compliantInstitutions +
                ", totalInstitutions=" + totalInstitutions +
                ", compliancePercentage=" + compliancePercentage +
                ", totalPositions=" + totalPositions +
                ", sentMessages=" + sentMessages +
                ", globalSendingPercentage=" + globalSendingPercentage +
                ", configuredThreshold=" + configuredThreshold +
                "}";
    }
}