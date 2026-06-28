package pe.edu.utec.nutritrack.dto.response;

import lombok.*;
import pe.edu.utec.nutritrack.model.Role;
import java.time.LocalDateTime;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserAuditResponse {
    private Long id;
    private String username;
    private String email;
    private LocalDateTime createdAt;
    private Set<Role> roles;
    private int allergenCount;
}
