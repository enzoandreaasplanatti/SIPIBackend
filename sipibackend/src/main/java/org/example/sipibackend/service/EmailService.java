package org.example.sipibackend.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.beans.factory.annotation.Autowired;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;

@Service
public class EmailService {
    @Autowired
    private JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String from;

    private final String maskedFrom = "eventossipi@gmail.com";

    public void sendTokenEmail(String to, String token) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(maskedFrom); // Enmascarado
        message.setReplyTo(from); // El usuario real para respuestas
        message.setTo(to);
        message.setSubject("Verifica tu cuenta");
        message.setText("Tu código de verificación es: " + token);
        mailSender.send(message);
    }
}
