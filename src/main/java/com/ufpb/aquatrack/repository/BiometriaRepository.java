package com.ufpb.aquatrack.repository;

import com.ufpb.aquatrack.biometria.Biometria;
import com.ufpb.aquatrack.ciclo.Ciclo;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface BiometriaRepository extends JpaRepository<Biometria, Long> {

    //Lista de biometrias de um ciclo específico, apenas as que não estão deletadas, ordenadas por data em ordem crescente
    List<Biometria> findByCicloAndDeletadoFalseOrderByDataBiometriaAsc(Ciclo ciclo);

    //Verifica se existe alguma biometria cadastrada para um ciclo e uma data específicos, apenas as que não estão deletadas
    boolean existsByCicloAndDataBiometriaAndDeletadoFalse(Ciclo ciclo, LocalDate dataBiometria);

}
