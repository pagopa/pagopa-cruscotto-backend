package com.nexigroup.pagopa.cruscotto.service.report.excel;

import com.nexigroup.pagopa.cruscotto.domain.KpiA2AnalyticIncorrectTaxonomyData;
import com.nexigroup.pagopa.cruscotto.repository.KpiA2AnalyticDataRepository;
import com.nexigroup.pagopa.cruscotto.repository.KpiA2AnalyticIncorrectTaxonomyDataRepository;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Component
@Order(2)
public class KpiA2AnalyticIncorrectTaxonomyDrillDownExporter
    implements DrillDownExcelExporter {

    private final KpiA2AnalyticIncorrectTaxonomyDataRepository drillDownRepository;
    private final KpiA2AnalyticDataRepository analyticDataRepository;

    private static final DateTimeFormatter HOUR_FMT =
        DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")
            .withZone(ZoneId.systemDefault());

    public KpiA2AnalyticIncorrectTaxonomyDrillDownExporter(
        KpiA2AnalyticIncorrectTaxonomyDataRepository drillDownRepository,
        KpiA2AnalyticDataRepository analyticDataRepository
    ) {
        this.drillDownRepository = drillDownRepository;
        this.analyticDataRepository = analyticDataRepository;
    }

    @Override
    public String getSheetName() {
        return "KPI_A2_INCORRECT_TAXONOMY";
    }

    @Override
    public int getOrder() {
        return 2;
    }

    @Override
    public List<KpiA2AnalyticIncorrectTaxonomyData> loadData(String instanceCode) {

        Long instanceId = Long.valueOf(instanceCode);
        List<Long> latestAnalyticDataIdByInstanceId = analyticDataRepository.findLatestAnalyticDataIdByInstanceId(instanceId);


        if (latestAnalyticDataIdByInstanceId == null || latestAnalyticDataIdByInstanceId.isEmpty() ){
            return List.of();
        }

        return drillDownRepository
            .findByKpiA2AnalyticDataIdInOrderByFromHourAsc(latestAnalyticDataIdByInstanceId);
    }

    @Override
    public void writeSheet(Sheet sheet, List<?> data) {

        int rowIdx = 0;

        // ===== HEADER =====
        Row header = sheet.createRow(rowIdx++);
        header.createCell(0).setCellValue("From Hour");
        header.createCell(1).setCellValue("End Hour");
        header.createCell(2).setCellValue("Transfer Category");
        header.createCell(3).setCellValue("Total Payments");
        header.createCell(4).setCellValue("Incorrect Payments");

        // ===== NO DATA =====
        if (data == null || data.isEmpty()) {
            Row row = sheet.createRow(rowIdx);
            row.createCell(0).setCellValue("NO DATA FOUND");
            return;
        }

        @SuppressWarnings("unchecked")
        List<KpiA2AnalyticIncorrectTaxonomyData> rows =
            (List<KpiA2AnalyticIncorrectTaxonomyData>) data;

        // ===== DATA =====
        for (KpiA2AnalyticIncorrectTaxonomyData r : rows) {
            Row row = sheet.createRow(rowIdx++);
            row.createCell(0).setCellValue(
                r.getFromHour() != null ? HOUR_FMT.format(r.getFromHour()) : ""
            );
            row.createCell(1).setCellValue(
                r.getEndHour() != null ? HOUR_FMT.format(r.getEndHour()) : ""
            );
            row.createCell(2).setCellValue(r.getTransferCategory());
            row.createCell(3).setCellValue(r.getTotPayments());
            row.createCell(4).setCellValue(r.getTotIncorrectPayments());
        }
    }
}
