package com.nexigroup.pagopa.cruscotto.domain;

import jakarta.persistence.*;
import lombok.Data;
import java.io.Serializable;

@Entity
@Table(name = "kpi_a2_analytic_incorrect_taxonomy_data")
@Data
public class KpiA2AnalyticIncorrectTaxonomyData implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SQCRUSC8_KPIA2ANALINCTAXDATA")
    @SequenceGenerator(name = "SQCRUSC8_KPIA2ANALINCTAXDATA", sequenceName = "SQCRUSC8_KPIA2ANALINCTAXDATA", allocationSize = 1)
    private Long id;

    @Column(name = "kpi_a2_analytic_data_id", nullable = false)
    private Long kpiA2AnalyticDataId;

    @Column(name = "transfer_category", nullable = false)
    private String transferCategory;

    @Column(name = "total", nullable = false)
    private Long total;

    @Column(name = "from_hour")
    private java.time.Instant fromHour;

    @Column(name = "end_hour")
    private java.time.Instant endHour;
}
