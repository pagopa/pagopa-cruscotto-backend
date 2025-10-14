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
import java.time.LocalDateTime;

/**
 * A KpiB3AnalyticData.
 *
 * Represents individual Stand-In event data for KPI B.3 "Zero Incident" calculation.
 * This entity stores detailed event information for drill-down analysis and debugging.
 */

@Entity
@Table(name = "KPI_B3_ANALYTIC_DATA")
@Getter
@Setter
@DynamicUpdate
@DynamicInsert
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class KpiB8AnalyticData implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "CO_ID")
    @SequenceGenerator(name = "SQCRUSC8_KPIB3ANALDATA", sequenceName = "SQCRUSC8_KPIB3ANALDATA", allocationSize = 1)
    @GeneratedValue(generator = "SQCRUSC8_KPIB3ANALDATA", strategy = GenerationType.SEQUENCE)
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
    @JoinColumn(name = "CO_ANAG_STATION_ID", nullable = false)
    private AnagStation anagStation;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "CO_KPI_B3_DETAIL_RESULT_ID")
    private KpiB3DetailResult kpiB3DetailResult;

    @Size(max = 255)
    @Column(name = "TE_EVENT_ID", length = 255)
    private String eventId;

    @Size(max = 255)
    @Column(name = "TE_EVENT_TYPE", length = 255)
    private String eventType;

    @Column(name = "DT_EVENT_TIMESTAMP")
    private LocalDateTime eventTimestamp;

    @Column(name = "CO_STAND_IN_COUNT")
    private Integer standInCount;

    // JHipster needle - entity add field

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof KpiB8AnalyticData)) {
            return false;
        }
        return id != null && id.equals(((KpiB8AnalyticData) o).id);
    }

    @Override
    public int hashCode() {
        return 31;
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "KpiB3AnalyticData{" +
            "id=" + id +
            ", eventId='" + eventId + "'" +
            ", eventType='" + eventType + "'" +
            ", eventTimestamp='" + eventTimestamp + "'" +
            ", standInCount=" + standInCount +
            "}";
    }
}
