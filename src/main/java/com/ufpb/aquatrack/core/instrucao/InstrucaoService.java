package com.ufpb.aquatrack.core.instrucao;

import com.ufpb.aquatrack.core.fazenda.Fazenda;
import com.ufpb.aquatrack.core.fazenda.FazendaService;
import com.ufpb.aquatrack.core.viveiro.Viveiro;
import com.ufpb.aquatrack.core.viveiro.ViveiroService;
import com.ufpb.aquatrack.core.usuario.Usuario;
import com.ufpb.aquatrack.infra.error.exceptions.RecursoNaoEncontradoException;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class InstrucaoService {

    private final InstrucaoRepository instrucaoRepository;
    private final ViveiroService viveiroService;
    private final FazendaService fazendaService;

    public InstrucaoService(
            InstrucaoRepository instrucaoRepository,
            ViveiroService viveiroService,
            FazendaService fazendaService
    ) {
        this.instrucaoRepository = instrucaoRepository;
        this.viveiroService = viveiroService;
        this.fazendaService = fazendaService;
    }

    @Transactional
    public Instrucao criarInstrucao(
            String codigoFazenda, Long viveiroId,
            String titulo, String descricao, Usuario usuario
    ) {
        Viveiro viveiro = validarAcesso(codigoFazenda, viveiroId, usuario);

        String tag = gerarTag();

        Instrucao instrucao = new Instrucao(tag, titulo, descricao, viveiro);
        return instrucaoRepository.save(instrucao);
    }


    public List<Instrucao> listarHistorico(String codigoFazenda, Long viveiroId, Usuario usuario) {
        Viveiro viveiro = validarAcesso(codigoFazenda, viveiroId, usuario);
        return instrucaoRepository
                .findByViveiroAndDeletadoFalseOrderByDataCriacaoDesc(viveiro);
    }

    public List<Instrucao> listar3Ultimas(String codigoFazenda, Long viveiroId, Usuario usuario) {
        Viveiro viveiro = validarAcesso(codigoFazenda, viveiroId, usuario);
        return instrucaoRepository
                .findTop3ByViveiroAndDeletadoFalseOrderByDataCriacaoDesc(viveiro);
    }

    public Instrucao buscarPorTag(
            String codigoFazenda, Long viveiroId,
            String tagInstrucao, Usuario usuario
    ) {
        Viveiro viveiro = validarAcesso(codigoFazenda, viveiroId, usuario);

        return instrucaoRepository
                .findByViveiroAndTagAndDeletadoFalse(viveiro, tagInstrucao)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Instrução não encontrada"));
    }


    @Transactional
    public void editarInstrucao(
            String codigoFazenda, Long viveiroId,
            String tagInstrucao, String titulo,
            String descricao, InstrucaoStatus status, Usuario usuario
    ) {
        Instrucao instrucao = buscarPorTag(codigoFazenda, viveiroId, tagInstrucao, usuario);
        instrucao.atualizar(titulo, descricao, status);
    }

    @Transactional
    public void alterarStatus(
            String codigoFazenda, Long viveiroId,
            String tagInstrucao, InstrucaoStatus status, Usuario usuario
    ) {
        Instrucao instrucao = buscarPorTag(codigoFazenda, viveiroId, tagInstrucao, usuario);
        instrucao.setStatus(status);
    }


    @Transactional
    public void removerInstrucao(
            String codigoFazenda, Long viveiroId,
            String tagInstrucao, Usuario usuario
    ) {
        Instrucao instrucao = buscarPorTag(codigoFazenda, viveiroId, tagInstrucao, usuario);
        instrucao.marcarComoDeletada();
    }

    private Viveiro validarAcesso(String codigoFazenda, Long viveiroId, Usuario usuario) {
        Fazenda fazenda = fazendaService.buscarFazendaPorCodigo(codigoFazenda);

        if (!fazenda.getUsuario().getId().equals(usuario.getId())) {
            throw new IllegalArgumentException("Acesso negado");
        }

        Viveiro viveiro = viveiroService.buscarViveiroPorId(viveiroId);

        if (!viveiro.getFazenda().getId().equals(fazenda.getId())) {
            throw new IllegalArgumentException("Viveiro não pertence à fazenda");
        }

        return viveiro;
    }

    private String gerarTag() {
        return "I-" + UUID.randomUUID().toString().substring(0, 4).toUpperCase();
    }
}
