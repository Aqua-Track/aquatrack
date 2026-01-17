package com.ufpb.aquatrack.estoqueRacao;

import com.ufpb.aquatrack.tipoRacao.TipoRacao;
import com.ufpb.aquatrack.usuario.Usuario;
import com.ufpb.aquatrack.tipoRacao.TipoRacaoService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
public class EstoqueRacaoController {

    private final EstoqueRacaoService estoqueRacaoService;
    private final TipoRacaoService tipoRacaoService;

    public EstoqueRacaoController(EstoqueRacaoService estoqueRacaoService, TipoRacaoService tipoRacaoService) {
        this.estoqueRacaoService = estoqueRacaoService;
        this.tipoRacaoService = tipoRacaoService;
    }

    @GetMapping("/fazenda/{id}/abastecer-racao")
    public String abrirAbastecimento(@PathVariable Long id, HttpSession session, Model model) {
        Usuario usuario = (Usuario) session.getAttribute("usuario");

        // lista de tipos de ração do usuário
        List<TipoRacao> tiposRacao = tipoRacaoService.listarRacoesDoUsuario(usuario);

        model.addAttribute("idFazenda", id);
        model.addAttribute("tiposRacao", tiposRacao);

        return "racao/formulario_adicionar_racao";
    }

    @PostMapping("/fazenda/{id}/abastecer-racao")
    public String abastecer(
            @PathVariable Long id, @RequestParam Long tipoRacaoId,
            @RequestParam int quantidadeSacos, HttpSession session, Model model
    ) {
        Usuario usuario = (Usuario) session.getAttribute("usuario");

        try {
            estoqueRacaoService.abastecerEstoque(id, tipoRacaoId, quantidadeSacos, usuario);
            return "redirect:/fazenda/" + id;

        } catch (IllegalArgumentException e) {
            model.addAttribute("erro", e.getMessage());
            model.addAttribute("idFazenda", id);
            model.addAttribute("tiposRacao", tipoRacaoService.listarRacoesDoUsuario(usuario));

            return "racao/formulario_adicionar_racao";
        }
    }
}