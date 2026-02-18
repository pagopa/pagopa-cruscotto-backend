package com.nexigroup.pagopa.cruscotto.service.report.excel;

import com.nexigroup.pagopa.cruscotto.domain.KpiB3DetailResult;
import com.nexigroup.pagopa.cruscotto.domain.PagopaNumeroStandinDrilldown;
import com.nexigroup.pagopa.cruscotto.domain.enumeration.EvaluationType;
import com.nexigroup.pagopa.cruscotto.repository.KpiB3DetailResultRepository;
import com.nexigroup.pagopa.cruscotto.repository.PagopaNumeroStandinDrilldownRepository;
import com.nexigroup.pagopa.cruscotto.service.dto.PagopaNumeroStandinDTO;
import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.springframework.stereotype.Component;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class KpiB3AnalyticDrillDownExporter implements DrillDownExcelExporter<PagopaNumeroStandinDTO> {

    private final PagopaNumeroStandinDrilldownRepository drilldownRepository;

    private final KpiB3DetailResultRepository kpiB3DetailResultRepository;
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
        List<KpiB3DetailResult> latestByInstanceId = kpiB3DetailResultRepository.findLatestByInstanceId(instanceId);

        List<Long> list = latestByInstanceId.stream()
            .filter(kpiB3DetailResult -> kpiB3DetailResult.getEvaluationType().equals(EvaluationType.MESE))
            .map(elem-> elem.getId())
            .toList();
        if (list==null || list.isEmpty()){
            list = latestByInstanceId.stream()
                .filter(kpiB3DetailResult -> kpiB3DetailResult.getEvaluationType().equals(EvaluationType.TOTALE))
                .map(elem-> elem.getId())
                .toList();
        }
        return drilldownRepository.findLatestByInstanceIdAndKpiB3AnalyticDataIn(instanceId,list)
            .stream()
            .map(this::toDto)
            .collect(Collectors.toList());
        // Puoi adattare i parametri in base alla tua logica
    }

    public PagopaNumeroStandinDTO toDto(PagopaNumeroStandinDrilldown entity) {
        PagopaNumeroStandinDTO dto = new PagopaNumeroStandinDTO();

        // Copy all fields from entity to DTO

        dto.setStationCode(entity.getStationCode());
        dto.setIntervalStart(entity.getIntervalStart());
        dto.setIntervalEnd(entity.getIntervalEnd());
        dto.setStandInCount(entity.getStandInCount());
        dto.setEventType(entity.getEventType());
        dto.setDataDate(entity.getDataDate());
        dto.setDataOraEvento(entity.getDataOraEvento());
        dto.setLoadTimestamp(entity.getLoadTimestamp());

        if (entity.getStation() != null && entity.getStation().getAnagPartner() != null) {
            dto.setPartnerId(entity.getStation().getAnagPartner().getId());
            dto.setPartnerName(entity.getStation().getAnagPartner().getName());
            dto.setPartnerFiscalCode(entity.getStation().getAnagPartner().getFiscalCode());
        }

        return dto;
    }
    @Override
    public void writeSheet(Sheet sheet, List<PagopaNumeroStandinDTO> data) {
        Row header = sheet.createRow(0);
        String[] columns = {"Period","Partner Fiscal Code",  "Start period", "End period", "Station Code", "Standin number"};
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
            int count =0;
            row.createCell(count++).setCellValue(d.getIntervalStart().format(dateFormatter));
            row.createCell(count++).setCellValue(d.getPartnerFiscalCode());
            row.createCell(count++).setCellValue(d.getIntervalStart().format(timeFormatter));
            row.createCell(count++).setCellValue(d.getIntervalEnd().format(timeFormatter));
            row.createCell(count++).setCellValue(d.getStationCode());
            row.createCell(count++).setCellValue(d.getStandInCount());

        }
    }
}
