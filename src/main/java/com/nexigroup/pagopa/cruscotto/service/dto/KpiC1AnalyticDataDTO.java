package com.nexigroup.pagopa.cruscotto.service.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import jakarta.validation.constraints.NotNull;
import java.io.Serializable;
import java.time.LocalDate;

/**
 * DTO per KpiC1AnalyticData.
 * Rappresenta i dati analitici granulari del KPI C.1
 * Conforme allo schema OpenAPI.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class KpiC1AnalyticDataDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    @NotNull
    private Long id;

    /**
     * Instance ID
     */
    @NotNull
    private Long instanceId;

    /**
     * Instance Module ID
     */
    @NotNull
    private Long instanceModuleId;

    /**
     * KPI C1 Detail Result ID
     */
    @NotNull
    private Long kpiC1DetailResultId;

    /**
     * Analysis Date
     */
    @NotNull
    private LocalDate analysisDate;

    /**
     * Data Date
     */
    @NotNull
    private LocalDate dataDate;

    /**
     * Institution Count (total institutions)
     */
    @NotNull
    private Integer institutionCount;

    /**
     * Positions Count (total positions aggregated)
     */
    @NotNull
    private Long positionsCount;

    /**
     * Messages Count (total messages aggregated)
     */
    @NotNull
    private Long messagesCount;
}