package com.nexigroup.pagopa.cruscotto.service.impl;

import com.nexigroup.pagopa.cruscotto.domain.Instance;
import com.nexigroup.pagopa.cruscotto.domain.ReportFile;
import com.nexigroup.pagopa.cruscotto.domain.ReportGeneration;
import com.nexigroup.pagopa.cruscotto.domain.enumeration.InstanceStatus;
import com.nexigroup.pagopa.cruscotto.domain.enumeration.ReportStatus;
import com.nexigroup.pagopa.cruscotto.repository.InstanceRepository;
import com.nexigroup.pagopa.cruscotto.repository.ReportFileRepository;
import com.nexigroup.pagopa.cruscotto.repository.ReportGenerationRepository;
import com.nexigroup.pagopa.cruscotto.security.SecurityUtils;
import com.nexigroup.pagopa.cruscotto.service.AzureBlobStorageService;
import com.nexigroup.pagopa.cruscotto.service.GenericServiceException;
import com.nexigroup.pagopa.cruscotto.service.report.excel.ExcelReportGenerator;
import com.nexigroup.pagopa.cruscotto.service.report.pdf.PDFReportGenerator;
import com.nexigroup.pagopa.cruscotto.service.report.pdf.wrapper.WrapperPdfFiles;
import com.nexigroup.pagopa.cruscotto.service.ReportGenerationService;
import com.nexigroup.pagopa.cruscotto.service.ReportPackagingService;
import com.nexigroup.pagopa.cruscotto.service.dto.DownloadInfoDTO;
import com.nexigroup.pagopa.cruscotto.service.dto.ExcelFile;
import com.nexigroup.pagopa.cruscotto.service.dto.ReportAsyncAcceptedDTO;
import com.nexigroup.pagopa.cruscotto.service.dto.ReportFilterDTO;
import com.nexigroup.pagopa.cruscotto.service.dto.ReportGenerationContext;
import com.nexigroup.pagopa.cruscotto.service.dto.ReportGenerationRequestDTO;
import com.nexigroup.pagopa.cruscotto.service.dto.ReportGenerationResponseDTO;
import com.nexigroup.pagopa.cruscotto.service.dto.ReportStatusEnum;
import com.nexigroup.pagopa.cruscotto.service.exception.DuplicateReportException;
import com.nexigroup.pagopa.cruscotto.service.exception.ReportGenerationException;
import com.nexigroup.pagopa.cruscotto.service.exception.ReportNotFoundException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing report generation.
 */
@Service
@Transactional
public class ReportGenerationServiceImpl implements ReportGenerationService {

    private static final Logger log = LoggerFactory.getLogger(ReportGenerationServiceImpl.class);

    private final ReportGenerationRepository reportGenerationRepository;
    private final ReportFileRepository reportFileRepository;
    private final InstanceRepository instanceRepository;
    private final PDFReportGenerator pdfGenerator;
    private final ExcelReportGenerator excelGenerator;
    private final AzureBlobStorageService blobStorageService;
    private final ReportPackagingService packagingService;

    @Value("${report.azure.blob.container-name:reports}")
    private String blobContainer;

    @Value("${report.generation.retention-days:30}")
    private int retentionDays;

    public ReportGenerationServiceImpl(
        ReportGenerationRepository reportGenerationRepository,
        ReportFileRepository reportFileRepository,
        InstanceRepository instanceRepository,
        PDFReportGenerator pdfGenerator,
        ExcelReportGenerator excelGenerator,
        AzureBlobStorageService blobStorageService,
        ReportPackagingService packagingService
    ) {
        this.reportGenerationRepository = reportGenerationRepository;
        this.reportFileRepository = reportFileRepository;
        this.instanceRepository = instanceRepository;
        this.pdfGenerator = pdfGenerator;
        this.excelGenerator = excelGenerator;
        this.blobStorageService = blobStorageService;
        this.packagingService = packagingService;
    }

    @Override
    @Transactional(readOnly = true)
    public boolean activeReportExistsForInstance(Long instanceId) {
        log.debug("Checking for active report for instance: {}", instanceId);

        // Check for PENDING or IN_PROGRESS reports (always blocking)
        List<ReportStatus> inProgressStatuses = Arrays.asList(ReportStatus.PENDING, ReportStatus.IN_PROGRESS);
        Optional<ReportGeneration> inProgress = reportGenerationRepository.findByInstanceIdAndStatusIn(instanceId, inProgressStatuses);

        if (inProgress.isPresent()) {
            log.debug("Found active report in PENDING/IN_PROGRESS status for instance: {}", instanceId);
            return true;
        }

        // Check for COMPLETED reports - only blocking if not expired
        List<ReportStatus> completedStatus = Arrays.asList(ReportStatus.COMPLETED);
        Optional<ReportGeneration> completed = reportGenerationRepository.findByInstanceIdAndStatusIn(instanceId, completedStatus);

        if (completed.isPresent()) {
            ReportGeneration report = completed.orElseThrow();
            ReportFile reportFile = report.getReportFile();

            // Defensive: if reportFile or expiryDate is null, treat as not expired (blocking)
            if (reportFile == null || reportFile.getExpiryDate() == null) {
                log.debug("COMPLETED report found but no expiryDate available - treating as active (blocking) for instance: {}", instanceId);
                return true;
            }

            // Check if report is still within retention period
            LocalDateTime now = LocalDateTime.now();
            if (now.isBefore(reportFile.getExpiryDate()) || now.isEqual(reportFile.getExpiryDate())) {
                log.debug("COMPLETED report found and not expired (expiryDate: {}) - blocking for instance: {}", reportFile.getExpiryDate(), instanceId);
                return true;
            }

            log.debug("COMPLETED report found but expired (expiryDate: {}) - allowing new generation for instance: {}", reportFile.getExpiryDate(), instanceId);
            return false;
        }

        log.debug("No active report found for instance: {}", instanceId);
        return false;
    }

    @Override
    @Transactional(readOnly = true)
    public ReportGenerationResponseDTO getActiveReportByInstance(Long instanceId) {
        log.debug("Getting active report for instance: {}", instanceId);

        // Check for PENDING or IN_PROGRESS reports first
        List<ReportStatus> inProgressStatuses = Arrays.asList(ReportStatus.PENDING, ReportStatus.IN_PROGRESS);
        Optional<ReportGeneration> inProgress = reportGenerationRepository.findByInstanceIdAndStatusIn(instanceId, inProgressStatuses);

        if (inProgress.isPresent()) {
            return mapToResponseDTO(inProgress.orElseThrow());
        }

        // Check for COMPLETED reports - return only if not expired
        List<ReportStatus> completedStatus = Arrays.asList(ReportStatus.COMPLETED);
        Optional<ReportGeneration> completed = reportGenerationRepository.findByInstanceIdAndStatusIn(instanceId, completedStatus);

        if (completed.isPresent()) {
            ReportGeneration report = completed.orElseThrow();
            ReportFile reportFile = report.getReportFile();

            // If no expiryDate, treat as active
            if (reportFile == null || reportFile.getExpiryDate() == null) {
                return mapToResponseDTO(report);
            }

            // Return only if not expired
            LocalDateTime now = LocalDateTime.now();
            if (now.isBefore(reportFile.getExpiryDate()) || now.isEqual(reportFile.getExpiryDate())) {
                return mapToResponseDTO(report);
            }

            // Report expired - return null
            log.debug("COMPLETED report expired for instance: {}", instanceId);
            return null;
        }

        return null;
    }

    @Override
    @Transactional(readOnly = true)
    public List<ReportGenerationResponseDTO> getAllReportsForInstance(Long instanceId) {
        log.debug("Getting all reports for instance: {}", instanceId);
        return reportGenerationRepository.findAllByInstanceId(instanceId).stream().map(this::mapToResponseDTO).toList();
    }

    @Override
    public List<ReportAsyncAcceptedDTO> scheduleAsyncReport(ReportGenerationRequestDTO request) throws DuplicateReportException {
        // Validate request has at least one instance
        if (request.getInstanceIds() == null || request.getInstanceIds().isEmpty()) {
            throw new ReportGenerationException("At least one instance ID is required");
        }

        log.info("Scheduling async reports for {} instances", request.getInstanceIds().size());

        List<ReportAsyncAcceptedDTO> responses = new ArrayList<>();

        // Process each instance ID
        for (Long instanceId : request.getInstanceIds()) {
            try {
                log.debug("Processing instance: {}", instanceId);

                // Validate instance exists and is in correct status
                Instance instance = instanceRepository
                    .findById(instanceId)
                    .orElseThrow(() -> new ReportGenerationException("Instance not found: " + instanceId));

                if (instance.getStatus() != InstanceStatus.ESEGUITA) {
                    log.warn("Skipping instance {}: not in ESEGUITA status (current: {})",
                        instanceId, instance.getStatus());
                    throw new ReportGenerationException(
                        "Instance must be in ESEGUITA status to generate report. Current status: " + instance.getStatus());
                }

                // Check for duplicate report
                if (activeReportExistsForInstance(instanceId)) {
                    log.warn("Skipping instance {}: active report already exists", instanceId);
                    throw new DuplicateReportException(instanceId,
                        "An active report already exists for instance: " + instanceId);
                }
                Optional<ReportGeneration> reportFailed = reportGenerationRepository.findByInstanceIdAndStatusIn(instanceId, Arrays.asList(ReportStatus.FAILED));
                ReportGeneration report = new ReportGeneration();
                if (reportFailed.isPresent()){
                    report= reportFailed.orElseThrow(() -> new RuntimeException("Impossible extract report with instance id :"+ instanceId));
                }
                // Create new report entity with PENDING status
                report.setInstance(instance);
                report.setStatus(ReportStatus.PENDING);
                report.setLanguage(request.getLanguage());
                report.setStartDate(request.getStartDate());
                report.setEndDate(request.getEndDate());
                report.setRequestedDate(LocalDateTime.now());
                report.setRequestedBy(SecurityUtils.getCurrentUserLogin().orElse("system"));
                report.setParameters(request.getParameters());
                report.setRetryCount(0);

                report = reportGenerationRepository.save(report);
                log.info("Report scheduled with ID: {} for instance: {}", report.getId(), instanceId);

                responses.add(mapToAcceptedDTO(report));

            } catch (DuplicateReportException | ReportGenerationException e) {
                log.error("Failed to schedule report for instance {}: {}", instanceId, e.getMessage());
                // For now, stop on first error. Could be changed to continue with remaining instances.
                throw e;
            }
        }

        log.info("Successfully scheduled {} reports", responses.size());
        return responses;
    }

    @Override
    public void executeAsyncGeneration(Long reportId) throws ReportGenerationException {
        log.info("Executing async generation for report: {}", reportId);

        // Fetch report
        ReportGeneration report = reportGenerationRepository.findById(reportId).orElseThrow(() -> new ReportNotFoundException(reportId));

        // Validate status is PENDING
        if (report.getStatus() != ReportStatus.PENDING) {
            log.warn("Report {} is not in PENDING status, current status: {}", reportId, report.getStatus());
            return;
        }

        try {
            // Update status to IN_PROGRESS
            report.setStatus(ReportStatus.IN_PROGRESS);
            report.setGenerationStartDate(LocalDateTime.now());
            reportGenerationRepository.save(report);
            log.info("Report {} status updated to IN_PROGRESS", reportId);

            // Build context
            ReportGenerationContext context = buildContext(report);

            // Generate PDF (always)
            log.debug("Generating PDF for report: {}", reportId);
            Locale locale = Locale.forLanguageTag(context.getLanguage());
            List<WrapperPdfFiles> pdfFiles = pdfGenerator.generatePDF(locale, context.getInstanceId());
            log.info("PDF generated successfully for report: {}, {} file(s)", reportId, pdfFiles.size());

            // Generate all 3 Excel files (always)
            log.debug("Generating Excel files for report: {}", reportId);

            byte[] drilldownExcel = excelGenerator.generateExcel(context.getInstanceId().toString());
            log.info(
                "Excel files generated successfully for report: {}, size: {} bytes",
                reportId,
                drilldownExcel.length
            );

            // Create ExcelFile objects
            List<ExcelFile> excelFiles = new ArrayList<>();

            ExcelFile drilldownFile = new ExcelFile();
            drilldownFile.setFileName(context.getInstanceName() + ".xlsx");
            drilldownFile.setContent(drilldownExcel);
            drilldownFile.setDescription("Detailed drill-down data");
            excelFiles.add(drilldownFile);

            // Package into ZIP
            log.debug("Packaging report: {}", reportId);
            byte[] zipContent = packagingService.createReportPackage(context, pdfFiles, drilldownFile);
            log.info("Report packaged successfully: {}, size: {} bytes", reportId, zipContent.length);

            // Generate blob name and file name
            String blobName = generateBlobName(report);
            String fileName = generateFileName(report);

            // Extract blob path from blob name (remove filename)
            int lastSlashIndex = blobName.lastIndexOf('/');
            String blobPath = lastSlashIndex > 0 ? blobName.substring(0, lastSlashIndex) : "";
            String blobFileName = lastSlashIndex > 0 ? blobName.substring(lastSlashIndex + 1) : blobName;

            // Upload to Azure Blob Storage
            log.debug("Uploading report to Azure Blob Storage: {}", blobName);
            blobStorageService.uploadFile(zipContent, blobPath, blobFileName);
            log.info("Report uploaded successfully: {}", reportId);

            // Set retention policy
            // blobStorageService.setRetentionPolicy(blobContainer, blobName, retentionDays);

            // Calculate checksum
            String checksum = calculateChecksum(zipContent);

            // Get package contents
            // List<String> packageContents = packagingService.getPackageContents(zipContent);

            // Create ReportFile entity
            ReportFile reportFile = new ReportFile();
            reportFile.setReportGeneration(report);
            reportFile.setBlobName(blobName);
            reportFile.setBlobContainer(blobContainer);
            reportFile.setFileName(fileName);
            reportFile.setContentType("application/zip");
            reportFile.setFileSizeBytes((long) zipContent.length);
            reportFile.setChecksum(checksum);
            reportFile.setUploadDate(LocalDateTime.now());
            reportFile.setExpiryDate(LocalDateTime.now().plusDays(retentionDays));
            reportFile.setDeleted(false);
            reportFile.setPackageContents(List.of());
            //List<String> packageContents = packagingService.getPackageContents(context, pdfFiles, excelFiles);
            //reportFile.setPackageContents(packageContents);

            reportFileRepository.save(reportFile);
            log.info("ReportFile entity created for report: {}", reportId);

            // Update report status to COMPLETED
            report.setStatus(ReportStatus.COMPLETED);
            report.setGenerationEndDate(LocalDateTime.now());
            report.setReportFile(reportFile);
            reportGenerationRepository.save(report);

            log.info("Report generation completed successfully: {}", reportId);
        } catch (Exception e) {
            log.error("Failed to generate report: {}", reportId, e);

            // Update status to FAILED
            report.setStatus(ReportStatus.FAILED);
            report.setGenerationEndDate(LocalDateTime.now());
            report.setErrorMessage(e.getMessage());
            report.setRetryCount(report.getRetryCount() + 1);
            report.setLastRetryDate(LocalDateTime.now());
            reportGenerationRepository.save(report);

            //throw new ReportGenerationException("Failed to generate report: " + reportId, e);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public ReportGenerationResponseDTO getReportStatus(Long reportId) {
        log.debug("Getting report status: {}", reportId);
        return reportGenerationRepository.findById(reportId).map(this::mapToResponseDTO).orElseThrow(() -> new ReportNotFoundException(reportId));
    }

    // @Override
    // @Transactional(readOnly = true)
    // public String getDownloadUrl(Long reportId) {
    //     log.debug("Getting download URL for report: {}", reportId);

    //     ReportGeneration report = reportGenerationRepository.findById(reportId).orElseThrow(() -> new ReportNotFoundException(reportId));

    //     if (report.getStatus() != ReportStatus.COMPLETED) {
    //         throw new ReportGenerationException("Report is not yet completed. Current status: " + report.getStatus());
    //     }

    //     ReportFile reportFile = reportFileRepository
    //         .findByReportGenerationId(reportId)
    //         .orElseThrow(() -> new ReportGenerationException("Report file not found for report: " + reportId));

    //     // Generate SAS URL with 1-hour expiry
    //     return blobStorageService.generateSasUrl(reportFile.getBlobContainer(), reportFile.getBlobName(), Duration.ofHours(1));
    // }

    /* @Override
    @Transactional(readOnly = true)
    public Page<ReportGenerationResponseDTO> listReports(ReportFilterDTO filter, Pageable pageable) {
        log.debug("Listing reports with filter: {}", filter);

        // For now, return simple implementation without filtering
        // TODO: Implement proper filtering with Specifications
        Page<ReportGeneration> reports = reportGenerationRepository.findAll(pageable);
        List<ReportGenerationResponseDTO> dtos = reports.getContent().stream().map(this::mapToResponseDTO).toList();

        return new PageImpl<>(dtos, pageable, reports.getTotalElements());
    } */

    /* @Override
    public void deleteReport(Long reportId) {
        log.info("Deleting report: {}", reportId);

        ReportGeneration report = reportGenerationRepository.findById(reportId).orElseThrow(() -> new ReportNotFoundException(reportId));

        // Delete from Azure Blob Storage
        ReportFile reportFile = reportFileRepository.findByReportGenerationId(reportId).orElse(null);

        if (reportFile != null) {
            try {
                blobStorageService.deleteFile(reportFile.getBlobContainer(), reportFile.getBlobName());
                log.info("Blob deleted: {}", reportFile.getBlobName());
            } catch (Exception e) {
                log.warn("Failed to delete blob: {}", reportFile.getBlobName(), e);
            }

            // Soft delete ReportFile
            reportFile.setDeleted(true);
            reportFileRepository.save(reportFile);
        }

        // Delete ReportGeneration entity (cascade will delete ReportFile)
        reportGenerationRepository.delete(report);
        log.info("Report deleted: {}", reportId);
    } */

    // Private helper methods

    private ReportGenerationContext buildContext(ReportGeneration report) {
        ReportGenerationContext context = ReportGenerationContext.builder()
            .reportId(report.getId())
            .instanceId(report.getInstance().getId())
            .instanceName(report.getInstance().getInstanceIdentification())
            .language(report.getLanguage())
            .startDate(report.getStartDate())
            .endDate(report.getEndDate())
            .parameters(report.getParameters())
            .requestedBy(report.getRequestedBy())
            .build();
        return context;
    }

    private String generateBlobName(ReportGeneration report) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss");
        String timestamp = LocalDateTime.now().format(formatter);
        return String.format("%d/%s_report_%d.zip", report.getInstance().getId(), timestamp, report.getId());
    }

    private String generateFileName(ReportGeneration report) {
        return String.format("Report_%s.zip", report.getInstance().getInstanceIdentification().replaceAll("[^a-zA-Z0-9]", "_"));
    }

    private String calculateChecksum(byte[] content) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(content);
            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (Exception e) {
            log.error("Failed to calculate checksum", e);
            return "";
        }
    }

    private ReportAsyncAcceptedDTO mapToAcceptedDTO(ReportGeneration report) {
        return ReportAsyncAcceptedDTO.builder()
            .id(report.getId())
            .reportType(null) // Not used in current implementation
            .status(ReportStatusEnum.valueOf(report.getStatus().name()))
            .requestedDate(report.getRequestedDate() != null ? report.getRequestedDate().atOffset(java.time.ZoneOffset.UTC) : null)
            .completionDate(report.getGenerationEndDate() != null ? report.getGenerationEndDate().atOffset(java.time.ZoneOffset.UTC) : null)
            .downloadUrl(null) // Not available at scheduling time
            .fileSizeBytes(null) // Not available at scheduling time
            .fileName(null) // Not available at scheduling time
            .packageContents(null) // Not available at scheduling time
            .build();
    }

    private ReportGenerationResponseDTO mapToResponseDTO(ReportGeneration report) {
        ReportGenerationResponseDTO dto = new ReportGenerationResponseDTO();
        dto.setId(report.getId());
        dto.setInstanceId(report.getInstance().getId());
        dto.setInstanceName(report.getInstance().getInstanceIdentification());
        dto.setStatus(report.getStatus().name());
        dto.setLanguage(report.getLanguage());
        dto.setStartDate(report.getStartDate());
        dto.setEndDate(report.getEndDate());
        dto.setRequestedDate(report.getRequestedDate());
        dto.setGenerationStartDate(report.getGenerationStartDate());
        dto.setGenerationEndDate(report.getGenerationEndDate());
        dto.setRequestedBy(report.getRequestedBy());
        dto.setErrorMessage(report.getErrorMessage());
        dto.setRetryCount(report.getRetryCount());
        dto.setLastRetryDate(report.getLastRetryDate());

        // Add file information if available
        if (report.getReportFile() != null) {
            ReportFile file = report.getReportFile();
            dto.setFileSizeBytes(file.getFileSizeBytes());
            dto.setFileName(file.getFileName());
            dto.setPackageContents(file.getPackageContents());

            // Generate download information if report is completed
            if (report.getStatus() == ReportStatus.COMPLETED) {
                try {
                    Duration sasUrlValidity = Duration.ofHours(1);

                    // Extract blob path and filename from blobName
                    String blobName = file.getBlobName();
                    int lastSlashIndex = blobName.lastIndexOf('/');
                    String blobPath = lastSlashIndex > 0 ? blobName.substring(0, lastSlashIndex) : "";
                    String blobFileName = lastSlashIndex > 0 ? blobName.substring(lastSlashIndex + 1) : blobName;

                    String downloadUrl = blobStorageService.generateSasUrl(
                        blobPath,
                        blobFileName,
                        sasUrlValidity,
                        file.getFileName()
                    );

                    DownloadInfoDTO downloadInfo = new DownloadInfoDTO(
                        downloadUrl,
                        file.getFileName(),
                        file.getFileSizeBytes(),
                        LocalDateTime.now().plus(sasUrlValidity)
                    );
                    dto.setDownloadInfo(downloadInfo);
                } catch (Exception e) {
                    log.warn("Failed to generate download URL for report: {}", report.getId(), e);
                }
            }
        }

        return dto;
    }
}
