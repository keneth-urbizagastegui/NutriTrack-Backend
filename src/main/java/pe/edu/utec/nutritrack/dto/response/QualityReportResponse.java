package pe.edu.utec.nutritrack.dto.response;

import lombok.*;
import pe.edu.utec.nutritrack.model.QualityReportStatus;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class QualityReportResponse {
    private Long reportId;
    private String batchNumber;
    private QualityReportStatus status;
    private LocalDateTime reportDate;
}
