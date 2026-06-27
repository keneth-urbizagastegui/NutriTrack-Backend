package pe.edu.utec.nutritrack.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import pe.edu.utec.nutritrack.dto.response.QualityReportResponse;
import pe.edu.utec.nutritrack.model.QualityReport;

@Mapper(componentModel = "spring")
public interface QualityReportMapper {

    @Mapping(target = "reportId", source = "id")
    @Mapping(target = "batchNumber", source = "batch.batchNumber")
    @Mapping(target = "batchId", source = "batch.id")
    @Mapping(target = "productName", source = "batch.product.name")
    @Mapping(target = "userName", source = "user.username")
    QualityReportResponse toResponse(QualityReport report);
}
