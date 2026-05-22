package pe.edu.utec.nutritrack.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pe.edu.utec.nutritrack.model.Batch;

import java.util.Optional;

@Repository
public interface BatchRepository extends JpaRepository<Batch, Long> {
    Optional<Batch> findByBatchNumber(String batchNumber);
    boolean existsByBatchNumber(String batchNumber);
}
