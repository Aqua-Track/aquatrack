package com.ufpb.aquatrack.repository;

import com.ufpb.aquatrack.models.Fazenda;
import com.ufpb.aquatrack.models.Viveiro;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ViveiroRepository extends JpaRepository<Viveiro, Long> {

    boolean existsByFazendaAndTagAndDeletadoFalse(Fazenda fazenda, String tag);

    List<Viveiro> findByFazendaAndDeletadoFalse(Fazenda fazenda);

    Optional<Viveiro> findByIdAndDeletadoFalse(Long id);
}
