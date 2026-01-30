package com.ufpb.aquatrack.core.relatorio;

import com.ufpb.aquatrack.core.biometria.Biometria;
import com.ufpb.aquatrack.core.ciclo.Ciclo;
import com.ufpb.aquatrack.core.ciclo.CicloService;
import com.ufpb.aquatrack.core.viveiro.Viveiro;
import com.ufpb.aquatrack.core.fazenda.Fazenda;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.time.temporal.ChronoUnit;

@Service
public class RelatorioCicloService {

    private final RelatorioCicloRepository repository;
    private final CicloService cicloService;

    public RelatorioCicloService(RelatorioCicloRepository repository, CicloService cicloService) {
        this.repository = repository;
        this.cicloService = cicloService;
    }

    public RelatorioCiclo gerarRelatorioFinal(
            Ciclo ciclo, Viveiro viveiro,
            Fazenda fazenda, Biometria ultimaBiometria,
            BigDecimal consumoTotalRacao
    ) {

        BigDecimal biomassaFinal = cicloService.calcularBiomassaKg(ultimaBiometria, ciclo);

        BigDecimal fca = cicloService.calcularFca(biomassaFinal, consumoTotalRacao);

        BigDecimal sobrevivencia = ciclo.getSobrevivenciaAtual().multiply(BigDecimal.valueOf(100));

        Long diasCultivo =
                ChronoUnit.DAYS.between(
                        ciclo.getDataPovoamento(), ciclo.getDataEncerramento()
                );

        RelatorioCiclo relatorio = new RelatorioCiclo(
                viveiro.getId(),
                viveiro.getTag(),
                fazenda.getCodigo(),
                fazenda.getNome(),
                ciclo.getLaboratorio(),
                ciclo.getDataPovoamento(),
                ciclo.getDataEncerramento(),
                diasCultivo,
                ciclo.getQuantidadePovoada(),
                ultimaBiometria.getPesoMedio(),
                biomassaFinal,
                consumoTotalRacao,
                sobrevivencia,
                fca
        );

        return repository.save(relatorio);
    }
}
