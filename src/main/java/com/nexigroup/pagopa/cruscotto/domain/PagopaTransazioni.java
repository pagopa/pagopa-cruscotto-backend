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
@Table(name = "PAGOPA_TRANSAZIONI")
@Getter
@Setter
@DynamicUpdate
@DynamicInsert
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class PagopaTransazioni implements Serializable {

    private static final long serialVersionUID = -6090145346504076144L;

    @Id
    @Column(name = "ID")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @Size(min = 1, max = 35)
    @Column(name = "PARTNER", length = 35, nullable = false)
    private String partner;

    @NotNull
    @Size(min = 1, max = 35)
    @Column(name = "ENTE", length = 35, nullable = false)
    private String ente;

    @NotNull
    @Column(name = "DATA", nullable = false)
    private LocalDate data;

    @NotNull
    @Size(min = 1, max = 35)
    @Column(name = "STAZIONE", length = 35, nullable = false)
    private String stazione;

    @NotNull
    @Column(name = "TOTALE_TRANSAZIONI", nullable = false)
    private Integer totaleTransazioni;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (!(o instanceof PagopaTransazioni that)) return false;

        return new EqualsBuilder().append(id, that.id).isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37).append(id).toHashCode();
    }

    @Override
    public String toString() {
        return (
            "PagopaTransazioni{" +
            "id=" +
            id +
            ", partner='" +
            partner + '\'' +
            ", ente='" +
            ente + '\'' +
            ", data=" +
            data +
            ", stazione='" +
            stazione + '\'' +
            ", totaleTransazioni=" +
            totaleTransazioni +
            '}'
        );
    }
}