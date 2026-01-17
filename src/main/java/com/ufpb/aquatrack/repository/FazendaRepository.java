package com.ufpb.aquatrack.repository;

import com.ufpb.aquatrack.fazenda.Fazenda;
import com.ufpb.aquatrack.usuario.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface FazendaRepository extends JpaRepository<Fazenda, Long> {

    // Conta quantas fazendas pertencem ao usuário e não estão deletadas
    long countByUsuarioAndDeletadoFalse(Usuario usuario);

    // Retorna todas as fazendas do usuário que não estão deletadas
    List<Fazenda> findByUsuarioAndDeletadoFalse(Usuario usuario);

    // Busca uma fazenda pelo ID apenas se ela não estiver deletada
    Optional<Fazenda> findByIdAndDeletadoFalse(Long id);
}