package com.ufpb.aquatrack.qualidadeAgua;

import com.ufpb.aquatrack.ciclo.Ciclo;
import com.ufpb.aquatrack.ciclo.CicloService;
import com.ufpb.aquatrack.parametroQualidadeAgua.ParametroQualidadeAgua;
import com.ufpb.aquatrack.repository.MedicaoQualidadeAguaRepository;
import com.ufpb.aquatrack.repository.ParametroQualidadeAguaRepository;
import com.ufpb.aquatrack.usuario.Usuario;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;

@Service
public class QualidadeAguaService {

    private final ParametroQualidadeAguaRepository parametroRepository;
    private final MedicaoQualidadeAguaRepository medicaoRepository;
    private final CicloService cicloService;

    public QualidadeAguaService(ParametroQualidadeAguaRepository parametroRepository,
            MedicaoQualidadeAguaRepository medicaoRepository, CicloService cicloService) {
        this.parametroRepository = parametroRepository;
        this.medicaoRepository = medicaoRepository;
        this.cicloService = cicloService;
    }


    public List<ParametroQualidadeAgua> listarParametrosDoUsuario(Usuario usuario) {
        return parametroRepository.findByUsuarioAndDeletadoFalseOrderByNomeAsc(usuario);
    }

    @Transactional
    public void cadastrarParametro(Usuario usuario, String nome) {
        if (nome == null || nome.isBlank()) {
            throw new IllegalArgumentException("Nome do parâmetro é obrigatório.");
        }

        if (parametroRepository.existsByUsuarioAndNomeAndDeletadoFalse(usuario, nome)) {
            throw new IllegalStateException("Já existe um parâmetro com esse nome.");
        }

        parametroRepository.save(new ParametroQualidadeAgua(usuario, nome));
    }

    @Transactional
    public void removerParametro(Long parametroId, Usuario usuario) {
        ParametroQualidadeAgua parametro = parametroRepository.findById(parametroId)
                .orElseThrow(() ->
                        new IllegalArgumentException("Parâmetro não encontrado.")
                );

        if (!parametro.getUsuario().getId().equals(usuario.getId())) {
            throw new IllegalArgumentException("Acesso negado ao parâmetro.");
        }

        parametro.marcarComoDeletado();
    }

    @Transactional
    public void registrarMedicoes(Long viveiroId, Usuario usuario,
                                  LocalDate dataMedicao, Map<Long, BigDecimal> valoresPorParametro) {

        Ciclo ciclo = cicloService.buscarCicloAtivo(viveiroId, usuario); // 1. Buscar ciclo ativo

        if (ciclo == null)
            throw new IllegalStateException("Não existe ciclo ativo para este viveiro.");

        if (dataMedicao == null || dataMedicao.isAfter(LocalDate.now())) // 2. Validar data
            throw new IllegalArgumentException("Data da medição inválida.");

        if (valoresPorParametro == null || valoresPorParametro.isEmpty()) // 3. Validar se veio algo para salvar
            throw new IllegalArgumentException("Nenhuma medição informada.");

        // 4. Percorrer APENAS os parâmetros enviados
        for (Map.Entry<Long, BigDecimal> entry : valoresPorParametro.entrySet()) {
            Long parametroId = entry.getKey();
            BigDecimal valor = entry.getValue();

            if (valor == null) { // Se não mediu esse parâmetro hoje, pula
                continue;
            }

            if (valor.compareTo(BigDecimal.ZERO) < 0) {
                throw new IllegalArgumentException("Valor inválido para parâmetro.");
            }

            ParametroQualidadeAgua parametro = parametroRepository.findById(parametroId) // 5. Buscar parâmetro
                    .orElseThrow(() ->
                            new IllegalArgumentException("Parâmetro não encontrado.")
                    );

            if (!parametro.getUsuario().getId().equals(usuario.getId()))// 6. Segurança: parâmetro precisa ser do usuário
                throw new IllegalArgumentException("Parâmetro não pertence ao usuário.");


            // 7. Regra: não repetir parâmetro no mesmo dia
            if (medicaoRepository.existsByCicloAndParametroAndDataMedicaoAndDeletadoFalse(ciclo, parametro, dataMedicao)) {
                throw new IllegalStateException(
                        "Já existe medição desse parâmetro nesta data."
                );
            }

            //Salva
            medicaoRepository.save(new MedicaoQualidadeAgua(ciclo, parametro, dataMedicao, valor));
        }
    }

    public List<MedicaoQualidadeAgua> listarMedicoesDoCiclo(Long viveiroId, Usuario usuario) {
        Ciclo ciclo = cicloService.buscarCicloAtivo(viveiroId, usuario);

        if (ciclo == null) {
            throw new IllegalStateException("Não existe ciclo ativo.");
        }

        return medicaoRepository.findByCicloAndDeletadoFalseOrderByDataMedicaoAsc(ciclo);
    }

    public ParametroQualidadeAgua buscarParametroPorId(Long id, Usuario usuario) {
        ParametroQualidadeAgua parametro = parametroRepository.findById(id)
                .orElseThrow(() ->
                        new IllegalArgumentException("Parâmetro não encontrado.")
                );

        if (!parametro.getUsuario().getId().equals(usuario.getId())) {
            throw new IllegalArgumentException("Acesso negado ao parâmetro.");
        }

        return parametro;
    }

    @Transactional
    public void editarParametro(Long parametroId, Usuario usuario, String nome) {
        if (nome == null || nome.isBlank()) {
            throw new IllegalArgumentException("Nome do parâmetro é obrigatório.");
        }

        ParametroQualidadeAgua parametro = buscarParametroPorId(parametroId, usuario);

        boolean nomeDuplicado =
                parametroRepository.existsByUsuarioAndNomeAndDeletadoFalse(usuario, nome)
                        && !parametro.getNome().equalsIgnoreCase(nome);

        if (nomeDuplicado) {
            throw new IllegalStateException("Já existe um parâmetro com esse nome.");
        }

        parametro.setNome(nome);
    }

    public ResumoQualidadeAgua obterResumoDoCiclo(Long viveiroId, Usuario usuario) {

        Ciclo ciclo = cicloService.buscarCicloAtivo(viveiroId, usuario);
        if (ciclo == null) {
            return null;
        }

        List<MedicaoQualidadeAgua> medicoes = medicaoRepository.findByCicloAndDeletadoFalseOrderByDataMedicaoAsc(ciclo);

        if (medicoes.isEmpty()) {
            return null;
        }

        Map<LocalDate, List<MedicaoQualidadeAgua>> porData =
                medicoes.stream()
                        .collect(Collectors.groupingBy(
                                MedicaoQualidadeAgua::getDataMedicao,
                                TreeMap::new,
                                Collectors.toList()
                        ));

        List<LocalDate> datas = new ArrayList<>(porData.keySet());
        ResumoQualidadeAgua resumo = new ResumoQualidadeAgua();
        LocalDate ultima = datas.get(datas.size() - 1);
        resumo.setUltimaData(ultima);
        resumo.setUltima(porData.get(ultima));

        if (datas.size() > 1) {
            LocalDate penultima = datas.get(datas.size() - 2);
            resumo.setPenultimaData(penultima);
            resumo.setPenultima(porData.get(penultima));
        }

        return resumo;
    }

}
