package pe.edu.utec.nutritrack.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;
import pe.edu.utec.nutritrack.model.Ingredient;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
class IngredientRepositoryTest {

    @Autowired
    private IngredientRepository ingredientRepository;

    @Test
    void shouldSaveAndFindIngredientByName() {
        // Given
        Ingredient ingredient = Ingredient.builder()
                .name("Cocoa Powder")
                .description("Pure organic cocoa")
                .shelfLifeDays(180)
                .build();
        ingredientRepository.save(ingredient);

        // When
        Optional<Ingredient> found = ingredientRepository.findByName("Cocoa Powder");
        boolean exists = ingredientRepository.existsByName("Cocoa Powder");
        boolean notExists = ingredientRepository.existsByName("Salt");

        // Then
        assertThat(found).isPresent();
        assertThat(found.get().getName()).isEqualTo("Cocoa Powder");
        assertThat(found.get().getShelfLifeDays()).isEqualTo(180);
        assertThat(exists).isTrue();
        assertThat(notExists).isFalse();
    }
}
