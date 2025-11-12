package com.nexigroup.pagopa.cruscotto.domain;

import com.nexigroup.pagopa.cruscotto.domain.enumeration.ModuleCode;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import java.io.Serializable;

/**
 * Generic KPI Analytic Data entity that can handle analytic data for any KPI type.
 * Uses discriminator pattern to support multiple KPI types in single table.
 */
@Entity
@Table(name = "kpi_analytic_data")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class KpiAnalyticData extends AbstractAuditingEntity<Long> implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @EqualsAndHashCode.Include
    @Column(name = "co_id")
    @SequenceGenerator(name = "SQCRUSC8_KPIANALYTICDATA", sequenceName = "SQCRUSC8_KPIANALYTICDATA", allocationSize = 1)
    @GeneratedValue(generator = "SQCRUSC8_KPIANALYTICDATA", strategy = GenerationType.SEQUENCE)
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

    @Column(name = "dt_data_date", nullable = false)
    private java.time.LocalDate dataDate;

    @Column(name = "co_kpi_detail_result_id", nullable = false)
    private Long kpiDetailResultId;

    // KPI-specific analytic data stored as JSON
    @Column(name = "te_data", columnDefinition = "TEXT")
    private String analyticData;
}
