package com.ufpb.aquatrack.qualidadeAgua;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
public class ResumoQualidadeAgua {

    private LocalDate ultimaData;
    private List<MedicaoQualidadeAgua> ultima;

    private LocalDate penultimaData;
    private List<MedicaoQualidadeAgua> penultima;

}

