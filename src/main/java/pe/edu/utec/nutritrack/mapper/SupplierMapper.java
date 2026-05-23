package pe.edu.utec.nutritrack.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import pe.edu.utec.nutritrack.dto.request.SupplierRequest;
import pe.edu.utec.nutritrack.dto.response.SupplierResponse;
import pe.edu.utec.nutritrack.model.Supplier;

@Mapper(componentModel = "spring")
public interface SupplierMapper {

    @Mapping(target = "id", ignore = true)
    Supplier toEntity(SupplierRequest request);

    @Mapping(target = "_links", ignore = true)
    SupplierResponse toResponse(Supplier supplier);
}
