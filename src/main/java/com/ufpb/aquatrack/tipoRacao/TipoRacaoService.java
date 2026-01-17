package com.ufpb.aquatrack.tipoRacao;

import com.ufpb.aquatrack.usuario.Usuario;
import com.ufpb.aquatrack.repository.TipoRacaoRepository;
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
            Double kgPorSaco, BigDecimal valorPorSaco, Usuario usuario
    ) {
        if (tipoRacaoRepository.existsByUsuarioAndNomeAndDeletadoFalse(usuario, nome)) {
            throw new IllegalArgumentException("Já existe uma ração com esse nome");
        }

        TipoRacao racao = new TipoRacao(nome, fabricante, kgPorSaco, valorPorSaco, usuario);

        return tipoRacaoRepository.save(racao);
    }

    public List<TipoRacao> listarRacoesDoUsuario(Usuario usuario) {
        return tipoRacaoRepository.findByUsuarioAndDeletadoFalse(usuario);
    }

    public TipoRacao buscarRacaoPorId(Long id, Usuario usuario) {
        return tipoRacaoRepository
                .findByIdAndUsuarioAndDeletadoFalse(id, usuario)
                .orElseThrow(() -> new IllegalArgumentException("Ração não encontrada"));
    }

    public void editarRacao(
            Long id, String nome, String fabricante,
            Double kgPorSaco, BigDecimal valorPorSaco, Usuario usuario
    ) {
        TipoRacao racao = buscarRacaoPorId(id, usuario);

        if (!racao.getNome().equals(nome) &&
                tipoRacaoRepository.existsByUsuarioAndNomeAndDeletadoFalse(usuario, nome)) {
            throw new IllegalArgumentException("Já existe uma ração com esse nome");
        }

        racao.setNome(nome);
        racao.setFabricante(fabricante);
        racao.setKgPorSaco(kgPorSaco);
        racao.setValorPorSaco(valorPorSaco);

        tipoRacaoRepository.save(racao);
    }

    public void removerRacao(Long id, Usuario usuario) {
        TipoRacao racao = buscarRacaoPorId(id, usuario);
        racao.setDeletado(true);
        tipoRacaoRepository.save(racao);
    }
}

