package pe.edu.utec.nutritrack.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
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

import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ConsumptionService {

    private final ConsumptionLogRepository consumptionLogRepository;
    private final UserRepository userRepository;
    private final BatchRepository batchRepository;
    private final ConsumptionMapper consumptionMapper;

    @Transactional
    public ConsumptionResponse registerConsumption(String username, ConsumptionRequest request) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado con el nombre: " + username));

        Batch batch = batchRepository.findById(request.getBatchId())
                .orElseThrow(() -> new ResourceNotFoundException("Lote no encontrado con el ID: " + request.getBatchId()));

        // 1. Check if the batch is recalled
        if (batch.getStatus() == BatchStatus.RECALLED) {
            throw new BatchRecallException("El lote " + batch.getBatchNumber() + " ha sido retirado por problemas de calidad/inocuidad y no se permite su consumo.");
        }

        // 2. Allergen check (intersection of batch ingredients and user allergens)
        Set<Long> allergenIds = user.getAllergens().stream()
                .map(Ingredient::getId)
                .collect(Collectors.toSet());

        for (BatchIngredient batchIngredient : batch.getIngredients()) {
            Ingredient ingredient = batchIngredient.getIngredient();
            if (allergenIds.contains(ingredient.getId())) {
                throw new AllergenAlertException("El lote del producto contiene el ingrediente '" + ingredient.getName() + "', el cual está registrado como alérgeno en tu perfil.");
            }
        }

        // 3. Save consumption log
        ConsumptionLog log = ConsumptionLog.builder()
                .consumptionDate(request.getConsumptionDate())
                .quantityGrams(request.getQuantityGrams())
                .user(user)
                .batch(batch)
                .build();

        ConsumptionLog savedLog = consumptionLogRepository.save(log);

        // 4. Calculate consumed macros and map to response
        ConsumptionResponse response = consumptionMapper.toResponse(savedLog);
        response.setConsumedMacros(calculateMacros(batch.getProduct(), request.getQuantityGrams()));

        return response;
    }

    @Transactional(readOnly = true)
    public Page<ConsumptionResponse> getConsumptionHistory(String username, Pageable pageable) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado con el nombre: " + username));

        Page<ConsumptionLog> logs = consumptionLogRepository.findByUserId(user.getId(), pageable);

        return logs.map(log -> {
            ConsumptionResponse response = consumptionMapper.toResponse(log);
            response.setConsumedMacros(calculateMacros(log.getBatch().getProduct(), log.getQuantityGrams()));
            return response;
        });
    }

    private MacrosDto calculateMacros(Product product, Integer quantityGrams) {
        double factor = quantityGrams / 100.0;
        return MacrosDto.builder()
                .protein(round(product.getProteinPer100g() * factor))
                .carbs(round(product.getCarbsPer100g() * factor))
                .fat(round(product.getFatPer100g() * factor))
                .build();
    }

    private Double round(Double value) {
        if (value == null) return 0.0;
        return Math.round(value * 100.0) / 100.0;
    }
}
