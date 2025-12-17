package com.nexigroup.pagopa.cruscotto.service.excel.exporter;

import com.nexigroup.pagopa.cruscotto.domain.KpiA1AnalyticDrillDown;
import com.nexigroup.pagopa.cruscotto.repository.KpiA1AnalyticDataRepository;
import com.nexigroup.pagopa.cruscotto.repository.KpiA1AnalyticDrillDownRepository;

import com.nexigroup.pagopa.cruscotto.service.report.excel.DrillDownExcelExporter;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Component
@Order(1)
public class KpiA1AnalyticDrillDownExporter implements DrillDownExcelExporter {

    private final KpiA1AnalyticDrillDownRepository drillDownRepository;
    private final KpiA1AnalyticDataRepository analyticDataRepository;

    private static final DateTimeFormatter HOUR_FMT =
        DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")
            .withZone(ZoneId.systemDefault());

    public KpiA1AnalyticDrillDownExporter(
        KpiA1AnalyticDrillDownRepository drillDownRepository,
        KpiA1AnalyticDataRepository analyticDataRepository
    ) {
        this.drillDownRepository = drillDownRepository;
        this.analyticDataRepository = analyticDataRepository;
    }

    @Override
    public String getSheetName() {
        return "KPI_A1_ANALYTIC";
    }

    @Override
    public int getOrder() {
        return 1;
    }

    @Override
    public List<KpiA1AnalyticDrillDown> loadData(String instanceCode) {

        Long instanceId = Long.valueOf(instanceCode);

        List<Long> latestAnalyticDataIdByInstanceId = analyticDataRepository.findLatestAnalyticDataIdByInstanceId(instanceId);

        if (latestAnalyticDataIdByInstanceId == null || latestAnalyticDataIdByInstanceId .isEmpty()) {
            return List.of();
        }

        return drillDownRepository
            .findByKpiA1AnalyticDataIdInOrderByFromHourAsc(latestAnalyticDataIdByInstanceId);
    }

    @Override
    public void writeSheet(Sheet sheet, List<?> data) {

        int rowIdx = 0;

        // ===== HEADER =====
        Row header = sheet.createRow(rowIdx++);
        header.createCell(0).setCellValue("From Hour");
        header.createCell(1).setCellValue("To Hour");
        header.createCell(2).setCellValue("Total Requests");
        header.createCell(3).setCellValue("OK Requests");
        header.createCell(4).setCellValue("Request Timeout");

        // ===== NO DATA FOUND =====
        if (data == null || data.isEmpty()) {
            Row row = sheet.createRow(rowIdx);
            row.createCell(0).setCellValue("NO DATA FOUND");
            return;
        }

        @SuppressWarnings("unchecked")
        List<KpiA1AnalyticDrillDown> rows =
            (List<KpiA1AnalyticDrillDown>) data;

        // ===== DATA =====
        for (KpiA1AnalyticDrillDown r : rows) {
            Row row = sheet.createRow(rowIdx++);
            row.createCell(0).setCellValue(HOUR_FMT.format(r.getFromHour()));
            row.createCell(1).setCellValue(HOUR_FMT.format(r.getToHour()));
            row.createCell(2).setCellValue(r.getTotalRequests());
            row.createCell(3).setCellValue(r.getOkRequests());
            row.createCell(4).setCellValue(r.getReqTimeout());
        }
    }
}
