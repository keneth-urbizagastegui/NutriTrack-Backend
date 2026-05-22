package pe.edu.utec.nutritrack.dto.request;

import jakarta.validation.constraints.*;
import lombok.*;
import pe.edu.utec.nutritrack.model.ProductCategory;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductRequest {

    @NotBlank(message = "El nombre del producto es obligatorio")
    @Size(max = 100, message = "El nombre no debe superar los 100 caracteres")
    private String name;

    @Size(max = 500, message = "La descripción no debe superar los 500 caracteres")
    private String description;

    @NotBlank(message = "La marca es obligatoria")
    @Size(max = 50, message = "La marca no debe superar los 50 caracteres")
    private String brand;

    @NotNull(message = "La categoría es obligatoria")
    private ProductCategory category;

    @NotNull(message = "La cantidad de proteína es obligatoria")
    @Min(value = 0, message = "La proteína no puede ser negativa")
    @Max(value = 100, message = "La proteína no puede superar los 100 gramos por cada 100g")
    private Double proteinPer100g;

    @NotNull(message = "La cantidad de carbohidratos es obligatoria")
    @Min(value = 0, message = "Los carbohidratos no pueden ser negativos")
    @Max(value = 100, message = "Los carbohidratos no pueden superar los 100 gramos por cada 100g")
    private Double carbsPer100g;

    @NotNull(message = "La cantidad de grasas es obligatoria")
    @Min(value = 0, message = "La grasa no puede ser negativa")
    @Max(value = 100, message = "La grasa no puede superar los 100 gramos por cada 100g")
    private Double fatPer100g;
}
