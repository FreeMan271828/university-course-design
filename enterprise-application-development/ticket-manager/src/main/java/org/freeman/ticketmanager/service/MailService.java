package org.freeman.ticketmanager.service;

import jakarta.mail.Message;
import jakarta.mail.internet.InternetAddress;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessagePreparator;
import org.springframework.stereotype.Service;

@Service
public class MailService {

    @Value("${spring.mail.username}")
    private String emailHost;

    @Autowired
    private JavaMailSender mailSender;

    public void sendMail(String toEmail, String subject,  String text){
        MimeMessagePreparator preparator = mimeMessage -> {
            mimeMessage.setRecipient(Message.RecipientType.TO,
                    new InternetAddress(toEmail));
            mimeMessage.setFrom(new InternetAddress(emailHost));
            mimeMessage.setText(text);
            mimeMessage.setSubject(subject);
        };

        try {
            this.mailSender.send(preparator);
        }
        catch (MailException ex) {
            // simply log it and go on...
            System.err.println(ex.getMessage());
        }
    }
}
