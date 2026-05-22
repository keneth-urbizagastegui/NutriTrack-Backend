package pe.edu.utec.nutritrack.service;

import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.nio.charset.StandardCharsets;

@Service
public class EmailService {

    private final JavaMailSender mailSender;
    private final TemplateEngine templateEngine;
    private final String emailFrom;

    @Autowired
    public EmailService(
            @Autowired(required = false) JavaMailSender mailSender,
            TemplateEngine templateEngine,
            @Value("${email.from:onboarding@resend.dev}") String emailFrom) {
        this.mailSender = mailSender;
        this.templateEngine = templateEngine;
        this.emailFrom = emailFrom;
    }

    public void sendWelcomeEmail(String toEmail, String username) {
        try {
            Context context = new Context();
            context.setVariable("username", username);
            String htmlContent = templateEngine.process("welcome", context);
            sendHtmlMail(toEmail, "¡Bienvenido a NutriTrack!", htmlContent);
        } catch (Exception e) {
            System.err.println("Error enviando correo de bienvenida: " + e.getMessage());
        }
    }

    public void sendQualityReportAlert(String toEmail, String managerName, String batchNumber, String title, String description) {
        try {
            Context context = new Context();
            context.setVariable("managerName", managerName);
            context.setVariable("batchNumber", batchNumber);
            context.setVariable("reportTitle", title);
            context.setVariable("reportDescription", description);
            String htmlContent = templateEngine.process("quality-report-alert", context);
            sendHtmlMail(toEmail, "ALERTA: Reporte de Calidad Registrado - Lote " + batchNumber, htmlContent);
        } catch (Exception e) {
            System.err.println("Error enviando correo de alerta de calidad: " + e.getMessage());
        }
    }

    public void sendBatchRecallAlert(String toEmail, String productName, String batchNumber) {
        try {
            Context context = new Context();
            context.setVariable("productName", productName);
            context.setVariable("batchNumber", batchNumber);
            String htmlContent = templateEngine.process("batch-recall-alert", context);
            sendHtmlMail(toEmail, "URGENTE: Retiro de Lote " + batchNumber + " - NutriTrack", htmlContent);
        } catch (Exception e) {
            System.err.println("Error enviando correo de retiro de lote: " + e.getMessage());
        }
    }

    private void sendHtmlMail(String to, String subject, String htmlContent) throws Exception {
        if (mailSender == null) {
            System.out.println("JavaMailSender no está configurado (modo offline). Imprimiendo correo:");
            System.out.println("Para: " + to);
            System.out.println("Asunto: " + subject);
            return;
        }
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(
                message, 
                MimeMessageHelper.MULTIPART_MODE_MIXED_RELATED, 
                StandardCharsets.UTF_8.name()
        );

        helper.setFrom(emailFrom);
        helper.setTo(to);
        helper.setSubject(subject);
        helper.setText(htmlContent, true);

        mailSender.send(message);
    }
}
