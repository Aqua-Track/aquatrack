package com.ufpb.aquatrack.core.fazenda;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface FazendaRepository extends JpaRepository<Fazenda, Long> {

    // Busca uma fazenda pelo ID apenas se ela não estiver deletada
    Optional<Fazenda> findByIdAndDeletadoFalse(Long id);

    boolean existsByCodigo(String codigo);

    Optional<Fazenda> findByCodigoAndDeletadoFalse(String codigo);
}