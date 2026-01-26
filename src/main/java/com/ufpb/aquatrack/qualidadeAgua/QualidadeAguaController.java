package com.ufpb.aquatrack.qualidadeAgua;

import com.ufpb.aquatrack.ciclo.Ciclo;
import com.ufpb.aquatrack.ciclo.CicloService;
import com.ufpb.aquatrack.fazenda.Fazenda;
import com.ufpb.aquatrack.fazenda.FazendaService;
import com.ufpb.aquatrack.usuario.Usuario;
import com.ufpb.aquatrack.viveiro.Viveiro;
import com.ufpb.aquatrack.viveiro.ViveiroService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@Controller
@RequestMapping("/fazenda/{codigo}/viveiro/{viveiroId}")
public class QualidadeAguaController {

    private final FazendaService fazendaService;
    private final CicloService cicloService;
    private final QualidadeAguaService service;

    public QualidadeAguaController(
            FazendaService fazendaService,
            CicloService cicloService,
            QualidadeAguaService service
    ) {
        this.fazendaService = fazendaService;
        this.cicloService = cicloService;
        this.service = service;
    }

    @GetMapping("/qualidade-agua/novo")
    public String formulario(
            @PathVariable String codigo,
            @PathVariable Long viveiroId,
            HttpSession session,
            Model model
    ) {
        validarAcesso(codigo, session);
        model.addAttribute("codigo", codigo);
        model.addAttribute("viveiroId", viveiroId);
        return "qualidadeAgua/formulario_qualidade_agua";
    }

    @PostMapping("/qualidade-agua/salvar")
    public String salvar(
            @PathVariable String codigo,
            @PathVariable Long viveiroId,

            @RequestParam LocalDate dataColeta,

            @RequestParam(required = false) Double amonia,
            @RequestParam(required = false) Double nitrito,
            @RequestParam(required = false) Double ph,
            @RequestParam(required = false) Double alcalinidade,
            @RequestParam(required = false) Double salinidade,
            @RequestParam(required = false) Double oxigenio,

            @RequestParam(required = false) Long id, // 🔥 diferença chave
            HttpSession session,
            Model model
    ) {
        validarAcesso(codigo, session);

        Ciclo ciclo = cicloService.buscarCicloAtivo(viveiroId, (Usuario) session.getAttribute("usuario"));

        try {
            if (id == null) { //Criar
                service.cadastrar(ciclo, dataColeta, amonia, nitrito, ph, alcalinidade, salinidade, oxigenio);
            } else { //Editar
                QualidadeAgua qualidade = service.buscarPorId(id);
                service.atualizar(qualidade, dataColeta, amonia, nitrito, ph, alcalinidade, salinidade, oxigenio);
            }
            return "redirect:/fazenda/" + codigo + "/viveiro/" + viveiroId + "/abrirViveiro";

        } catch (IllegalArgumentException e) {
            model.addAttribute("erro", e.getMessage());
            model.addAttribute("codigo", codigo);
            model.addAttribute("viveiroId", viveiroId);
            if (id != null) {
                model.addAttribute("qualidade", service.buscarPorId(id));
            }
            return "qualidadeAgua/formulario_qualidade_agua";
        }
    }


    @GetMapping("/qualidade-agua/{id}/editar")
    public String editarForm(
            @PathVariable String codigo, @PathVariable Long viveiroId, @PathVariable Long id,
            HttpSession session, Model model
    ) {
        validarAcesso(codigo, session);
        QualidadeAgua qualidade = service.buscarPorId(id);
        model.addAttribute("qualidade", qualidade);
        model.addAttribute("codigo", codigo);
        model.addAttribute("viveiroId", viveiroId);

        return "qualidadeAgua/formulario_qualidade_agua";
    }

    @PostMapping("/qualidade-agua/{id}/excluir")
    public String excluir(@PathVariable String codigo, @PathVariable Long viveiroId, @PathVariable Long id,
                          HttpSession session
    ) {
        validarAcesso(codigo, session);
        QualidadeAgua qualidade = service.buscarPorId(id);
        service.excluir(qualidade);

        return "redirect:/fazenda/" + codigo + "/viveiro/" + viveiroId + "/qualidade-agua/historico";
    }

    @GetMapping("/qualidade-agua/historico")
    public String historico(
            @PathVariable String codigo,
            @PathVariable Long viveiroId,
            HttpSession session,
            Model model
    ) {
        validarAcesso(codigo, session);

        Usuario usuario = (Usuario) session.getAttribute("usuario");
        Ciclo ciclo = cicloService.buscarCicloAtivo(viveiroId, usuario);

        List<QualidadeAgua> historico = service.listarHistorico(ciclo);
        if (historico == null) {
            historico = List.of();
        }

        model.addAttribute("codigo", codigo);
        model.addAttribute("viveiroId", viveiroId);
        model.addAttribute("ciclo", ciclo);
        model.addAttribute("abaAtiva", "agua");
        model.addAttribute("historicoQualidade", historico);

        return "qualidadeAgua/historico_qualidade_agua";
    }
    private void validarAcesso(String codigo,  HttpSession session) {
        Usuario usuario = (Usuario) session.getAttribute("usuario");
        Fazenda fazenda = fazendaService.buscarFazendaPorCodigo(codigo);
        if (!fazenda.getUsuario().getId().equals(usuario.getId())) {
            throw new IllegalArgumentException("Acesso negado");
        }
    }
}
