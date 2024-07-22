package com.mergeteam.service.impl;

import com.mergeteam.dto.MailParams;
import com.mergeteam.service.MailSenderService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MailServiceSenderImpl implements MailSenderService {

    private final JavaMailSender javaMailSender;
    @Value("${spring.mail.username}")
    private String emailFrom;
    @Value("${mail.activation.uri}")
    private String activationUri;
    @Override
    public void sendEmail(MailParams mailParams) {
        String subject = "Активация учетной записи";
        String mailBody = this.getActivationMailBody(mailParams.getId());
        String emailTo = mailParams.getEmailTo();
        SimpleMailMessage message = new SimpleMailMessage();
        message.setSubject(subject);
        message.setText(mailBody);
        message.setTo(emailTo);
        message.setFrom(emailFrom);
        javaMailSender.send(message);

    }

    private String getActivationMailBody(String id) {
        return String.format("Для завершения регистрации перейдите по ссылке:%n%s", activationUri)
                .replace("{id}", id);
    }
}
