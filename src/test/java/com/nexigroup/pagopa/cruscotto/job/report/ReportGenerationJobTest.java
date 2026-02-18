package com.nexigroup.pagopa.cruscotto.job.report;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.Mockito.*;

import com.nexigroup.pagopa.cruscotto.config.ApplicationProperties;
import com.nexigroup.pagopa.cruscotto.domain.enumeration.ReportStatus;
import com.nexigroup.pagopa.cruscotto.repository.ReportGenerationRepository;
import com.nexigroup.pagopa.cruscotto.service.ReportGenerationService;
import com.nexigroup.pagopa.cruscotto.domain.ReportGeneration;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.quartz.JobExecutionContext;

@ExtendWith(MockitoExtension.class)
class ReportGenerationJobTest {

    @Mock
    private ReportGenerationService reportGenerationService;

    @Mock
    private ApplicationProperties applicationProperties;

    @Mock
    private ApplicationProperties.Job jobProperties;

    @Mock
    private ApplicationProperties.ReportGenerationJob reportJobProperties;

    @Mock
    private ReportGenerationRepository reportGenerationRepository;

    @Mock
    private JobExecutionContext jobExecutionContext;

    private ReportGenerationJob job;

    @BeforeEach
    void setup() {
        when(applicationProperties.getJob()).thenReturn(jobProperties);
        when(jobProperties.getReportGenerationJob()).thenReturn(reportJobProperties);

        job = new ReportGenerationJob(
            reportGenerationService,
            applicationProperties,
            reportGenerationRepository
        );
    }

    @Test
    void whenJobIsDisabled_thenExecutionIsSkipped() {
        // Given
        when(reportJobProperties.isEnabled()).thenReturn(false);

        // When & Then
        assertDoesNotThrow(() -> job.executeInternal(jobExecutionContext));

        verify(reportJobProperties).isEnabled();
        verifyNoInteractions(reportGenerationRepository, reportGenerationService);
    }

    @Test
    void whenNoPendingReports_thenExitGracefully() {
        // Given
        when(reportJobProperties.isEnabled()).thenReturn(true);
        when(reportGenerationRepository.findByStatus(ReportStatus.PENDING))
            .thenReturn(List.of());

        // When & Then
        assertDoesNotThrow(() -> job.executeInternal(jobExecutionContext));

        verify(reportGenerationRepository).findByStatus(ReportStatus.PENDING);
        verifyNoInteractions(reportGenerationService);
    }

    @Test
    void whenPendingReportsExist_thenAllAreProcessed() {
        // Given
        when(reportJobProperties.isEnabled()).thenReturn(true);

        ReportGeneration r1 = mock(ReportGeneration.class);
        ReportGeneration r2 = mock(ReportGeneration.class);

        when(r1.getId()).thenReturn(1L);
        when(r2.getId()).thenReturn(2L);

        when(reportGenerationRepository.findByStatus(ReportStatus.PENDING))
            .thenReturn(List.of(r1, r2));

        // When
        job.executeInternal(jobExecutionContext);

        // Then
        verify(reportGenerationService).executeAsyncGeneration(1L);
        verify(reportGenerationService).executeAsyncGeneration(2L);
    }

    @Test
    void whenOneReportFails_thenOthersAreStillProcessed() {
        // Given
        when(reportJobProperties.isEnabled()).thenReturn(true);

        ReportGeneration r1 = mock(ReportGeneration.class);
        ReportGeneration r2 = mock(ReportGeneration.class);
        ReportGeneration r3 = mock(ReportGeneration.class);

        when(r1.getId()).thenReturn(1L);
        when(r2.getId()).thenReturn(2L);
        when(r3.getId()).thenReturn(3L);

        when(reportGenerationRepository.findByStatus(ReportStatus.PENDING))
            .thenReturn(List.of(r1, r2, r3));

        doThrow(new RuntimeException("Boom"))
            .when(reportGenerationService).executeAsyncGeneration(2L);

        // When & Then
        assertDoesNotThrow(() -> job.executeInternal(jobExecutionContext));

        verify(reportGenerationService).executeAsyncGeneration(1L);
        verify(reportGenerationService).executeAsyncGeneration(2L);
        verify(reportGenerationService).executeAsyncGeneration(3L);
    }
}
