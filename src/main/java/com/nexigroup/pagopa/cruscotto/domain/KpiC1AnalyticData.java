package com.nexigroup.pagopa.cruscotto.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
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

import java.time.LocalDate;

/**
 * JPA entity for KPI_C1_ANALYTIC_DATA table
 * Rappresenta i dati analitici granulari del KPI C.1 (Invio notifiche tramite servizio IO)
 */
@Entity
@Table(name = "KPI_C1_ANALYTIC_DATA")
@Getter
@Setter
@DynamicUpdate
@DynamicInsert
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class KpiC1AnalyticData extends AbstractAuditingEntity<Long> {

    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "CO_ID")
    @SequenceGenerator(name = "SQCRUSC8_KPIC1ANALDATA", sequenceName = "SQCRUSC8_KPIC1ANALDATA", allocationSize = 1)
    @GeneratedValue(generator = "SQCRUSC8_KPIC1ANALDATA", strategy = GenerationType.SEQUENCE)
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
    @Column(name = "DT_DATA_DATE", nullable = false)
    private LocalDate data;

    @NotNull
    @Size(max = 50)
    @Column(name = "TE_CF_INSTITUTION", nullable = false, length = 50)
    private String cfInstitution;

    // Link al dettaglio (KPI_C1_DETAIL_RESULT) per consentire drill-down diretto.
    // La colonna esiste già nello schema (CO_KPI_C1_DETAIL_RESULT_ID) ma non era mappata.
    // Non imponiamo NOT NULL a livello JPA per non rompere dati storici già inseriti null; nuova logica la popolerà sempre.
    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "CO_KPI_C1_DETAIL_RESULT_ID")
    private KpiC1DetailResult detailResult;

    @Column(name = "CO_POSITIONS_COUNT", nullable = false)
    private Long positionNumber = 0L;

    @Column(name = "CO_MESSAGES_COUNT", nullable = false)
    private Long messageNumber = 0L;

    @Column(name = "CO_PERCENTAGE")
    private Double percentage;

    @Column(name = "FL_MEETS_TOLERANCE")
    private Boolean meetsTolerance;

    @Size(max = 50)
    @Column(name = "TE_CF_PARTNER", length = 50)
    private String cfPartner;

    /**
     * Default constructor
     */
    public KpiC1AnalyticData() {
        this.positionNumber = 0L;
        this.messageNumber = 0L;
    }

    /**
     * Constructor per la creazione dei dati analitici
     */
    public KpiC1AnalyticData(Instance instance, InstanceModule instanceModule, LocalDate referenceDate,
                             LocalDate data, String cfInstitution, Long positionNumber, Long messageNumber) {
        this();
        this.instance = instance;
        this.instanceModule = instanceModule;
        this.referenceDate = referenceDate;
        this.data = data;
        this.cfInstitution = cfInstitution;
        this.positionNumber = positionNumber != null ? positionNumber : 0L;
        this.messageNumber = messageNumber != null ? messageNumber : 0L;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        KpiC1AnalyticData other = (KpiC1AnalyticData) obj;
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
        return "KpiC1AnalyticData{" +
                "id=" + id +
                ", referenceDate=" + referenceDate +
                ", data=" + data +
                ", cfInstitution='" + cfInstitution + '\'' +
                ", cfPartner='" + cfPartner + '\'' +
                ", positionNumber=" + positionNumber +
                ", messageNumber=" + messageNumber +
                ", percentage=" + percentage +
                ", meetsTolerance=" + meetsTolerance +
                "}";
    }
}