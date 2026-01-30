package com.ufpb.aquatrack.core.ciclo;

import com.ufpb.aquatrack.core.biometria.Biometria;
import com.ufpb.aquatrack.core.racao.consumo.ConsumoRacao;
import com.ufpb.aquatrack.infra.error.exceptions.RecursoNaoEncontradoException;
import com.ufpb.aquatrack.core.usuario.Usuario;
import com.ufpb.aquatrack.core.viveiro.Viveiro;
import com.ufpb.aquatrack.core.viveiro.ViveiroRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class CicloService {

    private final CicloRepository cicloRepository;
    private final ViveiroRepository viveiroRepository;

    public CicloService(CicloRepository cicloRepository, ViveiroRepository viveiroRepository) {
        this.cicloRepository = cicloRepository;
        this.viveiroRepository = viveiroRepository;
    }


    public boolean existeCicloAtivo(Long viveiroId, Usuario usuario) {
        Viveiro viveiro = buscarViveiroValido(viveiroId, usuario);
        return cicloRepository.existsByViveiroAndAtivoTrueAndDeletadoFalse(viveiro);
    }

    public Ciclo buscarCicloAtivo(Long viveiroId, Usuario usuario) {
        Viveiro viveiro = buscarViveiroValido(viveiroId, usuario);

        return cicloRepository
                .findByViveiroAndAtivoTrueAndDeletadoFalse(viveiro)
                .orElse(null);
    }

    public List<Ciclo> listarCiclosDoViveiro(Long viveiroId, Usuario usuario) {
        Viveiro viveiro = buscarViveiroValido(viveiroId, usuario);

        return cicloRepository
                .findByViveiroAndDeletadoFalseOrderByDataPovoamentoDesc(viveiro);
    }


    @Transactional
    public Ciclo iniciarCiclo(Long viveiroId, LocalDate dataPovoamento,
                              int quantidadePovoada, String laboratorio, Usuario usuario) {

        Viveiro viveiro = buscarViveiroValido(viveiroId, usuario);

        if (cicloRepository.existsByViveiroAndAtivoTrueAndDeletadoFalse(viveiro)) {
            throw new IllegalStateException("Já existe um ciclo ativo neste viveiro.");
        }

        Ciclo ciclo = new Ciclo(viveiro, dataPovoamento, quantidadePovoada, laboratorio);
        return cicloRepository.save(ciclo);
    }

    @Transactional
    public void encerrarCiclo(Long viveiroId, LocalDate dataEncerramento, Usuario usuario) {

        Viveiro viveiro = buscarViveiroValido(viveiroId, usuario);
        Ciclo ciclo = cicloRepository
                .findByViveiroAndAtivoTrueAndDeletadoFalse(viveiro)
                .orElseThrow(() ->
                        new IllegalStateException("Não existe ciclo ativo para encerrar.")
                );

        ciclo.encerrar(dataEncerramento);
    }

    private Viveiro buscarViveiroValido(Long viveiroId, Usuario usuario) {
        Viveiro viveiro = viveiroRepository
                .findByIdAndDeletadoFalse(viveiroId)
                .orElseThrow(() ->
                        new RecursoNaoEncontradoException("Viveiro não encontrado.")
                );

        if (!viveiro.getFazenda().getUsuario().getId().equals(usuario.getId())) {
            throw new IllegalArgumentException("Acesso negado ao viveiro.");
        }

        return viveiro;
    }

    public Map<Long, Boolean> obterStatusCicloPorViveiro(List<Viveiro> viveiros, Usuario usuario) {
        Map<Long, Boolean> statusCicloPorViveiro = new HashMap<>();
        for (Viveiro viveiro : viveiros) {
            boolean temCicloAtivo = existeCicloAtivo(viveiro.getId(), usuario);
            statusCicloPorViveiro.put(viveiro.getId(), temCicloAtivo);
        }
        return statusCicloPorViveiro;
    }

    public BigDecimal calcularSobrevivencia(Biometria biometria, Ciclo ciclo, ConsumoRacao consumoRacao) {
        if (consumoRacao == null) return null;
        else {
        BigDecimal quantidadePovoada = BigDecimal.valueOf(ciclo.getQuantidadePovoada());
        BigDecimal pesoMedioGrama = biometria.getPesoMedio();
        BigDecimal taxaArracoamento = BigDecimal.valueOf(0.04);
        BigDecimal sobrevivenciaAtual = ciclo.getSobrevivenciaAtual();

        // População estimada
        BigDecimal populacaoEstimada = quantidadePovoada.multiply(sobrevivenciaAtual);

        // Biomassa em kg = (população * peso médio g) / 1000
        BigDecimal biomassaKg =
                populacaoEstimada.multiply(pesoMedioGrama).divide(BigDecimal.valueOf(1000), 6, RoundingMode.HALF_UP);

        // Ração esperada
        BigDecimal racaoEsperadaKg = biomassaKg.multiply(taxaArracoamento);

        // Sobrevivência calculada
        BigDecimal sobrevivenciaCalculada = (consumoRacao.getQuantidadeKg()).divide(racaoEsperadaKg, 6, RoundingMode.HALF_UP);

        //Ajusta a sobrevivência estimada
        ciclo.setSobrevivenciaAtual(sobrevivenciaCalculada);

        //Transforma em valor melhor para porcetagem
        BigDecimal sobrevivenciaPercentual =
                    sobrevivenciaCalculada.multiply(BigDecimal.valueOf(100)).setScale(1, RoundingMode.HALF_UP);

        return sobrevivenciaPercentual;
        }
    }

    public BigDecimal calcularBiomassaKg(Biometria biometria, Ciclo ciclo) {
        BigDecimal pesoMedioGrama = biometria.getPesoMedio();
        if (pesoMedioGrama == null) return null;
        else {
            BigDecimal quantidadePovoada = BigDecimal.valueOf(ciclo.getQuantidadePovoada());
            BigDecimal sobrevivenciaAtual = ciclo.getSobrevivenciaAtual();
            BigDecimal populacaoEstimada = quantidadePovoada.multiply(sobrevivenciaAtual);

            // Biomassa em kg
            BigDecimal biomassaKg =
                    populacaoEstimada.multiply(pesoMedioGrama).divide(BigDecimal.valueOf(1000), 6, RoundingMode.HALF_UP);

            return biomassaKg;
        }
    }

    public BigDecimal calcularFca(BigDecimal biomassaKg, BigDecimal consumoTotalKg) {
        if (biomassaKg == null || biomassaKg.compareTo(BigDecimal.ZERO) <= 0) {
            return null;
        }

        if (consumoTotalKg == null || consumoTotalKg.compareTo(BigDecimal.ZERO) <= 0) {
            return BigDecimal.ZERO;
        }

        return consumoTotalKg.divide(biomassaKg, 2, RoundingMode.HALF_UP);
    }

}
