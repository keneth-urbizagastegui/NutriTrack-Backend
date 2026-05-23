package pe.edu.utec.nutritrack.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import pe.edu.utec.nutritrack.dto.request.SupplierRequest;
import pe.edu.utec.nutritrack.dto.response.SupplierResponse;
import pe.edu.utec.nutritrack.exception.ResourceNotFoundException;
import pe.edu.utec.nutritrack.exception.UserAlreadyExistsException; // or we can throw UserAlreadyExistsException/similar or custom, let's look at GlobalExceptionHandler
import pe.edu.utec.nutritrack.mapper.SupplierMapper;
import pe.edu.utec.nutritrack.model.Supplier;
import pe.edu.utec.nutritrack.repository.SupplierRepository;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class SupplierService {

    private final SupplierRepository supplierRepository;
    private final SupplierMapper supplierMapper;

    @Transactional
    public SupplierResponse createSupplier(SupplierRequest request) {
        if (supplierRepository.existsByName(request.getName())) {
            throw new IllegalArgumentException("El proveedor con nombre '" + request.getName() + "' ya existe.");
        }
        Supplier supplier = supplierMapper.toEntity(request);
        Supplier savedSupplier = supplierRepository.save(supplier);
        return addLinks(supplierMapper.toResponse(savedSupplier));
    }

    @Transactional(readOnly = true)
    public Page<SupplierResponse> getAllSuppliers(Pageable pageable) {
        return supplierRepository.findAll(pageable)
                .map(supplier -> addLinks(supplierMapper.toResponse(supplier)));
    }

    @Transactional(readOnly = true)
    public SupplierResponse getSupplierById(Long id) {
        Supplier supplier = supplierRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("El proveedor con ID " + id + " no existe."));
        return addLinks(supplierMapper.toResponse(supplier));
    }

    private SupplierResponse addLinks(SupplierResponse response) {
        try {
            String selfUrl = ServletUriComponentsBuilder.fromCurrentContextPath()
                    .path("/api/v1/suppliers/{id}")
                    .buildAndExpand(response.getId())
                    .toUriString();
            response.set_links(Map.of("self", Map.of("href", selfUrl)));
        } catch (Exception e) {
            response.set_links(Map.of("self", Map.of("href", "http://localhost:8080/api/v1/suppliers/" + response.getId())));
        }
        return response;
    }
}
