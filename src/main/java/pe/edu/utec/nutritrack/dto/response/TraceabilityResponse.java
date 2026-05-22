package pe.edu.utec.nutritrack.dto.response;

import lombok.*;
import pe.edu.utec.nutritrack.model.BatchStatus;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TraceabilityResponse {
    private Long batchId;
    private String batchNumber;
    private String productName;
    private BatchStatus status;
    private LocalDate productionDate;
    private LocalDate expirationDate;
    private List<TraceabilityIngredientResponse> timeline;
    private List<TraceabilityCertificateResponse> certificates;
}
