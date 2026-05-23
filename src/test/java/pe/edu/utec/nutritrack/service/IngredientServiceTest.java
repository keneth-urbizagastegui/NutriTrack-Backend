package pe.edu.utec.nutritrack.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import pe.edu.utec.nutritrack.dto.request.IngredientRequest;
import pe.edu.utec.nutritrack.dto.response.IngredientResponse;
import pe.edu.utec.nutritrack.exception.ResourceNotFoundException;
import pe.edu.utec.nutritrack.mapper.IngredientMapper;
import pe.edu.utec.nutritrack.model.Ingredient;
import pe.edu.utec.nutritrack.repository.IngredientRepository;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class IngredientServiceTest {

    @Mock
    private IngredientRepository ingredientRepository;

    @Mock
    private IngredientMapper ingredientMapper;

    @InjectMocks
    private IngredientService ingredientService;

    @Test
    void createIngredient_Success() {
        // Given
        IngredientRequest request = IngredientRequest.builder()
                .name("Cocoa Powder")
                .description("Pure organic cocoa")
                .shelfLifeDays(180)
                .build();

        Ingredient ingredient = Ingredient.builder()
                .id(1L)
                .name("Cocoa Powder")
                .description("Pure organic cocoa")
                .shelfLifeDays(180)
                .build();

        IngredientResponse expectedResponse = IngredientResponse.builder()
                .id(1L)
                .name("Cocoa Powder")
                .description("Pure organic cocoa")
                .shelfLifeDays(180)
                .build();

        when(ingredientRepository.existsByName("Cocoa Powder")).thenReturn(false);
        when(ingredientMapper.toEntity(request)).thenReturn(ingredient);
        when(ingredientRepository.save(ingredient)).thenReturn(ingredient);
        when(ingredientMapper.toResponse(ingredient)).thenReturn(expectedResponse);

        // When
        IngredientResponse result = ingredientService.createIngredient(request);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getName()).isEqualTo("Cocoa Powder");
        assertThat(result.getShelfLifeDays()).isEqualTo(180);
        verify(ingredientRepository).save(ingredient);
    }

    @Test
    void createIngredient_DuplicateName_ThrowsException() {
        // Given
        IngredientRequest request = IngredientRequest.builder()
                .name("Cocoa Powder")
                .build();

        when(ingredientRepository.existsByName("Cocoa Powder")).thenReturn(true);

        // When & Then
        assertThatThrownBy(() -> ingredientService.createIngredient(request))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("ya existe");
    }

    @Test
    void getIngredientById_Success() {
        // Given
        Ingredient ingredient = Ingredient.builder().id(1L).name("Salt").build();
        IngredientResponse response = IngredientResponse.builder().id(1L).name("Salt").build();

        when(ingredientRepository.findById(1L)).thenReturn(Optional.of(ingredient));
        when(ingredientMapper.toResponse(ingredient)).thenReturn(response);

        // When
        IngredientResponse result = ingredientService.getIngredientById(1L);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getName()).isEqualTo("Salt");
    }

    @Test
    void getIngredientById_NotFound_ThrowsException() {
        // Given
        when(ingredientRepository.findById(999L)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> ingredientService.getIngredientById(999L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("no existe");
    }

    @Test
    void getAllIngredients_Success() {
        // Given
        Pageable pageable = PageRequest.of(0, 10);
        Ingredient ingredient = Ingredient.builder().id(1L).name("Salt").build();
        IngredientResponse response = IngredientResponse.builder().id(1L).name("Salt").build();
        Page<Ingredient> page = new PageImpl<>(List.of(ingredient), pageable, 1);

        when(ingredientRepository.findAll(pageable)).thenReturn(page);
        when(ingredientMapper.toResponse(ingredient)).thenReturn(response);

        // When
        Page<IngredientResponse> result = ingredientService.getAllIngredients(pageable);

        // Then
        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).getName()).isEqualTo("Salt");
    }
}
