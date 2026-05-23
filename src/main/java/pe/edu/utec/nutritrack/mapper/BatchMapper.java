package pe.edu.utec.nutritrack.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import pe.edu.utec.nutritrack.dto.request.BatchRequest;
import pe.edu.utec.nutritrack.dto.response.BatchResponse;
import pe.edu.utec.nutritrack.model.Batch;

@Mapper(componentModel = "spring")
public interface BatchMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "qrCodeUrl", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "product", ignore = true)
    @Mapping(target = "ingredients", ignore = true)
    @Mapping(target = "certificates", ignore = true)
    Batch toEntity(BatchRequest request);

    @Mapping(target = "_links", ignore = true)
    BatchResponse toResponse(Batch batch);
}
