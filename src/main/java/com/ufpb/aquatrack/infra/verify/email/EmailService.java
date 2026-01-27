package com.ufpb.aquatrack.infra.verify.email;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    private final JavaMailSender mailSender;

    @Value("${aquaTrack.domain}") //Recupera o domínio do .proprieties do perfil atual
    private String dominio;

    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    public void enviarEmailAtivacao(String emailDestino, String token) {
        String linkAtivacao = dominio + "/ativar-conta?token=" + token;

        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(emailDestino);
        message.setSubject("Aquatrack - Ativação da sua conta");
        message.setText("Clique no link abaixo para ativar sua conta do AquaTrack: \n" + linkAtivacao);

        mailSender.send(message);
    }
}
