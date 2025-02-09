package com.javanauta.notification.business;

import com.javanauta.notification.business.dto.TaskDTO;
import com.javanauta.notification.infrastructure.exception.EmailException;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;

@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender javaMailSender;
    private final TemplateEngine templateEngine;

    @Value("${send.email.from}")
    public String from;

    @Value("${send.email.nameFrom}")
    public String nameFrom;

    public void sendEmail(TaskDTO dto){

        try {
            MimeMessage message = javaMailSender.createMimeMessage();
            MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(message, true, StandardCharsets.UTF_8.name());

            mimeMessageHelper.setFrom(new InternetAddress(from, nameFrom));
            mimeMessageHelper.setTo(InternetAddress.parse(dto.getUserEmail()));
            mimeMessageHelper.setSubject("Task Notification");

            // template context
            Context context = new Context();
            context.setVariable("nameTask", dto.getTaskName());
            context.setVariable("dateEvent", dto.getDateEvent());
            context.setVariable("description", dto.getDescription());
            String template = templateEngine.process("notification", context);
            mimeMessageHelper.setText(template, true);
            javaMailSender.send(message);
        } catch (MessagingException | UnsupportedEncodingException e) {
            throw new EmailException("Error while send email ", e.getCause());
        }
    }




}
