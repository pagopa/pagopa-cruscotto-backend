package com.nexigroup.pagopa.cruscotto.service.report.excel;

import com.nexigroup.pagopa.cruscotto.domain.KpiB2AnalyticDrillDown;
import com.nexigroup.pagopa.cruscotto.repository.KpiB2AnalyticDataRepository;
import com.nexigroup.pagopa.cruscotto.repository.KpiB2AnalyticDrillDownRepository;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Component
@Order(4) // ordine dopo B1
public class KpiB2AnalyticDrillDownExcelExporter
    implements DrillDownExcelExporter {

    private final KpiB2AnalyticDataRepository analyticDataRepository;
    private final KpiB2AnalyticDrillDownRepository drillDownRepository;

    private static final DateTimeFormatter HOUR_FMT =
        DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")
            .withZone(ZoneId.systemDefault());

    public KpiB2AnalyticDrillDownExcelExporter(
        KpiB2AnalyticDataRepository analyticDataRepository,
        KpiB2AnalyticDrillDownRepository drillDownRepository
    ) {
        this.analyticDataRepository = analyticDataRepository;
        this.drillDownRepository = drillDownRepository;
    }

    @Override
    public String getSheetName() {
        return "KPI_B2_ANALYTIC_DRILLDOWN";
    }

    @Override
    public int getOrder() {
        return 4;
    }

    @Override
    public List<KpiB2AnalyticDrillDown> loadData(String instanceCode) {

        Long instanceId = Long.valueOf(instanceCode);

        List<Long> analyticDataIds =
            analyticDataRepository.findLatestAnalyticDataIdsByInstanceId(instanceId);

        if (analyticDataIds == null || analyticDataIds.isEmpty()) {
            return List.of();
        }

        return drillDownRepository
            .findByKpiB2AnalyticDataIdIn(analyticDataIds);
    }

    @Override
    public void writeSheet(Sheet sheet, List<?> data) {

        int rowIdx = 0;

        // ===== HEADER =====
        Row header = sheet.createRow(rowIdx++);
        header.createCell(0).setCellValue("From Hour");
        header.createCell(1).setCellValue("End Hour");
        header.createCell(2).setCellValue("Total Requests");
        header.createCell(3).setCellValue("OK Requests");
        header.createCell(4).setCellValue("Average Time (ms)");

        // ===== NO DATA =====
        if (data == null || data.isEmpty()) {
            Row row = sheet.createRow(rowIdx);
            row.createCell(0).setCellValue("NO DATA FOUND");
            return;
        }

        @SuppressWarnings("unchecked")
        List<KpiB2AnalyticDrillDown> rows =
            (List<KpiB2AnalyticDrillDown>) data;

        // ===== DATA =====
        for (KpiB2AnalyticDrillDown r : rows) {
            Row row = sheet.createRow(rowIdx++);
            row.createCell(0).setCellValue(
                r.getFromHour() != null ? HOUR_FMT.format(r.getFromHour()) : ""
            );
            row.createCell(1).setCellValue(
                r.getEndHour() != null ? HOUR_FMT.format(r.getEndHour()) : ""
            );
            row.createCell(2).setCellValue(r.getTotalRequests());
            row.createCell(3).setCellValue(r.getOkRequests());
            row.createCell(4).setCellValue(
                r.getAverageTimeMs() != null ? r.getAverageTimeMs() : 0d
            );
        }

        // Autosize colonne
        for (int i = 0; i <= 4; i++) {
            sheet.autoSizeColumn(i);
        }
    }
}
