package pe.edu.utec.nutritrack.event;

import org.springframework.context.ApplicationEvent;
import pe.edu.utec.nutritrack.model.QualityReport;

public class QualityReportCreatedEvent extends ApplicationEvent {
    private final QualityReport qualityReport;

    public QualityReportCreatedEvent(Object source, QualityReport qualityReport) {
        super(source);
        this.qualityReport = qualityReport;
    }

    public QualityReport getQualityReport() {
        return qualityReport;
    }
}
