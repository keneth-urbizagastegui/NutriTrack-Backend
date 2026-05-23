package pe.edu.utec.nutritrack.dto.response;

import lombok.*;
import pe.edu.utec.nutritrack.model.FreshnessStatus;
import java.time.LocalDate;
import java.util.Map;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BatchIngredientResponse {
    private Long id;
    private Long batchId;
    private String ingredientName;
    private String supplierName;
    private LocalDate arrivalDate;
    private FreshnessStatus freshnessStatus;
    private Map<String, Map<String, String>> _links;
}
