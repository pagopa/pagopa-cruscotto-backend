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
 * A PagopaTransazioni.
 */
@Entity
@Table(name = "PAGOPA_TRANSACTION")
@Getter
@Setter
@DynamicUpdate
@DynamicInsert
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class PagopaTransaction implements Serializable {

    private static final long serialVersionUID = -6090145346504076144L;

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
    @Column(name = "CF_INSTITUTION", length = 35, nullable = false)
    private String cfInstitution;

    @NotNull
    @Column(name = "DATE", nullable = false)
    private LocalDate date;

    @NotNull
    @Size(min = 1, max = 35)
    @Column(name = "STATION", length = 35, nullable = false)
    private String station;

    @NotNull
    @Column(name = "TRANSACTION_TOTAL", nullable = false)
    private Integer transactionTotal;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (!(o instanceof PagopaTransaction that)) return false;

        return new EqualsBuilder().append(id, that.id).isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37).append(id).toHashCode();
    }

    @Override
    public String toString() {
        return (
            "PagopaTransaction{" +
            "id=" +
            id +
            ", cfPartner='" +
            cfPartner + '\'' +
            ", cfInstitution='" +
            cfInstitution + '\'' +
            ", date=" +
            date +
            ", station='" +
            station + '\'' +
            ", transactionTotal=" +
            transactionTotal +
            '}'
        );
    }
}