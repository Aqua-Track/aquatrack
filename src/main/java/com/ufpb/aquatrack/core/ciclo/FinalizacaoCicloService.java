package com.ufpb.aquatrack.core.ciclo;

import com.ufpb.aquatrack.core.biometria.Biometria;
import com.ufpb.aquatrack.core.biometria.BiometriaService;
import com.ufpb.aquatrack.core.racao.consumo.ConsumoRacao;
import com.ufpb.aquatrack.core.relatorio.RelatorioCicloService;
import com.ufpb.aquatrack.core.racao.consumo.ConsumoRacaoService;
import com.ufpb.aquatrack.core.usuario.Usuario;
import com.ufpb.aquatrack.core.viveiro.Viveiro;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Service
public class FinalizacaoCicloService {

    private final CicloService cicloService;
    private final RelatorioCicloService relatorioService;
    private final BiometriaService biometriaService;
    private final ConsumoRacaoService consumoRacaoService;

    public FinalizacaoCicloService(
            CicloService cicloService,
            RelatorioCicloService relatorioService,
            BiometriaService biometriaService,
            ConsumoRacaoService consumoRacaoService
    ) {
        this.cicloService = cicloService;
        this.relatorioService = relatorioService;
        this.biometriaService = biometriaService;
        this.consumoRacaoService = consumoRacaoService;
    }

    @Transactional
    public void finalizarCiclo(Long viveiroId, Usuario usuario) {

        Ciclo ciclo = cicloService.buscarCicloAtivo(viveiroId, usuario);
        if (ciclo == null) {
            throw new IllegalStateException("Não existe ciclo ativo para finalizar.");
        }
        ciclo.setDataEncerramento(LocalDate.now());

        Viveiro viveiro = ciclo.getViveiro();

        Biometria ultimaBiometria = biometriaService.listarBiometrias(viveiroId, usuario).getLast();

        if (ultimaBiometria == null) {
            throw new IllegalStateException("Não existe biometria para finalizar o ciclo.");
        }

        List<ConsumoRacao> consumos = consumoRacaoService.listarConsumosDoCiclo(viveiroId, usuario);
        BigDecimal consumoTotal =
                consumoRacaoService.calcularConsumoTotal(consumos);

        if (consumoTotal == null) {
            consumoTotal = BigDecimal.ZERO;
        }

        relatorioService.gerarRelatorioFinal(ciclo, viveiro, viveiro.getFazenda(), ultimaBiometria, consumoTotal);
        cicloService.encerrarCiclo(viveiroId, LocalDate.now(), usuario);
    }
}
