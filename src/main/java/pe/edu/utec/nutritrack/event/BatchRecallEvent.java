package pe.edu.utec.nutritrack.event;

import org.springframework.context.ApplicationEvent;
import pe.edu.utec.nutritrack.model.Batch;

public class BatchRecallEvent extends ApplicationEvent {
    private final Batch batch;

    public BatchRecallEvent(Object source, Batch batch) {
        super(source);
        this.batch = batch;
    }

    public Batch getBatch() {
        return batch;
    }
}
