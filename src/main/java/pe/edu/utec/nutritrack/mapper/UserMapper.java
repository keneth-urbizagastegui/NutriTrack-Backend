package pe.edu.utec.nutritrack.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import pe.edu.utec.nutritrack.dto.request.RegisterRequest;
import pe.edu.utec.nutritrack.dto.response.RegisterResponse;
import pe.edu.utec.nutritrack.model.User;

@Mapper(componentModel = "spring")
public interface UserMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "roles", ignore = true)
    @Mapping(target = "allergens", ignore = true)
    User toEntity(RegisterRequest request);

    RegisterResponse toResponse(User user);
}
