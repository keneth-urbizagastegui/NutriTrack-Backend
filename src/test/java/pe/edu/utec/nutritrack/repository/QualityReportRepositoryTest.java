package pe.edu.utec.nutritrack.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;
import pe.edu.utec.nutritrack.model.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
class QualityReportRepositoryTest {

    @Autowired
    private QualityReportRepository qualityReportRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private BatchRepository batchRepository;

    @Test
    void shouldSaveAndFindByBatchIdAndUserId() {
        // Given
        User inspector = User.builder()
                .username("inspector.quality")
                .email("inspector@utec.edu.pe")
                .password("super_secure")
                .createdAt(LocalDateTime.now())
                .roles(Set.of(Role.ROLE_USER))
                .build();
        userRepository.save(inspector);

        Product product = Product.builder()
                .name("Almond Milk")
                .brand("NutriCorp")
                .category(ProductCategory.BEVERAGE)
                .proteinPer100g(1.0)
                .carbsPer100g(3.0)
                .fatPer100g(2.5)
                .build();
        productRepository.save(product);

        Batch batch = Batch.builder()
                .batchNumber("B-ALM-111")
                .productionDate(LocalDate.now())
                .expirationDate(LocalDate.now().plusDays(30))
                .status(BatchStatus.ACTIVE)
                .product(product)
                .build();
        batchRepository.save(batch);

        QualityReport report = QualityReport.builder()
                .batch(batch)
                .user(inspector)
                .reportDate(LocalDateTime.now())
                .title("Inspection Report #1")
                .description("All parameters within standard limit.")
                .status(QualityReportStatus.RESOLVED_OK)
                .build();
        qualityReportRepository.save(report);

        // When
        List<QualityReport> reportsByBatch = qualityReportRepository.findByBatchId(batch.getId());
        List<QualityReport> reportsByUser = qualityReportRepository.findByUserId(inspector.getId());

        // Then
        assertThat(reportsByBatch).hasSize(1);
        assertThat(reportsByBatch.get(0).getDescription()).isEqualTo("All parameters within standard limit.");
        assertThat(reportsByUser).hasSize(1);
        assertThat(reportsByUser.get(0).getUser().getUsername()).isEqualTo("inspector.quality");
    }
}
