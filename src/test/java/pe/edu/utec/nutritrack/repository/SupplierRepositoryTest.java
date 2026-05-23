package pe.edu.utec.nutritrack.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;
import pe.edu.utec.nutritrack.model.Supplier;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
class SupplierRepositoryTest {

    @Autowired
    private SupplierRepository supplierRepository;

    @Test
    void shouldSaveAndFindSupplierByName() {
        // Given
        Supplier supplier = Supplier.builder()
                .name("Eco Farms")
                .contactEmail("contact@ecofarms.com")
                .isActive(true)
                .build();
        supplierRepository.save(supplier);

        // When
        Optional<Supplier> found = supplierRepository.findByName("Eco Farms");
        boolean exists = supplierRepository.existsByName("Eco Farms");
        boolean notExists = supplierRepository.existsByName("Fake Supplier");

        // Then
        assertThat(found).isPresent();
        assertThat(found.get().getName()).isEqualTo("Eco Farms");
        assertThat(found.get().getContactEmail()).isEqualTo("contact@ecofarms.com");
        assertThat(exists).isTrue();
        assertThat(notExists).isFalse();
    }
}
