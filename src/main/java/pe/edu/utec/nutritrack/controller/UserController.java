package pe.edu.utec.nutritrack.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import pe.edu.utec.nutritrack.dto.request.AllergenRequest;
import pe.edu.utec.nutritrack.service.UserService;

import java.security.Principal;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping("/allergens")
    @PreAuthorize("hasAuthority('ROLE_USER')")
    public ResponseEntity<Map<String, String>> addAllergen(
            @Valid @RequestBody AllergenRequest request,
            Principal principal
    ) {
        userService.addAllergen(principal.getName(), request);
        return ResponseEntity.ok(Map.of("message", "Ingrediente marcado como alérgeno correctamente"));
    }

    @DeleteMapping("/allergens/{ingredientId}")
    @PreAuthorize("hasAuthority('ROLE_USER')")
    public ResponseEntity<Map<String, String>> removeAllergen(
            @PathVariable Long ingredientId,
            Principal principal
    ) {
        userService.removeAllergen(principal.getName(), ingredientId);
        return ResponseEntity.ok(Map.of("message", "Ingrediente removido como alérgeno correctamente"));
    }
}
