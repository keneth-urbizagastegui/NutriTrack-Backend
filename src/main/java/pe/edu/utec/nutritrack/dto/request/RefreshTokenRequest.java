package pe.edu.utec.nutritrack.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RefreshTokenRequest {

    @NotBlank(message = "El token de actualización es obligatorio")
    private String refreshToken;
}
