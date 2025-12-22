package com.nexigroup.pagopa.cruscotto.service.report.excel;

import com.nexigroup.pagopa.cruscotto.domain.PagopaNumeroStandinDrilldown;
import com.nexigroup.pagopa.cruscotto.repository.PagopaNumeroStandinDrilldownRepository;
import com.nexigroup.pagopa.cruscotto.service.dto.PagopaNumeroStandinDTO;
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
public class KpiB3AnalyticDrillDownExporter implements DrillDownExcelExporter {

    private final PagopaNumeroStandinDrilldownRepository drilldownRepository;
    private final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @Override
    public String getSheetName() {
        return "KPI_B3";
    }

    @Override
    public int getOrder() {
        return 5;
    }

    @Override
    public List<PagopaNumeroStandinDTO> loadData(String instanceCode) {
        Long instanceId = Long.valueOf(instanceCode);
        // Recupera tutti i record ordinati per data evento
        return drilldownRepository.findLatestByInstanceId(instanceId)
            .stream()
            .map(this::toDto)
            .collect(Collectors.toList());
        // Puoi adattare i parametri in base alla tua logica
    }

    public PagopaNumeroStandinDTO toDto(PagopaNumeroStandinDrilldown entity) {
        PagopaNumeroStandinDTO dto = new PagopaNumeroStandinDTO();

        // Copy all fields from entity to DTO
        dto.setId(entity.getId());
        dto.setStationCode(entity.getStationCode());
        dto.setIntervalStart(entity.getIntervalStart());
        dto.setIntervalEnd(entity.getIntervalEnd());
        dto.setStandInCount(entity.getStandInCount());
        dto.setEventType(entity.getEventType());
        dto.setDataDate(entity.getDataDate());
        dto.setDataOraEvento(entity.getDataOraEvento());
        dto.setLoadTimestamp(entity.getLoadTimestamp());

        // Set partner information from station
        /*if (entity.getStation() != null && entity.getStation().getAnagPartner() != null) {
            dto.setPartnerId(entity.getStation().getAnagPartner().getId());
            dto.setPartnerName(entity.getStation().getAnagPartner().getName());
            dto.setPartnerFiscalCode(entity.getStation().getAnagPartner().getFiscalCode());
        }*/

        return dto;
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

        List<PagopaNumeroStandinDTO> records = (List<PagopaNumeroStandinDTO>) data;
        records.sort(Comparator.comparing(PagopaNumeroStandinDTO::getDataOraEvento));

        int rowIdx = 1;
        for (PagopaNumeroStandinDTO d : records) {
            Row row = sheet.createRow(rowIdx++);
            row.createCell(0).setCellValue(d.getId());
            row.createCell(1).setCellValue(d.getDataOraEvento().format(DateTimeFormatter.ISO_DATE));
            row.createCell(2).setCellValue(d.getStationCode());
            row.createCell(3).setCellValue(d.getIntervalStart().format(dateFormatter));
            row.createCell(4).setCellValue(d.getIntervalEnd().format(dateFormatter));
            row.createCell(5).setCellValue(d.getStandInCount());
            row.createCell(6).setCellValue(d.getEventType() != null ? d.getEventType() : "");
        }
    }
}
