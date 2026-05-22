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
    private LocalDateTime consumptionDate;
    private MacrosDto consumedMacros;
}
