package com.ufpb.aquatrack.repository;

import com.ufpb.aquatrack.fazenda.Fazenda;
import com.ufpb.aquatrack.viveiro.Viveiro;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ViveiroRepository extends JpaRepository<Viveiro, Long> {

    // Verifica se existe um viveiro com a tag informada na fazenda e que não esteja deletado
    boolean existsByFazendaAndTagAndDeletadoFalse(Fazenda fazenda, String tag);

    // Retorna todos os viveiros de uma fazenda que não estejam deletados
    List<Viveiro> findByFazendaAndDeletadoFalse(Fazenda fazenda);

    // Busca um viveiro pelo ID apenas se ele não estiver deletado
    Optional<Viveiro> findByIdAndDeletadoFalse(Long id);
}
