package com.ufpb.aquatrack.core.racao.estoque;

import com.ufpb.aquatrack.core.fazenda.Fazenda;
import com.ufpb.aquatrack.core.fazenda.FazendaService;
import com.ufpb.aquatrack.core.racao.tipo.TipoRacaoService;
import com.ufpb.aquatrack.core.racao.tipo.TipoRacao;
import com.ufpb.aquatrack.core.usuario.Usuario;
import com.ufpb.aquatrack.core.viveiro.Viveiro;
import com.ufpb.aquatrack.core.viveiro.ViveiroService;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

@Service
public class EstoqueRacaoService {

    private final EstoqueRacaoRepository estoqueRacaoRepository;
    private final FazendaService fazendaService;
    private final TipoRacaoService tipoRacaoService;
    private final ViveiroService viveiroService;

    public EstoqueRacaoService(
            EstoqueRacaoRepository estoqueRacaoRepository, FazendaService fazendaService,
            TipoRacaoService tipoRacaoService, ViveiroService viveiroService
    ) {
        this.estoqueRacaoRepository = estoqueRacaoRepository;
        this.fazendaService = fazendaService;
        this.tipoRacaoService = tipoRacaoService;
        this.viveiroService = viveiroService;
    }


    public void abastecerEstoque(String codigo, Long tipoRacaoId, int quantidadeSacos, Usuario usuario) {

        if (quantidadeSacos <= 0) {
            throw new IllegalArgumentException("Quantidade deve ser maior que zero");
        }

        Fazenda fazenda = fazendaService.buscarFazendaPorCodigo(codigo);

        if (!fazenda.getUsuario().getId().equals(usuario.getId())) {
            throw new IllegalArgumentException("Acesso negado");
        }

        TipoRacao tipoRacao = tipoRacaoService.buscarRacaoPorId(tipoRacaoId, usuario);

        // converte sacos -> KG
        BigDecimal kgParaAdicionar = tipoRacao.getKgPorSaco().multiply(BigDecimal.valueOf(quantidadeSacos));
        EstoqueRacao estoque = estoqueRacaoRepository
                .findByFazendaAndTipoRacaoAndDeletadoFalse(fazenda, tipoRacao)
                .orElse(null);

        if (estoque == null) {
            estoque = new EstoqueRacao(fazenda, tipoRacao, kgParaAdicionar);
        } else {
            estoque.adicionarKg(kgParaAdicionar);
        }

        estoqueRacaoRepository.save(estoque);
    }


    public BigDecimal totalEstoque(Long fazendaId, Usuario usuario) {
        //Calcula quanto custa, em dinheiro, toda a ração disponível no estoque da fazenda.
        return listarEstoqueDaFazenda(fazendaId, usuario)
                .stream()
                .map(estoque -> {
                    BigDecimal custoKg = estoque.getTipoRacao()
                            .getValorPorSaco()
                            .divide(estoque.getTipoRacao().getKgPorSaco(), 2, RoundingMode.HALF_UP);
                    return custoKg.multiply(estoque.getQuantidadeKg());
                })
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }


    public List<EstoqueRacao> listarEstoqueDaFazenda(Long fazendaId, Usuario usuario) {
        Fazenda fazenda = fazendaService.buscarFazendaPorId(fazendaId);

        if (!fazenda.getUsuario().getId().equals(usuario.getId())) {
            throw new IllegalArgumentException("Acesso negado");
        }

        return estoqueRacaoRepository.findByFazendaAndDeletadoFalse(fazenda);
    }


    public void removerEstoque(Long estoqueId, Usuario usuario) {
        EstoqueRacao estoque = estoqueRacaoRepository.findById(estoqueId)
                .orElseThrow(() -> new IllegalArgumentException("Estoque não encontrado"));

        Fazenda fazenda = estoque.getFazenda();

        if (!fazenda.getUsuario().getId().equals(usuario.getId())) {
            throw new IllegalArgumentException("Acesso negado");
        }

        estoque.setDeletado(true);
        estoqueRacaoRepository.save(estoque);
    }

    public List<EstoqueRacao> listarEstoqueDaFazendaPorViveiro(Long viveiroId) {
        Viveiro viveiro = viveiroService.buscarViveiroPorId(viveiroId);
        return estoqueRacaoRepository.findByFazendaAndDeletadoFalse(viveiro.getFazenda());
    }

}
