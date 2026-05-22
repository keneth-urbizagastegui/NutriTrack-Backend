package pe.edu.utec.nutritrack.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "products")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(length = 500)
    private String description;

    @Column(nullable = false, length = 50)
    private String brand;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private ProductCategory category;

    @Column(name = "protein_per_100g", nullable = false)
    @Min(0)
    private Double proteinPer100g;

    @Column(name = "carbs_per_100g", nullable = false)
    @Min(0)
    private Double carbsPer100g;

    @Column(name = "fat_per_100g", nullable = false)
    @Min(0)
    private Double fatPer100g;

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @Builder.Default
    private List<Batch> batches = new ArrayList<>();
}
