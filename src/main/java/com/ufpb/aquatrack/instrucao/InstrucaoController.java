package com.ufpb.aquatrack.instrucao;

import com.ufpb.aquatrack.ciclo.Ciclo;
import com.ufpb.aquatrack.ciclo.CicloService;
import com.ufpb.aquatrack.usuario.Usuario;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
public class InstrucaoController {

    private final InstrucaoService instrucaoService;
    private final CicloService cicloService;

    public InstrucaoController(
            InstrucaoService instrucaoService,
            CicloService cicloService
    ) {
        this.instrucaoService = instrucaoService;
        this.cicloService = cicloService;
    }

    @GetMapping("/fazenda/{codigo}/viveiro/{viveiroId}/instrucoes")
    public String listarInstrucoes(
            @PathVariable String codigo, @PathVariable Long viveiroId,
            HttpSession session, Model model
    ) {
        Usuario usuario = (Usuario) session.getAttribute("usuario");

        List<Instrucao> instrucoes =
                instrucaoService.listarHistorico(codigo, viveiroId, usuario);

        Ciclo ciclo = cicloService.buscarCicloAtivo(viveiroId, usuario);

        model.addAttribute("codigo", codigo);
        model.addAttribute("viveiroId", viveiroId);
        model.addAttribute("ciclo", ciclo);
        model.addAttribute("abaAtiva", "instrucoes");
        model.addAttribute("instrucoes", instrucoes);

        return "instrucoes/pagina_instrucoes";
    }


    @GetMapping("/fazenda/{codigo}/viveiro/{viveiroId}/instrucoes/nova")
    public String abrirModalNovaInstrucao(@PathVariable String codigo, @PathVariable Long viveiroId, Model model) {
        model.addAttribute("codigo", codigo);
        model.addAttribute("viveiroId", viveiroId);

        return "instrucoes/modal_nova_instrucao";
    }

    @PostMapping("/fazenda/{codigo}/viveiro/{viveiroId}/instrucoes/nova")
    public String criarInstrucao(
            @PathVariable String codigo, @PathVariable Long viveiroId,
            @RequestParam String titulo, @RequestParam String descricao, HttpSession session
    ) {
        Usuario usuario = (Usuario) session.getAttribute("usuario");

        instrucaoService.criarInstrucao(codigo, viveiroId, titulo, descricao, usuario);

        return "redirect:/fazenda/" + codigo + "/viveiro/" + viveiroId + "/instrucoes";
    }

    @GetMapping("/fazenda/{codigo}/viveiro/{viveiroId}/instrucoes/{tagInstrucao}")
    public String visualizarInstrucao(
            @PathVariable String codigo, @PathVariable Long viveiroId,
            @PathVariable String tagInstrucao, HttpSession session, Model model
    ) {
        Usuario usuario = (Usuario) session.getAttribute("usuario");

        Instrucao instrucao = instrucaoService.buscarPorTag(codigo, viveiroId, tagInstrucao, usuario);

        model.addAttribute("instrucao", instrucao);
        model.addAttribute("codigo", codigo);
        model.addAttribute("viveiroId", viveiroId);

        return "instrucoes/modal_visualizar_instrucao";
    }


    @GetMapping("/fazenda/{codigo}/viveiro/{viveiroId}/instrucoes/{tagInstrucao}/editar")
    public String abrirModalEditarInstrucao(
            @PathVariable String codigo,
            @PathVariable Long viveiroId,
            @PathVariable String tagInstrucao,
            HttpSession session,
            Model model
    ) {
        Usuario usuario = (Usuario) session.getAttribute("usuario");

        Instrucao instrucao = instrucaoService.buscarPorTag(codigo, viveiroId, tagInstrucao, usuario);

        model.addAttribute("instrucao", instrucao);
        model.addAttribute("codigo", codigo);
        model.addAttribute("viveiroId", viveiroId);

        return "instrucoes/modal_editar_instrucao";
    }

    @PostMapping("/fazenda/{codigo}/viveiro/{viveiroId}/instrucoes/{tagInstrucao}/editar")
    public String editarInstrucao(
            @PathVariable String codigo, @PathVariable Long viveiroId, @PathVariable String tagInstrucao,
            @RequestParam String titulo, @RequestParam String descricao, @RequestParam InstrucaoStatus status,
            HttpSession session
    ) {
        Usuario usuario = (Usuario) session.getAttribute("usuario");

        instrucaoService.editarInstrucao(codigo, viveiroId, tagInstrucao, titulo, descricao, status, usuario);

        return "redirect:/fazenda/" + codigo + "/viveiro/" + viveiroId + "/instrucoes";
    }


    @PostMapping("/fazenda/{codigo}/viveiro/{viveiroId}/instrucoes/{tagInstrucao}/remover")
    public String removerInstrucao(
            @PathVariable String codigo, @PathVariable Long viveiroId,
            @PathVariable String tagInstrucao, HttpSession session
    ) {
        Usuario usuario = (Usuario) session.getAttribute("usuario");

        instrucaoService.removerInstrucao(codigo, viveiroId, tagInstrucao, usuario);

        return "redirect:/fazenda/" + codigo + "/viveiro/" + viveiroId + "/instrucoes";
    }
}
