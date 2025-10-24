package com.nexigroup.pagopa.cruscotto.domain;

import com.nexigroup.pagopa.cruscotto.domain.enumeration.ModuleCode;
import com.nexigroup.pagopa.cruscotto.domain.enumeration.OutcomeStatus;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import java.io.Serializable;

/**
 * Generic KPI Detail Result entity that can handle detail results for any KPI type.
 * Uses discriminator pattern to support multiple KPI types in single table.
 */
@Entity
@Table(name = "kpi_detail_result")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@Data
@EqualsAndHashCode(callSuper = true)
public class KpiDetailResult extends AbstractAuditingEntity<Long> implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(name = "module_code", nullable = false)
    private ModuleCode moduleCode;

    @Column(name = "instance_id", nullable = false)
    private Long instanceId;

    @Column(name = "instance_module_id", nullable = false)
    private Long instanceModuleId;

    @Column(name = "station_code", nullable = false)
    private String stationCode;

    @Enumerated(EnumType.STRING)
    @Column(name = "outcome", nullable = false)
    private OutcomeStatus outcome;

    // KPI-specific data stored as JSON
    @Column(name = "additional_data", columnDefinition = "TEXT")
    private String additionalData;

    // Station-specific information
    @Column(name = "station_name")
    private String stationName;

    @Column(name = "partner_fiscal_code")
    private String partnerFiscalCode;
}