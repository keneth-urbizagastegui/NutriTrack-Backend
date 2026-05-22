package pe.edu.utec.nutritrack.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import pe.edu.utec.nutritrack.dto.request.QualityReportRequest;
import pe.edu.utec.nutritrack.dto.response.QualityReportResponse;
import pe.edu.utec.nutritrack.dto.response.TraceabilityResponse;
import pe.edu.utec.nutritrack.service.BatchService;
import pe.edu.utec.nutritrack.service.QualityReportService;

import java.security.Principal;

@RestController
@RequestMapping("/api/v1/batches")
@RequiredArgsConstructor
public class BatchController {

    private final BatchService batchService;
    private final QualityReportService qualityReportService;

    @GetMapping("/{id}/traceability")
    public ResponseEntity<TraceabilityResponse> getTraceability(@PathVariable Long id) {
        TraceabilityResponse response = batchService.getTraceability(id);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/{batchId}/quality-reports")
    @PreAuthorize("hasAuthority('ROLE_USER')")
    public ResponseEntity<QualityReportResponse> createReport(
            @PathVariable Long batchId,
            @Valid @RequestBody QualityReportRequest request,
            Principal principal
    ) {
        QualityReportResponse response = qualityReportService.createReport(batchId, principal.getName(), request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @PostMapping("/{id}/recall")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_MANAGER')")
    public ResponseEntity<Void> recallBatch(@PathVariable Long id) {
        batchService.recallBatch(id);
        return ResponseEntity.noContent().build();
    }
}
