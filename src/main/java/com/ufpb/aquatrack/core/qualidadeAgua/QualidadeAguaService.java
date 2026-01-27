package com.ufpb.aquatrack.core.qualidadeAgua;

import com.ufpb.aquatrack.core.ciclo.Ciclo;
import com.ufpb.aquatrack.error.exceptions.RecursoNaoEncontradoException;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class QualidadeAguaService {

    private final QualidadeAguaRepository repository;

    public QualidadeAguaService(QualidadeAguaRepository repository) {
        this.repository = repository;
    }

    public void cadastrar(
            Ciclo ciclo, LocalDate dataColeta,
            Double amonia, Double nitrito,
            Double ph, Double alcalinidade, Double salinidade, Double oxigenio
    ) {
        if (todosParametrosNulos(amonia, nitrito, ph, alcalinidade, salinidade, oxigenio)) {
            throw new IllegalArgumentException("Informe pelo menos um parâmetro da análise");
        }

        validarValores(amonia, nitrito, ph, alcalinidade, salinidade, oxigenio);

        if (repository.existsByCicloAndDataColetaAndDeletadoFalse(ciclo, dataColeta)) {
            throw new IllegalArgumentException("Já existe qualidade da água registrada nesta data");
        }

        QualidadeAgua qualidade = new QualidadeAgua(
                ciclo, dataColeta, amonia, nitrito, ph, alcalinidade, salinidade, oxigenio
        );

        repository.save(qualidade);
    }

    public void atualizar(
            QualidadeAgua qualidade, LocalDate dataColeta, Double amonia,
            Double nitrito, Double ph, Double alcalinidade,
            Double salinidade, Double oxigenio
    ) {
        validarValores(amonia, nitrito, ph, alcalinidade, salinidade, oxigenio);
        qualidade.atualizar(dataColeta, amonia, nitrito, ph, alcalinidade, salinidade, oxigenio);
        repository.save(qualidade);
    }

    public List<QualidadeAgua> listarHistorico(Ciclo ciclo) {
        return repository.findByCicloAndDeletadoFalseOrderByDataColetaAsc(ciclo);
    }

    public List<QualidadeAgua> listarPorCiclo(Ciclo ciclo) {
        return repository.findByCicloAndDeletadoFalseOrderByDataColetaAsc(ciclo);
    }

    public QualidadeAgua buscarUltima(Ciclo ciclo) {
        return repository.findTopByCicloAndDeletadoFalseOrderByDataColetaDesc(ciclo).orElse(null);
    }

    public QualidadeAgua buscarPenultima(Ciclo ciclo) {
        List<QualidadeAgua> lista = listarPorCiclo(ciclo);
        if (lista.size() < 2) return null;
        return lista.get(lista.size() - 2);
    }

    public QualidadeAgua buscarPorId(Long id) {
        return repository.findById(id)
                .filter(q -> !q.isDeletado())
                .orElseThrow(() -> new RecursoNaoEncontradoException("Qualidade da água não encontrada"));
    }

    public void excluir(QualidadeAgua qualidade) {
        qualidade.deletar();
        repository.save(qualidade);
    }

    private void validarValores(
            Double amonia, Double nitrito, Double ph,
            Double alcalinidade, Double salinidade, Double oxigenio
    ) {
        if (amonia != null && amonia < 0) {
            throw new IllegalArgumentException("Amônia não pode ser negativa");
        }
        if (nitrito != null && nitrito < 0) {
            throw new IllegalArgumentException("Nitrito não pode ser negativo");
        }
        if (alcalinidade != null && alcalinidade < 0) {
            throw new IllegalArgumentException("Alcalinidade não pode ser negativa");
        }
        if (salinidade != null && salinidade < 0) {
            throw new IllegalArgumentException("Salinidade não pode ser negativa");
        }
        if (oxigenio != null && oxigenio < 0) {
            throw new IllegalArgumentException("Oxigênio não pode ser negativo");
        }
        if (ph != null && (ph < 0 || ph > 14)) {
            throw new IllegalArgumentException("pH deve estar entre 0 e 14");
        }
    }

    private boolean todosParametrosNulos(
            Double amonia, Double nitrito, Double ph,
            Double alcalinidade, Double salinidade, Double oxigenio
    ) {
        return amonia == null && nitrito == null && ph == null
                && alcalinidade == null && salinidade == null && oxigenio == null;
    }

}
