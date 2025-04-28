package com.nexigroup.pagopa.cruscotto.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import java.io.Serializable;
import java.time.LocalDate;

/**
 * A Taxonomy.
 */
@Entity
@Table(name = "TAXONOMY")
@Getter
@Setter
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class Taxonomy extends AbstractAuditingEntity<Long> implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "CO_ID")
    @SequenceGenerator(name = "SQDASH_TAXO01", sequenceName = "SQDASH_TAXO01")
    @GeneratedValue(generator = "SQDASH_TAXO01", strategy = GenerationType.SEQUENCE)
    private Long id;

    @NotNull
    @Size(min = 1, max = 12)
    @Column(name = "TE_TAKINGS_IDENTIFIER", length = 12, nullable = false)
    private String takingsIdentifier;

    @Column(name = "DT_VALIDITY_START_DATE")
    private LocalDate validityStartDate;

    @Column(name = "DT_VALIDITY_END_DATE")
    private LocalDate validityEndDate;

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Taxonomy)) {
            return false;
        }
        return id != null && id.equals(((Taxonomy) o).id);
    }

    @Override
    public int hashCode() {
        return 31;
    }

    @Override
    public String toString() {
        return "Taxonomy{" +
            "id=" + id +
            ", takingsIdentifier='" + takingsIdentifier + '\'' +
            ", validityStartDate=" + validityStartDate +
            ", validityEndDate=" + validityEndDate +
            "} " + super.toString();
    }
}
