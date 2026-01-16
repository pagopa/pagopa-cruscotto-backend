package com.nexigroup.pagopa.cruscotto.domain;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class ReportFileTest {

    @Test
    void equalsVerifier() {
        ReportFile reportFile1 = new ReportFile();
        reportFile1.setId(1L);

        ReportFile reportFile2 = new ReportFile();
        reportFile2.setId(reportFile1.getId());

        assertThat(reportFile1).isEqualTo(reportFile2);

        reportFile2.setId(2L);
        assertThat(reportFile1).isNotEqualTo(reportFile2);

        reportFile1.setId(null);
        assertThat(reportFile1).isNotEqualTo(reportFile2);
    }
}
