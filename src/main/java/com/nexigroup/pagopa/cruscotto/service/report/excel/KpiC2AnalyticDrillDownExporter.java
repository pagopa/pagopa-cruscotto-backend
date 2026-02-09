package com.nexigroup.pagopa.cruscotto.service.report.excel;

import com.nexigroup.pagopa.cruscotto.domain.KpiC2AnalyticDrillDown;
import com.nexigroup.pagopa.cruscotto.repository.KpiC2AnalyticDrillDownRepository;

import com.nexigroup.pagopa.cruscotto.service.dto.KpiC2AnalyticDrillDownDTO;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;

import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class KpiC2AnalyticDrillDownExporter implements DrillDownExcelExporter<KpiC2AnalyticDrillDownDTO> {

    private final KpiC2AnalyticDrillDownRepository repository;

    public KpiC2AnalyticDrillDownExporter(KpiC2AnalyticDrillDownRepository repository) {
        this.repository = repository;
    }

    @Override
    public String getSheetName() {
        return "KPI_C2";
    }

    @Override
    public int getOrder() {
        return 12;
    }

    @Override
    public List<KpiC2AnalyticDrillDownDTO> loadData(String instanceCode) {

        Long instanceId = Long.valueOf(instanceCode); // se serve mapping diverso, qui

        List<KpiC2AnalyticDrillDownDTO> result =
            repository.findLatestByInstanceId(instanceId).stream()
                .map(this::toDto)
                .collect(Collectors.toList());

        if (result.isEmpty()) {
            return List.of();
        }

        // ⚠️ richiesto: excel del primo che trovi
        return result;
    }

    private KpiC2AnalyticDrillDownDTO toDto(KpiC2AnalyticDrillDown entity) {

            KpiC2AnalyticDrillDownDTO kpiC2AnalyticDrillDownDTO = new KpiC2AnalyticDrillDownDTO();

            kpiC2AnalyticDrillDownDTO.setId( entity.getId() );
            kpiC2AnalyticDrillDownDTO.setAnalysisDate( entity.getAnalysisDate() );
            kpiC2AnalyticDrillDownDTO.setEvaluationDate( entity.getEvaluationDate() );
            kpiC2AnalyticDrillDownDTO.setInstitutionCf( entity.getInstitutionCf() );
            kpiC2AnalyticDrillDownDTO.setNumPayment( entity.getNumPayment() );
            kpiC2AnalyticDrillDownDTO.setNumNotification( entity.getNumNotification() );
            kpiC2AnalyticDrillDownDTO.setPercentNotification( entity.getPercentNotification() );
            kpiC2AnalyticDrillDownDTO.setKpiC2AnalyticData( entity.getKpiC2AnalyticData() );

            return kpiC2AnalyticDrillDownDTO;
    }

    @Override
    public void writeSheet(Sheet sheet, List<KpiC2AnalyticDrillDownDTO> data) {

        int rowIdx = 0;
        // Header
        Row header = sheet.createRow(rowIdx++);
        String[] columns = {
            "Analysis Date",
            "Date",
            "CF Institution",
            "Total Payment",
            "Total Notification",
            "Percentage Notification"
        };

        for (int i = 0; i < columns.length; i++) {
            header.createCell(i).setCellValue(columns[i]);
        }
        if (data == null || data.isEmpty()) {
            Row row = sheet.createRow(rowIdx);
            row.createCell(0).setCellValue("NO DATA FOUND");
            return;
        }

        @SuppressWarnings("unchecked")
        List<KpiC2AnalyticDrillDownDTO> rows = (List<KpiC2AnalyticDrillDownDTO>) data;

        // Body
        for (KpiC2AnalyticDrillDownDTO r : rows) {
            Row row = sheet.createRow(rowIdx++);
            int count =0;
            row.createCell(count++).setCellValue(r.getAnalysisDate() != null ? r.getAnalysisDate().format(dateFormatter) : "");
            row.createCell(count++).setCellValue(r.getEvaluationDate() != null ? r.getEvaluationDate().format(dateFormatter) : "");
            row.createCell(count++).setCellValue(r.getInstitutionCf());
            row.createCell(count++).setCellValue(r.getNumPayment() != null ? r.getNumPayment() : 0);
            row.createCell(count++).setCellValue(r.getNumNotification() != null ? r.getNumNotification() : 0);
            row.createCell(count++).setCellValue(r.getPercentNotification() != null ? r.getPercentNotification().doubleValue() +"%" : "0.0%");

        }
    }
}

