package pe.edu.utec.nutritrack.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;
import pe.edu.utec.nutritrack.dto.request.BatchIngredientRequest;
import pe.edu.utec.nutritrack.dto.request.BatchRequest;
import pe.edu.utec.nutritrack.dto.response.BatchIngredientResponse;
import pe.edu.utec.nutritrack.dto.response.BatchResponse;
import pe.edu.utec.nutritrack.exception.InvalidBatchDateException;
import pe.edu.utec.nutritrack.exception.ResourceNotFoundException;
import pe.edu.utec.nutritrack.exception.SupplierNotActiveException;
import pe.edu.utec.nutritrack.mapper.BatchMapper;
import pe.edu.utec.nutritrack.model.*;
import pe.edu.utec.nutritrack.repository.*;
import pe.edu.utec.nutritrack.util.QrCodeGenerator;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BatchServiceTest {

    @Mock
    private BatchRepository batchRepository;

    @Mock
    private ProductRepository productRepository;

    @Mock
    private IngredientRepository ingredientRepository;

    @Mock
    private SupplierRepository supplierRepository;

    @Mock
    private BatchIngredientRepository batchIngredientRepository;

    @Mock
    private BatchMapper batchMapper;

    @Mock
    private QrCodeGenerator qrCodeGenerator;

    @Mock
    private S3StorageService s3StorageService;

    @Mock
    private ApplicationEventPublisher eventPublisher;

    @InjectMocks
    private BatchService batchService;

    @Test
    void createBatch_Success() {
        // Given
        Long productId = 1L;
        BatchRequest request = BatchRequest.builder()
                .batchNumber("B-100")
                .productionDate(LocalDate.now())
                .expirationDate(LocalDate.now().plusMonths(6))
                .build();

        Product product = Product.builder().id(productId).name("Oats").build();

        Batch batch = Batch.builder()
                .id(10L)
                .batchNumber("B-100")
                .productionDate(request.getProductionDate())
                .expirationDate(request.getExpirationDate())
                .product(product)
                .status(BatchStatus.ACTIVE)
                .build();

        BatchResponse expectedResponse = BatchResponse.builder()
                .id(10L)
                .batchNumber("B-100")
                .status(BatchStatus.ACTIVE)
                .build();

        when(productRepository.findById(productId)).thenReturn(Optional.of(product));
        when(batchMapper.toEntity(request)).thenReturn(batch);
        when(batchRepository.save(any(Batch.class))).thenReturn(batch);
        when(batchMapper.toResponse(batch)).thenReturn(expectedResponse);

        // When
        BatchResponse result = batchService.createBatch(productId, request);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getBatchNumber()).isEqualTo("B-100");
        verify(batchRepository, atLeastOnce()).save(any(Batch.class));
    }

    @Test
    void createBatch_ProductNotFound_ThrowsException() {
        // Given
        Long productId = 999L;
        BatchRequest request = BatchRequest.builder()
                .productionDate(LocalDate.now())
                .expirationDate(LocalDate.now().plusDays(30))
                .build();

        when(productRepository.findById(productId)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> batchService.createBatch(productId, request))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("no existe");
    }

    @Test
    void createBatch_InvalidDates_ThrowsException() {
        // Given
        Long productId = 1L;
        BatchRequest request = BatchRequest.builder()
                .productionDate(LocalDate.now())
                .expirationDate(LocalDate.now().minusDays(1))
                .build();

        Product product = Product.builder().id(productId).name("Oats").build();
        when(productRepository.findById(productId)).thenReturn(Optional.of(product));

        // When & Then
        assertThatThrownBy(() -> batchService.createBatch(productId, request))
                .isInstanceOf(InvalidBatchDateException.class)
                .hasMessageContaining("posterior");
    }

    @Test
    void recallBatch_Success() {
        // Given
        Long batchId = 1L;
        Batch batch = Batch.builder()
                .id(batchId)
                .batchNumber("B-100")
                .status(BatchStatus.ACTIVE)
                .build();

        when(batchRepository.findById(batchId)).thenReturn(Optional.of(batch));
        when(batchRepository.save(any(Batch.class))).thenReturn(batch);

        // When
        batchService.recallBatch(batchId);

        // Then
        assertThat(batch.getStatus()).isEqualTo(BatchStatus.RECALLED);
        verify(batchRepository).save(batch);
        verify(eventPublisher).publishEvent(any());
    }

    @Test
    void addIngredientToBatch_Success() {
        // Given
        Long batchId = 1L;
        BatchIngredientRequest request = new BatchIngredientRequest();
        request.setIngredientId(2L);
        request.setSupplierId(3L);
        request.setArrivalDate(LocalDate.now());

        Batch batch = Batch.builder().id(batchId).batchNumber("B-100").build();
        Ingredient ingredient = Ingredient.builder().id(2L).name("Cocoa").shelfLifeDays(180).build();
        Supplier supplier = Supplier.builder().id(3L).name("FarmCo").isActive(true).build();

        BatchIngredient saved = BatchIngredient.builder()
                .id(100L)
                .batch(batch)
                .ingredient(ingredient)
                .supplier(supplier)
                .arrivalDate(request.getArrivalDate())
                .freshnessStatus(FreshnessStatus.FRESH)
                .build();

        when(batchRepository.findById(batchId)).thenReturn(Optional.of(batch));
        when(ingredientRepository.findById(2L)).thenReturn(Optional.of(ingredient));
        when(supplierRepository.findById(3L)).thenReturn(Optional.of(supplier));
        when(batchIngredientRepository.save(any(BatchIngredient.class))).thenReturn(saved);

        // When
        BatchIngredientResponse result = batchService.addIngredientToBatch(batchId, request);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getIngredientName()).isEqualTo("Cocoa");
        assertThat(result.getSupplierName()).isEqualTo("FarmCo");
        verify(batchIngredientRepository).save(any(BatchIngredient.class));
    }

    @Test
    void addIngredientToBatch_SupplierNotActive_ThrowsException() {
        // Given
        Long batchId = 1L;
        BatchIngredientRequest request = new BatchIngredientRequest();
        request.setIngredientId(2L);
        request.setSupplierId(3L);
        request.setArrivalDate(LocalDate.now());

        Batch batch = Batch.builder().id(batchId).build();
        Ingredient ingredient = Ingredient.builder().id(2L).name("Cocoa").shelfLifeDays(30).build();
        Supplier supplier = Supplier.builder().id(3L).name("InactiveCo").isActive(false).build();

        when(batchRepository.findById(batchId)).thenReturn(Optional.of(batch));
        when(ingredientRepository.findById(2L)).thenReturn(Optional.of(ingredient));
        when(supplierRepository.findById(3L)).thenReturn(Optional.of(supplier));

        // When & Then
        assertThatThrownBy(() -> batchService.addIngredientToBatch(batchId, request))
                .isInstanceOf(SupplierNotActiveException.class)
                .hasMessageContaining("no está activo");
    }

    @Test
    void getTraceability_BatchNotFound_ThrowsException() {
        // Given
        when(batchRepository.findById(999L)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> batchService.getTraceability(999L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("no existe");
    }

    @Test
    void getTraceability_Success() {
        // Given
        Product product = Product.builder().id(1L).name("Oats").build();
        Batch batch = Batch.builder()
                .id(1L)
                .batchNumber("B-100")
                .product(product)
                .status(BatchStatus.ACTIVE)
                .productionDate(LocalDate.now())
                .expirationDate(LocalDate.now().plusMonths(6))
                .ingredients(new ArrayList<>())
                .certificates(new ArrayList<>())
                .build();

        when(batchRepository.findById(1L)).thenReturn(Optional.of(batch));

        // When
        var result = batchService.getTraceability(1L);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getBatchNumber()).isEqualTo("B-100");
        assertThat(result.getProductName()).isEqualTo("Oats");
    }
}
