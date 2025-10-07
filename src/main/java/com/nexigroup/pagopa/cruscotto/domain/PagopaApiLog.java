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
    @Column(name = "cf_partner", length = 35, nullable = false)
    private String cfPartner;

    @Id
    @Column(name = "date", nullable = false)
    private LocalDate date;

    @Id
    @Column(name = "station", length = 35, nullable = false)
    private String station;

    @Id
    @Column(name = "cf_ente", length = 35, nullable = false)
    private String cfEnte;

    @Id
    @Column(name = "api", length = 35, nullable = false)
    private String api;

    @Column(name = "tot_req", nullable = false)
    private Integer totReq;

    @Column(name = "req_ok", nullable = false)
    private Integer reqOk;

    @Column(name = "req_ko", nullable = false)
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