package com.ufpb.aquatrack.core.relatorio;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface RelatorioCicloRepository extends JpaRepository<RelatorioCiclo, Long> {

    List<RelatorioCiclo> findByViveiroIdAndDeletadoFalseOrderByDataEncerramentoDesc(Long viveiroId);

    Optional<RelatorioCiclo> findByIdAndViveiroId(Long relatorioId, Long viveiroId);
}
