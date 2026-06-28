package pe.edu.utec.nutritrack.service;

import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import pe.edu.utec.nutritrack.dto.request.BatchIngredientRequest;
import pe.edu.utec.nutritrack.dto.request.BatchRequest;
import pe.edu.utec.nutritrack.dto.response.BatchIngredientResponse;
import pe.edu.utec.nutritrack.dto.response.BatchResponse;
import pe.edu.utec.nutritrack.dto.response.TraceabilityCertificateResponse;
import pe.edu.utec.nutritrack.dto.response.TraceabilityIngredientResponse;
import pe.edu.utec.nutritrack.dto.response.TraceabilityResponse;
import pe.edu.utec.nutritrack.event.BatchRecallEvent;
import pe.edu.utec.nutritrack.exception.InvalidBatchDateException;
import pe.edu.utec.nutritrack.exception.ResourceNotFoundException;
import pe.edu.utec.nutritrack.exception.SupplierNotActiveException;
import pe.edu.utec.nutritrack.mapper.BatchMapper;
import pe.edu.utec.nutritrack.model.*;
import pe.edu.utec.nutritrack.repository.*;
import pe.edu.utec.nutritrack.util.QrCodeGenerator;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BatchService {

    private final BatchRepository batchRepository;
    private final ProductRepository productRepository;
    private final IngredientRepository ingredientRepository;
    private final SupplierRepository supplierRepository;
    private final BatchIngredientRepository batchIngredientRepository;
    private final BatchMapper batchMapper;
    private final QrCodeGenerator qrCodeGenerator;
    private final S3StorageService s3StorageService;
    private final ApplicationEventPublisher eventPublisher;
    private final QualityReportRepository qualityReportRepository;

    @Transactional
    public BatchResponse createBatch(Long productId, BatchRequest request) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("El producto con ID " + productId + " no existe."));

        if (request.getExpirationDate().isBefore(request.getProductionDate()) ||
                request.getExpirationDate().isEqual(request.getProductionDate())) {
            throw new InvalidBatchDateException("La fecha de vencimiento del lote debe ser posterior a la de producción.");
        }

        Batch batch = batchMapper.toEntity(request);
        batch.setProduct(product);
        batch.setStatus(BatchStatus.ACTIVE);

        Batch savedBatch = batchRepository.save(batch);

        try {
            String traceabilityUrl = "https://nutritrack.app/traceability/" + savedBatch.getId();
            byte[] qrBytes = qrCodeGenerator.generateQrCodeImage(traceabilityUrl, 300, 300);
            String qrUrl = s3StorageService.uploadFile("qrs/" + savedBatch.getBatchNumber() + ".png", qrBytes, "image/png");
            savedBatch.setQrCodeUrl(qrUrl);
            savedBatch = batchRepository.save(savedBatch);
        } catch (Exception e) {
            try {
                String traceabilityUrl = "https://nutritrack.app/traceability/" + savedBatch.getId();
                byte[] qrBytes = qrCodeGenerator.generateQrCodeImage(traceabilityUrl, 300, 300);
                String base64Qr = "data:image/png;base64," + java.util.Base64.getEncoder().encodeToString(qrBytes);
                savedBatch.setQrCodeUrl(base64Qr);
                savedBatch = batchRepository.save(savedBatch);
            } catch (Exception ex) {
                savedBatch.setQrCodeUrl("https://nutritrack-certificates.s3.amazonaws.com/qrs/" + savedBatch.getBatchNumber() + ".png");
                savedBatch = batchRepository.save(savedBatch);
            }
        }

        return addBatchLinks(batchMapper.toResponse(savedBatch));
    }

    @Transactional(readOnly = true)
    public TraceabilityResponse getTraceability(Long batchId) {
        Batch batch = batchRepository.findById(batchId)
                .orElseThrow(() -> new ResourceNotFoundException("El lote con ID " + batchId + " no existe."));

        LocalDate today = LocalDate.now();

        List<TraceabilityIngredientResponse> timeline = batch.getIngredients().stream()
                .map(bi -> {
                    int shelfLife = bi.getIngredient().getShelfLifeDays();
                    LocalDate arrival = bi.getArrivalDate();
                    LocalDate midExpiry = arrival.plusDays(shelfLife / 2);
                    LocalDate fullExpiry = arrival.plusDays(shelfLife);

                    FreshnessStatus freshness;
                    if (today.isBefore(midExpiry)) {
                        freshness = FreshnessStatus.FRESH;
                    } else if (today.isBefore(fullExpiry) || today.isEqual(fullExpiry)) {
                        freshness = FreshnessStatus.MATURING;
                    } else {
                        freshness = FreshnessStatus.EXPIRED;
                    }

                    return TraceabilityIngredientResponse.builder()
                            .ingredientName(bi.getIngredient().getName())
                            .supplierName(bi.getSupplier().getName())
                            .arrivalDate(arrival)
                            .freshness(freshness)
                            .build();
                })
                .collect(Collectors.toList());

        List<TraceabilityCertificateResponse> certList = batch.getCertificates().stream()
                .map(c -> TraceabilityCertificateResponse.builder()
                        .laboratoryName(c.getLaboratoryName())
                        .documentUrl(c.getDocumentUrl())
                        .issueDate(c.getIssueDate())
                        .build())
                .collect(Collectors.toList());

        return TraceabilityResponse.builder()
                .batchId(batch.getId())
                .batchNumber(batch.getBatchNumber())
                .productName(batch.getProduct().getName())
                .status(batch.getStatus())
                .productionDate(batch.getProductionDate())
                .expirationDate(batch.getExpirationDate())
                .timeline(timeline)
                .certificates(certList)
                .build();
    }

    @Transactional
    public void recallBatch(Long batchId) {
        Batch batch = batchRepository.findById(batchId)
                .orElseThrow(() -> new ResourceNotFoundException("El lote con ID " + batchId + " no existe."));

        batch.setStatus(BatchStatus.RECALLED);
        batchRepository.save(batch);

        // Actualizar todos los reportes de calidad relacionados con este lote a RECALLED_BATCH
        List<QualityReport> reports = qualityReportRepository.findByBatchId(batchId);
        for (QualityReport report : reports) {
            report.setStatus(QualityReportStatus.RECALLED_BATCH);
        }
        qualityReportRepository.saveAll(reports);

        eventPublisher.publishEvent(new BatchRecallEvent(this, batch));
    }

    @Transactional
    public BatchIngredientResponse addIngredientToBatch(Long batchId, BatchIngredientRequest request) {
        Batch batch = batchRepository.findById(batchId)
                .orElseThrow(() -> new ResourceNotFoundException("El lote con ID " + batchId + " no existe."));
        Ingredient ingredient = ingredientRepository.findById(request.getIngredientId())
                .orElseThrow(() -> new ResourceNotFoundException("El ingrediente con ID " + request.getIngredientId() + " no existe."));
        Supplier supplier = supplierRepository.findById(request.getSupplierId())
                .orElseThrow(() -> new ResourceNotFoundException("El proveedor con ID " + request.getSupplierId() + " no existe."));

        if (!Boolean.TRUE.equals(supplier.getIsActive())) {
            throw new SupplierNotActiveException("El proveedor '" + supplier.getName() + "' no está activo.");
        }

        LocalDate today = LocalDate.now();
        int shelfLife = ingredient.getShelfLifeDays();
        LocalDate arrival = request.getArrivalDate();
        LocalDate midExpiry = arrival.plusDays(shelfLife / 2);
        LocalDate fullExpiry = arrival.plusDays(shelfLife);

        FreshnessStatus freshness;
        if (today.isBefore(midExpiry)) {
            freshness = FreshnessStatus.FRESH;
        } else if (today.isBefore(fullExpiry) || today.isEqual(fullExpiry)) {
            freshness = FreshnessStatus.MATURING;
        } else {
            freshness = FreshnessStatus.EXPIRED;
        }

        BatchIngredient batchIngredient = BatchIngredient.builder()
                .batch(batch)
                .ingredient(ingredient)
                .supplier(supplier)
                .arrivalDate(arrival)
                .freshnessStatus(freshness)
                .build();

        BatchIngredient saved = batchIngredientRepository.save(batchIngredient);

        BatchIngredientResponse response = BatchIngredientResponse.builder()
                .id(saved.getId())
                .batchId(batch.getId())
                .ingredientName(ingredient.getName())
                .supplierName(supplier.getName())
                .arrivalDate(saved.getArrivalDate())
                .freshnessStatus(saved.getFreshnessStatus())
                .build();

        return addBatchIngredientLinks(response);
    }

    @Transactional(readOnly = true)
    public List<BatchResponse> getActiveBatches() {
        return batchRepository.findAll().stream()
                .filter(b -> b.getStatus() == BatchStatus.ACTIVE)
                .map(batch -> {
                    BatchResponse res = batchMapper.toResponse(batch);
                    if (batch.getProduct() != null) {
                        res.setProductName(batch.getProduct().getName());
                        res.setProductId(batch.getProduct().getId());
                    }
                    return addBatchLinks(res);
                })
                .collect(Collectors.toList());
    }

    private BatchResponse addBatchLinks(BatchResponse response) {
        try {
            String selfUrl = ServletUriComponentsBuilder.fromCurrentContextPath()
                    .path("/api/v1/batches/{id}/traceability")
                    .buildAndExpand(response.getId())
                    .toUriString();
            response.set_links(Map.of("self", Map.of("href", selfUrl)));
        } catch (Exception e) {
            response.set_links(Map.of("self", Map.of("href", "http://localhost:8080/api/v1/batches/" + response.getId() + "/traceability")));
        }
        return response;
    }

    private BatchIngredientResponse addBatchIngredientLinks(BatchIngredientResponse response) {
        try {
            String selfUrl = ServletUriComponentsBuilder.fromCurrentContextPath()
                    .path("/api/v1/batches/{batchId}/traceability")
                    .buildAndExpand(response.getBatchId())
                    .toUriString();
            response.set_links(Map.of("self", Map.of("href", selfUrl)));
        } catch (Exception e) {
            response.set_links(Map.of("self", Map.of("href", "http://localhost:8080/api/v1/batches/" + response.getBatchId() + "/traceability")));
        }
        return response;
    }
}
