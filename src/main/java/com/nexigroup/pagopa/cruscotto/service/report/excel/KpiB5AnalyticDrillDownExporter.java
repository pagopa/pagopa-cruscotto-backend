package com.nexigroup.pagopa.cruscotto.service.report.excel;

import com.nexigroup.pagopa.cruscotto.domain.KpiB5AnalyticDrillDown;
import com.nexigroup.pagopa.cruscotto.repository.KpiB5AnalyticDrillDownRepository;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class KpiB5AnalyticDrillDownExporter implements DrillDownExcelExporter {

    private final KpiB5AnalyticDrillDownRepository repository;

    public KpiB5AnalyticDrillDownExporter(
        KpiB5AnalyticDrillDownRepository repository
    ) {
        this.repository = repository;
    }

    @Override
    public String getSheetName() {
        return "KPI_B5_ANALYTIC";
    }

    @Override
    public int getOrder() {
        return 2;
    }

    @Override
    public List<KpiB5AnalyticDrillDown> loadData(String instanceCode) {

        // instanceCode = instanceId (come da tuoi esempi precedenti)
        Long instanceId = Long.valueOf(instanceCode);

        List<KpiB5AnalyticDrillDown> result =
            repository.findLatestByInstanceId(instanceId);

        if (result == null || result.isEmpty()) {
            return List.of();
        }

        // richiesto: excel del primo record trovato
        return List.of(result.get(0));
    }

    @Override
    public void writeSheet(Sheet sheet, List<?> data) {

        @SuppressWarnings("unchecked")
        List<KpiB5AnalyticDrillDown> rows =
            (List<KpiB5AnalyticDrillDown>) data;

        int rowIdx = 0;

        // ===== HEADER =====
        Row header = sheet.createRow(rowIdx++);
        header.createCell(0).setCellValue("Partner ID");
        header.createCell(1).setCellValue("Partner Name");
        header.createCell(2).setCellValue("Partner Fiscal Code");
        header.createCell(3).setCellValue("Station Code");
        header.createCell(4).setCellValue("Fiscal Code");
        header.createCell(5).setCellValue("Spontaneous Payment");
        header.createCell(6).setCellValue("Spontaneous Payments");

        // ===== DATA (1 record) =====
        for (KpiB5AnalyticDrillDown r : rows) {
            Row row = sheet.createRow(rowIdx++);
            row.createCell(0).setCellValue(
                r.getPartnerId() != null ? r.getPartnerId() : 0
            );
            row.createCell(1).setCellValue(
                r.getPartnerName() != null ? r.getPartnerName() : ""
            );
            row.createCell(2).setCellValue(r.getPartnerFiscalCode());
            row.createCell(3).setCellValue(r.getStationCode());
            row.createCell(4).setCellValue(r.getFiscalCode());
            row.createCell(5).setCellValue(
                Boolean.TRUE.equals(r.getSpontaneousPayment())
            );
            row.createCell(6).setCellValue(
                r.getSpontaneousPayments() != null
                    ? r.getSpontaneousPayments().name()
                    : ""
            );
        }
    }
}
