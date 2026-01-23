package com.ufpb.aquatrack.qualidadeAgua;

import com.ufpb.aquatrack.parametroQualidadeAgua.ParametroQualidadeAgua;
import com.ufpb.aquatrack.usuario.Usuario;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/fazenda/{codigo}/viveiro/{viveiroId}/qualidade-agua")
public class QualidadeAguaController {

    private final QualidadeAguaService qualidadeAguaService;

    public QualidadeAguaController(QualidadeAguaService qualidadeAguaService) {
        this.qualidadeAguaService = qualidadeAguaService;
    }

    @GetMapping("/nova")
    public String formulario(@PathVariable String codigo, @PathVariable Long viveiroId, HttpSession session, Model model) {
        Usuario usuario = (Usuario) session.getAttribute("usuario");

        List<ParametroQualidadeAgua> parametros = qualidadeAguaService.listarParametrosDoUsuario(usuario);

        model.addAttribute("codigo", codigo);
        model.addAttribute("viveiroId", viveiroId);
        model.addAttribute("parametros", parametros);

        return "qualidadeAgua/formulario_qualidade_agua";
    }

    @PostMapping("/registrar")
    public String salvar(
            @PathVariable String codigo, @PathVariable Long viveiroId,
            @RequestParam LocalDate dataMedicao, @RequestParam Map<String, String> requestParams,
            HttpSession session, Model model) {
        Usuario usuario = (Usuario) session.getAttribute("usuario");

        Map<Long, BigDecimal> valoresPorParametro = new HashMap<>();

        // Converte inputs do formulário para o Map<Long, BigDecimal>, isso é tratato para ser salvo no Service!
        for (String key : requestParams.keySet()) {
            if (key.startsWith("parametro_")) {
                Long parametroId = Long.valueOf(key.replace("parametro_", ""));
                String valorStr = requestParams.get(key);

                if (valorStr != null && !valorStr.isBlank()) {
                    valoresPorParametro.put(parametroId, new BigDecimal(valorStr));
                }
            }
        }

        try {
            qualidadeAguaService.registrarMedicoes(viveiroId, usuario, dataMedicao, valoresPorParametro);

            return "redirect:/fazenda/" + codigo + "/viveiro/" + viveiroId + "/abrirViveiro";

        } catch (RuntimeException e) {
            model.addAttribute("erro", e.getMessage());
            model.addAttribute("codigo", codigo);
            model.addAttribute("viveiroId", viveiroId);
            model.addAttribute("parametros", qualidadeAguaService.listarParametrosDoUsuario(usuario));
            return "qualidadeAgua/formulario_qualidade_agua";
        }
    }

    @GetMapping("/historico")
    public String historico(@PathVariable String codigo, @PathVariable Long viveiroId, HttpSession session, Model model) {
        Usuario usuario = (Usuario) session.getAttribute("usuario");

        List<MedicaoQualidadeAgua> medicoes = qualidadeAguaService.listarMedicoesDoCiclo(viveiroId, usuario);

        Map<LocalDate, List<MedicaoQualidadeAgua>> medicoesPorData =
                medicoes.stream()
                        .collect(Collectors.groupingBy(MedicaoQualidadeAgua::getDataMedicao, LinkedHashMap::new,
                                Collectors.toList()
                        ));

        model.addAttribute("medicoesPorData", medicoesPorData);
        model.addAttribute("codigo", codigo);
        model.addAttribute("viveiroId", viveiroId);

        return "qualidadeAgua/historico_qualidade_agua";
    }

}
