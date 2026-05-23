package pe.edu.utec.nutritrack.dto.response;

import lombok.*;
import java.util.Map;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class IngredientResponse {
    private Long id;
    private String name;
    private String description;
    private Integer shelfLifeDays;
    private Map<String, Map<String, String>> _links;
}
