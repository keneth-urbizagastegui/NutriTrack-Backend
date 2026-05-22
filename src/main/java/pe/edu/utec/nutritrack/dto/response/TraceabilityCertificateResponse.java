package pe.edu.utec.nutritrack.dto.response;

import lombok.*;
import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TraceabilityCertificateResponse {
    private String laboratoryName;
    private String documentUrl;
    private LocalDate issueDate;
}
