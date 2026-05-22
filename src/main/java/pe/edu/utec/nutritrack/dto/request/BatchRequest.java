package pe.edu.utec.nutritrack.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BatchRequest {

    @NotBlank(message = "El número de lote es obligatorio")
    private String batchNumber;

    @NotNull(message = "La fecha de producción es obligatoria")
    private LocalDate productionDate;

    @NotNull(message = "La fecha de expiración es obligatoria")
    private LocalDate expirationDate;
}
