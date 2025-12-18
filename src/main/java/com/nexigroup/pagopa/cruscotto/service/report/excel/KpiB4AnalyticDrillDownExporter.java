package com.nexigroup.pagopa.cruscotto.service.report.excel;

import com.nexigroup.pagopa.cruscotto.domain.PagopaApiLogDrilldown;
import com.nexigroup.pagopa.cruscotto.repository.PagopaApiLogDrilldownRepository;
import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class KpiB4AnalyticDrillDownExporter implements DrillDownExcelExporter {

    private final PagopaApiLogDrilldownRepository drilldownRepository;
    private final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    @Override
    public String getSheetName() {
        return "KPI_B4_ANALYTIC_DRILLDOWN";
    }

    @Override
    public int getOrder() {
        return 6;
    }

    @Override
    public List<PagopaApiLogDrilldown> loadData(String instanceCode) {
        Long instanceId = Long.valueOf(instanceCode);

        // Prende solo i record più recenti per l’istanza
        List<PagopaApiLogDrilldown> records = drilldownRepository.findLatestB4ByInstanceId(instanceId);

        if (records.isEmpty()) {
            return List.of();
        }


        // Filtra solo i record della data più recente
        return records;
    }

    @Override
    public void writeSheet(Sheet sheet, List<?> data) {
        // Header
        Row header = sheet.createRow(0);
        String[] columns = {"Data", "Partner Fiscal Code", "Station Code", "Fiscal Code", "API", "Total Requests", "OK Requests", "KO Requests"};
        for (int i = 0; i < columns.length; i++) {
            header.createCell(i).setCellValue(columns[i]);
        }

        if (data == null || data.isEmpty()) {
            Row row = sheet.createRow(1);
            row.createCell(0).setCellValue("No data found");
            return;
        }

        // Scrive i dati
        List<PagopaApiLogDrilldown> records = (List<PagopaApiLogDrilldown>) data;

        // Ordina per dataDate -> partnerFiscalCode -> stationCode
        records.sort(Comparator
            .comparing(PagopaApiLogDrilldown::getDataDate)
            .thenComparing(PagopaApiLogDrilldown::getPartnerFiscalCode)
            .thenComparing(PagopaApiLogDrilldown::getStationCode)
        );

        int rowIdx = 1;
        for (PagopaApiLogDrilldown d : records) {
            Row row = sheet.createRow(rowIdx++);
            row.createCell(0).setCellValue(d.getDataDate().format(dateFormatter));
            row.createCell(1).setCellValue(d.getPartnerFiscalCode());
            row.createCell(2).setCellValue(d.getStationCode());
            row.createCell(3).setCellValue(d.getFiscalCode());
            row.createCell(4).setCellValue(d.getApi());
            row.createCell(5).setCellValue(d.getTotalRequests());
            row.createCell(6).setCellValue(d.getOkRequests());
            row.createCell(7).setCellValue(d.getKoRequests());
        }
    }
}
