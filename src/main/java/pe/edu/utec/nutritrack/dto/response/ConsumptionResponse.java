package pe.edu.utec.nutritrack.dto.response;

import lombok.*;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ConsumptionResponse {
    private Long id;
    private String productName;
    private Integer quantityGrams;
    private java.time.LocalDateTime consumptionDate;
    private MacrosDto consumedMacros;
    private Long batchId;
    private java.util.Map<String, java.util.Map<String, String>> _links;
}
