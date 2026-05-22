package pe.edu.utec.nutritrack.dto.response;

import lombok.*;
import pe.edu.utec.nutritrack.model.BatchStatus;
import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BatchResponse {
    private Long id;
    private String batchNumber;
    private LocalDate productionDate;
    private LocalDate expirationDate;
    private String qrCodeUrl;
    private BatchStatus status;
}
