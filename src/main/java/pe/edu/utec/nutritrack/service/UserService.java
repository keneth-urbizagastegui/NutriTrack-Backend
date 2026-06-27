package pe.edu.utec.nutritrack.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pe.edu.utec.nutritrack.dto.request.AllergenRequest;
import pe.edu.utec.nutritrack.exception.ResourceNotFoundException;
import pe.edu.utec.nutritrack.model.Ingredient;
import pe.edu.utec.nutritrack.model.User;
import pe.edu.utec.nutritrack.repository.IngredientRepository;
import pe.edu.utec.nutritrack.repository.UserRepository;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final IngredientRepository ingredientRepository;

    @Transactional
    public void addAllergen(String username, AllergenRequest request) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado con el nombre: " + username));

        Ingredient ingredient = ingredientRepository.findById(request.getIngredientId())
                .orElseThrow(() -> new ResourceNotFoundException("Ingrediente no encontrado con el ID: " + request.getIngredientId()));

        user.getAllergens().add(ingredient);
        userRepository.save(user);
    }

    @Transactional
    public void removeAllergen(String username, Long ingredientId) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado con el nombre: " + username));

        Ingredient ingredient = ingredientRepository.findById(ingredientId)
                .orElseThrow(() -> new ResourceNotFoundException("Ingrediente no encontrado con el ID: " + ingredientId));

        user.getAllergens().remove(ingredient);
        userRepository.save(user);
    }
}
