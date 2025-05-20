package com.nexigroup.pagopa.cruscotto.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.nexigroup.pagopa.cruscotto.domain.enumeration.TypePlanned;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import java.io.Serializable;
import java.time.Instant;
import lombok.*;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * A AnagPlannedShutdown.
 */
@Entity
@Table(name = "ANAG_PLANNED_SHUTDOWN")
@Getter
@Setter
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class AnagPlannedShutdown extends AbstractAuditingEntity<Long> implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "CO_ID")
    @SequenceGenerator(name = "SQDASH_PLSH01", sequenceName = "SQDASH_PLSH01", allocationSize = 1)
    @GeneratedValue(generator = "SQDASH_PLSH01", strategy = GenerationType.SEQUENCE)
    private Long id;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "TE_TYPE_PLANNED", length = 35, nullable = false)
    private TypePlanned typePlanned;

    @NotNull
    @Column(name = "FL_STAND_IND", nullable = false)
    private boolean standInd = false;

    @NotNull
    @Column(name = "DT_SHUTDOWN_START_DATE", nullable = false)
    private Instant shutdownStartDate;

    @NotNull
    @Column(name = "DT_SHUTDOWN_END_DATE", nullable = false)
    private Instant shutdownEndDate;

    @NotNull
    @Column(name = "CO_YEAR")
    private Long year;

    @Column(name = "CO_EXTERNAL_ID")
    private Long externalId;

    @JsonIgnore
    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "CO_ANAG_PARTNER_ID", nullable = false)
    private AnagPartner anagPartner;

    @JsonIgnore
    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "CO_ANAG_STATION_ID", nullable = false)
    private AnagStation anagStation;



    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof AnagPlannedShutdown)) {
            return false;
        }
        return id != null && id.equals(((AnagPlannedShutdown) o).id);
    }

    @Override
    public int hashCode() {
        return 31;
    }

    @Override
    public String toString() {
        return "AnagPlannedShutdown{" +
            "id=" + id +
            ", typePlanned=" + typePlanned +
            ", standInd=" + standInd +
            ", shutdownStartDate=" + shutdownStartDate +
            ", shutdownEndDate=" + shutdownEndDate +
            ", year=" + year +
            ", externalId=" + externalId +
            ", anagPartner=" + anagPartner +
            ", anagStation=" + anagStation +
            ", createdBy='" + getCreatedBy() +
            ", createdDate=" + getCreatedDate() +
            ", lastModifiedBy='" + getLastModifiedBy() +
            ", lastModifiedDate=" + getLastModifiedDate();
    }
}
