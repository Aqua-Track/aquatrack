package com.ufpb.aquatrack.repository;

import com.ufpb.aquatrack.qualidadeAgua.QualidadeAgua;
import com.ufpb.aquatrack.ciclo.Ciclo;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface QualidadeAguaRepository extends JpaRepository<QualidadeAgua, Long> {

    // Verifica se existe um registro do ciclo na data de coleta informada e que não esteja deletado
    boolean existsByCicloAndDataColetaAndDeletadoFalse(Ciclo ciclo, LocalDate dataColeta);

    // Busca todas as qualidades de água de um ciclo que não estejam deletadas, ordenadas pela data de coleta (mais antiga primeiro)
    List<QualidadeAgua> findByCicloAndDeletadoFalseOrderByDataColetaAsc(Ciclo ciclo);

    // Busca o registro mais recente de qualidade de água de um ciclo que não esteja deletado
    Optional<QualidadeAgua> findTopByCicloAndDeletadoFalseOrderByDataColetaDesc(Ciclo ciclo);

}
