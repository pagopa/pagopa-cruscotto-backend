package com.nexigroup.pagopa.cruscotto.domain;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import java.io.Serializable;
import java.time.Instant;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

/**
 * A PagoPaTaxonomyAggregatePosition.
 */

@Entity
@Table(name = "PAGOPA_TAXONOMY_AGGREGATE_POSITION")
@Getter
@Setter
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class PagoPaTaxonomyAggregatePosition implements Serializable {

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
    @Size(min = 1, max = 35)
    @Column(name = "TRANSFER_CATEGORY", length = 35, nullable = false)
    private String transferCategory;

    @NotNull
    @Column(name = "START_DATE", nullable = false)
    private Instant startDate;

    @NotNull
    @Column(name = "END_DATE", nullable = false)
    private Instant endDate;

    @NotNull
    @Column(name = "TOTAL", nullable = false)
    private Long total;


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (!(o instanceof PagoPaTaxonomyAggregatePosition that)) return false;

        return new EqualsBuilder()
            .append(cfPartner, that.cfPartner)
            .append(station, that.station)
            .append(transferCategory, that.transferCategory)
            .append(startDate, that.startDate)
            .append(endDate, that.endDate)
            .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37).append(cfPartner).append(station).append(transferCategory).append(startDate).append(endDate).toHashCode();
    }

    @Override
	public String toString() {
		return "PagoPaTaxonomyAggregatePosition [id=" + id + ", cfPartner=" + cfPartner + ", station=" + station
				+ ", transferCategory=" + transferCategory + ", startDate=" + startDate + ", endDate=" + endDate
				+ ", total=" + total + "]";
	}
}
