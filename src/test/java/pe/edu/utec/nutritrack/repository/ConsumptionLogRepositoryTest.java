package pe.edu.utec.nutritrack.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ActiveProfiles;
import pe.edu.utec.nutritrack.model.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
@ActiveProfiles("test")
class ConsumptionLogRepositoryTest {

    @Autowired
    private ConsumptionLogRepository consumptionLogRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private BatchRepository batchRepository;

    @Test
    void shouldFindEmailsByBatchIdAndFindByUserId() {
        // Given
        User user = User.builder()
                .username("fitness.user")
                .email("user@utec.edu.pe")
                .password("encoded_pass")
                .createdAt(LocalDateTime.now())
                .roles(Set.of(Role.ROLE_USER))
                .build();
        userRepository.save(user);

        Product product = Product.builder()
                .name("Whey Protein")
                .brand("Brand X")
                .category(ProductCategory.SUPPLEMENT)
                .proteinPer100g(80.0)
                .carbsPer100g(5.0)
                .fatPer100g(2.0)
                .build();
        productRepository.save(product);

        Batch batch = Batch.builder()
                .batchNumber("B-12345")
                .productionDate(LocalDate.now().minusDays(5))
                .expirationDate(LocalDate.now().plusMonths(6))
                .status(BatchStatus.ACTIVE)
                .product(product)
                .build();
        batchRepository.save(batch);

        ConsumptionLog log = ConsumptionLog.builder()
                .consumptionDate(LocalDateTime.now())
                .quantityGrams(50)
                .user(user)
                .batch(batch)
                .build();
        consumptionLogRepository.save(log);

        // When
        List<String> emails = consumptionLogRepository.findEmailsByBatchId(batch.getId());
        Page<ConsumptionLog> logsPage = consumptionLogRepository.findByUserId(user.getId(), PageRequest.of(0, 10));

        // Then
        assertThat(emails).containsOnly("user@utec.edu.pe");
        assertThat(logsPage.getContent()).hasSize(1);
        assertThat(logsPage.getContent().get(0).getQuantityGrams()).isEqualTo(50);
    }
}
