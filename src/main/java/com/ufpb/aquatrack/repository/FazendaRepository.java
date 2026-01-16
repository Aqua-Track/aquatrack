package com.ufpb.aquatrack.repository;

import com.ufpb.aquatrack.models.Fazenda;
import com.ufpb.aquatrack.models.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface FazendaRepository extends JpaRepository<Fazenda, Long> {

    long countByUsuarioAndDeletadoFalse(Usuario usuario);

    List<Fazenda> findByUsuarioAndDeletadoFalse(Usuario usuario);

    Optional<Fazenda> findByIdAndDeletadoFalse(Long id);
}