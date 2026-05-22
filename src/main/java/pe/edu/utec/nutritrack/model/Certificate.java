package pe.edu.utec.nutritrack.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Table(name = "certificates")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Certificate {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "certificate_number", unique = true, nullable = false, length = 100)
    private String certificateNumber;

    @Column(name = "laboratory_name", nullable = false, length = 150)
    private String laboratoryName;

    @Column(name = "document_url", nullable = false, length = 500)
    private String documentUrl;

    @Column(name = "issue_date", nullable = false)
    private LocalDate issueDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "batch_id", nullable = false)
    private Batch batch;
}
