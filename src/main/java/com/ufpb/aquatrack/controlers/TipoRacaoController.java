package com.ufpb.aquatrack.controlers;

import com.ufpb.aquatrack.models.Usuario;
import com.ufpb.aquatrack.services.TipoRacaoService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpSession;

import java.math.BigDecimal;

@Controller
@RequestMapping("/racoes")
public class TipoRacaoController {

    private final TipoRacaoService tipoRacaoService;

    public TipoRacaoController(TipoRacaoService tipoRacaoService) {
        this.tipoRacaoService = tipoRacaoService;
    }

    @GetMapping
    public String listar(HttpSession session, Model model) {
        Usuario usuario = (Usuario) session.getAttribute("usuario");

        model.addAttribute("racoes", tipoRacaoService.listarRacoesDoUsuario(usuario));

        return "racao/listar_tipo";
    }

    @GetMapping("/nova")
    public String nova() {
        return "racao/formulario_tipo";
    }


    @PostMapping("/criar")
    public String criar(
            @RequestParam String nome, @RequestParam String fabricante, @RequestParam Double kgPorSaco,
            @RequestParam BigDecimal valorPorSaco, HttpSession session, Model model
    ) {
        Usuario usuario = (Usuario) session.getAttribute("usuario");

        try {
            tipoRacaoService.cadastrarRacao(nome, fabricante, kgPorSaco, valorPorSaco, usuario);
            return "redirect:/racoes";

        } catch (IllegalArgumentException e) {
            model.addAttribute("erro", e.getMessage());
            return "racao/formulario_tipo";
        }
    }


    @PostMapping("/{id}/remover")
    public String remover(@PathVariable Long id, HttpSession session) {
        Usuario usuario = (Usuario) session.getAttribute("usuario");
        tipoRacaoService.removerRacao(id, usuario);
        return "redirect:/racoes";
    }
}
