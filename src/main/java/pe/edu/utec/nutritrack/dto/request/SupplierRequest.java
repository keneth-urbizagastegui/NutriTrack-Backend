package pe.edu.utec.nutritrack.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SupplierRequest {

    @NotBlank(message = "El nombre del proveedor es obligatorio")
    @Size(max = 100, message = "El nombre del proveedor no puede superar los 100 caracteres")
    private String name;

    @Email(message = "El formato de correo de contacto es inválido")
    @Size(max = 100, message = "El correo de contacto no puede superar los 100 caracteres")
    private String contactEmail;

    @NotNull(message = "El estado activo es obligatorio")
    private Boolean isActive;
}
