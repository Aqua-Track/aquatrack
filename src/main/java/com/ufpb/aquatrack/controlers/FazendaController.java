package com.ufpb.aquatrack.controlers;

import com.ufpb.aquatrack.models.Fazenda;
import com.ufpb.aquatrack.models.Usuario;
import com.ufpb.aquatrack.services.EstoqueRacaoService;
import com.ufpb.aquatrack.services.FazendaService;
import com.ufpb.aquatrack.services.ViveiroService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.math.BigDecimal;

@Controller
public class FazendaController {

    private final FazendaService fazendaService;
    private final ViveiroService viveiroService;
    private final EstoqueRacaoService estoqueRacaoService;

    public FazendaController(FazendaService fazendaService, ViveiroService viveiroService, EstoqueRacaoService estoqueRacaoService) {
        this.fazendaService = fazendaService;
        this.viveiroService = viveiroService;
        this.estoqueRacaoService = estoqueRacaoService;
    }

    @GetMapping("/fazendas")
    public String listarFazendas(HttpSession session, Model model) {
        Usuario usuario = (Usuario) session.getAttribute("usuario");

        model.addAttribute("fazendas", fazendaService.listarFazendasDoUsuario(usuario));

        return "fazendas/listar_fazenda";
    }

    @GetMapping("/fazendas/cadastrar")
    public String formularioFazenda() {
        return "fazendas/formulario_fazendas";
    }

    @PostMapping("/fazendas/cadastrar")
    public String criarFazenda(
            @RequestParam String nome, @RequestParam
            String localizacao, HttpSession session, Model model
    ) {
        Usuario usuario = (Usuario) session.getAttribute("usuario");

        try {
            fazendaService.criarFazenda(nome, localizacao, usuario);
            return "redirect:/fazendas";
        } catch (IllegalArgumentException e) {
            model.addAttribute("erro", e.getMessage());
            return "fazendas/formulario_fazendas";
        }
    }

    @GetMapping("/fazenda/{id}")
    public String abrirFazenda(@PathVariable Long id, HttpSession session, Model model) {
        Usuario usuario = (Usuario) session.getAttribute("usuario");
        Fazenda fazenda = fazendaService.buscarFazendaPorId(id);

        if (!fazenda.getUsuario().getId().equals(usuario.getId())) {
            throw new IllegalArgumentException("Acesso negado");
        }

        model.addAttribute("fazenda", fazenda);
        model.addAttribute("viveiros", viveiroService.listarViveiros(id, usuario));

        model.addAttribute("estoques", estoqueRacaoService.listarEstoqueDaFazenda(id, usuario));

        BigDecimal valorTotalEstoque = estoqueRacaoService
                .listarEstoqueDaFazenda(id, usuario)
                .stream()
                .map(estoque ->
                        estoque.getTipoRacao()
                                .getValorPorSaco()
                                .multiply(BigDecimal.valueOf(estoque.getQuantidadeSacos()))
                )
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        model.addAttribute("valorTotalEstoque", valorTotalEstoque);

        return "fazendas/pagina_fazenda";
    }

    @PostMapping("/fazenda/{id}/remover")
    public String removerFazenda(@PathVariable Long id) {
        fazendaService.deletarFazenda(id);
        return "redirect:/fazendas";
    }
}
