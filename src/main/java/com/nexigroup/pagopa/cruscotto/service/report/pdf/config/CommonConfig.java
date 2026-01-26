package com.nexigroup.pagopa.cruscotto.service.report.pdf.config;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;

public class CommonConfig {

    public static DecimalFormat INT_FMT = new DecimalFormat(
        "#,###",
        DecimalFormatSymbols.getInstance(Locale.ITALY));

    public static  DecimalFormat PERC_FMT = new DecimalFormat(
        "#,##0.####",
        DecimalFormatSymbols.getInstance(Locale.ITALY));

    public static final DecimalFormat DEC_FMT = new DecimalFormat(
        "#,##0.00",
        DecimalFormatSymbols.getInstance(Locale.ITALY)
    );
}
