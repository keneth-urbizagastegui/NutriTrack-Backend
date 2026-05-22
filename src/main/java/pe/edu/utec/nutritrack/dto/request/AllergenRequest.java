package pe.edu.utec.nutritrack.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AllergenRequest {

    @NotNull(message = "El ID del ingrediente es obligatorio")
    private Long ingredientId;
}
