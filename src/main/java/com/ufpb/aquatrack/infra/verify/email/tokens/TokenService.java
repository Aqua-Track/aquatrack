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

    public TokenUsuario gerarToken(Usuario usuario, TokenType tipoDeToken) {
        TokenUsuario token = new TokenUsuario();
        token.setToken(UUID.randomUUID().toString());
        token.setUsuario(usuario);

        if (tipoDeToken == TokenType.ATIVACAO_CONTA) {
            token.setExpiraca(LocalDateTime.now().plusDays(7));
        } else {
            token.setExpiraca(LocalDateTime.now().plusHours(1));
        }
        token.setTokenType(tipoDeToken);

        return tokenRepository.save(token);
    }

    public boolean validaToken(String token, TokenType tipoDoToken) {
        TokenUsuario tokenUsuario = tokenRepository.findByToken(token);

        if (tokenUsuario == null || tokenUsuario.isUsado() || tokenUsuario.getExpiraca().isBefore(LocalDateTime.now()) || tipoDoToken != tokenUsuario.getTokenType()) {
            return false;
        } else return true;
       }

    public void consumirToken(String token) {
        TokenUsuario tokenUsuario = tokenRepository.findByToken(token);
        if (tokenUsuario != null) {
            tokenUsuario.setUsado(true);
            tokenRepository.save(tokenUsuario);
        }
    }

    public Usuario getUsuario(String token, TokenType tipoDeToken) {
        if (!validaToken(token, tipoDeToken)) {
            return null; //Excessão de usuário não localizado pelo token
        }
        TokenUsuario tokenUsuario = tokenRepository.findByToken(token);
        return tokenUsuario.getUsuario();
    }
}
