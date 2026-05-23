package pe.edu.utec.nutritrack.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BatchIngredientRequest {

    @NotNull(message = "El ID del ingrediente es obligatorio")
    private Long ingredientId;

    @NotNull(message = "El ID del proveedor es obligatorio")
    private Long supplierId;

    @NotNull(message = "La fecha de llegada del ingrediente es obligatoria")
    private LocalDate arrivalDate;
}
