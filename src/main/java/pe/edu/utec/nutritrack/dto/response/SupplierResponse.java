package pe.edu.utec.nutritrack.dto.response;

import lombok.*;
import java.util.Map;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SupplierResponse {
    private Long id;
    private String name;
    private String contactEmail;
    private Boolean isActive;
    private String address;
    private Double latitude;
    private Double longitude;
    private Map<String, Map<String, String>> _links;
}
