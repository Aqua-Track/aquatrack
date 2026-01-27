package com.ufpb.aquatrack.infra.verify.email;

import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import jakarta.mail.internet.MimeMessage;
import org.springframework.mail.javamail.MimeMessageHelper;
import jakarta.mail.MessagingException;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

@Service
public class EmailService {

    private final JavaMailSender mailSender;
    private final TemplateEngine templateEngine;

    @Value("${aquaTrack.domain}")
    private String dominio;

    public EmailService(JavaMailSender mailSender, TemplateEngine templateEngine) {
        this.mailSender = mailSender;
        this.templateEngine = templateEngine;
    }

    public void enviarEmailAtivacao(String emailDestino, String token) {

        String linkAtivacao = dominio + "/ativar-conta?token=" + token;

        Context context = new Context();
        context.setVariable("linkAtivacao", linkAtivacao);
        context.setVariable("logoUrl", dominio + "/images/logo.png");

        String html = templateEngine.process("email/ativar-conta-email", context);

        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper =
                    new MimeMessageHelper(message, true, "UTF-8");

            helper.setTo(emailDestino);
            helper.setSubject("AquaTrack - Ativação da sua conta");
            helper.setText(html, true);
            // Logo embutida
            helper.addInline("logo", new ClassPathResource("static/images/logo-simples.png"));

            mailSender.send(message);

        } catch (MessagingException e) {
            throw new RuntimeException("Erro ao enviar e-mail de ativação", e);
        }
    }

    public void enviarEmailResetSenha(String emailDestino, String token) {
        String linkRedefinirSenha = dominio + "/redefinir-senha/nova-senha?token=" + token;

        Context context = new Context();
        context.setVariable("linkRedefinirSenha", linkRedefinirSenha);
        context.setVariable("logoUrl", dominio + "/images/logo.png");

        String html = templateEngine.process("email/redefinir-senha", context);

        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper =
                    new MimeMessageHelper(message, true, "UTF-8");

            helper.setTo(emailDestino);
            helper.setSubject("AquaTrack - Redefinir sua senha");
            helper.setText(html, true);
            // Logo embutida
            helper.addInline("logo", new ClassPathResource("static/images/logo-simples.png"));

            mailSender.send(message);

        } catch (MessagingException e) {
            throw new RuntimeException("Erro ao enviar e-mail de nova senha", e);
        }
    }
}