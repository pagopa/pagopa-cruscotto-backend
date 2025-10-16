package com.nexigroup.pagopa.cruscotto.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.io.Serializable;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

/**
 * A PagopaApiLog.
 *
 * Represents API log data collected from PagoPA.
 */
@Entity
@Table(name = "pagopa_apilog", schema = "public", catalog = "qualification_dashboard")
@Getter
@Setter
@DynamicUpdate
@DynamicInsert
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class PagopaApiLog implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "id")
    private Long id;

    @NotNull
    @Size(max = 255)
    @Column(name = "cf_partner", nullable = false, length = 255)
    private String cfPartner;

    @NotNull
    @Column(name = "date", nullable = false)
    private LocalDateTime date;

    @NotNull
    @Size(max = 255)
    @Column(name = "station", nullable = false, length = 255)
    private String station;

    @NotNull
    @Size(max = 255)
    @Column(name = "cf_institution", nullable = false, length = 255)
    private String cfInstitution;

    @NotNull
    @Size(max = 255)
    @Column(name = "api", nullable = false, length = 255)
    private String api;

    @NotNull
    @Column(name = "req_total", nullable = false)
    private Long reqTotal;

    @NotNull
    @Column(name = "req_ok", nullable = false)
    private Long reqOk;

    @NotNull
    @Column(name = "req_ko", nullable = false)
    private Long reqKo;

    // JHipster needle - entity add field

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof PagopaApiLog)) return false;
        return id != null && id.equals(((PagopaApiLog) o).id);
    }

    @Override
    public int hashCode() {
        return 31;
    }

    @Override
    public String toString() {
        return "PagopaApiLog{" +
            "id=" + id +
            ", cfPartner='" + cfPartner + '\'' +
            ", date=" + date +
            ", station='" + station + '\'' +
            ", cfInstitution='" + cfInstitution + '\'' +
            ", api='" + api + '\'' +
            ", reqTotal=" + reqTotal +
            ", reqOk=" + reqOk +
            ", reqKo=" + reqKo +
            '}';
    }
}
