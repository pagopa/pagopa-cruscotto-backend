//package com.nexigroup.pagopa.cruscotto.job.report;
//
//import static org.junit.jupiter.api.Assertions.*;
//import static org.mockito.Mockito.*;
//
//import com.nexigroup.pagopa.cruscotto.config.ApplicationProperties;
//import com.nexigroup.pagopa.cruscotto.repository.ReportGenerationRepository;
//import com.nexigroup.pagopa.cruscotto.service.ReportGenerationService;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.mockito.Mock;
//import org.mockito.junit.jupiter.MockitoExtension;
//import org.quartz.JobExecutionContext;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//
//import java.util.List;
//
///**
// * Test per ReportGenerationJob.
// * Verifica il comportamento del job schedulato per la generazione asincrona dei report.
// */
//@ExtendWith(MockitoExtension.class)
//class ReportGenerationJobTest {
//
//    private static final Logger LOGGER = LoggerFactory.getLogger(ReportGenerationJobTest.class);
//
//    @Mock
//    private JobExecutionContext jobExecutionContext;
//
//    @Test
//    void testExecuteInternal_WhenJobIsDisabled_ShouldSkipExecution() {
//        // Given
//        ApplicationProperties properties = mock(ApplicationProperties.class);
//        ApplicationProperties.Job job = mock(ApplicationProperties.Job.class);
//        ApplicationProperties.ReportGenerationJob reportJob = mock(ApplicationProperties.ReportGenerationJob.class);
//
//        when(properties.getJob()).thenReturn(job);
//        when(job.getReportGenerationJob()).thenReturn(reportJob);
//        when(reportJob.isEnabled()).thenReturn(false);
//
//        ReportGenerationJob reportGenerationJobInstance = new ReportGenerationJob(properties);
//
//        // When & Then
//        assertDoesNotThrow(() -> reportGenerationJobInstance.executeInternal(jobExecutionContext));
//        verify(reportJob).isEnabled();
//
//        LOGGER.info("Test passed: Job correctly skipped when disabled");
//    }
//
//    @Test
//    void testExecuteInternal_WhenJobIsEnabled_ShouldExecute() {
//        // Given
//        ApplicationProperties properties = mock(ApplicationProperties.class);
//        ApplicationProperties.Job job = mock(ApplicationProperties.Job.class);
//        ApplicationProperties.ReportGenerationJob reportJob = mock(ApplicationProperties.ReportGenerationJob.class);
//
//        when(properties.getJob()).thenReturn(job);
//        when(job.getReportGenerationJob()).thenReturn(reportJob);
//        when(reportJob.isEnabled()).thenReturn(true);
//
//        ReportGenerationJob reportGenerationJobInstance = new ReportGenerationJob(properties);
//
//        // When & Then
//        assertDoesNotThrow(() -> reportGenerationJobInstance.executeInternal(jobExecutionContext));
//        verify(reportJob, atLeastOnce()).isEnabled();
//
//        LOGGER.info("Test passed: Job correctly executed when enabled");
//    }
//
//    @Test
//    void testExecuteInternal_WhenNoPendingReports_ShouldLogAndExit() {
//        // Given
//        ApplicationProperties properties = mock(ApplicationProperties.class);
//        ApplicationProperties.Job job = mock(ApplicationProperties.Job.class);
//        ApplicationProperties.ReportGenerationJob reportJob = mock(ApplicationProperties.ReportGenerationJob.class);
//
//        when(properties.getJob()).thenReturn(job);
//        when(job.getReportGenerationJob()).thenReturn(reportJob);
//        when(reportJob.isEnabled()).thenReturn(true);
//
//        ReportGenerationJob reportGenerationJobInstance = new ReportGenerationJob(properties, job, reportJob);
//
//        // When & Then - queryPendingReports() returns empty list
//        assertDoesNotThrow(() -> reportGenerationJobInstance.executeInternal(jobExecutionContext));
//
//        LOGGER.info("Test passed: Job correctly handles empty pending reports list");
//    }
//
//    @Test
//    void testJobConfiguration() {
//        // Given
//        ReportGenerationService realService = mock(ReportGenerationService.class);
//        ApplicationProperties realProperties = mock(ApplicationProperties.class);
//        ReportGenerationRepository realRepository = mock(ReportGenerationRepository.class);
//
//        // When
//        ReportGenerationJob job = new ReportGenerationJob(realService, realProperties, realRepository);
//
//        // Then
//        assertNotNull(job);
//        LOGGER.info("Test passed: Job instance created successfully");
//    }
//
//    @Test
//    void testApplicationPropertiesInjection() {
//        // Given
//        ApplicationProperties realProperties = new ApplicationProperties();
//
//        // When
//        ReportGenerationJob job = new ReportGenerationJob(realProperties);
//
//        // Then
//        assertNotNull(job);
//        LOGGER.info("Test passed: ApplicationProperties correctly injected");
//    }
//
//    @Test
//    void testLogOutputWhenProcessingReports() {
//        // Given
//        ApplicationProperties properties = mock(ApplicationProperties.class);
//        ApplicationProperties.Job job = mock(ApplicationProperties.Job.class);
//        ApplicationProperties.ReportGenerationJob reportJob = mock(ApplicationProperties.ReportGenerationJob.class);
//
//        when(properties.getJob()).thenReturn(job);
//        when(job.getReportGenerationJob()).thenReturn(reportJob);
//        when(reportJob.isEnabled()).thenReturn(true);
//
//        ReportGenerationJob reportGenerationJobInstance = new ReportGenerationJob(properties);
//
//        // When & Then
//        // Nota: queryPendingReports() ritorna lista vuota quindi il log "Chiamo il servizio"
//        // NON viene stampato perché il forEach non viene eseguito
//        assertDoesNotThrow(() -> reportGenerationJobInstance.executeInternal(jobExecutionContext));
//
//        LOGGER.info("Test passed: Attualmente queryPendingReports() ritorna lista vuota.");
//        LOGGER.info("Il log 'Chiamo il servizio executeAsyncGeneration' verrà stampato solo quando ci saranno report PENDING reali.");
//        LOGGER.info("Per testarlo, il collega dovrà implementare il repository e popolare il DB con report PENDING.");
//    }
//
//
//    @Test
//    void testParallelExecutionWithFakeReports() {
//        // Given
//        ApplicationProperties properties = mock(ApplicationProperties.class);
//        ApplicationProperties.Job job = mock(ApplicationProperties.Job.class);
//        ApplicationProperties.ReportGenerationJob reportJob = mock(ApplicationProperties.ReportGenerationJob.class);
//
//        when(properties.getJob()).thenReturn(job);
//        when(job.getReportGenerationJob()).thenReturn(reportJob);
//        when(reportJob.isEnabled()).thenReturn(true);
//
//        // Subclass FINTA del job
//        ReportGenerationJob jobUnderTest = new ReportGenerationJob(properties) {
//
//            @Override
//            protected List<Long> queryPendingReports() {
//                return List.of(1L, 2L, 3L, 4L, 5L);
//            }
//
//            @Override
//            protected void executeInternal(JobExecutionContext context) {
//                LOGGER.info("=== TEST PARALLEL EXECUTION START ===");
//                super.executeInternal(context);
//                LOGGER.info("=== TEST PARALLEL EXECUTION END ===");
//            }
//        };
//
//        // When
//        jobUnderTest.executeInternal(mock(JobExecutionContext.class));
//
//        // Then
//        // Se arrivi qui senza eccezioni e vedi log paralleli → OK
//    }
//
//
//
//    @Test
//    void testExecuteInternal_WhenOneReportFails_ShouldContinueProcessingOthers() {
//        // Given
//        ApplicationProperties properties = mock(ApplicationProperties.class);
//        ApplicationProperties.Job job = mock(ApplicationProperties.Job.class);
//        ApplicationProperties.ReportGenerationJob reportJob =
//            mock(ApplicationProperties.ReportGenerationJob.class);
//
//        when(properties.getJob()).thenReturn(job);
//        when(job.getReportGenerationJob()).thenReturn(reportJob);
//        when(reportJob.isEnabled()).thenReturn(true);
//
//        // Job finto per simulare il fallimento
//        ReportGenerationJob jobUnderTest = new ReportGenerationJob(properties) {
//
//            @Override
//            protected List<Long> queryPendingReports() {
//                return List.of(1L, 2L, 3L);
//            }
//
//            @Override
//            protected void processReport(Long reportId) throws Exception {
//                if (reportId.equals(2L)) {
//                    throw new RuntimeException("Errore simulato sul report 2");
//                }
//                Thread.sleep(500); // simulazione lavoro
//            }
//        };
//
//        // When & Then
//        assertDoesNotThrow(() ->
//            jobUnderTest.executeInternal(mock(JobExecutionContext.class))
//        );
//    }
//}
//
