package com.ufpb.aquatrack.repository;

import com.ufpb.aquatrack.ciclo.Ciclo;
import com.ufpb.aquatrack.viveiro.Viveiro;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CicloRepository extends JpaRepository<Ciclo, Long> {

    //Verifica se existe um ciclo ativo e não deletado para o viveiro
    boolean existsByViveiroAndAtivoTrueAndDeletadoFalse(Viveiro viveiro);

    //Busca um ciclo ativo e não deletado do viveiro
    Optional<Ciclo> findByViveiroAndAtivoTrueAndDeletadoFalse(Viveiro viveiro);

    //Lista todos os ciclos não deletados do viveiro ordenados pela data de povoamento (mais recente primeiro)
    List<Ciclo> findByViveiroAndDeletadoFalseOrderByDataPovoamentoDesc(Viveiro viveiro);

}
