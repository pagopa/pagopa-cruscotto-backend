package com.nexigroup.pagopa.cruscotto.domain;

import jakarta.persistence.*;
import java.io.Serializable;
import java.util.Objects;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

@Setter
@Getter
@Entity
@IdClass(AnagStationAnagInstitutionId.class)
@DynamicUpdate
@DynamicInsert
@Table(name = "ANAG_STATION_ANAG_INSTITUTION")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class AnagStationAnagInstitution implements Serializable {

    private static final long serialVersionUID = 2063225717101367016L;

    @Id
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "CO_ANAG_STATION_ID", nullable = false)
    private AnagStation anagStation;

    @Id
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "CO_ANAG_INSTITUTION_ID", nullable = false)
    private AnagInstitution anagInstitution;

    @Column(name = "ACA")
    private Boolean aca;

    @Column(name = "STANDIN")
    private Boolean standin;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AnagStationAnagInstitution that = (AnagStationAnagInstitution) o;
        return Objects.equals(anagStation, that.anagStation) && Objects.equals(anagInstitution, that.anagInstitution);
    }

    @Override
    public int hashCode() {
        return Objects.hash(anagStation, anagInstitution);
    }
}
