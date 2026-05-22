package pe.edu.utec.nutritrack.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pe.edu.utec.nutritrack.model.QualityReport;

import java.util.List;

@Repository
public interface QualityReportRepository extends JpaRepository<QualityReport, Long> {
    List<QualityReport> findByBatchId(Long batchId);
    List<QualityReport> findByUserId(Long userId);
}
