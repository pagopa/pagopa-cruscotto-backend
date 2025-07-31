package com.nexigroup.pagopa.cruscotto.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.nexigroup.pagopa.cruscotto.domain.enumeration.PartnerStatus;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.io.Serializable;
import java.time.Instant;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;
import lombok.*;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * A AnagPartner.
 */
@Entity
@Table(name = "ANAG_PARTNER")
@Getter
@Setter
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class AnagPartner extends AbstractAuditingEntity<Long> implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "CO_ID")
    @SequenceGenerator(name = "SQCRUSC8_ANAGPARTN", sequenceName = "SQCRUSC8_ANAGPARTN")
    @GeneratedValue(generator = "SQCRUSC8_ANAGPARTN", strategy = GenerationType.SEQUENCE)
    private Long id;

    @NotNull
    @Size(min = 1, max = 35)
    @Column(name = "TE_FISCAL_CODE", length = 35, nullable = false)
    private String fiscalCode;

    @Size(min = 1, max = 256)
    @Column(name = "TE_NAME", length = 256)
    private String name;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "TE_STATUS", nullable = false)
    private PartnerStatus status;

    @Column(name = "FL_QUALIFIED", nullable = false)
    private Boolean qualified = false;

    @Column(name = "DT_DEACTIVATION_DATE")
    private LocalDate deactivationDate;

    @Column(name = "DT_LAST_ANALYSIS_DATE")
    private LocalDate lastAnalysisDate;

    @Column(name = "DT_ANALYSIS_PERIOD_START_DATE")
    private LocalDate analysisPeriodStartDate;

    @Column(name = "DT_ANALYSIS_PERIOD_END_DATE")
    private LocalDate analysisPeriodEndDate;

    @Column(name = "QT_STATIONS_COUNT")
    private Long stationsCount;

    @OneToMany(mappedBy = "anagPartner", fetch = FetchType.LAZY)
    @JsonIgnore
    private Set<AnagStation> anagStations = new HashSet<>();

    @OneToMany(mappedBy = "anagPartner", fetch = FetchType.LAZY)
    @JsonIgnore
    private Set<AnagPlannedShutdown> anagPlannedShutdowns = new HashSet<>();

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof AnagPartner)) {
            return false;
        }
        return id != null && id.equals(((AnagPartner) o).id);
    }

    @Override
    public int hashCode() {
        return 31;
    }

    @Override
    public String toString() {
        return (
            "AnagPartner{" +
            "id=" +
            id +
            ", fiscalCode='" +
            fiscalCode +
            '\'' +
            ", name='" +
            name +
            '\'' +
            ", status=" +
            status +
            ", qualified=" +
            qualified +
            ", deactivationDate=" +
            deactivationDate +
            ", lastAnalysisDate=" +
            lastAnalysisDate +
            ", analysisPeriodStartDate=" +
            analysisPeriodStartDate +
            ", analysisPeriodEndDate=" +
            analysisPeriodEndDate +
            ", stationsCount=" +
            stationsCount +
            ", anagStations=" +
            anagStations +
            ", anagInstitutions=" +
            anagInstitutions +
            ", anagPlannedShutdowns=" +
            anagPlannedShutdowns +
            "} " +
            super.toString()
        );
    }
}
