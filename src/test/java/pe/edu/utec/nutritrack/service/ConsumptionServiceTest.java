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
import pe.edu.utec.nutritrack.dto.request.ConsumptionRequest;
import pe.edu.utec.nutritrack.dto.response.ConsumptionResponse;
import pe.edu.utec.nutritrack.dto.response.MacrosDto;
import pe.edu.utec.nutritrack.exception.AllergenAlertException;
import pe.edu.utec.nutritrack.exception.BatchRecallException;
import pe.edu.utec.nutritrack.exception.ResourceNotFoundException;
import pe.edu.utec.nutritrack.mapper.ConsumptionMapper;
import pe.edu.utec.nutritrack.model.*;
import pe.edu.utec.nutritrack.repository.BatchRepository;
import pe.edu.utec.nutritrack.repository.ConsumptionLogRepository;
import pe.edu.utec.nutritrack.repository.UserRepository;
import pe.edu.utec.nutritrack.service.ConsumptionService;

import java.time.LocalDateTime;
import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ConsumptionServiceTest {

    @Mock
    private ConsumptionLogRepository consumptionLogRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private BatchRepository batchRepository;

    @Mock
    private ConsumptionMapper consumptionMapper;

    @InjectMocks
    private ConsumptionService consumptionService;

    @Test
    void registerConsumption_Success() {
        // Given
        String username = "fitness.pro";
        ConsumptionRequest request = new ConsumptionRequest();
        request.setBatchId(1L);
        request.setConsumptionDate(LocalDateTime.now());
        request.setQuantityGrams(200);

        User user = User.builder()
                .id(10L)
                .username(username)
                .email("pro@utec.edu.pe")
                .allergens(new HashSet<>())
                .build();

        Product product = Product.builder()
                .id(5L)
                .name("Oats")
                .proteinPer100g(12.0)
                .carbsPer100g(60.0)
                .fatPer100g(7.0)
                .build();

        Batch batch = Batch.builder()
                .id(1L)
                .batchNumber("B-999")
                .status(BatchStatus.ACTIVE)
                .product(product)
                .ingredients(new ArrayList<>())
                .build();

        ConsumptionLog log = ConsumptionLog.builder()
                .id(100L)
                .quantityGrams(200)
                .user(user)
                .batch(batch)
                .build();

        ConsumptionResponse mockResponse = new ConsumptionResponse();
        mockResponse.setId(100L);
        mockResponse.setQuantityGrams(200);
        mockResponse.setProductName("Oats");

        when(userRepository.findByUsername(username)).thenReturn(Optional.of(user));
        when(batchRepository.findById(1L)).thenReturn(Optional.of(batch));
        when(consumptionLogRepository.save(any(ConsumptionLog.class))).thenReturn(log);
        when(consumptionMapper.toResponse(log)).thenReturn(mockResponse);

        // When
        ConsumptionResponse response = consumptionService.registerConsumption(username, request);

        // Then
        assertThat(response).isNotNull();
        assertThat(response.getId()).isEqualTo(100L);
        assertThat(response.getProductName()).isEqualTo("Oats");
        
        MacrosDto macros = response.getConsumedMacros();
        assertThat(macros).isNotNull();
        // quantityGrams = 200, so factor = 2.0
        // protein = 12 * 2 = 24.0
        // carbs = 60 * 2 = 120.0
        // fat = 7 * 2 = 14.0
        assertThat(macros.getProtein()).isEqualTo(24.0);
        assertThat(macros.getCarbs()).isEqualTo(120.0);
        assertThat(macros.getFat()).isEqualTo(14.0);

        verify(consumptionLogRepository).save(any(ConsumptionLog.class));
    }

    @Test
    void registerConsumption_UserNotFound_ThrowsException() {
        String username = "unknown";
        ConsumptionRequest request = new ConsumptionRequest();
        when(userRepository.findByUsername(username)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> consumptionService.registerConsumption(username, request))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Usuario no encontrado");
    }

    @Test
    void registerConsumption_BatchNotFound_ThrowsException() {
        String username = "fitness.pro";
        ConsumptionRequest request = new ConsumptionRequest();
        request.setBatchId(999L);
        User user = User.builder().username(username).build();

        when(userRepository.findByUsername(username)).thenReturn(Optional.of(user));
        when(batchRepository.findById(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> consumptionService.registerConsumption(username, request))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Lote no encontrado");
    }

    @Test
    void registerConsumption_RecalledBatch_ThrowsException() {
        String username = "fitness.pro";
        ConsumptionRequest request = new ConsumptionRequest();
        request.setBatchId(1L);

        User user = User.builder().username(username).build();
        Batch batch = Batch.builder()
                .id(1L)
                .batchNumber("B-RECALLED")
                .status(BatchStatus.RECALLED)
                .build();

        when(userRepository.findByUsername(username)).thenReturn(Optional.of(user));
        when(batchRepository.findById(1L)).thenReturn(Optional.of(batch));

        assertThatThrownBy(() -> consumptionService.registerConsumption(username, request))
                .isInstanceOf(BatchRecallException.class)
                .hasMessageContaining("ha sido retirado");
    }

    @Test
    void registerConsumption_AllergenAlert_ThrowsException() {
        String username = "fitness.pro";
        ConsumptionRequest request = new ConsumptionRequest();
        request.setBatchId(1L);

        Ingredient peanut = Ingredient.builder().id(2L).name("Peanut").build();
        User user = User.builder()
                .username(username)
                .allergens(Set.of(peanut))
                .build();

        BatchIngredient batchIngredient = BatchIngredient.builder()
                .ingredient(peanut)
                .build();

        Batch batch = Batch.builder()
                .id(1L)
                .batchNumber("B-1")
                .status(BatchStatus.ACTIVE)
                .ingredients(List.of(batchIngredient))
                .build();

        when(userRepository.findByUsername(username)).thenReturn(Optional.of(user));
        when(batchRepository.findById(1L)).thenReturn(Optional.of(batch));

        assertThatThrownBy(() -> consumptionService.registerConsumption(username, request))
                .isInstanceOf(AllergenAlertException.class)
                .hasMessageContaining("contiene el ingrediente 'Peanut'");
    }

    @Test
    void getConsumptionHistory_Success() {
        String username = "fitness.pro";
        Pageable pageable = PageRequest.of(0, 10);
        User user = User.builder().id(10L).username(username).build();

        Product product = Product.builder()
                .name("Oats")
                .proteinPer100g(12.0)
                .carbsPer100g(60.0)
                .fatPer100g(7.0)
                .build();

        Batch batch = Batch.builder().product(product).build();
        ConsumptionLog log = ConsumptionLog.builder()
                .quantityGrams(100)
                .batch(batch)
                .build();

        Page<ConsumptionLog> logPage = new PageImpl<>(List.of(log), pageable, 1);
        ConsumptionResponse mockResponse = new ConsumptionResponse();
        mockResponse.setQuantityGrams(100);
        mockResponse.setProductName("Oats");

        when(userRepository.findByUsername(username)).thenReturn(Optional.of(user));
        when(consumptionLogRepository.findByUserId(10L, pageable)).thenReturn(logPage);
        when(consumptionMapper.toResponse(log)).thenReturn(mockResponse);

        Page<ConsumptionResponse> history = consumptionService.getConsumptionHistory(username, pageable);

        assertThat(history).isNotNull();
        assertThat(history.getContent()).hasSize(1);
        assertThat(history.getContent().get(0).getProductName()).isEqualTo("Oats");
        assertThat(history.getContent().get(0).getConsumedMacros().getProtein()).isEqualTo(12.0);
    }
}
