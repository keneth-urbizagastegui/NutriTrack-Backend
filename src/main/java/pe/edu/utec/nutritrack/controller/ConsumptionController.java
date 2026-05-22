package pe.edu.utec.nutritrack.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import pe.edu.utec.nutritrack.dto.request.ConsumptionRequest;
import pe.edu.utec.nutritrack.dto.response.ConsumptionResponse;
import pe.edu.utec.nutritrack.service.ConsumptionService;

import java.security.Principal;

@RestController
@RequestMapping("/api/v1/consumption")
@RequiredArgsConstructor
public class ConsumptionController {

    private final ConsumptionService consumptionService;

    @PostMapping
    @PreAuthorize("hasAuthority('ROLE_USER')")
    public ResponseEntity<ConsumptionResponse> registerConsumption(
            @Valid @RequestBody ConsumptionRequest request,
            Principal principal
    ) {
        ConsumptionResponse response = consumptionService.registerConsumption(principal.getName(), request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping
    @PreAuthorize("hasAuthority('ROLE_USER')")
    public ResponseEntity<Page<ConsumptionResponse>> getConsumptionHistory(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "consumptionDate,desc") String sort,
            Principal principal
    ) {
        String[] sortParts = sort.split(",");
        String sortField = sortParts[0];
        Sort.Direction sortDirection = sortParts.length > 1 && sortParts[1].equalsIgnoreCase("asc")
                ? Sort.Direction.ASC : Sort.Direction.DESC;

        Pageable pageable = PageRequest.of(page, size, Sort.by(sortDirection, sortField));
        Page<ConsumptionResponse> response = consumptionService.getConsumptionHistory(principal.getName(), pageable);
        return ResponseEntity.ok(response);
    }
}
