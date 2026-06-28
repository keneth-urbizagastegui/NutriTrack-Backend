package pe.edu.utec.nutritrack.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import pe.edu.utec.nutritrack.dto.request.AllergenRequest;
import pe.edu.utec.nutritrack.dto.response.UserProfileResponse;
import pe.edu.utec.nutritrack.service.UserService;

import java.security.Principal;
import java.util.Map;

import java.util.List;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<List<pe.edu.utec.nutritrack.dto.response.UserAuditResponse>> getAllUsers() {
        return ResponseEntity.ok(userService.getAllUsers());
    }

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

    @GetMapping("/me")
    @PreAuthorize("hasAuthority('ROLE_USER')")
    public ResponseEntity<UserProfileResponse> getCurrentUser(Principal principal) {
        return ResponseEntity.ok(userService.getUserProfile(principal.getName()));
    }
}
