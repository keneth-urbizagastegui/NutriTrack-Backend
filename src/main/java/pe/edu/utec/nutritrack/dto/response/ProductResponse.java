package pe.edu.utec.nutritrack.dto.response;

import lombok.*;
import pe.edu.utec.nutritrack.model.ProductCategory;
import java.util.Map;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductResponse {
    private Long id;
    private String name;
    private String description;
    private String brand;
    private ProductCategory category;
    private Double proteinPer100g;
    private Double carbsPer100g;
    private Double fatPer100g;
    private Map<String, Map<String, String>> _links;
}
