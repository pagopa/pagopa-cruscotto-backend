package com.nexigroup.pagopa.cruscotto.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * A PagopaSpontaneous entity representing the PAGOPA_SPONTANEOUS table.
 * This table contains spontaneous payments configuration for stations.
 */
@Entity
@Table(name = "PAGOPA_SPONTANEOUS")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class PagopaSpontaneous implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private Long id;

    @NotNull
    @Column(name = "CF_PARTNER", length = 255, nullable = false)
    private String cfPartner;

    @NotNull
    @Column(name = "STATION", length = 255, nullable = false)
    private String station;

    @NotNull
    @Column(name = "SPONTANEOUS_PAYMENT", nullable = false)
    private Boolean spontaneousPayment;
}