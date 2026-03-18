package com.nexigroup.pagopa.cruscotto.service.impl;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import com.nexigroup.pagopa.cruscotto.domain.Instance;
import com.nexigroup.pagopa.cruscotto.domain.ReportFile;
import com.nexigroup.pagopa.cruscotto.domain.ReportGeneration;
import com.nexigroup.pagopa.cruscotto.domain.enumeration.InstanceStatus;
import com.nexigroup.pagopa.cruscotto.domain.enumeration.ReportStatus;
import com.nexigroup.pagopa.cruscotto.repository.InstanceRepository;
import com.nexigroup.pagopa.cruscotto.repository.ReportFileRepository;
import com.nexigroup.pagopa.cruscotto.repository.ReportGenerationRepository;
import com.nexigroup.pagopa.cruscotto.service.AzureBlobStorageService;
import com.nexigroup.pagopa.cruscotto.service.ReportPackagingService;
import com.nexigroup.pagopa.cruscotto.service.dto.ReportAsyncAcceptedDTO;
import com.nexigroup.pagopa.cruscotto.service.dto.ReportGenerationRequestDTO;
import com.nexigroup.pagopa.cruscotto.service.dto.ReportGenerationResponseDTO;
import com.nexigroup.pagopa.cruscotto.service.dto.ReportStatusEnum;
import com.nexigroup.pagopa.cruscotto.service.exception.DuplicateReportException;
import com.nexigroup.pagopa.cruscotto.service.exception.ReportGenerationException;
import com.nexigroup.pagopa.cruscotto.service.report.excel.ExcelReportGenerator;
import com.nexigroup.pagopa.cruscotto.service.report.pdf.PDFReportGenerator;
import com.nexigroup.pagopa.cruscotto.service.report.pdf.wrapper.WrapperPdfFiles;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;

@ExtendWith(MockitoExtension.class)
class ReportGenerationServiceImplTest {

    @Mock
    private ReportGenerationRepository reportGenerationRepository;

    @Mock
    private ReportFileRepository reportFileRepository;

    @Mock
    private InstanceRepository instanceRepository;

    @Mock
    private PDFReportGenerator pdfGenerator;

    @Mock
    private ExcelReportGenerator excelGenerator;

    @Mock
    private AzureBlobStorageService blobStorageService;

    @Mock
    private ReportPackagingService packagingService;

    @InjectMocks
    private ReportGenerationServiceImpl service;

    // ---------------------------------------------------------------------
    // Helpers
    // ---------------------------------------------------------------------

    private Instance buildInstance(Long id, InstanceStatus status) {
        Instance instance = new Instance();
        instance.setId(id);
        instance.setStatus(status);
        instance.setInstanceIdentification("INST_" + id);
        return instance;
    }

    private ReportGeneration buildReport(Long id, Instance instance, ReportStatus status) {
        ReportGeneration report = new ReportGeneration();
        report.setId(id);
        report.setInstance(instance);
        report.setStatus(status);
        report.setRequestedDate(LocalDateTime.now());
        report.setStartDate(LocalDate.now().minusDays(5));
        report.setEndDate(LocalDate.now());
        report.setLanguage("it");
        report.setRetryCount(0);
        return report;
    }

    // ---------------------------------------------------------------------
    // activeReportExistsForInstance
    // ---------------------------------------------------------------------

    @Test
    void activeReportExistsForInstance_shouldReturnTrue() {
        when(reportGenerationRepository.findByInstanceIdAndStatusIn(eq(1L), anyList()))
            .thenReturn(Optional.of(new ReportGeneration()));

        boolean result = service.activeReportExistsForInstance(1L);

        assertTrue(result);
    }

    @Test
    void activeReportExistsForInstance_shouldReturnFalse() {
        when(reportGenerationRepository.findByInstanceIdAndStatusIn(eq(1L), anyList()))
            .thenReturn(Optional.empty());

        boolean result = service.activeReportExistsForInstance(1L);

        assertFalse(result);
    }

    @Test
    void activeReportExistsForInstance_shouldReturnTrue_whenPendingExists() {
        Instance instance = buildInstance(1L, InstanceStatus.ESEGUITA);
        ReportGeneration pendingReport = buildReport(10L, instance, ReportStatus.PENDING);

        when(reportGenerationRepository.findByInstanceIdAndStatusIn(eq(1L), anyList()))
            .thenReturn(Optional.of(pendingReport));

        boolean result = service.activeReportExistsForInstance(1L);

        assertTrue(result, "PENDING report should always be considered active");
    }

    @Test
    void activeReportExistsForInstance_shouldReturnTrue_whenInProgressExists() {
        Instance instance = buildInstance(1L, InstanceStatus.ESEGUITA);
        ReportGeneration inProgressReport = buildReport(10L, instance, ReportStatus.IN_PROGRESS);

        when(reportGenerationRepository.findByInstanceIdAndStatusIn(eq(1L), anyList()))
            .thenReturn(Optional.of(inProgressReport));

        boolean result = service.activeReportExistsForInstance(1L);

        assertTrue(result, "IN_PROGRESS report should always be considered active");
    }

    @Test
    void activeReportExistsForInstance_shouldReturnTrue_whenCompletedNotExpired() {
        Instance instance = buildInstance(1L, InstanceStatus.ESEGUITA);
        ReportGeneration completedReport = buildReport(10L, instance, ReportStatus.COMPLETED);

        ReportFile reportFile = new ReportFile();
        reportFile.setExpiryDate(LocalDateTime.now().plusDays(15)); // Not expired (15 days remaining)
        completedReport.setReportFile(reportFile);

        // First call for PENDING/IN_PROGRESS (empty)
        when(reportGenerationRepository.findByInstanceIdAndStatusIn(eq(1L), eq(List.of(ReportStatus.PENDING, ReportStatus.IN_PROGRESS))))
            .thenReturn(Optional.empty());

        // Second call for COMPLETED (found)
        when(reportGenerationRepository.findByInstanceIdAndStatusIn(eq(1L), eq(List.of(ReportStatus.COMPLETED))))
            .thenReturn(Optional.of(completedReport));

        boolean result = service.activeReportExistsForInstance(1L);

        assertTrue(result, "COMPLETED report not yet expired should be considered active (blocking)");
    }

    @Test
    void activeReportExistsForInstance_shouldReturnFalse_whenCompletedExpired() {
        Instance instance = buildInstance(1L, InstanceStatus.ESEGUITA);
        ReportGeneration completedReport = buildReport(10L, instance, ReportStatus.COMPLETED);

        ReportFile reportFile = new ReportFile();
        reportFile.setExpiryDate(LocalDateTime.now().minusDays(1)); // Expired yesterday
        completedReport.setReportFile(reportFile);

        // First call for PENDING/IN_PROGRESS (empty)
        when(reportGenerationRepository.findByInstanceIdAndStatusIn(eq(1L), eq(List.of(ReportStatus.PENDING, ReportStatus.IN_PROGRESS))))
            .thenReturn(Optional.empty());

        // Second call for COMPLETED (found)
        when(reportGenerationRepository.findByInstanceIdAndStatusIn(eq(1L), eq(List.of(ReportStatus.COMPLETED))))
            .thenReturn(Optional.of(completedReport));

        boolean result = service.activeReportExistsForInstance(1L);

        assertFalse(result, "COMPLETED report that is expired should NOT be considered active (allow new generation)");
    }

    @Test
    void activeReportExistsForInstance_shouldReturnTrue_whenCompletedWithNullExpiryDate() {
        Instance instance = buildInstance(1L, InstanceStatus.ESEGUITA);
        ReportGeneration completedReport = buildReport(10L, instance, ReportStatus.COMPLETED);

        ReportFile reportFile = new ReportFile();
        reportFile.setExpiryDate(null); // No expiry date
        completedReport.setReportFile(reportFile);

        // First call for PENDING/IN_PROGRESS (empty)
        when(reportGenerationRepository.findByInstanceIdAndStatusIn(eq(1L), eq(List.of(ReportStatus.PENDING, ReportStatus.IN_PROGRESS))))
            .thenReturn(Optional.empty());

        // Second call for COMPLETED (found)
        when(reportGenerationRepository.findByInstanceIdAndStatusIn(eq(1L), eq(List.of(ReportStatus.COMPLETED))))
            .thenReturn(Optional.of(completedReport));

        boolean result = service.activeReportExistsForInstance(1L);

        assertTrue(result, "COMPLETED report with null expiryDate should be treated as active (defensive)");
    }

    @Test
    void activeReportExistsForInstance_shouldReturnTrue_whenCompletedWithNullReportFile() {
        Instance instance = buildInstance(1L, InstanceStatus.ESEGUITA);
        ReportGeneration completedReport = buildReport(10L, instance, ReportStatus.COMPLETED);
        completedReport.setReportFile(null); // No report file

        // First call for PENDING/IN_PROGRESS (empty)
        when(reportGenerationRepository.findByInstanceIdAndStatusIn(eq(1L), eq(List.of(ReportStatus.PENDING, ReportStatus.IN_PROGRESS))))
            .thenReturn(Optional.empty());

        // Second call for COMPLETED (found)
        when(reportGenerationRepository.findByInstanceIdAndStatusIn(eq(1L), eq(List.of(ReportStatus.COMPLETED))))
            .thenReturn(Optional.of(completedReport));

        boolean result = service.activeReportExistsForInstance(1L);

        assertTrue(result, "COMPLETED report with null reportFile should be treated as active (defensive)");
    }

    // ---------------------------------------------------------------------
    // getActiveReportByInstance
    // ---------------------------------------------------------------------

    @Test
    void getActiveReportByInstance_shouldReturnDTO() {
        Instance instance = buildInstance(1L, InstanceStatus.ESEGUITA);
        ReportGeneration report = buildReport(10L, instance, ReportStatus.PENDING);

        when(reportGenerationRepository.findByInstanceIdAndStatusIn(eq(1L), anyList()))
            .thenReturn(Optional.of(report));

        ReportGenerationResponseDTO dto = service.getActiveReportByInstance(1L);

        assertNotNull(dto);
        assertEquals(10L, dto.getId());
        assertEquals("PENDING", dto.getStatus());
    }

    @Test
    void getActiveReportByInstance_shouldReturnNullIfNotFound() {
        when(reportGenerationRepository.findByInstanceIdAndStatusIn(eq(1L), anyList()))
            .thenReturn(Optional.empty());

        ReportGenerationResponseDTO dto = service.getActiveReportByInstance(1L);

        assertNull(dto);
    }

    // ---------------------------------------------------------------------
    // scheduleAsyncReport
    // ---------------------------------------------------------------------

    @Test
    void scheduleAsyncReport_shouldCreateReport() {
        Instance instance = buildInstance(1L, InstanceStatus.ESEGUITA);

        when(instanceRepository.findById(1L)).thenReturn(Optional.of(instance));
        when(reportGenerationRepository.findByInstanceIdAndStatusIn(anyLong(), anyList()))
            .thenReturn(Optional.empty());
        when(reportGenerationRepository.save(any()))
            .thenAnswer(inv -> inv.getArgument(0));

        ReportGenerationRequestDTO request = new ReportGenerationRequestDTO();
        request.setInstanceIds(List.of(1L));
        request.setLanguage("it");
        createSecurityContext();
        List<ReportAsyncAcceptedDTO> result = service.scheduleAsyncReport(request);

        assertEquals(1, result.size());
        assertEquals(ReportStatusEnum.PENDING, result.get(0).getStatus());
    }

    @Test
    void scheduleAsyncReport_shouldThrowDuplicateReportException() {
        Instance instance = buildInstance(1L, InstanceStatus.ESEGUITA);

        when(instanceRepository.findById(1L)).thenReturn(Optional.of(instance));
        when(reportGenerationRepository.findByInstanceIdAndStatusIn(anyLong(), anyList()))
            .thenReturn(Optional.of(new ReportGeneration()));

        ReportGenerationRequestDTO request = new ReportGenerationRequestDTO();
        request.setInstanceIds(List.of(1L));

        assertThrows(DuplicateReportException.class,
            () -> service.scheduleAsyncReport(request));
    }

    private static void createSecurityContext() {
        Map<String, Object> claims = new HashMap<>();
        claims.put("preferred_username", "admin");

        Jwt jwt = new Jwt(
            "token-value",
            Instant.now(),
            Instant.now().plusSeconds(60),
            Map.of("alg", "none"),
            claims
        );

        Authentication authentication = new JwtAuthenticationToken(jwt);

        SecurityContext securityContext = SecurityContextHolder.createEmptyContext();
        securityContext.setAuthentication(authentication);
        SecurityContextHolder.setContext(securityContext);
    }
    @Test
    void scheduleAsyncReport_shouldAllowWhenCompletedReportExpired() {
        Instance instance = buildInstance(1L, InstanceStatus.ESEGUITA);
        ReportGeneration expiredReport = buildReport(10L, instance, ReportStatus.COMPLETED);

        ReportFile reportFile = new ReportFile();
        reportFile.setExpiryDate(LocalDateTime.now().minusDays(5)); // Expired 5 days ago
        expiredReport.setReportFile(reportFile);

        when(instanceRepository.findById(1L)).thenReturn(Optional.of(instance));

        // First call for PENDING/IN_PROGRESS (empty)
        when(reportGenerationRepository.findByInstanceIdAndStatusIn(eq(1L), eq(List.of(ReportStatus.PENDING, ReportStatus.IN_PROGRESS))))
            .thenReturn(Optional.empty());

        // Second call for COMPLETED (found expired)
        when(reportGenerationRepository.findByInstanceIdAndStatusIn(eq(1L), eq(List.of(ReportStatus.COMPLETED))))
            .thenReturn(Optional.of(expiredReport));

        when(reportGenerationRepository.save(any()))
            .thenAnswer(inv -> inv.getArgument(0));

        ReportGenerationRequestDTO request = new ReportGenerationRequestDTO();
        request.setInstanceIds(List.of(1L));
        request.setLanguage("it");
        request.setStartDate(LocalDate.now().minusDays(7));
        request.setEndDate(LocalDate.now());
        createSecurityContext();
        List<ReportAsyncAcceptedDTO> result = service.scheduleAsyncReport(request);

        assertEquals(1, result.size());
        assertEquals(ReportStatusEnum.PENDING, result.get(0).getStatus());
        verify(reportGenerationRepository, times(1)).save(any());
    }

    @Test
    void scheduleAsyncReport_shouldBlockWhenCompletedReportNotExpired() {
        Instance instance = buildInstance(1L, InstanceStatus.ESEGUITA);
        ReportGeneration activeReport = buildReport(10L, instance, ReportStatus.COMPLETED);

        ReportFile reportFile = new ReportFile();
        reportFile.setExpiryDate(LocalDateTime.now().plusDays(20)); // Still valid for 20 days
        activeReport.setReportFile(reportFile);

        when(instanceRepository.findById(1L)).thenReturn(Optional.of(instance));

        // First call for PENDING/IN_PROGRESS (empty)
        when(reportGenerationRepository.findByInstanceIdAndStatusIn(eq(1L), eq(List.of(ReportStatus.PENDING, ReportStatus.IN_PROGRESS))))
            .thenReturn(Optional.empty());

        // Second call for COMPLETED (found not expired)
        when(reportGenerationRepository.findByInstanceIdAndStatusIn(eq(1L), eq(List.of(ReportStatus.COMPLETED))))
            .thenReturn(Optional.of(activeReport));

        ReportGenerationRequestDTO request = new ReportGenerationRequestDTO();
        request.setInstanceIds(List.of(1L));
        request.setLanguage("it");

        assertThrows(DuplicateReportException.class,
            () -> service.scheduleAsyncReport(request),
            "Should block generation when COMPLETED report is not yet expired");
    }

    @Test
    void scheduleAsyncReport_shouldFailIfInstanceNotExecuted() {
        Instance instance = buildInstance(1L, InstanceStatus.IN_ESECUZIONE);

        when(instanceRepository.findById(1L)).thenReturn(Optional.of(instance));

        ReportGenerationRequestDTO request = new ReportGenerationRequestDTO();
        request.setInstanceIds(List.of(1L));

        assertThrows(ReportGenerationException.class,
            () -> service.scheduleAsyncReport(request));
    }

    // ---------------------------------------------------------------------
    // executeAsyncGeneration
    // ---------------------------------------------------------------------

    @Test
    void executeAsyncGeneration_shouldCompleteSuccessfully() throws Exception {
        Instance instance = buildInstance(1L, InstanceStatus.ESEGUITA);
        ReportGeneration report = buildReport(10L, instance, ReportStatus.PENDING);

        when(reportGenerationRepository.findById(10L))
            .thenReturn(Optional.of(report));

        WrapperPdfFiles pdfMock = mock(WrapperPdfFiles.class);

        when(pdfGenerator.generatePDF(any(), anyLong()))
            .thenReturn(List.of(pdfMock));

        when(excelGenerator.generateExcel(anyString()))
            .thenReturn("excel".getBytes(StandardCharsets.UTF_8));

        when(packagingService.createReportPackage(any(), anyList(), any()))
            .thenReturn("zip".getBytes(StandardCharsets.UTF_8));

        doNothing().when(blobStorageService)
            .uploadFile(any(), anyString(), anyString());

        service.executeAsyncGeneration(10L);

        assertEquals(ReportStatus.COMPLETED, report.getStatus());
        verify(reportFileRepository).save(any(ReportFile.class));
    }

    @Test
    void executeAsyncGeneration_shouldFailAndUpdateStatus() throws Exception {
        Instance instance = buildInstance(1L, InstanceStatus.ESEGUITA);
        ReportGeneration report = buildReport(10L, instance, ReportStatus.PENDING);

        when(reportGenerationRepository.findById(10L))
            .thenReturn(Optional.of(report));

        doThrow(new Exception("PDF error"))
            .when(pdfGenerator)
            .generatePDF(any(), anyLong());
        service.executeAsyncGeneration(10L);

        assertEquals(ReportStatus.FAILED, report.getStatus());
        assertEquals(1, report.getRetryCount());
    }

    // ---------------------------------------------------------------------
    // getReportStatus
    // ---------------------------------------------------------------------

    @Test
    void getReportStatus_shouldReturnDTO() {
        Instance instance = buildInstance(1L, InstanceStatus.ESEGUITA);
        ReportGeneration report = buildReport(10L, instance, ReportStatus.COMPLETED);

        when(reportGenerationRepository.findById(10L))
            .thenReturn(Optional.of(report));

        ReportGenerationResponseDTO dto = service.getReportStatus(10L);

        assertEquals("COMPLETED", dto.getStatus());
        assertEquals(10L, dto.getId());
    }
}
