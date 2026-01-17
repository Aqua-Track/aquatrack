package com.ufpb.aquatrack.estoqueRacao;

import com.ufpb.aquatrack.fazenda.Fazenda;
import com.ufpb.aquatrack.repository.EstoqueRacaoRepository;
import com.ufpb.aquatrack.fazenda.FazendaService;
import com.ufpb.aquatrack.tipoRacao.TipoRacaoService;
import com.ufpb.aquatrack.tipoRacao.TipoRacao;
import com.ufpb.aquatrack.usuario.Usuario;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EstoqueRacaoService {

    private final EstoqueRacaoRepository estoqueRacaoRepository;
    private final FazendaService fazendaService;
    private final TipoRacaoService tipoRacaoService;

    public EstoqueRacaoService(
            EstoqueRacaoRepository estoqueRacaoRepository,
            FazendaService fazendaService,
            TipoRacaoService tipoRacaoService
    ) {
        this.estoqueRacaoRepository = estoqueRacaoRepository;
        this.fazendaService = fazendaService;
        this.tipoRacaoService = tipoRacaoService;
    }


    public void abastecerEstoque(Long fazendaId, Long tipoRacaoId, int quantidadeSacos, Usuario usuario) {
        if (quantidadeSacos <= 0) {
            throw new IllegalArgumentException("Quantidade deve ser maior que zero");
        }

        Fazenda fazenda = fazendaService.buscarFazendaPorId(fazendaId);

        if (!fazenda.getUsuario().getId().equals(usuario.getId())) {
            throw new IllegalArgumentException("Acesso negado");
        }

        TipoRacao tipoRacao = tipoRacaoService.buscarRacaoPorId(tipoRacaoId, usuario); //Analisa se existe o tipo
        EstoqueRacao estoque = estoqueRacaoRepository //Pega o estoque do tipo que será abastecido
                .findByFazendaAndTipoRacaoAndDeletadoFalse(fazenda, tipoRacao)
                .orElse(null);

        if (estoque == null) { //Se não existir estoque é criado um
            estoque = new EstoqueRacao(fazenda, tipoRacao, quantidadeSacos);
        } else {
            estoque.setQuantidadeSacos(
                    estoque.getQuantidadeSacos() + quantidadeSacos //Adiciona a nova quantidade á quant anterior
            );
        }

        estoqueRacaoRepository.save(estoque);
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

    //Não foi feito o metodo de consumir por conta q isso é relacionado ao ciclo, e não existe ciclo ainda
}
