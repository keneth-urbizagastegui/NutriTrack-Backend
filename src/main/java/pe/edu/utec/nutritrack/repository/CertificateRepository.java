package pe.edu.utec.nutritrack.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pe.edu.utec.nutritrack.model.Certificate;

import java.util.List;
import java.util.Optional;

@Repository
public interface CertificateRepository extends JpaRepository<Certificate, Long> {
    Optional<Certificate> findByCertificateNumber(String certificateNumber);
    boolean existsByCertificateNumber(String certificateNumber);
    List<Certificate> findByBatchId(Long batchId);
}
