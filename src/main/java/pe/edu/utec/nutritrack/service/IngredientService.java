package pe.edu.utec.nutritrack.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import pe.edu.utec.nutritrack.dto.request.IngredientRequest;
import pe.edu.utec.nutritrack.dto.response.IngredientResponse;
import pe.edu.utec.nutritrack.exception.ResourceNotFoundException;
import pe.edu.utec.nutritrack.mapper.IngredientMapper;
import pe.edu.utec.nutritrack.model.Ingredient;
import pe.edu.utec.nutritrack.repository.IngredientRepository;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class IngredientService {

    private final IngredientRepository ingredientRepository;
    private final IngredientMapper ingredientMapper;

    @Transactional
    public IngredientResponse createIngredient(IngredientRequest request) {
        if (ingredientRepository.existsByName(request.getName())) {
            throw new IllegalArgumentException("El ingrediente con nombre '" + request.getName() + "' ya existe.");
        }
        Ingredient ingredient = ingredientMapper.toEntity(request);
        Ingredient savedIngredient = ingredientRepository.save(ingredient);
        return addLinks(ingredientMapper.toResponse(savedIngredient));
    }

    @Transactional(readOnly = true)
    public Page<IngredientResponse> getAllIngredients(Pageable pageable) {
        return ingredientRepository.findAll(pageable)
                .map(ingredient -> addLinks(ingredientMapper.toResponse(ingredient)));
    }

    @Transactional(readOnly = true)
    public IngredientResponse getIngredientById(Long id) {
        Ingredient ingredient = ingredientRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("El ingrediente con ID " + id + " no existe."));
        return addLinks(ingredientMapper.toResponse(ingredient));
    }

    private IngredientResponse addLinks(IngredientResponse response) {
        try {
            String selfUrl = ServletUriComponentsBuilder.fromCurrentContextPath()
                    .path("/api/v1/ingredients/{id}")
                    .buildAndExpand(response.getId())
                    .toUriString();
            response.set_links(Map.of("self", Map.of("href", selfUrl)));
        } catch (Exception e) {
            response.set_links(Map.of("self", Map.of("href", "http://localhost:8080/api/v1/ingredients/" + response.getId())));
        }
        return response;
    }
}
