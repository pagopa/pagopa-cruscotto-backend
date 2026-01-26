package com.nexigroup.pagopa.cruscotto.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.io.Serializable;
import java.time.LocalDate;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

/**
 * A PagopaIO.
 *
 * Represents IO messages data for KPI C.1 calculation.
 * Contains daily data about positions and IO messages sent by each entity.
 */
@Entity
@Table(name = "pagopa_io", indexes = {
    @Index(name = "ix_pagopaio_cf_partner_cf_institution_date", columnList = "cf_partner, cf_institution, date"),
    @Index(name = "ix_pagopaio_cf_institution_date", columnList = "cf_institution, date"),
    @Index(name = "ix_pagopaio_cf_partner_date", columnList = "cf_partner, date")
})
@Getter
@Setter
@DynamicUpdate
@DynamicInsert
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class PagopaIO implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "id")
    @SequenceGenerator(name = "SQCRUSC8_PAGOPAIO", sequenceName = "SQCRUSC8_PAGOPAIO", allocationSize = 1)
    @GeneratedValue(generator = "SQCRUSC8_PAGOPAIO", strategy = GenerationType.SEQUENCE)
    private Long id;

    @Size(max = 35)
    @Column(name = "cf_partner", length = 35)
    private String cfPartner;

    @NotNull
    @Size(max = 35)
    @Column(name = "cf_institution", length = 35, nullable = false)
    private String cfInstitution;

    @NotNull
    @Column(name = "date", nullable = false)
    private LocalDate date;

    @NotNull
    @Column(name = "position_number", nullable = false)
    private Long positionNumber;

    @NotNull
    @Column(name = "message_number", nullable = false)
    private Long messageNumber;

    // Constructors
    public PagopaIO() {}

    public PagopaIO(String cfPartner, String cfInstitution, LocalDate date, Long positionNumber, Long messageNumber) {
        this.cfPartner = cfPartner;
        this.cfInstitution = cfInstitution;
        this.date = date;
        this.positionNumber = positionNumber;
        this.messageNumber = messageNumber;
    }

    /**
     * Calculate the percentage of messages sent vs debt positions
     * @return percentage as double (0-100)
     */
    public double getPercentualeMessaggi() {
        if (positionNumber == null || positionNumber == 0) {
            return messageNumber != null && messageNumber > 0 ? 100.0 : 0.0;
        }
        if (messageNumber == null) {
            return 0.0;
        }
        return (double) messageNumber / positionNumber * 100.0;
    }

    /**
     * Check if this record meets the specified tolerance threshold
     * NUOVA LOGICA: un ente è OK (meets tolerance) se percentuale >= tolleranza
     * (cioè raggiunge o supera la soglia di messaggi richiesta)
     * @param toleranceThreshold the minimum percentage threshold
     * @return true if percentage meets or exceeds threshold
     */
    public boolean meetsToleranceThreshold(double toleranceThreshold) {
        return getPercentualeMessaggi() >= toleranceThreshold;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof PagopaIO)) {
            return false;
        }
        return id != null && id.equals(((PagopaIO) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "PagopaIO{" +
            "id=" + getId() +
            ", cfPartner='" + getCfPartner() + "'" +
            ", cfInstitution='" + getCfInstitution() + "'" +
            ", date='" + getDate() + "'" +
            ", positionNumber=" + getPositionNumber() +
            ", messageNumber=" + getMessageNumber() +
            ", percentuale=" + String.format(java.util.Locale.US, "%.2f%%", getPercentualeMessaggi()) +
            "}";
    }
}