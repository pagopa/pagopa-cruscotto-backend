package com.nexigroup.pagopa.cruscotto.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import java.io.Serializable;
import java.time.Instant;
import java.time.LocalDate;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

/**
 * A PagoPaPaymentReceiptDrilldown.
 * 
 * Stores quarter-hour aggregated payment receipt data for drilldown analysis.
 * This table maintains a snapshot of PAGOPA_PAYMENT_RECEIPT data at the time of KPI analysis execution.
 */
@Entity
@Table(name = "PAGOPA_PAYMENT_RECEIPT_DRILLDOWN")
@Getter
@Setter
@DynamicUpdate
@DynamicInsert
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class PagoPaPaymentReceiptDrilldown implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "CO_ID")
    @SequenceGenerator(name = "SQCRUSC8_PAGOPAPAYRECDRILL", sequenceName = "SQCRUSC8_PAGOPAPAYRECDRILL", allocationSize = 1)
    @GeneratedValue(generator = "SQCRUSC8_PAGOPAPAYRECDRILL", strategy = GenerationType.SEQUENCE)
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
    @JoinColumn(name = "CO_STATION_ID", nullable = false)
    private AnagStation station;

    @NotNull
    @Column(name = "DT_ANALYSIS_DATE", nullable = false)
    private LocalDate analysisDate;

    @NotNull
    @Column(name = "DT_EVALUATION_DATE", nullable = false)
    private LocalDate evaluationDate;

    @NotNull
    @Column(name = "DT_START_TIME", nullable = false)
    private Instant startTime;

    @NotNull
    @Column(name = "DT_END_TIME", nullable = false)
    private Instant endTime;

    @NotNull
    @Column(name = "CO_TOT_RES", nullable = false)
    private Long totRes;

    @NotNull
    @Column(name = "CO_RES_OK", nullable = false)
    private Long resOk;

    @NotNull
    @Column(name = "CO_RES_KO", nullable = false)
    private Long resKo;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (!(o instanceof PagoPaPaymentReceiptDrilldown that)) return false;

        return new EqualsBuilder().append(id, that.id).isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37).append(id).toHashCode();
    }

    @Override
    public String toString() {
        return "PagoPaPaymentReceiptDrilldown{" +
            "id=" + id +
            ", analysisDate=" + analysisDate +
            ", evaluationDate=" + evaluationDate +
            ", startTime=" + startTime +
            ", endTime=" + endTime +
            ", totRes=" + totRes +
            ", resOk=" + resOk +
            ", resKo=" + resKo +
            '}';
    }
}