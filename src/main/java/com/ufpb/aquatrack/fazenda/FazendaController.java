package com.ufpb.aquatrack.fazenda;

import com.ufpb.aquatrack.ciclo.CicloService;
import com.ufpb.aquatrack.usuario.Usuario;
import com.ufpb.aquatrack.estoqueRacao.EstoqueRacaoService;
import com.ufpb.aquatrack.viveiro.Viveiro;
import com.ufpb.aquatrack.viveiro.ViveiroService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class FazendaController {

    private final FazendaService fazendaService;
    private final ViveiroService viveiroService;
    private final EstoqueRacaoService estoqueRacaoService;
    private final CicloService cicloService;

    public FazendaController(FazendaService fazendaService, ViveiroService viveiroService,
                             EstoqueRacaoService estoqueRacaoService, CicloService cicloService) {
        this.fazendaService = fazendaService;
        this.viveiroService = viveiroService;
        this.estoqueRacaoService = estoqueRacaoService;
        this.cicloService = cicloService;
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

    @GetMapping("/fazenda/{codigo}")
    public String abrirFazenda(@PathVariable String codigo, HttpSession session, Model model) {
        Usuario usuario = (Usuario) session.getAttribute("usuario");

        Fazenda fazenda = fazendaService.buscarFazendaPorCodigo(codigo);

        if (!fazenda.getUsuario().getId().equals(usuario.getId())) {
            throw new IllegalArgumentException("Acesso negado");
        }

        model.addAttribute("fazenda", fazenda);
        Long fazendaId = fazenda.getId(); // ID técnico só para uso interno
        List<Viveiro> viveiros = viveiroService.listarViveiros(fazendaId, usuario);

        model.addAttribute("viveiros", viveiros);
        model.addAttribute("estoques", estoqueRacaoService.listarEstoqueDaFazenda(fazendaId, usuario));

        BigDecimal valorTotalEstoque = estoqueRacaoService
                .listarEstoqueDaFazenda(fazendaId, usuario)
                .stream()
                .map(estoque ->
                        estoque.getTipoRacao()
                                .getValorPorSaco()
                                .multiply(BigDecimal.valueOf(estoque.getQuantidadeSacos()))
                )
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        model.addAttribute("valorTotalEstoque", valorTotalEstoque);

        Map<Long, Boolean> statusCicloPorViveiro = new HashMap<>();

        for (Viveiro viveiro : viveiros) {
            boolean temCicloAtivo = cicloService.existeCicloAtivo(viveiro.getId(), usuario);
            statusCicloPorViveiro.put(viveiro.getId(), temCicloAtivo);
        }

        model.addAttribute("statusCiclo", statusCicloPorViveiro);


        return "fazendas/pagina_fazenda";
    }


    @PostMapping("/fazenda/{codigo}/remover")
    public String removerFazenda(@PathVariable String codigo, HttpSession session) {
        Usuario usuario = (Usuario) session.getAttribute("usuario");
        Fazenda fazenda = fazendaService.buscarFazendaPorCodigo(codigo);

        if (!fazenda.getUsuario().getId().equals(usuario.getId())) {
            throw new IllegalArgumentException("Acesso negado");
        }

        fazendaService.deletarFazenda(fazenda.getId());
        return "redirect:/fazendas";
    }
}
