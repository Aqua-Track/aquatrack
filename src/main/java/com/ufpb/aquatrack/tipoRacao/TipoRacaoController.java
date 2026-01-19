package com.ufpb.aquatrack.tipoRacao;

import com.ufpb.aquatrack.usuario.Usuario;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

@Controller
@RequestMapping("/fazenda/{fazendaId}/racoes")
public class TipoRacaoController {

    private final TipoRacaoService tipoRacaoService;

    public TipoRacaoController(TipoRacaoService tipoRacaoService) {
        this.tipoRacaoService = tipoRacaoService;
    }


    @GetMapping
    public String listar(@PathVariable Long fazendaId, HttpSession session, Model model
    ) {
        Usuario usuario = (Usuario) session.getAttribute("usuario");

        model.addAttribute("racoes", tipoRacaoService.listarRacoesDoUsuario(usuario));
        model.addAttribute("idFazenda", fazendaId);

        return "racao/listar_tipo";
    }


    @GetMapping("/nova")
    public String nova(@PathVariable Long fazendaId, Model model) {
        model.addAttribute("idFazenda", fazendaId);
        return "racao/formulario_tipo";
    }


    @PostMapping("/criar")
    public String criar(
            @PathVariable Long fazendaId, @RequestParam String nome,
            @RequestParam String fabricante, @RequestParam Double kgPorSaco,
            @RequestParam BigDecimal valorPorSaco, HttpSession session, Model model
    ) {
        Usuario usuario = (Usuario) session.getAttribute("usuario");

        try {
            tipoRacaoService.cadastrarRacao(nome, fabricante, kgPorSaco, valorPorSaco, usuario);

            return "redirect:/fazenda/" + fazendaId + "/racoes";

        } catch (IllegalArgumentException e) {
            model.addAttribute("erro", e.getMessage());
            model.addAttribute("idFazenda", fazendaId);
            return "racao/formulario_tipo";
        }
    }


    @PostMapping("/{id}/remover")
    public String remover(@PathVariable Long fazendaId, @PathVariable Long id, HttpSession session) {
        Usuario usuario = (Usuario) session.getAttribute("usuario");

        tipoRacaoService.removerRacao(id, usuario);

        return "redirect:/fazenda/" + fazendaId + "/racoes";
    }
}
