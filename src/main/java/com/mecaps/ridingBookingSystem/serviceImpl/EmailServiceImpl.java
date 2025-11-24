package com.mecaps.ridingBookingSystem.serviceImpl;

import com.mecaps.ridingBookingSystem.service.EmailService;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
/**
 * Implementation of {@link EmailService} that handles sending simple text emails.
 * Uses Spring's {@link JavaMailSender} to send messages.
 */

@Service
public class EmailServiceImpl  implements EmailService {

    private final JavaMailSender mailSender;
    /**
     * Constructor-based dependency injection for JavaMailSender.
     *
     * @param mailSender the mail sender used to send emails
     */
    public EmailServiceImpl(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }
    /**
     * Sends a basic text email to the specified recipient.
     * Mails Related to Forget Password , Notification etc.
     * @param to      recipient email address
     * @param subject subject of the email
     * @param body    content/body of the email
     */
    @Override
    public void sendSimpleEmail(String to, String subject, String body) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject(subject);
        message.setText(body);

        mailSender.send(message);
    }
}
