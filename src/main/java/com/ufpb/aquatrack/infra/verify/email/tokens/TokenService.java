package com.ufpb.aquatrack.infra.verify.email.tokens;

import com.ufpb.aquatrack.repository.TokenRepository;
import com.ufpb.aquatrack.usuario.Usuario;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class TokenService {

    private final TokenRepository tokenRepository;

    public TokenService(TokenRepository tokenRepository) {
        this.tokenRepository = tokenRepository;
    }

    public TokenUsuario gerarToken(Usuario usuario) {
        TokenUsuario token = new TokenUsuario();
        token.setToken(UUID.randomUUID().toString());
        token.setUsuario(usuario);
        token.setExpiraca(LocalDateTime.now().plusDays(7));
        return tokenRepository.save(token);
    }

    public boolean validaToken(String token) {
        TokenUsuario tokenUsuario = tokenRepository.findByToken(token);
        if (tokenUsuario == null) {
            System.out.println("Token null");
            return false;
        }
        if (tokenUsuario.isUsado()) {
            System.out.println("Token usado");
            return false;
        }
        if (tokenUsuario.getExpiraca().isBefore(LocalDateTime.now())) {
            System.out.println("Token expirado");
            return false;
        }
        return true;
       }

    public void consumirToken(String token) {
        TokenUsuario tokenUsuario = tokenRepository.findByToken(token);
        if (tokenUsuario != null) {
            tokenUsuario.setUsado(true);
            tokenRepository.save(tokenUsuario);
        }
    }

    public Usuario getUsuario(String token) {
        validaToken(token);
        TokenUsuario tokenUsuario = tokenRepository.findByToken(token);
        return tokenUsuario.getUsuario();
    }
}
