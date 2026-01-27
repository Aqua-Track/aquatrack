package com.ufpb.aquatrack.repository;

import com.ufpb.aquatrack.fazenda.Fazenda;
import com.ufpb.aquatrack.usuario.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface FazendaRepository extends JpaRepository<Fazenda, Long> {

    // Busca uma fazenda pelo ID apenas se ela não estiver deletada
    Optional<Fazenda> findByIdAndDeletadoFalse(Long id);

    boolean existsByCodigo(String codigo);

    Optional<Fazenda> findByCodigoAndDeletadoFalse(String codigo);
}