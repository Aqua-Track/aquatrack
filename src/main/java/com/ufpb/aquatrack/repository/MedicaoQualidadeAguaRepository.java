package com.ufpb.aquatrack.repository;

import com.ufpb.aquatrack.ciclo.Ciclo;
import com.ufpb.aquatrack.qualidadeAgua.MedicaoQualidadeAgua;
import com.ufpb.aquatrack.parametroQualidadeAgua.ParametroQualidadeAgua;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface MedicaoQualidadeAguaRepository
        extends JpaRepository<MedicaoQualidadeAgua, Long> {

    List<MedicaoQualidadeAgua>
    findByCicloAndDeletadoFalseOrderByDataMedicaoAsc(Ciclo ciclo);

    boolean existsByCicloAndParametroAndDataMedicaoAndDeletadoFalse(
            Ciclo ciclo,
            ParametroQualidadeAgua parametro,
            LocalDate dataMedicao
    );
}
