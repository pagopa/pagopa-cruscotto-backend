package com.nexigroup.pagopa.cruscotto.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

/**
 * A PagopaTransazioni.
 */
@Entity
@Table(name = "PAGOPA_TRANSAZIONI")
@Getter
@Setter
@DynamicUpdate
@DynamicInsert
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class PagopaTransazioni implements Serializable {

    private static final long serialVersionUID = -6090145346504076144L;

    @Id
    @Column(name = "CO_ID")
    @SequenceGenerator(name = "SQCRUSC8_PAGOPATRA", sequenceName = "SQCRUSC8_PAGOPATRA", allocationSize = 1)
    @GeneratedValue(generator = "SQCRUSC8_PAGOPATRA", strategy = GenerationType.SEQUENCE)
    private Long id;

    @JsonIgnore
    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "CO_INSTANCE_ID", nullable = false)
    private Instance instance;

    @NotNull
    @Size(min = 1, max = 35)
    @Column(name = "TE_PARTNER_CODE", length = 35, nullable = false)
    private String partnerCode;

    @Size(max = 100)
    @Column(name = "TE_PARTNER_NAME", length = 100)
    private String partnerName;

    @NotNull
    @Size(min = 1, max = 35)
    @Column(name = "TE_ENTITY_CODE", length = 35, nullable = false)
    private String entityCode;

    @Size(max = 100)
    @Column(name = "TE_ENTITY_NAME", length = 100)
    private String entityName;

    @NotNull
    @Size(min = 1, max = 50)
    @Column(name = "TE_TRANSACTION_ID", length = 50, nullable = false)
    private String transactionId;

    @NotNull
    @Column(name = "DT_TRANSACTION_DATE", nullable = false)
    private LocalDateTime transactionDate;

    @NotNull
    @Column(name = "DT_REFERENCE_DATE", nullable = false)
    private LocalDate referenceDate;

    @Column(name = "CO_TRANSACTION_AMOUNT", precision = 19, scale = 2)
    private BigDecimal transactionAmount;

    @Size(max = 10)
    @Column(name = "TE_CURRENCY_CODE", length = 10)
    private String currencyCode;

    @Size(max = 50)
    @Column(name = "TE_PAYMENT_TYPE", length = 50)
    private String paymentType;

    @Size(max = 20)
    @Column(name = "TE_TRANSACTION_STATUS", length = 20)
    private String transactionStatus;

    @Size(max = 255)
    @Column(name = "TE_TRANSACTION_DESCRIPTION", length = 255)
    private String transactionDescription;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (!(o instanceof PagopaTransazioni that)) return false;

        return new EqualsBuilder().append(id, that.id).isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37).append(id).toHashCode();
    }

    @Override
    public String toString() {
        return (
            "PagopaTransazioni{" +
            "id=" +
            id +
            ", instance=" +
            instance +
            ", partnerCode='" +
            partnerCode + '\'' +
            ", partnerName='" +
            partnerName + '\'' +
            ", entityCode='" +
            entityCode + '\'' +
            ", entityName='" +
            entityName + '\'' +
            ", transactionId='" +
            transactionId + '\'' +
            ", transactionDate=" +
            transactionDate +
            ", referenceDate=" +
            referenceDate +
            ", transactionAmount=" +
            transactionAmount +
            ", currencyCode='" +
            currencyCode + '\'' +
            ", paymentType='" +
            paymentType + '\'' +
            ", transactionStatus='" +
            transactionStatus + '\'' +
            ", transactionDescription='" +
            transactionDescription + '\'' +
            '}'
        );
    }
}