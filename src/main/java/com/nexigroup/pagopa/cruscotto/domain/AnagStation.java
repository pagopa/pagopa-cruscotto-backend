package com.nexigroup.pagopa.cruscotto.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.nexigroup.pagopa.cruscotto.domain.enumeration.StationStatus;
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
 * A AnagStation.
 */
@Entity
@Table(name = "ANAG_STATION")
@Getter
@Setter
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class AnagStation extends AbstractAuditingEntity<Long> implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "CO_ID")
    @SequenceGenerator(name = "SQCRUSC8_ANAGSTAT", sequenceName = "SQCRUSC8_ANAGSTAT", allocationSize = 1)
    @GeneratedValue(generator = "SQCRUSC8_ANAGSTAT", strategy = GenerationType.SEQUENCE)
    private Long id;

    @NotNull
    @Size(min = 1, max = 35)
    @Column(name = "TE_NAME", length = 35, nullable = false)
    private String name;

    @Column(name = "DT_ACTIVATION_DATE")
    private LocalDate activationDate;

    @Size(min = 1, max = 35)
    @Column(name = "TE_TYPE_CONNECTION", length = 35)
    private String typeConnection;

    @Column(name = "TE_PRIMITIVE_VERSION")
    private Integer primitiveVersion;

    @Column(name = "FL_PAYMENT_OPTION")
    private Boolean paymentOption = false;

    @NotNull
    @Column(name = "CO_ASSOCIATED_INSTITUTES", nullable = false)
    private Integer associatedInstitutes;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "TE_STATUS", nullable = false)
    private StationStatus status;

    @Column(name = "DT_DEACTIVATION_DATE")
    private LocalDate deactivationDate;

    @JsonIgnore
    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "CO_ANAG_PARTNER_ID", nullable = false)
    private AnagPartner anagPartner;

    @OneToMany(mappedBy = "anagStation", fetch = FetchType.LAZY)
    @JsonIgnore
    private Set<AnagPlannedShutdown> anagPlannedShutdowns = new HashSet<>();

    @ManyToMany(fetch = FetchType.LAZY)
    @Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
    @JoinTable(
        name = "ANAG_STATION_ANAG_INSTITUTION",
        joinColumns = @JoinColumn(name = "CO_ANAG_STATION_ID", referencedColumnName = "CO_ID"),
        inverseJoinColumns = @JoinColumn(name = "CO_ANAG_INSTITUTION_ID", referencedColumnName = "CO_ID")
    )
    private Set<AnagInstitution> anagInstitutions = new HashSet<>();

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof AnagStation)) {
            return false;
        }
        return id != null && id.equals(((AnagStation) o).id);
    }

    @Override
    public int hashCode() {
        return 31;
    }

    @Override
    public String toString() {
        return (
            "AnagStation{" +
            "id=" +
            id +
            ", name='" +
            name +
            '\'' +
            ", activationDate=" +
            activationDate +
            ", typeConnection='" +
            typeConnection +
            '\'' +
            ", primitiveVersion='" +
            primitiveVersion +
            '\'' +
            ", paymentOption=" +
            paymentOption +
            ", associatedInstitutes=" +
            associatedInstitutes +
            ", status=" +
            status +
            ", deactivationDate=" +
            deactivationDate +
            ", anagPartner=" +
            anagPartner +
            ", anagPlannedShutdowns=" +
            anagPlannedShutdowns +
            ", anagInstitutions=" +
            anagInstitutions +
            "} " +
            super.toString()
        );
    }
}
