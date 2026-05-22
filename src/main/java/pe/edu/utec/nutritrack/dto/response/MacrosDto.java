package pe.edu.utec.nutritrack.dto.response;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MacrosDto {
    private Double protein;
    private Double carbs;
    private Double fat;
}
