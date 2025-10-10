package com.nexigroup.pagopa.cruscotto.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
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
 * A PagopaNumeroStandin.
 * 
 * Represents aggregated Stand-In events data loaded daily from PagoPA API.
 * Events are aggregated by 15-minute intervals and by station for performance optimization.
 */
@Entity
@Table(name = "STANDIN_NUMBER")
@Getter
@Setter
@DynamicUpdate
@DynamicInsert
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class PagopaNumeroStandin implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "CO_ID")
    @SequenceGenerator(name = "SQCRUSC8_PAGOPASTANDIN", sequenceName = "SQCRUSC8_PAGOPASTANDIN", allocationSize = 1)
    @GeneratedValue(generator = "SQCRUSC8_PAGOPASTANDIN", strategy = GenerationType.SEQUENCE)
    private Long id;

    @NotNull
    @Size(max = 255)
    @Column(name = "TE_STATION_CODE", length = 255, nullable = false)
    private String stationCode;

    @NotNull
    @Column(name = "DT_INTERVAL_START", nullable = false)
    private LocalDateTime intervalStart;

    @NotNull
    @Column(name = "DT_INTERVAL_END", nullable = false)
    private LocalDateTime intervalEnd;

    @NotNull
    @Column(name = "CO_STANDIN_COUNT", nullable = false)
    private Integer standInCount;

    @Size(max = 50)
    @Column(name = "TE_EVENT_TYPE", length = 50)
    private String eventType;

    @NotNull
    @Column(name = "DT_DATA_DATE", nullable = false)
    private LocalDateTime dataDate;

    @Column(name = "DT_LOAD_TIMESTAMP")
    private LocalDateTime loadTimestamp;

    // JHipster needle - entity add field

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof PagopaNumeroStandin)) {
            return false;
        }
        return id != null && id.equals(((PagopaNumeroStandin) o).id);
    }

    @Override
    public int hashCode() {
        return 31;
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "PagopaNumeroStandin{" +
            "id=" + id +
            ", stationCode='" + stationCode + "'" +
            ", intervalStart='" + intervalStart + "'" +
            ", intervalEnd='" + intervalEnd + "'" +
            ", standInCount=" + standInCount +
            ", eventType='" + eventType + "'" +
            ", dataDate='" + dataDate + "'" +
            ", loadTimestamp='" + loadTimestamp + "'" +
            "}";
    }
}