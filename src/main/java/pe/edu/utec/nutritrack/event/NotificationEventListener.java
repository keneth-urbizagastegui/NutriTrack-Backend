package pe.edu.utec.nutritrack.event;

import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import pe.edu.utec.nutritrack.model.Role;
import pe.edu.utec.nutritrack.model.User;
import pe.edu.utec.nutritrack.repository.ConsumptionLogRepository;
import pe.edu.utec.nutritrack.repository.UserRepository;
import pe.edu.utec.nutritrack.service.EmailService;

import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class NotificationEventListener {

    private final EmailService emailService;
    private final UserRepository userRepository;
    private final ConsumptionLogRepository consumptionLogRepository;

    @Async
    @EventListener
    public void handleUserRegistered(UserRegisteredEvent event) {
        User user = event.getUser();
        emailService.sendWelcomeEmail(user.getEmail(), user.getUsername());
    }

    @Async
    @EventListener
    public void handleQualityReportCreated(QualityReportCreatedEvent event) {
        String batchNumber = event.getQualityReport().getBatch().getBatchNumber();
        String title = event.getQualityReport().getTitle();
        String description = event.getQualityReport().getDescription();

        // Send alert to all managers/admins
        List<User> users = userRepository.findAll();
        List<User> managers = users.stream()
                .filter(u -> u.getRoles().contains(Role.ROLE_MANAGER) || u.getRoles().contains(Role.ROLE_ADMIN))
                .collect(Collectors.toList());

        for (User manager : managers) {
            emailService.sendQualityReportAlert(
                    manager.getEmail(),
                    manager.getUsername(),
                    batchNumber,
                    title,
                    description
            );
        }
    }

    @Async
    @EventListener
    public void handleBatchRecall(BatchRecallEvent event) {
        Long batchId = event.getBatch().getId();
        String batchNumber = event.getBatch().getBatchNumber();
        String productName = event.getBatch().getProduct().getName();

        // Get emails of all users who consumed this batch
        List<String> userEmails = consumptionLogRepository.findEmailsByBatchId(batchId);

        for (String email : userEmails) {
            emailService.sendBatchRecallAlert(email, productName, batchNumber);
        }
    }
}
