package com.ufpb.aquatrack.core.viveiro;

import com.ufpb.aquatrack.infra.error.exceptions.RecursoNaoEncontradoException;
import com.ufpb.aquatrack.core.fazenda.Fazenda;
import com.ufpb.aquatrack.core.fazenda.FazendaService;
import com.ufpb.aquatrack.core.usuario.Usuario;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ViveiroService {

    private final ViveiroRepository viveiroRepository;
    private final FazendaService fazendaService;

    public ViveiroService(
            ViveiroRepository viveiroRepository,
            FazendaService fazendaService
    ) {
        this.viveiroRepository = viveiroRepository;
        this.fazendaService = fazendaService;
    }

    public Viveiro criarViveiro(String codigo, String tag, double area, Usuario usuario) {
        Fazenda fazenda = fazendaService.buscarFazendaPorCodigo(codigo);

        if (!fazenda.getUsuario().getId().equals(usuario.getId())) {
            throw new IllegalArgumentException("Acesso negado");
        }

        if (viveiroRepository.existsByFazendaAndTagAndDeletadoFalse(fazenda, tag)) {
            throw new IllegalArgumentException("Já existe um viveiro com essa tag");
        }

        Viveiro viveiro = new Viveiro(tag, area);
        fazenda.adicionarViveiro(viveiro);

        return viveiroRepository.save(viveiro);
    }

    public List<Viveiro> listarViveiros(Long fazendaId, Usuario usuario) {
        Fazenda fazenda = fazendaService.buscarFazendaPorId(fazendaId);

        if (!fazenda.getUsuario().getId().equals(usuario.getId())) {
            throw new IllegalArgumentException("Acesso negado");
        }

        return viveiroRepository.findByFazendaAndDeletadoFalse(fazenda);
    }

    public Viveiro buscarViveiroPorId(Long id){
        return viveiroRepository.findByIdAndDeletadoFalse(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Viveiro não encontrado"));
    }

    @Transactional
    public void removerViveiro(Long viveiroId, Usuario usuario) {
        Viveiro viveiro = buscarViveiroPorId(viveiroId);

        if (!viveiro.getFazenda().getUsuario().getId().equals(usuario.getId())) {
            throw new IllegalArgumentException("Acesso negado");
        }

        viveiro.marcarComoDeletado();
    }


    public void editarViveiro(Long viveiroId, String tag, double area, Usuario usuario) {
        Viveiro viveiro = buscarViveiroPorId(viveiroId);
        Fazenda fazenda = viveiro.getFazenda();

        if (!fazenda.getUsuario().getId().equals(usuario.getId())) {
            throw new IllegalArgumentException("Acesso negado");
        }

        if (viveiroRepository.existsByFazendaAndTagAndDeletadoFalse(fazenda, tag)) {
            throw new IllegalArgumentException("Já existe um viveiro com essa tag");
        }

        viveiro.setTag(tag);
        viveiro.setArea(area);

        viveiroRepository.save(viveiro);
    }
}
