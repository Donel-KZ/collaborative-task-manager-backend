package com.donel.taskmanager.service;

import com.donel.taskmanager.model.UserAccount;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    private static final Logger LOGGER = LoggerFactory.getLogger(EmailService.class);

    private final JavaMailSender mailSender;
    private final String mailHost;
    private final String fromAddress;

    public EmailService(
            ObjectProvider<JavaMailSender> mailSenderProvider,
            @Value("${spring.mail.host:}") String mailHost,
            @Value("${app.mail.from:no-reply@taskmanager.local}") String fromAddress
    ) {
        this.mailSender = mailSenderProvider.getIfAvailable();
        this.mailHost = mailHost;
        this.fromAddress = fromAddress;
    }

    public void sendPasswordReset(UserAccount user, String resetLink) {
        if (mailSender == null || mailHost.isBlank()) {
            LOGGER.info("Password reset link for {}: {}", user.getEmail(), resetLink);
            return;
        }

        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(fromAddress);
        message.setTo(user.getEmail());
        message.setSubject("Reset your Task Manager password");
        message.setText("""
                Hi %s,

                Use this link to reset your password:
                %s

                This link will expire soon. If you did not request it, ignore this email.
                """.formatted(user.getDisplayName(), resetLink));
        mailSender.send(message);
    }
}
