package com.ufpb.aquatrack.repository;

import com.ufpb.aquatrack.enums.UsuarioRole;
import com.ufpb.aquatrack.models.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

    // Verifica se existe algum usuário com o papel informado
    boolean existsByRole(UsuarioRole usuarioRole);

    // Busca um usuário pelo login apenas se ele não estiver deletado
    Optional<Usuario> findByLoginAndDeletadoFalse(String login);

    // Retorna a lista de todos os usuários que não estão deletados
    List<Usuario> findByDeletadoFalse();

    // Busca um usuário pelo ID apenas se ele não estiver deletado
    Optional<Usuario> findByIdAndDeletadoFalse(Long id);

}
