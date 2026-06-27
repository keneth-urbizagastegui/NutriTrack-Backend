package pe.edu.utec.nutritrack.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import pe.edu.utec.nutritrack.dto.response.ConsumptionResponse;
import pe.edu.utec.nutritrack.model.ConsumptionLog;

@Mapper(componentModel = "spring")
public interface ConsumptionMapper {

    @Mapping(target = "productName", source = "batch.product.name")
    @Mapping(target = "batchId", source = "batch.id")
    @Mapping(target = "consumedMacros", ignore = true) // Calculated and set manually in service
    @Mapping(target = "_links", ignore = true)
    ConsumptionResponse toResponse(ConsumptionLog log);
}
