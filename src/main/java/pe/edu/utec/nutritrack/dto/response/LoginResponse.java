package pe.edu.utec.nutritrack.dto.response;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LoginResponse {
    private String accessToken;
    private String refreshToken;
    private Long expiresIn;
    @Builder.Default
    private String tokenType = "Bearer";
}
