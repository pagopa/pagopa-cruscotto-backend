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
@EqualsAndHashCode(callSuper = true)
public class KpiAnalyticData extends AbstractAuditingEntity<Long> implements Serializable {

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

    // KPI-specific analytic data stored as JSON
    @Column(name = "analytic_data", columnDefinition = "TEXT")
    private String analyticData;

    // Additional fields for common analytic data
    @Column(name = "station_name")
    private String stationName;

    @Column(name = "partner_fiscal_code")
    private String partnerFiscalCode;

    @Column(name = "analysis_period_start")
    private String analysisPeriodStart;

    @Column(name = "analysis_period_end")
    private String analysisPeriodEnd;
}