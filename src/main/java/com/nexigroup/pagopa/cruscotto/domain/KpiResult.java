package com.nexigroup.pagopa.cruscotto.domain;

import com.nexigroup.pagopa.cruscotto.domain.enumeration.EvaluationType;
import com.nexigroup.pagopa.cruscotto.domain.enumeration.ModuleCode;
import com.nexigroup.pagopa.cruscotto.domain.enumeration.OutcomeStatus;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import java.io.Serializable;
import java.math.BigDecimal;
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
    @Column(name = "id")
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(name = "module_code", nullable = false)
    private ModuleCode moduleCode;

    @Column(name = "instance_id", nullable = false)
    private Long instanceId;

    @Column(name = "instance_module_id", nullable = false)
    private Long instanceModuleId;

    @Column(name = "analysis_start_date", nullable = false)
    private LocalDate analysisStartDate;

    @Column(name = "analysis_end_date", nullable = false)
    private LocalDate analysisEndDate;

    @Column(name = "partner_fiscal_code", nullable = false)
    private String partnerFiscalCode;

    @Enumerated(EnumType.STRING)
    @Column(name = "evaluation_type", nullable = false)
    private EvaluationType evaluationType;

    @Column(name = "target_value", precision = 21, scale = 2)
    private BigDecimal targetValue;

    @Column(name = "actual_value", precision = 21, scale = 2)
    private BigDecimal actualValue;

    @Column(name = "tolerance", precision = 21, scale = 2)
    private BigDecimal tolerance;

    @Enumerated(EnumType.STRING)
    @Column(name = "outcome", nullable = false)
    private OutcomeStatus outcome;

    @Column(name = "outcome_percentage", precision = 21, scale = 2)
    private BigDecimal outcomePercentage;

    @Column(name = "calculation_date", nullable = false)
    private LocalDate calculationDate;

    // Additional fields for specific KPI metrics (stored as JSON or specific columns)
    @Column(name = "additional_metrics", columnDefinition = "TEXT")
    private String additionalMetrics; // JSON string for KPI-specific data

    @Column(name = "total_entities")
    private Integer totalEntities;

    @Column(name = "compliant_entities")
    private Integer compliantEntities;

    @Column(name = "non_compliant_entities")
    private Integer nonCompliantEntities;
}