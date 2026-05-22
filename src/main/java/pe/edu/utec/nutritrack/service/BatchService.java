package pe.edu.utec.nutritrack.service;

import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pe.edu.utec.nutritrack.dto.request.BatchRequest;
import pe.edu.utec.nutritrack.dto.response.BatchResponse;
import pe.edu.utec.nutritrack.dto.response.TraceabilityCertificateResponse;
import pe.edu.utec.nutritrack.dto.response.TraceabilityIngredientResponse;
import pe.edu.utec.nutritrack.dto.response.TraceabilityResponse;
import pe.edu.utec.nutritrack.event.BatchRecallEvent;
import pe.edu.utec.nutritrack.exception.InvalidBatchDateException;
import pe.edu.utec.nutritrack.exception.ResourceNotFoundException;
import pe.edu.utec.nutritrack.mapper.BatchMapper;
import pe.edu.utec.nutritrack.model.*;
import pe.edu.utec.nutritrack.repository.BatchRepository;
import pe.edu.utec.nutritrack.repository.ProductRepository;
import pe.edu.utec.nutritrack.util.QrCodeGenerator;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BatchService {

    private final BatchRepository batchRepository;
    private final ProductRepository productRepository;
    private final BatchMapper batchMapper;
    private final QrCodeGenerator qrCodeGenerator;
    private final S3StorageService s3StorageService;
    private final ApplicationEventPublisher eventPublisher;

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
            savedBatch.setQrCodeUrl("https://nutritrack-certificates.s3.amazonaws.com/qrs/" + savedBatch.getBatchNumber() + ".png");
            savedBatch = batchRepository.save(savedBatch);
        }

        return batchMapper.toResponse(savedBatch);
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

        eventPublisher.publishEvent(new BatchRecallEvent(this, batch));
    }
}
