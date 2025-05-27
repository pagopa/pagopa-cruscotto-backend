package com.nexigroup.pagopa.cruscotto.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.io.Serializable;
import java.time.LocalDate;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * A Taxonomy.
 */
@Entity
@Table(name = "TAXONOMY")
@Getter
@Setter
@ToString
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class Taxonomy extends AbstractAuditingEntity<Long> implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "CO_ID")
    @SequenceGenerator(name = "SQCRUSC8_TAXO", sequenceName = "SQCRUSC8_TAXO")
    @GeneratedValue(generator = "SQCRUSC8_TAXO", strategy = GenerationType.SEQUENCE)
    private Long id;

    @Size(min = 1, max = 10)
    @Column(name = "TE_INSTITUTION_TYPE_CODE", length = 10)
    private String institutionTypeCode;

    @Size(min = 1, max = 255)
    @Column(name = "TE_INSTITUTION_TYPE", length = 255)
    private String institutionType;

    @Size(min = 1, max = 10)
    @Column(name = "TE_AREA_PROGRESSIVE_CODE", length = 10)
    private String areaProgressiveCode;

    @Size(min = 1, max = 100)
    @Column(name = "TE_AREA_NAME", length = 100)
    private String areaName;

    @Size(min = 1, max = 500)
    @Column(name = "TE_AREA_DESCRIPTION", length = 500)
    private String areaDescription;

    @Size(min = 1, max = 10)
    @Column(name = "TE_SERVICE_TYPE_CODE", length = 10)
    private String serviceTypeCode;

    @Size(min = 1, max = 100)
    @Column(name = "TE_SERVICE_TYPE", length = 100)
    private String serviceType;

    @Size(min = 1, max = 1000)
    @Column(name = "TE_SERVICE_TYPE_DESCRIPTION", length = 1000)
    private String serviceTypeDescription;

    @Size(min = 1, max = 10)
    @Column(name = "TE_VERSION", length = 10)
    private String version;

    @Size(min = 1, max = 10)
    @Column(name = "TE_REASON_COLLECTION", length = 10)
    private String reasonCollection;

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
}
