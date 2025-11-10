package com.nexigroup.pagopa.cruscotto.domain;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Entity che rappresenta la tabella PAGOPA_SEND.
 * Contiene informazioni sui pagamenti e notifiche inviate da PagoPA.
 */
@Entity
@Table(name = "PAGOPA_SEND")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class PagopaSend implements Serializable {

    private static final long serialVersionUID = 1L;

    /** Chiave primaria autoincrementale */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID", nullable = false, updatable = false)
    private Long id;

    /** Codice fiscale del partner */
    @Column(name = "CF_PARTNER", length = 255, nullable = false)
    private String cfPartner;

    /** Codice fiscale dell'ente (istituzione) */
    @Column(name = "CF_INSTITUTION", length = 255, nullable = false)
    private String cfInstitution;

    /** Data e ora dell’invio */
    @Column(name = "DATE", nullable = false)
    private LocalDateTime date;

    /** Numero di pagamenti inviati */
    @Column(name = "PAYMENTS_NUMBER", nullable = false)
    private Long paymentsNumber;

    /** Numero di notifiche inviate */
    @Column(name = "NOTIFICATION_NUMBER", nullable = false)
    private Long notificationNumber;


}
