package pe.edu.utec.nutritrack.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import pe.edu.utec.nutritrack.dto.request.ProductRequest;
import pe.edu.utec.nutritrack.dto.response.ProductResponse;
import pe.edu.utec.nutritrack.model.Product;

@Mapper(componentModel = "spring")
public interface ProductMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "batches", ignore = true)
    Product toEntity(ProductRequest request);

    @Mapping(target = "_links", ignore = true)
    ProductResponse toResponse(Product product);
}
