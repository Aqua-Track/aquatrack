package com.ufpb.aquatrack.services;

import com.ufpb.aquatrack.models.Fazenda;
import com.ufpb.aquatrack.models.Usuario;
import com.ufpb.aquatrack.models.Viveiro;
import com.ufpb.aquatrack.repository.ViveiroRepository;
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

    public Viveiro criarViveiro(Long fazendaId, String tag, double area, Usuario usuario) {
        Fazenda fazenda = fazendaService.buscarFazendaPorId(fazendaId);

        if (!fazenda.getUsuario().equals(usuario)) {
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

        if (!fazenda.getUsuario().equals(usuario)) {
            throw new IllegalArgumentException("Acesso negado");
        }

        return viveiroRepository.findByFazendaAndDeletadoFalse(fazenda);
    }

    public Viveiro buscarViveiroPorId(Long id){
        return viveiroRepository.findByIdAndDeletadoFalse(id)
                .orElseThrow(() -> new IllegalArgumentException("Viveiro não encontrado"));
    }

    public void apagarViveiro(Long viveiroId, Usuario usuario) {
        Viveiro viveiro = buscarViveiroPorId(viveiroId);

        Fazenda fazenda = viveiro.getFazenda();

        if (!fazenda.getUsuario().equals(usuario)) {
            throw new IllegalArgumentException("Acesso negado");
        }

        viveiro.marcarComoDeletado();
        viveiroRepository.save(viveiro);
    }

    public void editarViveiro(Long viveiroId, String tag, double area, Usuario usuario) {
        Viveiro viveiro = buscarViveiroPorId(viveiroId);
        Fazenda fazenda = viveiro.getFazenda();

        if (!fazenda.getUsuario().equals(usuario)) {
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
