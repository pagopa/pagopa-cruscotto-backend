package com.nexigroup.pagopa.cruscotto.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.io.Serializable;
import java.time.Instant;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * A PagoPaPaymentReceipt.
 */

@Entity
@Table(name = "PAGOPA_PAYMENT_RECEIPT")
@Getter
@Setter
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class PagoPaPaymentReceipt implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "ID")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @Size(min = 1, max = 35)
    @Column(name = "CF_PARTNER", length = 35, nullable = false)
    private String cfPartner;

    @NotNull
    @Size(min = 1, max = 35)
    @Column(name = "STATION", length = 35, nullable = false)
    private String station;

    @NotNull
    @Column(name = "START_DATE", nullable = false)
    private Instant startDate;

    @NotNull
    @Column(name = "END_DATE", nullable = false)
    private Instant endDate;

    @NotNull
    @Column(name = "TOT_RES", nullable = false)
    private Long totRes;

    @NotNull
    @Column(name = "RES_OK", nullable = false)
    private Long resOk;

    @NotNull
    @Column(name = "RES_KO", nullable = false)
    private Long resKo;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (!(o instanceof PagoPaPaymentReceipt that)) return false;

        return new EqualsBuilder()
            .append(cfPartner, that.cfPartner)
            .append(station, that.station)
            .append(startDate, that.startDate)
            .append(endDate, that.endDate)
            .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37).append(cfPartner).append(station).append(startDate).append(endDate).toHashCode();
    }

    @Override
    public String toString() {
        return (
            "PagoPaPaymentReceipt [id=" +
            id +
            ", cfPartner=" +
            cfPartner +
            ", station=" +
            station +
            ", startDate=" +
            startDate +
            ", endDate=" +
            endDate +
            ", totRes=" +
            totRes +
            ", resOk=" +
            resOk +
            ", resKo=" +
            resKo +
            "]"
        );
    }
}
