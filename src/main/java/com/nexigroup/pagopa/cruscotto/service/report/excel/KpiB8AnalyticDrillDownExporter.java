package com.nexigroup.pagopa.cruscotto.service.report.excel;

import com.nexigroup.pagopa.cruscotto.domain.PagopaApiLogDrilldown;
import com.nexigroup.pagopa.cruscotto.repository.PagopaApiLogDrilldownRepository;
import com.nexigroup.pagopa.cruscotto.service.dto.PagopaAPILogDTO;
import com.nexigroup.pagopa.cruscotto.service.mapper.PagopaApiLogDrilldownMapper;
import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.springframework.stereotype.Component;

import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class KpiB8AnalyticDrillDownExporter implements DrillDownExcelExporter<PagopaAPILogDTO> {

    private final PagopaApiLogDrilldownRepository drilldownRepository;
    private final PagopaApiLogDrilldownMapper pagopaApiLogDrilldownMapper;

    @Override
    public String getSheetName() {
        return "KPI_B8";
    }

    @Override
    public int getOrder() {
        return 9;
    }

    @Override
    public List<PagopaAPILogDTO> loadData(String instanceCode) {
        Long instanceId = Long.valueOf(instanceCode);

        // Prende solo i record più recenti per l’istanza
        List<PagopaAPILogDTO> records = drilldownRepository.findLatestB8ByInstanceId(instanceId)
            .stream()
            .map(this::toDto)
            .collect(Collectors.toList());


        if (records.isEmpty()) {
            return List.of();
        }


        // Filtra solo i record della data più recente
        return records;
    }


    private PagopaAPILogDTO toDto(PagopaApiLogDrilldown pagopaApiLogDrilldown) {
        return pagopaApiLogDrilldownMapper.toDto(pagopaApiLogDrilldown);
    }

    @Override
    public void writeSheet(Sheet sheet, List<PagopaAPILogDTO> data) {
        // Header
        Row header = sheet.createRow(0);
        String[] columns = {"Partner Fiscal Code","Data",  "Station Code", "Fiscal Code", "API", "Total Requests", "OK Requests", "KO Requests"};
        for (int i = 0; i < columns.length; i++) {
            header.createCell(i).setCellValue(columns[i]);
        }

        if (data == null || data.isEmpty()) {
            Row row = sheet.createRow(1);
            row.createCell(0).setCellValue("No data found");
            return;
        }

        // Scrive i dati
        List<PagopaAPILogDTO> records = (List<PagopaAPILogDTO>) data;

        // Ordina per dataDate -> partnerFiscalCode -> stationCode
        records.sort(Comparator
            .comparing(PagopaAPILogDTO::getDate)
            .thenComparing(PagopaAPILogDTO::getCfPartner)
            .thenComparing(PagopaAPILogDTO::getStation)
        );

        int rowIdx = 1;
        for (PagopaAPILogDTO d : records) {
            Row row = sheet.createRow(rowIdx++);
            int count =0;
            row.createCell(count++).setCellValue(d.getCfPartner());
            row.createCell(count++).setCellValue(d.getDate().format(dateFormatter));
            row.createCell(count++).setCellValue(d.getStation());
            row.createCell(count++).setCellValue(d.getCfEnte());
            row.createCell(count++).setCellValue(d.getApi());
            row.createCell(count++).setCellValue(d.getTotReq());
            row.createCell(count++).setCellValue(d.getReqOk());
            row.createCell(count++).setCellValue(d.getReqKo());
        }
    }
}
