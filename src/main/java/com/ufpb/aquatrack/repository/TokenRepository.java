package com.ufpb.aquatrack.repository;


import com.ufpb.aquatrack.infra.auth.tokens.TokenUsuario;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TokenRepository extends JpaRepository<TokenUsuario, Long> {
    TokenUsuario findByToken(String token);
}
