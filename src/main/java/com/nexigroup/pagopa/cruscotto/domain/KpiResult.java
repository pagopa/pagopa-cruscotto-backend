package com.nexigroup.pagopa.cruscotto.domain;

import com.nexigroup.pagopa.cruscotto.domain.enumeration.ModuleCode;
import com.nexigroup.pagopa.cruscotto.domain.enumeration.OutcomeStatus;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import java.io.Serializable;
import java.time.LocalDate;

/**
 * Generic KPI Result entity that can handle any KPI type.
 * Uses discriminator pattern to support multiple KPI types in single table.
 */
@Entity
@Table(name = "kpi_result")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@Data
@EqualsAndHashCode(callSuper = true)
public class KpiResult extends AbstractAuditingEntity<Long> implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "co_id")
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(name = "te_module_code", nullable = false)
    private ModuleCode moduleCode;

    @Column(name = "co_instance_id", nullable = false)
    private Long instanceId;

    @Column(name = "co_instance_module_id", nullable = false)
    private Long instanceModuleId;

    @Column(name = "dt_analisys_date", nullable = false)
    private LocalDate analysisDate;

    @Enumerated(EnumType.STRING)
    @Column(name = "te_outcome", nullable = false)
    private OutcomeStatus outcome;

    // KPI-specific data stored as JSON
    @Column(name = "te_data", columnDefinition = "TEXT")
    private String additionalData;
}