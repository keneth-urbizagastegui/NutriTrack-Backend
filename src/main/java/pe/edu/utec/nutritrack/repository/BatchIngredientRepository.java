package pe.edu.utec.nutritrack.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pe.edu.utec.nutritrack.model.BatchIngredient;

import java.util.List;

@Repository
public interface BatchIngredientRepository extends JpaRepository<BatchIngredient, Long> {
    List<BatchIngredient> findByBatchId(Long batchId);
}
