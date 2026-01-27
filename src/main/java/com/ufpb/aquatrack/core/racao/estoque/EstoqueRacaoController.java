package com.ufpb.aquatrack.core.racao.estoque;

import com.ufpb.aquatrack.core.fazenda.Fazenda;
import com.ufpb.aquatrack.core.fazenda.FazendaService;
import com.ufpb.aquatrack.core.racao.tipo.TipoRacao;
import com.ufpb.aquatrack.core.usuario.Usuario;
import com.ufpb.aquatrack.core.racao.tipo.TipoRacaoService;
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
    private final FazendaService fazendaService;

    public EstoqueRacaoController(EstoqueRacaoService estoqueRacaoService, TipoRacaoService tipoRacaoService, FazendaService fazendaService) {
        this.estoqueRacaoService = estoqueRacaoService;
        this.tipoRacaoService = tipoRacaoService;
        this.fazendaService = fazendaService;
    }

    @GetMapping("/fazenda/{codigo}/abastecer-racao")
    public String abrirAbastecimento(@PathVariable String codigo, HttpSession session, Model model) {
        Usuario usuario = (Usuario) session.getAttribute("usuario");

        // lista de tipos de ração do usuário
        List<TipoRacao> tiposRacao = tipoRacaoService.listarRacoesDoUsuario(usuario);

        model.addAttribute("codigo", codigo);
        model.addAttribute("tiposRacao", tiposRacao);

        return "racao/formulario_adicionar_racao";
    }

    @PostMapping("/fazenda/{codigo}/abastecer-racao")
    public String abastecer(
            @PathVariable String codigo, @RequestParam Long tipoRacaoId,
            @RequestParam int quantidadeSacos, HttpSession session, Model model
    ) {
        Usuario usuario = (Usuario) session.getAttribute("usuario");
        Fazenda fazenda = fazendaService.buscarFazendaPorCodigo(codigo);

        try {
            estoqueRacaoService.abastecerEstoque(fazenda.getCodigo(), tipoRacaoId, quantidadeSacos, usuario);
            return "redirect:/fazenda/" + codigo;

        } catch (IllegalArgumentException e) {
            model.addAttribute("erro", e.getMessage());
            model.addAttribute("codigo", codigo);
            model.addAttribute("tiposRacao", tipoRacaoService.listarRacoesDoUsuario(usuario));

            return "racao/formulario_adicionar_racao";
        }
    }
}