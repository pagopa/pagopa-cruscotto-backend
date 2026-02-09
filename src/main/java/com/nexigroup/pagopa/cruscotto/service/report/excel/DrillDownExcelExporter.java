package com.nexigroup.pagopa.cruscotto.service.report.excel;

import org.apache.poi.ss.usermodel.Sheet;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;

public interface DrillDownExcelExporter {

    /** Nome dello sheet */
    String getSheetName();

    /** Ordine di apparizione nello Excel */
    int getOrder();

    /** Recupero dati (input generico: instanceId o codice) */
    List<?> loadData(String instanceCode);

    /** Scrittura sheet */
    void writeSheet(Sheet sheet, List<?> data);

    DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");
    DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

     static String formatDateFromInstant(Instant instant) {
        if (instant == null) {
            return null;
        }

        return instant
            .atZone(ZoneId.of("Europe/Rome"))
            .toLocalDate()
            .format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
    }

}

