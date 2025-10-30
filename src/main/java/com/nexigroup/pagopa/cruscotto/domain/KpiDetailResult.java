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
    @Column(name = "co_id")
    @SequenceGenerator(name = "SQCRUSC8_KPIDETAILRESULT", sequenceName = "SQCRUSC8_KPIDETAILRESULT", allocationSize = 1)
    @GeneratedValue(generator = "SQCRUSC8_KPIDETAILRESULT", strategy = GenerationType.SEQUENCE)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(name = "te_module_code", nullable = false)
    private ModuleCode moduleCode;

    @Column(name = "co_instance_id", nullable = false)
    private Long instanceId;

    @Column(name = "co_instance_module_id", nullable = false)
    private Long instanceModuleId;

    @Column(name = "dt_analisys_date", nullable = false)
    private java.time.LocalDate analysisDate;

    @Enumerated(EnumType.STRING)
    @Column(name = "te_outcome")
    private OutcomeStatus outcome;

    @Column(name = "co_kpi_result_id")
    private Long kpiResultId;

    // KPI-specific data stored as JSON
    @Column(name = "te_data", columnDefinition = "TEXT")
    private String additionalData;
}