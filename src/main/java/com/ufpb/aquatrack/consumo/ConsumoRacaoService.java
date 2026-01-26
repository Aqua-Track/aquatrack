package com.ufpb.aquatrack.consumo;

import com.ufpb.aquatrack.ciclo.Ciclo;
import com.ufpb.aquatrack.ciclo.CicloService;
import com.ufpb.aquatrack.estoqueRacao.EstoqueRacao;
import com.ufpb.aquatrack.repository.EstoqueRacaoRepository;
import com.ufpb.aquatrack.repository.ConsumoRacaoRepository;
import com.ufpb.aquatrack.tipoRacao.TipoRacao;
import com.ufpb.aquatrack.repository.TipoRacaoRepository;
import com.ufpb.aquatrack.usuario.Usuario;
import com.ufpb.aquatrack.viveiro.Viveiro;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
public class ConsumoRacaoService {

    private final ConsumoRacaoRepository consumoRepository;
    private final CicloService cicloService;
    private final TipoRacaoRepository tipoRacaoRepository;
    private final EstoqueRacaoRepository estoqueRacaoRepository;

    public ConsumoRacaoService(
            ConsumoRacaoRepository consumoRepository, CicloService cicloService,
            TipoRacaoRepository tipoRacaoRepository, EstoqueRacaoRepository estoqueRacaoRepository) {

        this.consumoRepository = consumoRepository;
        this.cicloService = cicloService;
        this.tipoRacaoRepository = tipoRacaoRepository;
        this.estoqueRacaoRepository = estoqueRacaoRepository;
    }

    public List<ConsumoRacao> listarConsumosDoCiclo(Long viveiroId, Usuario usuario) {
        Ciclo ciclo = cicloService.buscarCicloAtivo(viveiroId, usuario);

        if (ciclo == null) {
            throw new IllegalStateException("Não existe ciclo ativo.");
        }

        return consumoRepository.findByCicloAndDeletadoFalseOrderByDataConsumoDesc(ciclo);
    }

    @Transactional
    public void registrarConsumo(Long viveiroId, Long tipoRacaoId,
                                 BigDecimal quantidadeKg, LocalDate dataConsumo, Usuario usuario) {


        if (quantidadeKg.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Quantidade deve ser maior que zero.");
        }

        Ciclo ciclo = cicloService.buscarCicloAtivo(viveiroId, usuario);
        if (ciclo == null) {
            throw new IllegalStateException("Não existe ciclo ativo para este viveiro.");
        }

        TipoRacao tipoRacao = tipoRacaoRepository
                .findByIdAndDeletadoFalse(tipoRacaoId)
                .orElseThrow(() ->
                        new IllegalArgumentException("Tipo de ração não encontrado.")
                );

        Viveiro viveiro = ciclo.getViveiro();

        EstoqueRacao estoque = estoqueRacaoRepository
                .findByFazendaAndTipoRacaoAndDeletadoFalse(viveiro.getFazenda(), tipoRacao)
                .orElseThrow(() ->
                        new IllegalStateException("Não existe estoque desta ração na fazenda.")
                );

        if (estoque.getQuantidadeKg().compareTo(quantidadeKg) < 0) {
            throw new IllegalStateException("Quantidade insuficiente em estoque.");
        }

        estoque.consumirKg(quantidadeKg);
        ConsumoRacao consumo = new ConsumoRacao(ciclo, tipoRacao, quantidadeKg, dataConsumo);

        consumoRepository.save(consumo);
    }

    public BigDecimal calcularConsumoTotal(List<ConsumoRacao> consumos) {
        if (consumos == null || consumos.isEmpty()) {
            return BigDecimal.ZERO;
        }
        return consumos.stream()
                .map(ConsumoRacao::getQuantidadeKg)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public Map<String, BigDecimal> calcularConsumoPorTipo(List<ConsumoRacao> consumos) {
        Map<String, BigDecimal> consumoPorTipo = new LinkedHashMap<>();

        if (consumos == null || consumos.isEmpty()) {
            return consumoPorTipo;
        }
        for (ConsumoRacao consumo : consumos) {
            String nomeRacao = consumo.getTipoRacao().getNome();
            BigDecimal quantidade = consumo.getQuantidadeKg();

            consumoPorTipo.merge(nomeRacao, quantidade, BigDecimal::add);
        }

        return consumoPorTipo;
    }

    public ConsumoRacao ultimoConsumo(Long viveiroId, Usuario usuario){
        return listarConsumosDoCiclo(viveiroId, usuario).getFirst();
    }
}
