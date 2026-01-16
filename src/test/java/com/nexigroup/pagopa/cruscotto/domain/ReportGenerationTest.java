package com.nexigroup.pagopa.cruscotto.domain;

import static org.assertj.core.api.Assertions.assertThat;

import com.nexigroup.pagopa.cruscotto.domain.enumeration.ReportStatus;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import org.junit.jupiter.api.Test;

class ReportGenerationTest {

    @Test
    void equalsVerifier() {
        ReportGeneration reportGeneration1 = new ReportGeneration();
        reportGeneration1.setId(1L);

        ReportGeneration reportGeneration2 = new ReportGeneration();
        reportGeneration2.setId(reportGeneration1.getId());

        assertThat(reportGeneration1).isEqualTo(reportGeneration2);

        reportGeneration2.setId(2L);
        assertThat(reportGeneration1).isNotEqualTo(reportGeneration2);

        reportGeneration1.setId(null);
        assertThat(reportGeneration1).isNotEqualTo(reportGeneration2);
    }

    @Test
    void hashCodeVerifier() {
        ReportGeneration reportGeneration1 = new ReportGeneration();
        ReportGeneration reportGeneration2 = new ReportGeneration();

        assertThat(reportGeneration1.hashCode()).isEqualTo(reportGeneration2.hashCode());
    }

    @Test
    void gettersAndSettersTest() {
        ReportGeneration reportGeneration = new ReportGeneration();

        Instance instance = new Instance();
        LocalDate startDate = LocalDate.now().minusDays(10);
        LocalDate endDate = LocalDate.now();
        LocalDateTime now = LocalDateTime.now();

        Map<String, Object> parameters = new HashMap<>();
        parameters.put("key", "value");

        reportGeneration.setId(1L);
        reportGeneration.setInstance(instance);
        reportGeneration.setStatus(ReportStatus.PENDING);
        reportGeneration.setLanguage("it");
        reportGeneration.setStartDate(startDate);
        reportGeneration.setEndDate(endDate);
        reportGeneration.setRequestedDate(now);
        reportGeneration.setGenerationStartDate(now.plusMinutes(1));
        reportGeneration.setGenerationEndDate(now.plusMinutes(5));
        reportGeneration.setRequestedBy("test-user");
        reportGeneration.setErrorMessage("error");
        reportGeneration.setRetryCount(2);
        reportGeneration.setLastRetryDate(now.plusHours(1));
        reportGeneration.setParameters(parameters);

        assertThat(reportGeneration.getId()).isEqualTo(1L);
        assertThat(reportGeneration.getInstance()).isEqualTo(instance);
        assertThat(reportGeneration.getStatus()).isEqualTo(ReportStatus.PENDING);
        assertThat(reportGeneration.getLanguage()).isEqualTo("it");
        assertThat(reportGeneration.getStartDate()).isEqualTo(startDate);
        assertThat(reportGeneration.getEndDate()).isEqualTo(endDate);
        assertThat(reportGeneration.getRequestedDate()).isEqualTo(now);
        assertThat(reportGeneration.getGenerationStartDate()).isNotNull();
        assertThat(reportGeneration.getGenerationEndDate()).isNotNull();
        assertThat(reportGeneration.getRequestedBy()).isEqualTo("test-user");
        assertThat(reportGeneration.getErrorMessage()).isEqualTo("error");
        assertThat(reportGeneration.getRetryCount()).isEqualTo(2);
        assertThat(reportGeneration.getLastRetryDate()).isEqualTo(now.plusHours(1));
        assertThat(reportGeneration.getParameters()).containsEntry("key", "value");
    }

    @Test
    void fluentApiTest() {
        ReportGeneration reportGeneration =
            new ReportGeneration()
                .id(10L)
                .status(ReportStatus.IN_PROGRESS)
                .language("en")
                .requestedBy("admin");

        assertThat(reportGeneration.getId()).isEqualTo(10L);
        assertThat(reportGeneration.getStatus()).isEqualTo(ReportStatus.IN_PROGRESS);
        assertThat(reportGeneration.getLanguage()).isEqualTo("en");
        assertThat(reportGeneration.getRequestedBy()).isEqualTo("admin");
    }

    @Test
    void reportFileRelationshipTest() {
        ReportGeneration reportGeneration = new ReportGeneration();
        ReportFile reportFile = new ReportFile();

        reportGeneration.setReportFile(reportFile);

        assertThat(reportGeneration.getReportFile()).isEqualTo(reportFile);
        assertThat(reportFile.getReportGeneration()).isEqualTo(reportGeneration);

        reportGeneration.setReportFile(null);
        assertThat(reportFile.getReportGeneration()).isNull();
    }

    @Test
    void defaultRetryCountTest() {
        ReportGeneration reportGeneration = new ReportGeneration();
        assertThat(reportGeneration.getRetryCount()).isEqualTo(0);
    }

    @Test
    void toStringTest() {
        ReportGeneration reportGeneration = new ReportGeneration();
        reportGeneration.setId(1L);
        reportGeneration.setStatus(ReportStatus.COMPLETED);

        String toString = reportGeneration.toString();

        assertThat(toString).contains("ReportGeneration");
        assertThat(toString).contains("id=1");
        assertThat(toString).contains("COMPLETED");
    }
}
