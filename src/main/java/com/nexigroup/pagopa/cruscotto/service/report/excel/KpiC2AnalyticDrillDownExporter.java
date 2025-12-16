package com.nexigroup.pagopa.cruscotto.service.report.excel;

import com.nexigroup.pagopa.cruscotto.domain.KpiC2AnalyticDrillDown;
import com.nexigroup.pagopa.cruscotto.repository.KpiC2AnalyticDrillDownRepository;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;

import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;

@Component
public class KpiC2AnalyticDrillDownExporter implements DrillDownExcelExporter {

    private final KpiC2AnalyticDrillDownRepository repository;

    public KpiC2AnalyticDrillDownExporter(KpiC2AnalyticDrillDownRepository repository) {
        this.repository = repository;
    }

    @Override
    public String getSheetName() {
        return "KPI_C2_ANALYTIC";
    }

    @Override
    public int getOrder() {
        return 1;
    }

    @Override
    public List<KpiC2AnalyticDrillDown> loadData(String instanceCode) {

        Long instanceId = Long.valueOf(instanceCode); // se serve mapping diverso, qui

        List<KpiC2AnalyticDrillDown> result =
            repository.findLatestByInstanceId(instanceId);

        if (result.isEmpty()) {
            return List.of();
        }

        // ⚠️ richiesto: excel del primo che trovi
        return List.of(result.get(0));
    }

    @Override
    public void writeSheet(Sheet sheet, List<?> data) {
        @SuppressWarnings("unchecked")
        List<KpiC2AnalyticDrillDown> rows = (List<KpiC2AnalyticDrillDown>) data;

        int rowIdx = 0;

        // Header
        Row header = sheet.createRow(rowIdx++);
        header.createCell(0).setCellValue("Institution CF");
        header.createCell(1).setCellValue("Num Payment");
        header.createCell(2).setCellValue("Num Notification");
        header.createCell(3).setCellValue("% Notification");

        // Body
        for (KpiC2AnalyticDrillDown r : rows) {
            Row row = sheet.createRow(rowIdx++);
            row.createCell(0).setCellValue(r.getInstitutionCf());
            row.createCell(1).setCellValue(
                r.getNumPayment() != null ? r.getNumPayment() : 0
            );
            row.createCell(2).setCellValue(
                r.getNumNotification() != null ? r.getNumNotification() : 0
            );
            row.createCell(3).setCellValue(
                r.getPercentNotification() != null ? r.getPercentNotification().doubleValue() : 0
            );
        }
    }
}

