package com.nexigroup.pagopa.cruscotto.service.report.excel;

import com.nexigroup.pagopa.cruscotto.domain.PagopaNumeroStandinDrilldown;
import com.nexigroup.pagopa.cruscotto.repository.PagopaNumeroStandinDrilldownRepository;
import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.springframework.stereotype.Component;

import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.List;

@Component
@RequiredArgsConstructor
public class KpiB3AnalyticDrillDownExporter implements DrillDownExcelExporter {

    private final PagopaNumeroStandinDrilldownRepository drilldownRepository;
    private final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @Override
    public String getSheetName() {
        return "KPI_B3_DRILLDOWN";
    }

    @Override
    public int getOrder() {
        return 5;
    }

    @Override
    public List<PagopaNumeroStandinDrilldown> loadData(String instanceCode) {
        Long instanceId = Long.valueOf(instanceCode);
        // Recupera tutti i record ordinati per data evento
        return drilldownRepository.findLatestByInstanceId(instanceId);
        // Puoi adattare i parametri in base alla tua logica
    }

    @Override
    public void writeSheet(Sheet sheet, List<?> data) {
        Row header = sheet.createRow(0);
        String[] columns = {"ID", "Analysis Date", "Station Code", "Interval Start", "Interval End", "StandIn Count", "Event Type"};
        for (int i = 0; i < columns.length; i++) {
            header.createCell(i).setCellValue(columns[i]);
        }

        if (data == null || data.isEmpty()) {
            sheet.createRow(1).createCell(0).setCellValue("No data found");
            return;
        }

        List<PagopaNumeroStandinDrilldown> records = (List<PagopaNumeroStandinDrilldown>) data;
        records.sort(Comparator.comparing(PagopaNumeroStandinDrilldown::getDataOraEvento));

        int rowIdx = 1;
        for (PagopaNumeroStandinDrilldown d : records) {
            Row row = sheet.createRow(rowIdx++);
            row.createCell(0).setCellValue(d.getId());
            row.createCell(1).setCellValue(d.getAnalysisDate().format(DateTimeFormatter.ISO_DATE));
            row.createCell(2).setCellValue(d.getStationCode());
            row.createCell(3).setCellValue(d.getIntervalStart().format(dateFormatter));
            row.createCell(4).setCellValue(d.getIntervalEnd().format(dateFormatter));
            row.createCell(5).setCellValue(d.getStandInCount());
            row.createCell(6).setCellValue(d.getEventType() != null ? d.getEventType() : "");
        }
    }
}
