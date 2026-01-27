package com.ufpb.aquatrack.core.racao.tipo;

import com.ufpb.aquatrack.core.usuario.Usuario;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

@Controller
@RequestMapping("/fazenda/{codigo}/racoes")
public class TipoRacaoController {

    private final TipoRacaoService tipoRacaoService;

    public TipoRacaoController(TipoRacaoService tipoRacaoService) {
        this.tipoRacaoService = tipoRacaoService;
    }


    @GetMapping
    public String listar(@PathVariable String codigo, HttpSession session, Model model) {
        Usuario usuario = (Usuario) session.getAttribute("usuario");

        model.addAttribute("racoes", tipoRacaoService.listarRacoesDoUsuario(usuario));
        model.addAttribute("codigo", codigo);

        return "racao/listar_tipo";
    }


    @GetMapping("/nova")
    public String nova(@PathVariable String codigo, Model model) {
        model.addAttribute("codigo", codigo);
        return "racao/formulario_tipo";
    }


    @PostMapping("/criar")
    public String criar(
            @PathVariable String codigo, @RequestParam String nome,
            @RequestParam String fabricante, @RequestParam BigDecimal kgPorSaco,
            @RequestParam BigDecimal valorPorSaco, HttpSession session, Model model
    ) {
        Usuario usuario = (Usuario) session.getAttribute("usuario");

        try {
            tipoRacaoService.cadastrarRacao(nome, fabricante, kgPorSaco, valorPorSaco, usuario);

            return "redirect:/fazenda/" + codigo + "/racoes";

        } catch (IllegalArgumentException e) {
            model.addAttribute("erro", e.getMessage());
            model.addAttribute("codigo", codigo);
            return "racao/formulario_tipo";
        }
    }

    @PostMapping("/{id}/remover")
    public String remover(@PathVariable String codigo, @PathVariable Long id, HttpSession session) {
        Usuario usuario = (Usuario) session.getAttribute("usuario");

        tipoRacaoService.removerRacao(id, usuario);

        return "redirect:/fazenda/" + codigo + "/racoes";
    }

    @GetMapping("/{id}/editar")
    public String editarForm(@PathVariable String codigo, @PathVariable Long id, HttpSession session, Model model) {
        Usuario usuario = (Usuario) session.getAttribute("usuario");

        TipoRacao racao = tipoRacaoService.buscarRacaoPorId(id, usuario);

        model.addAttribute("racao", racao);
        model.addAttribute("codigo", codigo);
        return "racao/formulario_tipo"; //O mesmo form de adicionar serve para editar
    }

    @PostMapping("/{id}/editar")
    public String editar(
            @PathVariable String codigo, @PathVariable Long id, @RequestParam String nome,
            @RequestParam String fabricante, @RequestParam BigDecimal kgPorSaco, @RequestParam BigDecimal valorPorSaco,
            HttpSession session, Model model
    ) {
        Usuario usuario = (Usuario) session.getAttribute("usuario");

        try {
            tipoRacaoService.editarRacao(id, nome, fabricante, kgPorSaco, valorPorSaco, usuario);
            return "redirect:/fazenda/" + codigo + "/racoes";

        } catch (IllegalArgumentException e) {
            model.addAttribute("erro", e.getMessage());
            model.addAttribute("codigo", codigo);
            model.addAttribute("racao", tipoRacaoService.buscarRacaoPorId(id, usuario));
            return "racao/formulario_tipo";
        }
    }

}
