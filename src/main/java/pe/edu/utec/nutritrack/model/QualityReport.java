package pe.edu.utec.nutritrack.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "quality_reports")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class QualityReport {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 200)
    private String title;

    @Column(nullable = false, length = 1000)
    private String description;

    @Column(name = "report_date", nullable = false)
    private LocalDateTime reportDate;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private QualityReportStatus status;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "batch_id", nullable = false)
    private Batch batch;
}
