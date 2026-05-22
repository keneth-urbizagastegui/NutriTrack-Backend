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
import pe.edu.utec.nutritrack.dto.request.BatchRequest;
import pe.edu.utec.nutritrack.dto.request.ProductRequest;
import pe.edu.utec.nutritrack.dto.response.BatchResponse;
import pe.edu.utec.nutritrack.dto.response.ProductResponse;
import pe.edu.utec.nutritrack.service.BatchService;
import pe.edu.utec.nutritrack.service.ProductService;

@RestController
@RequestMapping("/api/v1/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;
    private final BatchService batchService;

    @PostMapping
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_MANAGER')")
    public ResponseEntity<ProductResponse> createProduct(@Valid @RequestBody ProductRequest request) {
        ProductResponse response = productService.createProduct(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<Page<ProductResponse>> getAllProducts(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) Double minProtein,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "name,asc") String sort
    ) {
        String[] sortParts = sort.split(",");
        String sortField = sortParts[0];
        Sort.Direction sortDirection = sortParts.length > 1 && sortParts[1].equalsIgnoreCase("desc")
                ? Sort.Direction.DESC : Sort.Direction.ASC;

        Pageable pageable = PageRequest.of(page, size, Sort.by(sortDirection, sortField));
        Page<ProductResponse> response = productService.getAllProducts(name, minProtein, pageable);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/{productId}/batches")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_MANAGER')")
    public ResponseEntity<BatchResponse> createBatch(
            @PathVariable Long productId,
            @Valid @RequestBody BatchRequest request
    ) {
        BatchResponse response = batchService.createBatch(productId, request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }
}
