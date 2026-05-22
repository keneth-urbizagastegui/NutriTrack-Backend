package pe.edu.utec.nutritrack.dto.response;

import lombok.*;
import pe.edu.utec.nutritrack.model.FreshnessStatus;
import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TraceabilityIngredientResponse {
    private String ingredientName;
    private String supplierName;
    private LocalDate arrivalDate;
    private FreshnessStatus freshness;
}
