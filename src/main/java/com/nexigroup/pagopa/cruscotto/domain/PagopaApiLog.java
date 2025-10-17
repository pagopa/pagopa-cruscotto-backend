package com.nexigroup.pagopa.cruscotto.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.Table;
import java.io.Serializable;
import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * A PagopaApiLog entity representing the pagopa_apilog table.
 * This table contains API usage logs from PagoPA for KPI B4 and B8 calculations.
 */
@Entity
@Table(name = "pagopa_apilog")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
@IdClass(PagopaApiLog.PagopaApiLogId.class)
public class PagopaApiLog implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "CF_PARTNER", length = 35, nullable = false)
    private String cfPartner;

    @Id
    @Column(name = "DATE", nullable = false)
    private LocalDate date;

    @Id
    @Column(name = "STATION", length = 35, nullable = false)
    private String station;

    @Id
    @Column(name = "CF_INSTITUTION", length = 35, nullable = false)
    private String cfEnte;

    @Id
    @Column(name = "API", length = 35, nullable = false)
    private String api;

    @Column(name = "REQ_TOTAL", nullable = false)
    private Integer totReq;

    @Column(name = "REQ_OK", nullable = false)
    private Integer reqOk;

    @Column(name = "REQ_KO", nullable = false)
    private Integer reqKo;

    /**
     * Composite primary key for PagopaApiLog entity.
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @EqualsAndHashCode
    public static class PagopaApiLogId implements Serializable {

        private static final long serialVersionUID = 1L;

        private String cfPartner;
        private LocalDate date;
        private String station;
        private String cfEnte;
        private String api;
    }
}