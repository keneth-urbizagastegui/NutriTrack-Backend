package pe.edu.utec.nutritrack.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;
import pe.edu.utec.nutritrack.model.Product;
import pe.edu.utec.nutritrack.model.ProductCategory;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
class ProductRepositoryTest {

    @Autowired
    private ProductRepository productRepository;

    @Test
    void shouldSaveAndFindProductById() {
        // Given
        Product product = Product.builder()
                .name("Oatmeal Premium")
                .brand("NutriCorp")
                .category(ProductCategory.READY_MEAL)
                .proteinPer100g(12.0)
                .carbsPer100g(67.0)
                .fatPer100g(6.0)
                .build();

        Product saved = productRepository.save(product);

        // When
        Optional<Product> found = productRepository.findById(saved.getId());

        // Then
        assertThat(found).isPresent();
        assertThat(found.get().getName()).isEqualTo("Oatmeal Premium");
        assertThat(found.get().getBrand()).isEqualTo("NutriCorp");
        assertThat(found.get().getCategory()).isEqualTo(ProductCategory.READY_MEAL);
    }
}
