package com.nexigroup.pagopa.cruscotto.service.report.repository;

import com.nexigroup.pagopa.cruscotto.service.report.excel.dto.KpiA1ResultReportExcelDTO;
import com.nexigroup.pagopa.cruscotto.service.report.excel.dto.KpiA2ResultReportExcelDTO;
import com.nexigroup.pagopa.cruscotto.service.report.excel.dto.KpiB1ResultReportExcelDTO;
import com.nexigroup.pagopa.cruscotto.service.report.excel.dto.KpiB2ResultReportExcelDTO;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Component;

import java.sql.Timestamp;
import java.sql.Date;
import java.util.List;

@Component
public class QueryReportRepository {

    @PersistenceContext
    private final EntityManager entityManager;

    QueryReportRepository(EntityManager entityManager) {
        this.entityManager = entityManager;
    }


    public List<KpiA1ResultReportExcelDTO> findKpiA1DrilldownForExcel(Long instanceId) {

        String sql = """
        SELECT
            dd.kpi_a1_analytic_data_id,
            dd.from_hour,
            dd.to_hour,
            dd.total_requests,
            dd.ok_requests,
            dd.req_timeout,
            dr.te_outcome,
            dr.dt_evaluation_start_date
        FROM cruscotto.cruscotto.kpi_a1_analytic_drilldown dd
        JOIN cruscotto.cruscotto.kpi_a1_analytic_data ad
            ON ad.co_id = dd.kpi_a1_analytic_data_id
        JOIN cruscotto.cruscotto.kpi_a1_detail_result dr
            ON dr.co_id = ad.co_kpi_a1_detail_result_id
        WHERE dr.co_instance_id = :instanceId
        AND dr.dt_analisys_date = (
            SELECT MAX(dr2.dt_analisys_date)
            FROM cruscotto.cruscotto.kpi_a1_detail_result dr2
            WHERE dr2.co_instance_id = :instanceId
        )
        """;

        List<Object[]> results = entityManager
            .createNativeQuery(sql)
            .setParameter("instanceId", instanceId)
            .getResultList();

        return results.stream()
            .map(r -> new KpiA1ResultReportExcelDTO(
                ((Number) r[0]).longValue(),
                ((Timestamp) r[1]).toInstant(),
                ((Timestamp) r[2]).toInstant(),
                ((Number) r[3]).longValue(),
                ((Number) r[4]).longValue(),
                ((Number) r[5]).longValue(),
                (String) r[6],
                ((Date) r[7]).toLocalDate()
            ))
            .toList();
    }

    public List<KpiA2ResultReportExcelDTO> findKpiA2DrilldownForExcel(Long instanceId) {

        String sql = """
        SELECT
            td.kpi_a2_analytic_data_id,
            td.transfer_category,
            td.co_tot_incorrect_payments,
            td.from_hour,
            td.end_hour,
            td.co_tot_payments,
            dr.te_outcome,
            dr.dt_evaluation_start_date
        FROM cruscotto.cruscotto.kpi_a2_analytic_incorrect_taxonomy_data td
        JOIN cruscotto.cruscotto.kpi_a2_analytic_data ad
            ON ad.co_id = td.kpi_a2_analytic_data_id
        JOIN cruscotto.cruscotto.kpi_a2_detail_result dr
            ON dr.co_id = ad.co_kpi_a2_detail_result_id
        WHERE dr.co_instance_id = :instanceId
        AND dr.dt_analisys_date = (
            SELECT MAX(dr2.dt_analisys_date)
            FROM cruscotto.cruscotto.kpi_a2_detail_result dr2
            WHERE dr2.co_instance_id = :instanceId
        )
        """;

        List<Object[]> results = entityManager
            .createNativeQuery(sql)
            .setParameter("instanceId", instanceId)
            .getResultList();

        return results.stream()
            .map(r -> new KpiA2ResultReportExcelDTO(
                ((Number) r[0]).longValue(),
                (String) r[1],
                ((Number) r[2]).longValue(),
                r[3] != null ? ((Timestamp) r[3]).toInstant() : null,
                r[4] != null ? ((Timestamp) r[4]).toInstant() : null,
                ((Number) r[5]).longValue(),
                (String) r[6],
                ((Date) r[7]).toLocalDate()
            ))
            .toList();
    }

    public List<KpiB1ResultReportExcelDTO> findKpiB1DrilldownForExcel(Long instanceId) {

        String sql = """
        SELECT
            dd.co_kpi_b1_analytic_data_id,
            dd.dt_data_date,
            dd.te_partner_fiscal_code,
            dd.te_institution_fiscal_code,
            dd.co_transaction_count,
            dd.te_station_code,
            dr.te_institution_outcome,
            dr.te_transaction_outcome,
            dr.dt_evaluation_start_date
        FROM cruscotto.cruscotto.kpi_b1_analytic_drill_down dd
        JOIN cruscotto.cruscotto.kpi_b1_analytic_data ad
            ON ad.co_id = dd.co_kpi_b1_analytic_data_id
        JOIN cruscotto.cruscotto.kpi_b1_detail_result dr
            ON dr.co_id = ad.co_kpi_b1_detail_result_id
        WHERE dr.co_instance_id = :instanceId
        AND dr.dt_analisys_date = (
            SELECT MAX(dr2.dt_analisys_date)
            FROM cruscotto.cruscotto.kpi_b1_detail_result dr2
            WHERE dr2.co_instance_id = :instanceId
        )
        """;

        List<Object[]> results = entityManager
            .createNativeQuery(sql)
            .setParameter("instanceId", instanceId)
            .getResultList();

        return results.stream()
            .map(r -> new KpiB1ResultReportExcelDTO(
                ((Number) r[0]).longValue(),
                ((Date) r[1]).toLocalDate(),
                (String) r[2],
                (String) r[3],
                ((Number) r[4]).intValue(),
                (String) r[5],
                (String) r[6],
                (String) r[7],
                ((Date) r[8]).toLocalDate()
            ))
            .toList();
    }

    public List<KpiB2ResultReportExcelDTO> findKpiB2DrilldownForExcel(Long instanceId) {

        String sql = """
        SELECT
            dd.kpi_b2_analytic_data_id,
            dd.from_hour,
            dd.end_hour,
            dd.total_requests,
            dd.ok_requests,
            dd.average_time_ms,
            dr.te_outcome,
            dr.dt_evaluation_start_date
        FROM cruscotto.cruscotto.kpi_b2_analytic_drilldown dd
        JOIN cruscotto.cruscotto.kpi_b2_analytic_data ad
            ON ad.co_id = dd.kpi_b2_analytic_data_id
        JOIN cruscotto.cruscotto.kpi_b2_detail_result dr
            ON dr.co_id = ad.co_kpi_b2_detail_result_id
        WHERE dr.co_instance_id = :instanceId
        AND dr.dt_analisys_date = (
            SELECT MAX(dr2.dt_analisys_date)
            FROM cruscotto.cruscotto.kpi_b2_detail_result dr2
            WHERE dr2.co_instance_id = :instanceId
        )
        """;

        List<Object[]> results = entityManager
            .createNativeQuery(sql)
            .setParameter("instanceId", instanceId)
            .getResultList();

        return results.stream()
            .map(r -> new KpiB2ResultReportExcelDTO(
                ((Number) r[0]).longValue(),
                ((Timestamp) r[1]).toInstant(),
                ((Timestamp) r[2]).toInstant(),
                ((Number) r[3]).longValue(),
                ((Number) r[4]).longValue(),
                ((Number) r[5]).doubleValue(),
                (String) r[6],
                ((Date) r[7]).toLocalDate()
            ))
            .toList();
    }


}
