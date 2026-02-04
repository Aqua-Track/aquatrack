package com.ufpb.aquatrack.core.racao.tipo;

import com.ufpb.aquatrack.core.fazenda.Fazenda;
import com.ufpb.aquatrack.infra.error.exceptions.RecursoNaoEncontradoException;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
public class TipoRacaoService {

    private final TipoRacaoRepository tipoRacaoRepository;

    public TipoRacaoService(TipoRacaoRepository tipoRacaoRepository) {
        this.tipoRacaoRepository = tipoRacaoRepository;
    }

    public TipoRacao cadastrarRacao(
            String nome, String fabricante,
            BigDecimal kgPorSaco, BigDecimal valorPorSaco, Fazenda fazenda
    ) {
        if (tipoRacaoRepository.existsByFazendaAndNomeAndDeletadoFalse(fazenda, nome)) {
            throw new IllegalArgumentException("Já existe uma ração com esse nome");
        }

        TipoRacao racao = new TipoRacao(nome, fabricante, kgPorSaco, valorPorSaco, fazenda);

        return tipoRacaoRepository.save(racao);
    }

    public List<TipoRacao> listarRacoesDaFazenda(Fazenda fazenda) {
        return tipoRacaoRepository.findByFazendaAndDeletadoFalse(fazenda);
    }

    public TipoRacao buscarRacaoPorId(Long id, Fazenda fazenda) {
        return tipoRacaoRepository
                .findByIdAndFazendaAndDeletadoFalse(id, fazenda)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Ração não encontrada"));
    }

    public void editarRacao(
            Long id, String nome, String fabricante,
            BigDecimal kgPorSaco, BigDecimal valorPorSaco, Fazenda fazenda
    ) {
        TipoRacao racao = buscarRacaoPorId(id, fazenda);

        if (!racao.getNome().equals(nome) &&
                tipoRacaoRepository.existsByFazendaAndNomeAndDeletadoFalse(fazenda, nome)) {
            throw new IllegalArgumentException("Já existe uma ração com esse nome");
        }

        racao.setNome(nome);
        racao.setFabricante(fabricante);
        racao.setKgPorSaco(kgPorSaco);
        racao.setValorPorSaco(valorPorSaco);

        tipoRacaoRepository.save(racao);
    }

    public void removerRacao(Long id, Fazenda fazenda) {
        TipoRacao racao = buscarRacaoPorId(id, fazenda);
        racao.setDeletado(true);
        tipoRacaoRepository.save(racao);
    }
}

