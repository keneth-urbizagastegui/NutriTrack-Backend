package pe.edu.utec.nutritrack.specification;

import org.springframework.data.jpa.domain.Specification;
import pe.edu.utec.nutritrack.model.Product;

public class ProductSpecification {

    public static Specification<Product> hasName(String name) {
        return (root, query, cb) -> {
            if (name == null || name.trim().isEmpty()) {
                return cb.conjunction();
            }
            return cb.like(cb.lower(root.get("name")), "%" + name.trim().toLowerCase() + "%");
        };
    }

    public static Specification<Product> hasProteinGreaterThanOrEqual(Double minProtein) {
        return (root, query, cb) -> {
            if (minProtein == null) {
                return cb.conjunction();
            }
            return cb.greaterThanOrEqualTo(root.get("proteinPer100g"), minProtein);
        };
    }
}
