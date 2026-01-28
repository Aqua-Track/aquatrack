package com.ufpb.aquatrack.core.relatorio;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface RelatorioCicloRepository extends JpaRepository<RelatorioCiclo, Long> {

    List<RelatorioCiclo> findByViveiroIdAndDeletadoFalseOrderByDataEncerramentoDesc(Long viveiroId);

}
