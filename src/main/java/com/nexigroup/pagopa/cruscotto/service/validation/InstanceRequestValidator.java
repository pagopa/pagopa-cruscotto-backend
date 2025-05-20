package com.nexigroup.pagopa.cruscotto.service.validation;

import com.nexigroup.pagopa.cruscotto.service.bean.InstanceRequestBean;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.temporal.TemporalAdjusters;

public class InstanceRequestValidator {

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    /**
     * Metodo per controllare e correggere le date di analysisPeriodStartDate e analysisPeriodEndDate.
     * Se le date sono già corrette (primo e ultimo giorno del mese), non effettua modifiche.
     *
     * @param instance InstanceRequestBean da validare e correggere.
     */
    public static void adjustAnalysisPeriodDates(InstanceRequestBean instance) {
        if (instance.getAnalysisPeriodStartDate() != null) {
            // Controlla e aggiorna solo se necessario
            instance.setAnalysisPeriodStartDate(verifyOrAdjustToFirstDayOfMonth(instance.getAnalysisPeriodStartDate()));
        }

        if (instance.getAnalysisPeriodEndDate() != null) {
            // Controlla e aggiorna solo se necessario
            instance.setAnalysisPeriodEndDate(verifyOrAdjustToLastDayOfMonth(instance.getAnalysisPeriodEndDate()));
        }
    }

    /**
     * Verifica se una data è già il primo giorno del mese. Se non lo è, la converte.
     *
     * @param date La data sotto forma di stringa (formato "dd/MM/yyyy").
     * @return String La data (corretta se necessario).
     */
    private static String verifyOrAdjustToFirstDayOfMonth(String date) {
        try {
            LocalDate localDate = LocalDate.parse(date, DATE_FORMATTER);
            // Controlla se la data è già il primo giorno del mese
            if (localDate.getDayOfMonth() == 1) {
                return date; // Data già corretta, restituisci senza modifiche
            }
            // Altrimenti, calcola il primo giorno del mese
            LocalDate firstDayOfMonth = localDate.with(TemporalAdjusters.firstDayOfMonth());
            return firstDayOfMonth.format(DATE_FORMATTER);
        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException("Invalid date format for analysisPeriodStartDate: " + date, e);
        }
    }

    /**
     * Verifica se una data è già l'ultimo giorno del mese. Se non lo è, la converte.
     *
     * @param date La data sotto forma di stringa (formato "dd/MM/yyyy").
     * @return String La data (corretta se necessario).
     */
    private static String verifyOrAdjustToLastDayOfMonth(String date) {
        try {
            LocalDate localDate = LocalDate.parse(date, DATE_FORMATTER);
            // Controlla se la data è già l'ultimo giorno del mese
            if (localDate.equals(localDate.with(TemporalAdjusters.lastDayOfMonth()))) {
                return date; // Data già corretta, restituisci senza modifiche
            }
            // Altrimenti, calcola l'ultimo giorno del mese
            LocalDate lastDayOfMonth = localDate.with(TemporalAdjusters.lastDayOfMonth());
            return lastDayOfMonth.format(DATE_FORMATTER);
        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException("Invalid date format for analysisPeriodEndDate: " + date, e);
        }
    }
}
