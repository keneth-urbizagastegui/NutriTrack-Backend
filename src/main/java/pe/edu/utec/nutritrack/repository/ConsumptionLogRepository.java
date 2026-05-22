package pe.edu.utec.nutritrack.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import pe.edu.utec.nutritrack.model.ConsumptionLog;

import java.util.List;

@Repository
public interface ConsumptionLogRepository extends JpaRepository<ConsumptionLog, Long>, JpaSpecificationExecutor<ConsumptionLog> {
    Page<ConsumptionLog> findByUserId(Long userId, Pageable pageable);
    
    @Query("SELECT DISTINCT cl.user.email FROM ConsumptionLog cl WHERE cl.batch.id = :batchId")
    List<String> findEmailsByBatchId(@Param("batchId") Long batchId);
}
