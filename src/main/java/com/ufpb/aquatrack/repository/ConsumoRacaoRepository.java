package com.ufpb.aquatrack.repository;

import com.ufpb.aquatrack.ciclo.Ciclo;
import com.ufpb.aquatrack.consumo.ConsumoRacao;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ConsumoRacaoRepository extends JpaRepository<ConsumoRacao, Long> {

    // Retorna os consumos de ração de um ciclo, não deletados, ordenados da data mais recente para a mais antiga
    List<ConsumoRacao> findByCicloAndDeletadoFalseOrderByDataConsumoDesc(Ciclo ciclo);

}
