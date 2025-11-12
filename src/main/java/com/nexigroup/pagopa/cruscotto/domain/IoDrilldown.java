package com.nexigroup.pagopa.cruscotto.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.io.Serializable;
import java.time.LocalDate;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

/**
 * Entity representing a snapshot of negative evidences for KPI C.1 (IO messages below tolerance).
 */
@Entity
@Table(name = "IO_DRILLDOWN")
@Getter
@Setter
@DynamicUpdate
@DynamicInsert
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class IoDrilldown implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "CO_ID")
    @SequenceGenerator(name = "SQCRUSC8_IODRILLDOWN", sequenceName = "SQCRUSC8_IODRILLDOWN", allocationSize = 1)
    @GeneratedValue(generator = "SQCRUSC8_IODRILLDOWN", strategy = GenerationType.SEQUENCE)
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

    @JsonIgnore
    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "CO_KPI_C1_ANALYTIC_DATA_ID", nullable = false)
    private KpiC1AnalyticData kpiC1AnalyticData;

    @NotNull
    @Column(name = "DT_REFERENCE_DATE", nullable = false)
    private LocalDate referenceDate;

    @NotNull
    @Column(name = "DT_DATA_DATE", nullable = false)
    private LocalDate dataDate;

    @NotNull
    @Size(max = 50)
    @Column(name = "TE_CF_INSTITUTION", nullable = false, length = 50)
    private String cfInstitution;

    @Size(max = 35)
    @Column(name = "TE_CF_PARTNER", length = 35)
    private String cfPartner;

    @Column(name = "CO_POSITIONS_COUNT", nullable = false)
    private Long positionsCount = 0L;

    @Column(name = "CO_MESSAGES_COUNT", nullable = false)
    private Long messagesCount = 0L;

    @Column(name = "CO_PERCENTAGE")
    private Double percentage;

    @Column(name = "FL_MEETS_TOLERANCE")
    private Boolean meetsTolerance;

    public IoDrilldown() {}

    public IoDrilldown(Instance instance, InstanceModule instanceModule, KpiC1AnalyticData analyticData,
                       LocalDate referenceDate, LocalDate dataDate, String cfInstitution, String cfPartner,
                       Long positionsCount, Long messagesCount, Double percentage, Boolean meetsTolerance) {
        this.instance = instance;
        this.instanceModule = instanceModule;
        this.kpiC1AnalyticData = analyticData;
        this.referenceDate = referenceDate;
        this.dataDate = dataDate;
        this.cfInstitution = cfInstitution;
        this.cfPartner = cfPartner;
        this.positionsCount = positionsCount != null ? positionsCount : 0L;
        this.messagesCount = messagesCount != null ? messagesCount : 0L;
        this.percentage = percentage;
        this.meetsTolerance = meetsTolerance;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof IoDrilldown)) return false;
        IoDrilldown other = (IoDrilldown) o;
        return id != null && id.equals(other.id);
    }

    @Override
    public int hashCode() { return getClass().hashCode(); }

    @Override
    public String toString() {
        return "IoDrilldown{" +
            "id=" + id +
            ", cfInstitution='" + cfInstitution + '\'' +
            ", cfPartner='" + cfPartner + '\'' +
            ", dataDate=" + dataDate +
            ", positionsCount=" + positionsCount +
            ", messagesCount=" + messagesCount +
            ", percentage=" + percentage +
            ", meetsTolerance=" + meetsTolerance +
            '}';
    }
}
