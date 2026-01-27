package com.ufpb.aquatrack.core.biometria;

import com.ufpb.aquatrack.core.ciclo.Ciclo;
import com.ufpb.aquatrack.core.ciclo.CicloService;
import com.ufpb.aquatrack.core.usuario.Usuario;
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

    @Transactional
    public void atualizarBiometria(Long id, Usuario usuario, LocalDate data, Integer quantidade, BigDecimal pesoTotal) {
        Biometria biometria = buscarPorId(id, usuario);

        if (data == null || data.isAfter(LocalDate.now())) {
            throw new IllegalArgumentException("Data inválida.");
        }
        if (quantidade == null || quantidade <= 0) {
            throw new IllegalArgumentException("Quantidade inválida.");
        }
        if (pesoTotal == null || pesoTotal.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Peso total inválido.");
        }

        Ciclo ciclo = biometria.getCiclo();

        boolean existeOutraNaData =
                biometriaRepository.existsByCicloAndDataBiometriaAndDeletadoFalse(ciclo, data)
                        && !biometria.getDataBiometria().equals(data);

        if (existeOutraNaData) {
            throw new IllegalStateException("Já existe biometria registrada nesta data.");
        }

        biometria.setDataBiometria(data);
        biometria.setQuantidadeAmostrada(quantidade);
        biometria.setPesoTotalAmostra(pesoTotal);

        // recalcula peso médio
        biometria.setPesoMedio(
                pesoTotal.divide(BigDecimal.valueOf(quantidade), 2, java.math.RoundingMode.HALF_UP)
        );
        biometriaRepository.save(biometria);
    }

    @Transactional
    public void excluirBiometria(Long id, Usuario usuario) {
        Biometria biometria = buscarPorId(id, usuario);
        biometria.marcarComoDeletado();
        biometriaRepository.save(biometria);
    }

    public List<Biometria> listarBiometrias(Long viveiroId, Usuario usuario) {
        Ciclo ciclo = cicloService.buscarCicloAtivo(viveiroId, usuario);

        if (ciclo == null) {
            throw new IllegalStateException("Não existe ciclo ativo.");
        }

        return biometriaRepository.findByCicloAndDeletadoFalseOrderByDataBiometriaAsc(ciclo);
    }

    public Biometria buscarPorId(Long id, Usuario usuario) {
        Biometria biometria = biometriaRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Biometria não encontrada."));

        if (biometria.isDeletado()) {
            throw new IllegalArgumentException("Biometria não encontrada.");
        }

        return biometria;
    }

}
