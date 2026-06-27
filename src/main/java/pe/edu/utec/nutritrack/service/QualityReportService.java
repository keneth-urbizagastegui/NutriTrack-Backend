package pe.edu.utec.nutritrack.service;

import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pe.edu.utec.nutritrack.dto.request.QualityReportRequest;
import pe.edu.utec.nutritrack.dto.response.QualityReportResponse;
import pe.edu.utec.nutritrack.event.QualityReportCreatedEvent;
import pe.edu.utec.nutritrack.exception.ResourceNotFoundException;
import pe.edu.utec.nutritrack.mapper.QualityReportMapper;
import pe.edu.utec.nutritrack.model.Batch;
import pe.edu.utec.nutritrack.model.QualityReport;
import pe.edu.utec.nutritrack.model.QualityReportStatus;
import pe.edu.utec.nutritrack.model.User;
import pe.edu.utec.nutritrack.repository.BatchRepository;
import pe.edu.utec.nutritrack.repository.QualityReportRepository;
import pe.edu.utec.nutritrack.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class QualityReportService {

    private final QualityReportRepository qualityReportRepository;
    private final BatchRepository batchRepository;
    private final UserRepository userRepository;
    private final QualityReportMapper qualityReportMapper;
    private final ApplicationEventPublisher eventPublisher;

    @Transactional
    public QualityReportResponse createReport(Long batchId, String username, QualityReportRequest request) {
        Batch batch = batchRepository.findById(batchId)
                .orElseThrow(() -> new ResourceNotFoundException("El lote con ID " + batchId + " no existe."));

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("El usuario " + username + " no existe."));

        QualityReport report = QualityReport.builder()
                .title(request.getTitle())
                .description(request.getDescription())
                .reportDate(LocalDateTime.now())
                .status(QualityReportStatus.PENDING)
                .batch(batch)
                .user(user)
                .build();

        QualityReport savedReport = qualityReportRepository.save(report);

        eventPublisher.publishEvent(new QualityReportCreatedEvent(this, savedReport));

        return qualityReportMapper.toResponse(savedReport);
    }

    @Transactional(readOnly = true)
    public List<QualityReportResponse> getAllReports() {
        return qualityReportRepository.findAll().stream()
                .map(qualityReportMapper::toResponse)
                .collect(Collectors.toList());
    }
}
