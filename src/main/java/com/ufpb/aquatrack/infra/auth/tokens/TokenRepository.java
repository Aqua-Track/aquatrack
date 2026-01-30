package com.ufpb.aquatrack.infra.auth.tokens;


import org.springframework.data.jpa.repository.JpaRepository;

public interface TokenRepository extends JpaRepository<TokenUsuario, Long> {
    TokenUsuario findByToken(String token);
}
