package pe.edu.utec.nutritrack.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import pe.edu.utec.nutritrack.dto.request.AllergenRequest;
import pe.edu.utec.nutritrack.exception.ResourceNotFoundException;
import pe.edu.utec.nutritrack.model.Ingredient;
import pe.edu.utec.nutritrack.model.User;
import pe.edu.utec.nutritrack.repository.IngredientRepository;
import pe.edu.utec.nutritrack.repository.UserRepository;

import java.util.HashSet;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private IngredientRepository ingredientRepository;

    @InjectMocks
    private UserService userService;

    @Test
    void addAllergen_Success() {
        // Given
        String username = "fitness.pro";
        AllergenRequest request = AllergenRequest.builder().ingredientId(5L).build();

        Ingredient peanut = Ingredient.builder().id(5L).name("Peanut").build();
        User user = User.builder()
                .id(1L)
                .username(username)
                .allergens(new HashSet<>())
                .build();

        when(userRepository.findByUsername(username)).thenReturn(Optional.of(user));
        when(ingredientRepository.findById(5L)).thenReturn(Optional.of(peanut));
        when(userRepository.save(any(User.class))).thenReturn(user);

        // When
        userService.addAllergen(username, request);

        // Then
        assertThat(user.getAllergens()).contains(peanut);
        verify(userRepository).save(user);
    }

    @Test
    void addAllergen_UserNotFound_ThrowsException() {
        // Given
        String username = "unknown";
        AllergenRequest request = AllergenRequest.builder().ingredientId(1L).build();

        when(userRepository.findByUsername(username)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> userService.addAllergen(username, request))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Usuario no encontrado");
    }

    @Test
    void addAllergen_IngredientNotFound_ThrowsException() {
        // Given
        String username = "fitness.pro";
        AllergenRequest request = AllergenRequest.builder().ingredientId(999L).build();

        User user = User.builder().username(username).build();
        when(userRepository.findByUsername(username)).thenReturn(Optional.of(user));
        when(ingredientRepository.findById(999L)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> userService.addAllergen(username, request))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Ingrediente no encontrado");
    }
}
