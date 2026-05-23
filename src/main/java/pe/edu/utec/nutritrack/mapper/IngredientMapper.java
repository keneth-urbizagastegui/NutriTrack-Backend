package pe.edu.utec.nutritrack.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import pe.edu.utec.nutritrack.dto.request.IngredientRequest;
import pe.edu.utec.nutritrack.dto.response.IngredientResponse;
import pe.edu.utec.nutritrack.model.Ingredient;

@Mapper(componentModel = "spring")
public interface IngredientMapper {

    @Mapping(target = "id", ignore = true)
    Ingredient toEntity(IngredientRequest request);

    @Mapping(target = "_links", ignore = true)
    IngredientResponse toResponse(Ingredient ingredient);
}
