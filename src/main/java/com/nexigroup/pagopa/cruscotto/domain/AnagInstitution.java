package com.nexigroup.pagopa.cruscotto.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;
import lombok.*;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * A AnagInstitution.
 */
@Entity
@Table(name = "ANAG_INSTITUTION")
@Getter
@Setter
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class AnagInstitution extends AbstractAuditingEntity<Long> implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "CO_ID")
    @SequenceGenerator(name = "SQCRUSC8_ANAGINST", sequenceName = "SQCRUSC8_ANAGINST", allocationSize = 1)
    @GeneratedValue(generator = "SQCRUSC8_ANAGINST", strategy = GenerationType.SEQUENCE)
    private Long id;

    @NotNull
    @Size(min = 1, max = 35)
    @Column(name = "TE_FISCAL_CODE", length = 35, nullable = false)
    private String fiscalCode;

    @NotNull
    @Size(min = 1, max = 256)
    @Column(name = "TE_NAME", length = 256, nullable = false)
    private String name;

    @NotNull
    @Column(name = "FL_ACA", nullable = false)
    private boolean aca = false;

    @NotNull
    @Column(name = "FL_STAND_IND", nullable = false)
    private boolean standInd = false;

    @NotNull
    @Column(name = "DT_ACTIVATION_DATE", nullable = false)
    private LocalDate activationDate;

    @JsonIgnore
    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "CO_ANAG_PARTNER_ID", nullable = false)
    private AnagPartner anagPartner;

    @ManyToMany(mappedBy = "anagInstitutions", fetch = FetchType.LAZY)
    @Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
    @JsonIgnore
    private Set<AnagStation> anagStations = new HashSet<>();

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof AnagInstitution)) {
            return false;
        }
        return id != null && id.equals(((AnagInstitution) o).id);
    }

    @Override
    public int hashCode() {
        return 31;
    }

    @Override
    public String toString() {
        return (
            "AnagInstitution{" +
            "id=" +
            id +
            ", fiscalCode='" +
            fiscalCode +
            '\'' +
            ", name='" +
            name +
            '\'' +
            ", aca=" +
            aca +
            ", standInd=" +
            standInd +
            ", activationDate=" +
            activationDate +
            ", anagPartner=" +
            anagPartner +
            ", anagStations=" +
            anagStations +
            "} " +
            super.toString()
        );
    }
}
