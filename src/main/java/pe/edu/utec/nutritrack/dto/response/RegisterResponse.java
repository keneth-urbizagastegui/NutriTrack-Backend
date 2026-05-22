package pe.edu.utec.nutritrack.dto.response;

import lombok.*;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RegisterResponse {
    private Long id;
    private String username;
    private String email;
    private LocalDateTime createdAt;
}
