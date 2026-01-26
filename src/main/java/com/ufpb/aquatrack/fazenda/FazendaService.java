package com.ufpb.aquatrack.fazenda;

import com.ufpb.aquatrack.exceptions.RecursoNaoEncontradoException;
import com.ufpb.aquatrack.usuario.Usuario;
import com.ufpb.aquatrack.repository.FazendaRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

@Service
public class FazendaService {

    private final FazendaRepository fazendaRepository;

    public FazendaService(FazendaRepository fazendaRepository) {
        this.fazendaRepository = fazendaRepository;
    }

    public Fazenda criarFazenda(String nome, String localizacao, Usuario usuario) {

        if (usuario.getFazenda() != null) {
            throw new IllegalStateException("Usuário já possui uma fazenda cadastrada.");
        }
        String codigo = gerarCodigoFazenda();
        Fazenda fazenda = new Fazenda(nome, localizacao, usuario, codigo);
        usuario.setFazenda(fazenda); //Vincula fazenda ao usuario

        return fazendaRepository.save(fazenda);
    }

    @Transactional
    public void deletarFazenda(Long id){
        Fazenda fazenda = buscarFazendaPorId(id);
        Usuario usuario = fazenda.getUsuario();

        usuario.setFazenda(null);
        fazenda.setUsuario(null);

        fazenda.setDeletado(true);
        fazendaRepository.save(fazenda);
    }

    public Fazenda buscarFazendaPorId(Long id) {
        return fazendaRepository.findByIdAndDeletadoFalse(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Fazenda não encontrada"));
    }

    public Fazenda buscarFazendaPorCodigo(String codigo) {
        return fazendaRepository
                .findByCodigoAndDeletadoFalse(codigo)
                .orElseThrow(() -> new IllegalArgumentException("Fazenda não encontrada"));
    }


    public void editarFazenda(Long id, String nome, String localizacao){
        Fazenda fazenda = buscarFazendaPorId(id);

        fazenda.setNome(nome);
        fazenda.setLocalizacao(localizacao);

        fazendaRepository.save(fazenda);
    }

    private String gerarCodigoFazenda() {
        String codigo;
        do {
            int numero = ThreadLocalRandom.current().nextInt(1000, 9999);
            codigo = "F-" + numero;
        } while (fazendaRepository.existsByCodigo(codigo));

        return codigo;
    }

}
