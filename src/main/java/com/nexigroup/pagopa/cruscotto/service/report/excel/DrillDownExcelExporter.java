package com.nexigroup.pagopa.cruscotto.service.report.excel;

import org.apache.poi.ss.usermodel.Sheet;

import java.time.format.DateTimeFormatter;
import java.util.List;

public interface DrillDownExcelExporter<T> {

    /** Nome dello sheet */
    String getSheetName();

    /** Ordine di apparizione nello Excel */
    int getOrder();

    /** Recupero dati (input generico: instanceId o codice) */
    List<T> loadData(String instanceCode);

    /** Scrittura sheet */
    void writeSheet(Sheet sheet, List<T> data);

    DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");
    DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
}

