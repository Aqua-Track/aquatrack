package com.ufpb.aquatrack.ciclo;

import com.ufpb.aquatrack.repository.CicloRepository;
import com.ufpb.aquatrack.usuario.Usuario;
import com.ufpb.aquatrack.viveiro.Viveiro;
import com.ufpb.aquatrack.repository.ViveiroRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
public class CicloService {

    private final CicloRepository cicloRepository;
    private final ViveiroRepository viveiroRepository;

    public CicloService(CicloRepository cicloRepository, ViveiroRepository viveiroRepository) {
        this.cicloRepository = cicloRepository;
        this.viveiroRepository = viveiroRepository;
    }


    public boolean existeCicloAtivo(Long viveiroId, Usuario usuario) {
        Viveiro viveiro = buscarViveiroValido(viveiroId, usuario);
        return cicloRepository.existsByViveiroAndAtivoTrueAndDeletadoFalse(viveiro);
    }

    public Ciclo buscarCicloAtivo(Long viveiroId, Usuario usuario) {
        Viveiro viveiro = buscarViveiroValido(viveiroId, usuario);

        return cicloRepository
                .findByViveiroAndAtivoTrueAndDeletadoFalse(viveiro)
                .orElse(null);
    }

    public List<Ciclo> listarCiclosDoViveiro(Long viveiroId, Usuario usuario) {
        Viveiro viveiro = buscarViveiroValido(viveiroId, usuario);

        return cicloRepository
                .findByViveiroAndDeletadoFalseOrderByDataPovoamentoDesc(viveiro);
    }


    @Transactional
    public Ciclo iniciarCiclo(Long viveiroId, LocalDate dataPovoamento,
                              int quantidadePovoada, String laboratorio, Usuario usuario) {

        Viveiro viveiro = buscarViveiroValido(viveiroId, usuario);

        if (cicloRepository.existsByViveiroAndAtivoTrueAndDeletadoFalse(viveiro)) {
            throw new IllegalStateException("Já existe um ciclo ativo neste viveiro.");
        }

        Ciclo ciclo = new Ciclo(viveiro, dataPovoamento, quantidadePovoada, laboratorio);
        return cicloRepository.save(ciclo);
    }

    @Transactional
    public void encerrarCiclo(Long viveiroId, LocalDate dataEncerramento, Usuario usuario) {

        Viveiro viveiro = buscarViveiroValido(viveiroId, usuario);
        Ciclo ciclo = cicloRepository
                .findByViveiroAndAtivoTrueAndDeletadoFalse(viveiro)
                .orElseThrow(() ->
                        new IllegalStateException("Não existe ciclo ativo para encerrar.")
                );

        ciclo.encerrar(dataEncerramento);
    }

    private Viveiro buscarViveiroValido(Long viveiroId, Usuario usuario) {
        Viveiro viveiro = viveiroRepository
                .findByIdAndDeletadoFalse(viveiroId)
                .orElseThrow(() ->
                        new IllegalArgumentException("Viveiro não encontrado.")
                );

        if (!viveiro.getFazenda().getUsuario().getId().equals(usuario.getId())) {
            throw new IllegalArgumentException("Acesso negado ao viveiro.");
        }

        return viveiro;
    }
}
