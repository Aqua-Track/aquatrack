package com.ufpb.aquatrack.biometria;

import com.ufpb.aquatrack.ciclo.Ciclo;
import com.ufpb.aquatrack.ciclo.CicloService;
import com.ufpb.aquatrack.repository.BiometriaRepository;
import com.ufpb.aquatrack.usuario.Usuario;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Service
public class BiometriaService {

    private final BiometriaRepository biometriaRepository;
    private final CicloService cicloService;

    public BiometriaService(BiometriaRepository biometriaRepository, CicloService cicloService) {
        this.biometriaRepository = biometriaRepository;
        this.cicloService = cicloService;
    }

    @Transactional
    public void registrarBiometria(Long viveiroId, Usuario usuario, LocalDate data, Integer quantidade, BigDecimal pesoTotal) {
        Ciclo ciclo = cicloService.buscarCicloAtivo(viveiroId, usuario);

        if (ciclo == null) {
            throw new IllegalStateException("Não existe ciclo ativo para este viveiro.");
        }
        if (data == null || data.isAfter(LocalDate.now())) {
            throw new IllegalArgumentException("Data inválida.");
        }
        if (quantidade == null || quantidade <= 0) {
            throw new IllegalArgumentException("Quantidade inválida.");
        }
        if (pesoTotal == null || pesoTotal.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Peso total inválido.");
        }
        if (biometriaRepository.existsByCicloAndDataBiometriaAndDeletadoFalse(ciclo, data)) {
            throw new IllegalStateException("Já existe biometria registrada nesta data.");
        }

        biometriaRepository.save(new Biometria(ciclo, data, quantidade, pesoTotal));
    }

    public List<Biometria> listarBiometrias(Long viveiroId, Usuario usuario) {
        Ciclo ciclo = cicloService.buscarCicloAtivo(viveiroId, usuario);

        if (ciclo == null) {
            throw new IllegalStateException("Não existe ciclo ativo.");
        }

        return biometriaRepository.findByCicloAndDeletadoFalseOrderByDataBiometriaAsc(ciclo);
    }

    public List<Biometria> listarBiometriasDoCiclo(Ciclo ciclo) {
        return biometriaRepository.findByCicloAndDeletadoFalseOrderByDataBiometriaAsc(ciclo);
    }

}
