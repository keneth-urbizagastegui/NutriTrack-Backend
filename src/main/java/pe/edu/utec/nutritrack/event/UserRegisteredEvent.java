package pe.edu.utec.nutritrack.event;

import org.springframework.context.ApplicationEvent;
import pe.edu.utec.nutritrack.model.User;

public class UserRegisteredEvent extends ApplicationEvent {
    private final User user;

    public UserRegisteredEvent(Object source, User user) {
        super(source);
        this.user = user;
    }

    public User getUser() {
        return user;
    }
}
