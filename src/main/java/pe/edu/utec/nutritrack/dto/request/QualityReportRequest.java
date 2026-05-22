package pe.edu.utec.nutritrack.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class QualityReportRequest {

    @NotBlank(message = "El título del reporte es obligatorio")
    @Size(max = 200, message = "El título no debe superar los 200 caracteres")
    private String title;

    @NotBlank(message = "La descripción es obligatoria")
    @Size(max = 1000, message = "La descripción no debe superar los 1000 caracteres")
    private String description;
}
