package pe.edu.utec.nutritrack.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ConsumptionRequest {

    @NotNull(message = "El ID del lote es obligatorio")
    private Long batchId;

    @NotNull(message = "La cantidad en gramos es obligatoria")
    @Min(value = 1, message = "La cantidad consumida debe ser al menos 1 gramo")
    private Integer quantityGrams;

    @NotNull(message = "La fecha y hora de consumo es obligatoria")
    private LocalDateTime consumptionDate;
}
