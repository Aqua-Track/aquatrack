package com.ufpb.aquatrack.repository;

import com.ufpb.aquatrack.enums.UsuarioRole;
import com.ufpb.aquatrack.models.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

    boolean existsByRole(UsuarioRole usuarioRole);

    Optional<Usuario> findByLoginAndDeletadoFalse(String login);
    List<Usuario> findByDeletadoFalse();
    Optional<Usuario> findByIdAndDeletadoFalse(Long id);
}
