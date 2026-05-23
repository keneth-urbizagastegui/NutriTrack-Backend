package pe.edu.utec.nutritrack.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;
import pe.edu.utec.nutritrack.model.Batch;
import pe.edu.utec.nutritrack.model.BatchStatus;
import pe.edu.utec.nutritrack.model.Product;
import pe.edu.utec.nutritrack.model.ProductCategory;

import java.time.LocalDate;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
class BatchRepositoryTest {

    @Autowired
    private BatchRepository batchRepository;

    @Autowired
    private ProductRepository productRepository;

    @Test
    void shouldSaveAndFindByBatchNumber() {
        // Given
        Product product = Product.builder()
                .name("Greek Yogurt")
                .brand("MilkPro")
                .category(ProductCategory.READY_MEAL)
                .proteinPer100g(10.0)
                .carbsPer100g(4.0)
                .fatPer100g(0.0)
                .build();
        Product savedProduct = productRepository.save(product);

        Batch batch = Batch.builder()
                .batchNumber("B-YOG-999")
                .productionDate(LocalDate.now())
                .expirationDate(LocalDate.now().plusMonths(1))
                .status(BatchStatus.ACTIVE)
                .product(savedProduct)
                .build();
        batchRepository.save(batch);

        // When
        Optional<Batch> found = batchRepository.findByBatchNumber("B-YOG-999");
        boolean exists = batchRepository.existsByBatchNumber("B-YOG-999");
        boolean notExists = batchRepository.existsByBatchNumber("B-FAKE");

        // Then
        assertThat(found).isPresent();
        assertThat(found.get().getBatchNumber()).isEqualTo("B-YOG-999");
        assertThat(found.get().getProduct().getName()).isEqualTo("Greek Yogurt");
        assertThat(exists).isTrue();
        assertThat(notExists).isFalse();
    }
}
