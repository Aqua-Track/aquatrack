package com.ufpb.aquatrack.core.instrucao;

import com.ufpb.aquatrack.core.viveiro.Viveiro;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface InstrucaoRepository extends JpaRepository<Instrucao, Long> {

    // Busca uma instrução pela tag dentro do viveiro, respeitando soft delete
    Optional<Instrucao> findByViveiroAndTagAndDeletadoFalse(Viveiro viveiro, String tag);

    // Verifica se já existe uma instrução com a mesma tag no viveiro
    boolean existsByViveiroAndTagAndDeletadoFalse(Viveiro viveiro, String tag);

    // Lista todas as instruções do viveiro (histórico completo)
    List<Instrucao> findByViveiroAndDeletadoFalseOrderByDataCriacaoDesc(Viveiro viveiro);

    // Lista as últimas 3 instruções do viveiro (card do viveiro)
    List<Instrucao> findTop3ByViveiroAndDeletadoFalseOrderByDataCriacaoDesc(Viveiro viveiro);
}