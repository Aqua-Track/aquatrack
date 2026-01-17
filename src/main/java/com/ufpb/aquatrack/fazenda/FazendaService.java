package com.ufpb.aquatrack.fazenda;

import com.ufpb.aquatrack.usuario.Usuario;
import com.ufpb.aquatrack.repository.FazendaRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class FazendaService {

    private final FazendaRepository fazendaRepository;

    public FazendaService(FazendaRepository fazendaRepository) {
        this.fazendaRepository = fazendaRepository;
    }

    public Fazenda criarFazenda(String nome, String localizacao, Usuario usuario) {
        long total = fazendaRepository.countByUsuarioAndDeletadoFalse(usuario);

        if (total >= 3) {
            throw new IllegalArgumentException("Usuário já atingiu o limite de 3 fazendas");
        }
        Fazenda fazenda = new Fazenda(nome, localizacao, usuario);
        return fazendaRepository.save(fazenda);
    }

    public List<Fazenda> listarFazendasDoUsuario(Usuario usuario) {
        return fazendaRepository.findByUsuarioAndDeletadoFalse(usuario);
    }

    public void deletarFazenda(Long id){
        Fazenda fazenda = buscarFazendaPorId(id);

        fazenda.setDeletado(true);
        fazendaRepository.save(fazenda);
    }

    public Fazenda buscarFazendaPorId(Long id) {
        return fazendaRepository.findByIdAndDeletadoFalse(id)
                .orElseThrow(() -> new IllegalArgumentException("Fazenda não encontrada"));
    }

    public void editarFazenda(Long id, String nome, String localizacao){
        Fazenda fazenda = buscarFazendaPorId(id);

        fazenda.setNome(nome);
        fazenda.setLocalizacao(localizacao);

        fazendaRepository.save(fazenda);
    }
}
