package com.nexigroup.pagopa.cruscotto.web.rest;

import com.nexigroup.pagopa.cruscotto.service.ReportGenerationService;
import com.nexigroup.pagopa.cruscotto.service.dto.ReportAsyncAcceptedDTO;
import com.nexigroup.pagopa.cruscotto.service.dto.ReportGenerationRequestDTO;
import com.nexigroup.pagopa.cruscotto.service.dto.ReportGenerationResponseDTO;
import com.nexigroup.pagopa.cruscotto.service.exception.DuplicateReportException;
import com.nexigroup.pagopa.cruscotto.service.exception.ReportGenerationException;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/api/reports")
@Validated
public class ReportGenerationController {

    private final ReportGenerationService reportService;

    @Autowired
    public ReportGenerationController(ReportGenerationService reportService) {
        this.reportService = reportService;
    }

    @PostMapping("/generate-async")
    public ResponseEntity<List<ReportAsyncAcceptedDTO>> generateAsync(
        @Valid @RequestBody ReportGenerationRequestDTO request) {
        try {
            List<ReportAsyncAcceptedDTO> accepted = reportService.scheduleAsyncReport(request);
            return ResponseEntity.accepted().body(accepted);
        } catch (DuplicateReportException e) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, e.getMessage(), e);
        }
    }

    @GetMapping("/exists/instance/{instanceId}")
    public ResponseEntity<Boolean> activeReportExists(@PathVariable Long instanceId) {
        boolean exists = reportService.activeReportExistsForInstance(instanceId);
        return ResponseEntity.ok(exists);
    }

    @GetMapping("/instance/{instanceId}/active")
    public ResponseEntity<ReportGenerationResponseDTO> getActiveReport(@PathVariable Long instanceId) {
        ReportGenerationResponseDTO dto = reportService.getActiveReportByInstance(instanceId);
        if (dto == null) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(dto);
    }

    @GetMapping("/instance/{instanceId}")
    public ResponseEntity<List<ReportGenerationResponseDTO>> getAllReportsForInstance(@PathVariable Long instanceId) {
        List<ReportGenerationResponseDTO> list = reportService.getAllReportsForInstance(instanceId);
        return ResponseEntity.ok(list);
    }

    @GetMapping("/{id}/status")
    public ResponseEntity<ReportGenerationResponseDTO> getStatus(@PathVariable Long id) {
        ReportGenerationResponseDTO status = reportService.getReportStatus(id);
        if (status == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(status);
    }

    @PostMapping("/{id}/execute")
    public ResponseEntity<Void> executeAsyncGeneration(@PathVariable Long id) {
        try {
            reportService.executeAsyncGeneration(id);
            return ResponseEntity.noContent().build();
        } catch (ReportGenerationException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage(), e);
        }
    }
}
